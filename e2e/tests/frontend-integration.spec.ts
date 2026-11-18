import { test, expect } from '@playwright/test'
import type { Page, APIRequestContext } from '@playwright/test'
import { mkdirSync } from 'node:fs'
import path from 'node:path'
const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD do seed.')
let projects: string[] = [], tasks: string[] = []
test.beforeEach(() => { projects = []; tasks = [] })
async function headers(request: APIRequestContext) { return { 'X-CSRF-Token': (await (await request.get('/api/sessao/csrf')).json()).token } }
async function login(request: APIRequestContext) { expect((await request.post('/api/sessao', { headers: await headers(request), data: { email: 'ana@example.test', palavraPasse: password } })).status()).toBe(200) }
async function enter(page: Page, hash = '', email = 'ana@example.test') {
  await page.goto(`/${hash}`)
  await page.getByLabel('Email', { exact: true }).fill(email); await page.getByLabel('Palavra-passe').fill(password!)
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Terminar sessão' })).toBeVisible()
}
async function project(request: APIRequestContext) {
  await login(request)
  const r = await request.post('/api/projetos', { headers: await headers(request), data: { nome: `Integração ${Date.now()}` } })
  expect(r.status()).toBe(201); const p = await r.json(); projects.push(p.id); return p
}
async function task(request: APIRequestContext, body: Record<string, unknown>) {
  const r = await request.post('/api/tarefas', { headers: await headers(request), data: body })
  expect(r.status()).toBe(201); const t = await r.json(); if (!t.projetoId) tasks.push(t.id); return t
}
test.afterEach(async ({ request }) => {
  if (!projects.length && !tasks.length) return
  await login(request)
  for (const id of projects) expect([204, 404]).toContain((await request.delete(`/api/projetos/${id}`, { headers: await headers(request) })).status())
  for (const id of tasks) expect([204, 404]).toContain((await request.delete(`/api/tarefas/${id}`, { headers: await headers(request) })).status())
})
test('painel inicial e contagens seguem criações/edições/arquivo com navegação real', async ({ page, request }) => {
  const p = await project(request)
  const t = await task(request, { titulo: `Painel ${Date.now()}`, projetoId: p.id })
  await task(request, { titulo: 'Pessoal no painel', estado: 'DONE' })
  await enter(page)
  const region = page.getByRole('region', { name: 'Painel inicial', exact: true })
  const before = await (await request.get('/api/painel?incluirIndependentes=true')).json()
  for (const state of ['TODO', 'IN_PROGRESS', 'DONE']) await expect(region.getByTestId(`count-${state}`)).toHaveText(String(before.contagens[state]))
  await region.getByRole('link', { name: p.nome, exact: true }).click()
  await page.getByRole('button', { name: t.titulo, exact: true }).click()
  await page.getByRole('button', { name: 'Editar tarefa', exact: true }).click()
  await page.getByLabel('Estado da tarefa').selectOption('DONE')
  await page.getByRole('button', { name: 'Guardar tarefa', exact: true }).click()
  await expect(page.getByRole('status').filter({ hasText: /^Tarefa atualizada\.$/ })).toBeVisible()
  await page.getByRole('button', { name: 'Painel', exact: true }).click()
  await expect(region.getByTestId('count-TODO')).toHaveText(String(before.contagens.TODO - 1))
  await expect(region.getByTestId('count-DONE')).toHaveText(String(before.contagens.DONE + 1))
  expect((await request.patch(`/api/projetos/${p.id}`, { headers: await headers(request), data: { arquivado: true } })).status()).toBe(200)
  await region.getByRole('button', { name: 'Atualizar painel' }).click()
  await expect(region.getByRole('link', { name: p.nome, exact: true })).toHaveCount(0)
  await expect(region.getByTestId('count-DONE')).toHaveText(String(before.contagens.DONE))
})
test('colaboradores: partilhar, remover em arquivo, revogar atribuições e conservar comentários', async ({ page, request, browser }) => {
  const p = await project(request); const t = await task(request, { titulo: 'Tarefa partilhável', projetoId: p.id })
  await enter(page, `#projetos/${p.id}`)
  const members = page.getByRole('region', { name: 'Colaboradores do projeto', exact: true })
  await members.getByLabel('Email do colaborador').fill('bruno@example.test')
  await members.getByRole('button', { name: 'Adicionar colaborador' }).click()
  await expect(members.getByRole('button', { name: 'Remover colaborador Bruno Demo', exact: true })).toBeVisible()
  const context = await browser.newContext({ baseURL: process.env.MATRIZ_BASE_URL || 'http://127.0.0.1:8080' })
  try {
    const other = await context.newPage(); await enter(other, `#projetos/${p.id}`, 'bruno@example.test')
    await expect(other.getByRole('heading', { name: p.nome, exact: true })).toBeVisible()
    await expect(other.getByLabel('Email do colaborador')).toHaveCount(0)
    const profile = await (await other.request.get('/api/sessao')).json()
    expect((await request.post(`/api/tarefas/${t.id}/responsaveis`, { headers: await headers(request), data: { utilizadorId: profile.id } })).status()).toBe(201)
    expect((await other.request.post(`/api/tarefas/${t.id}/comentarios`, { headers: await headers(other.request), data: { conteudo: 'Autoria conservada' } })).status()).toBe(201)
    await page.getByRole('button', { name: 'Arquivar projeto', exact: true }).click()
    await expect(page.getByRole('button', { name: 'Restaurar projeto', exact: true })).toBeVisible()
    await members.getByRole('button', { name: 'Remover colaborador Bruno Demo', exact: true }).click()
    await expect(members.getByText('Sem colaboradores adicionais.', { exact: true })).toBeVisible()
    expect((await other.request.get(`/api/tarefas/${t.id}`)).status()).toBe(403)
    expect(await (await request.get(`/api/tarefas/${t.id}/responsaveis`)).json()).toHaveLength(0)
    expect((await (await request.get(`/api/tarefas/${t.id}/comentarios`)).json())[0].autor.email).toBe('bruno@example.test')
    await members.getByLabel('Email do colaborador').fill('bruno@example.test')
    await members.getByRole('button', { name: 'Adicionar colaborador' }).click()
    await expect(members.getByRole('button', { name: 'Remover colaborador Bruno Demo', exact: true })).toBeVisible()
  } finally { await context.close() }
})
test('vistas mantêm filtros, estado e prazo; layout utilizável a 360px', async ({ page, request }) => {
  const p = await project(request)
  const noDate = await task(request, { titulo: 'Sem prazo próprio', projetoId: p.id, prioridade: 'LOW' })
  const due = await task(request, { titulo: 'Prazo próprio', projetoId: p.id, estado: 'IN_PROGRESS', prioridade: 'HIGH', prazo: '2026-11-19' })
  await task(request, { titulo: 'Dezembro próprio', projetoId: p.id, estado: 'DONE', prioridade: 'HIGH', prazo: '2026-12-01' })
  await enter(page, '#tarefas')
  await page.getByLabel('Filtrar por projeto').selectOption(p.id)
  await page.getByRole('button', { name: 'Aplicar filtros' }).click()
  await expect(page.getByTestId('task-count')).toHaveText('Nesta lista: 3 tarefas.')
  await page.getByRole('button', { name: 'Kanban', exact: true }).click()
  await expect(page.getByRole('region', { name: 'Coluna Por fazer', exact: true }).getByRole('button', { name: noDate.titulo, exact: true })).toBeVisible()
  await expect(page.getByRole('region', { name: 'Coluna Em curso', exact: true }).getByRole('button', { name: due.titulo, exact: true })).toBeVisible()
  mkdirSync(path.resolve(process.cwd(), '../tmp/ui'), { recursive: true })
  await page.screenshot({ path: path.resolve(process.cwd(), '../tmp/ui/ex7-kanban-desktop.png'), fullPage: true })
  await page.getByLabel('Filtrar por prioridade').selectOption('HIGH')
  await page.getByRole('button', { name: 'Aplicar filtros' }).click()
  await expect(page.getByRole('button', { name: noDate.titulo, exact: true })).toHaveCount(0)
  await page.getByRole('button', { name: 'Calendário', exact: true }).click()
  await page.getByLabel('Mês do calendário').fill('2026-11')
  const november = page.getByRole('region', { name: '2026-11-19', exact: true })
  await expect(november.getByRole('button', { name: due.titulo, exact: true })).toBeVisible()
  await november.getByRole('button', { name: due.titulo, exact: true }).click()
  await page.getByRole('button', { name: 'Editar tarefa' }).click()
  await page.getByLabel('Prazo da tarefa (opcional)').fill('2026-12-02')
  await page.getByLabel('Estado da tarefa').selectOption('DONE')
  await page.getByRole('button', { name: 'Guardar tarefa' }).click()
  await expect(page.getByText('Sem tarefas com prazo neste mês para estes filtros.', { exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Mês seguinte' }).click()
  await expect(page.getByRole('region', { name: '2026-12-02', exact: true }).getByRole('button', { name: due.titulo, exact: true })).toBeVisible()
  await page.setViewportSize({ width: 360, height: 800 })
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true)
  await expect(page.getByRole('heading', { name: 'Prazos neste mês', exact: true })).toBeVisible()
  await expect(page.getByRole('button', { name: due.titulo, exact: true })).toBeVisible()
  await page.screenshot({ path: path.resolve(process.cwd(), '../tmp/ui/ex7-calendar-mobile.png'), fullPage: true })
  await page.getByRole('button', { name: 'Lista', exact: true }).click()
  await expect(page.getByRole('button', { name: due.titulo, exact: true })).toBeVisible()
})
