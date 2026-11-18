import { test, expect } from '@playwright/test'
import type { APIRequestContext, Page } from '@playwright/test'
import { execFileSync } from 'node:child_process'
import path from 'node:path'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD com a palavra-passe fictícia do seed.')
let created: string[] = []
test.beforeEach(() => { created = [] })
async function loginApi(request: APIRequestContext) {
  const { token } = await (await request.get('/api/sessao/csrf')).json()
  expect((await request.post('/api/sessao', { headers: { 'X-CSRF-Token': token },
    data: { email: 'ana@example.test', palavraPasse: password } })).status()).toBe(200)
}
async function remove(request: APIRequestContext, id: string) {
  const { token } = await (await request.get('/api/sessao/csrf')).json()
  const response = await request.delete(`/api/projetos/${id}`, { headers: { 'X-CSRF-Token': token } })
  expect([204, 404]).toContain(response.status())
}
test.afterEach(async ({ request }) => {
  // Only IDs created by this test, never seed fixtures or arbitrary existing projects.
  if (created.length) { await loginApi(request); for (const id of created) await remove(request, id) }
})
async function enter(page: Page, email = 'ana@example.test', navigate = true) {
  if (navigate) await page.goto('/')
  await page.getByLabel('Email', { exact: true }).fill(email)
  await page.getByLabel('Palavra-passe').fill(password!)
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Terminar sessão' })).toBeVisible()
  if (!page.url().includes('#projetos/')) await page.getByRole('button', { name: 'Projetos', exact: true }).click()
}
async function createUi(page: Page, name: string, description = '') {
  await page.getByRole('button', { name: 'Criar projeto', exact: true }).click()
  await page.getByLabel('Nome do projeto').fill(name)
  await page.getByLabel('Descrição (opcional)').fill(description)
  const response = page.waitForResponse(response => new URL(response.url()).pathname === '/api/projetos' && response.request().method() === 'POST')
  await page.getByRole('button', { name: 'Guardar projeto' }).click()
  const saved = await response
  expect(saved.status()).toBe(201)
  const project = await saved.json(); created.push(project.id)
  await expect(page.getByRole('heading', { name, exact: true })).toBeVisible()
  return project.id as string
}

test('criar, listar, consultar, editar, recarregar e eliminar com cancelamento sem pedido', async ({ page }) => {
  await enter(page)
  const name = `Projeto CRUD ${Date.now()}`
  const id = await createUi(page, name, 'Descrição inicial')
  await page.getByRole('link', { name: 'Projetos', exact: true }).click()
  await page.getByRole('link', { name, exact: true }).click()
  await page.getByRole('button', { name: 'Editar projeto' }).click()
  await page.getByLabel('Nome do projeto').fill(`${name} editado`)
  await page.getByLabel('Descrição (opcional)').fill('Descrição conservada após reload')
  await page.getByRole('button', { name: 'Guardar projeto' }).click()
  await expect(page.getByRole('heading', { name: `${name} editado`, exact: true })).toBeVisible()
  await page.reload()
  await expect(page.getByRole('heading', { name: `${name} editado`, exact: true })).toBeVisible()
  await expect(page.getByText('Descrição conservada após reload', { exact: true })).toBeVisible()
  let deletes = 0
  page.on('request', request => { if (request.method() === 'DELETE' && request.url().endsWith(`/api/projetos/${id}`)) deletes++ })
  await page.getByRole('button', { name: 'Eliminar projeto', exact: true }).click()
  await expect(page.getByRole('dialog')).toContainText('comentários, atribuições de responsáveis e colaborações')
  await page.getByRole('dialog').getByRole('button', { name: 'Cancelar' }).click()
  expect(deletes).toBe(0)
  expect((await page.request.get(`/api/projetos/${id}`)).status()).toBe(200)
  await page.getByRole('button', { name: 'Eliminar projeto', exact: true }).click()
  await page.getByRole('button', { name: 'Confirmar eliminação' }).click()
  await expect(page.getByRole('status').filter({ hasText: /^Projeto eliminado\.$/ })).toBeVisible()
  await expect(page.getByRole('link', { name: `${name} editado`, exact: true })).toHaveCount(0)
  expect(deletes).toBe(1)
  expect((await page.request.get(`/api/projetos/${id}`)).status()).toBe(404)
})

