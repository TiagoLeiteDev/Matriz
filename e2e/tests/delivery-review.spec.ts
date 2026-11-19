import { test, expect } from '@playwright/test'
import type { APIRequestContext } from '@playwright/test'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD do seed.')
let projects: string[] = [], personal: string[] = []
test.beforeEach(() => { projects = []; personal = [] })
async function csrf(request: APIRequestContext) { return { 'X-CSRF-Token': (await (await request.get('/api/sessao/csrf')).json()).token } }
async function login(request: APIRequestContext) {
  expect((await request.post('/api/sessao', { headers: await csrf(request), data: { email: 'ana@example.test', palavraPasse: password } })).status()).toBe(200)
}
async function createProject(request: APIRequestContext, nome: string) {
  const response = await request.post('/api/projetos', { headers: await csrf(request), data: { nome } })
  expect(response.status()).toBe(201); const project = await response.json(); projects.push(project.id); return project
}
async function task(request: APIRequestContext, projetoId: string | null, estado: string) {
  const response = await request.post('/api/tarefas', { headers: await csrf(request), data: { titulo: `Progresso ${Date.now()}`, projetoId, estado } })
  expect(response.status()).toBe(201); const row = await response.json(); if (!projetoId) personal.push(row.id); return row
}
test.afterEach(async ({ request }) => {
  if (!projects.length && !personal.length) return
  await login(request)
  for (const id of projects) expect([204, 404]).toContain((await request.delete(`/api/projetos/${id}`, { headers: await csrf(request) })).status())
  for (const id of personal) expect([204, 404]).toContain((await request.delete(`/api/tarefas/${id}`, { headers: await csrf(request) })).status())
})
test('revisão de entrega: progresso exato, fração em português, pessoais e transferência', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await login(request)
  const project = await createProject(request, `Revisão de progresso ${Date.now()}`)
  const done = await task(request, project.id, 'DONE')
  const pending = []
  for (let i = 0; i < 3; i++) pending.push(await task(request, project.id, 'TODO'))
  await task(request, null, 'DONE')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await (await request.get(`/api/projetos/${project.id}`)).json()).progresso.percentagem).toBe(25)

  // Arrange — preparar o próximo cenário/dados.
  await page.goto(`/#projetos/${project.id}`)
  await page.getByLabel('Email', { exact: true }).fill('ana@example.test'); await page.getByLabel('Palavra-passe').fill(password!)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByText('25% (1/4)', { exact: true })).toBeVisible()
  expect((await request.delete(`/api/tarefas/${pending[0].id}`, { headers: await csrf(request) })).status()).toBe(204)
  expect((await (await request.get(`/api/projetos/${project.id}`)).json()).progresso.percentagem).toBe(33.3)

  // Act — executar a operação em teste.
  await page.reload()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByText('33,3% (1/3)', { exact: true })).toBeVisible()
  expect((await request.delete(`/api/tarefas/${pending[1].id}`, { headers: await csrf(request) })).status()).toBe(204)
  await page.reload(); await expect(page.getByText('50% (1/2)', { exact: true })).toBeVisible()
  expect((await request.delete(`/api/tarefas/${pending[2].id}`, { headers: await csrf(request) })).status()).toBe(204)
  await page.reload(); await expect(page.getByText('100% (1/1)', { exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  const destination = await createProject(request, 'Destino de progresso')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await request.patch(`/api/tarefas/${done.id}`, { headers: await csrf(request), data: { projetoId: destination.id } })).status()).toBe(200)
  expect((await (await request.get(`/api/projetos/${project.id}`)).json()).progresso.percentagem).toBe(0)
  expect((await (await request.get(`/api/projetos/${destination.id}`)).json()).progresso.percentagem).toBe(100)
  await page.reload(); await expect(page.getByText('0% — Sem tarefas', { exact: true })).toBeVisible()
})