test('pesquisa ignora acentos/maiúsculas e arquivo/restauro conserva o projeto', async ({ page }) => {
  await enter(page)
  const suffix = String(Date.now())
  const name = `Preparar apresentação ${suffix}`
  await createUi(page, name, 'Conservar no arquivo')
  await page.getByRole('link', { name: 'Projetos', exact: true }).click()
  await page.getByLabel('Pesquisar por nome').fill(`APRESENTACAO ${suffix}`)
  await page.getByRole('button', { name: 'Pesquisar', exact: true }).click()
  await page.getByRole('link', { name, exact: true }).click()
  await page.getByRole('button', { name: 'Arquivar projeto', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Restaurar projeto' })).toBeVisible()
  await page.getByRole('link', { name: 'Projetos', exact: true }).click()
  await expect(page.getByText('Nenhum projeto encontrado.')).toBeVisible()
  await page.getByLabel('Mostrar projetos arquivados').check()
  await page.getByRole('link', { name, exact: true }).click()
  await expect(page.getByText('Conservar no arquivo', { exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Restaurar projeto', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Arquivar projeto' })).toBeVisible()
})

test('colaborador consulta mas não recebe controlos de gestão ou projetos alheios', async ({ page }) => {
  await enter(page, 'bruno@example.test')
  await page.getByRole('link', { name: 'Projeto partilhado da Ana', exact: true }).click()
  await expect(page.getByText('Tens acesso de colaboração a este projeto.')).toBeVisible()
  await expect(page.getByRole('button', { name: 'Editar projeto' })).toHaveCount(0)
  await expect(page.getByRole('button', { name: 'Eliminar projeto' })).toHaveCount(0)
  await page.getByRole('button', { name: 'Terminar sessão' }).click()
  await page.goto('/')
  await enter(page, 'ana@example.test')
  await expect(page.getByRole('button', { name: 'Criar projeto' })).toBeVisible()
  await expect(page.getByRole('link', { name: 'Projeto privado do Bruno', exact: true })).toHaveCount(0)
})

test('erro de comunicação conserva formulário e não anuncia criação nem duplica submissão', async ({ page }) => {
  await enter(page)
  await page.getByRole('button', { name: 'Criar projeto', exact: true }).click()
  await page.getByLabel('Nome do projeto').fill('Rascunho conservado')
  await page.getByLabel('Descrição (opcional)').fill('Não perder estes dados')
  let writes = 0
  await page.route('**/api/projetos', async route => {
    if (route.request().method() === 'POST') { writes++; await route.abort() } else await route.continue()
  })
  await page.getByRole('button', { name: 'Guardar projeto' }).click()
  await expect(page.getByRole('alert')).toBeVisible()
  await expect(page.getByRole('alert')).toContainText('Não foi possível contactar o serviço.')
  await expect(page.getByLabel('Nome do projeto')).toHaveValue('Rascunho conservado')
  await expect(page.getByLabel('Descrição (opcional)')).toHaveValue('Não perder estes dados')
  await expect(page.getByText('Projeto criado.', { exact: true })).toHaveCount(0)
  expect(writes).toBe(1)
  await page.unroute('**/api/projetos')
  const response = page.waitForResponse(response => new URL(response.url()).pathname === '/api/projetos' && response.request().method() === 'POST')
  await page.getByRole('button', { name: 'Guardar projeto' }).click()
  const saved = await response; expect(saved.status()).toBe(201); const project = await saved.json(); created.push(project.id)
  await expect(page.getByRole('heading', { name: 'Rascunho conservado', exact: true })).toBeVisible()
})

test('nome só com espaços é recusado sem pedido e nome de um carácter é aceite', async ({ page }) => {
  await enter(page)
  await page.getByRole('button', { name: 'Criar projeto', exact: true }).click()
  await page.getByLabel('Nome do projeto').fill('   ')
  let creates = 0
  page.on('request', request => { if (request.method() === 'POST' && request.url().endsWith('/api/projetos')) creates++ })
  await page.getByRole('button', { name: 'Guardar projeto' }).click()
  await expect(page.getByRole('alert')).toContainText('Indica um nome')
  expect(creates).toBe(0)
  await page.getByLabel('Nome do projeto').fill('X')
  const response = page.waitForResponse(response => response.request().method() === 'POST' && response.url().endsWith('/api/projetos'))
  await page.getByRole('button', { name: 'Guardar projeto' }).click()
  const project = await (await response).json(); created.push(project.id)
  await expect(page.getByRole('heading', { name: 'X', exact: true })).toBeVisible()
})

test('primeiro percurso vertical mantém projeto após reinício e novo login @restart', async ({ page }) => {
  test.skip(process.env.MATRIZ_RESTART_TEST !== '1', 'Reinicia app explicitamente; executar em separado.')
  test.setTimeout(100_000)
  await enter(page)
  const name = `Persistência após reinício ${Date.now()}`
  const id = await createUi(page, name, 'Dados reais em PostgreSQL')
  await page.reload()
  await expect(page.getByRole('heading', { name, exact: true })).toBeVisible()
  execFileSync('docker', ['compose', 'restart', 'app'], { cwd: path.resolve(process.cwd(), '..'), stdio: 'pipe', timeout: 30_000 })
  await expect.poll(async () => {
    try { return (await page.request.get('/actuator/health', { timeout: 1000 })).status() } catch { return 0 }
  }, { timeout: 45_000, intervals: [500, 1000] }).toBe(200)
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  await enter(page, 'ana@example.test', false)
  await expect(page.getByRole('heading', { name, exact: true })).toBeVisible()
  await expect(page.getByText('Dados reais em PostgreSQL', { exact: true })).toBeVisible()
  expect((await page.request.get(`/api/projetos/${id}`)).status()).toBe(200)
})
