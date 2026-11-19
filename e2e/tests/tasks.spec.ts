import { test, expect } from '@playwright/test'
import type { APIRequestContext, Page } from '@playwright/test'
import { execFileSync } from 'node:child_process'
import path from 'node:path'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD com a palavra-passe fictícia do seed.')
let ids: string[] = []
let projects: string[] = []
test.beforeEach(() => { ids = []; projects = [] })
async function login(request: APIRequestContext, email = 'ana@example.test') {
  const { token } = await (await request.get('/api/sessao/csrf')).json()
  expect((await request.post('/api/sessao', { data: { email, palavraPasse: password }, headers: { 'X-CSRF-Token': token } })).status()).toBe(200)
}
async function csrf(request: APIRequestContext) { return { 'X-CSRF-Token': (await (await request.get('/api/sessao/csrf')).json()).token } }
test.afterEach(async ({ request }) => {
  if (!ids.length && !projects.length) return
  await login(request)
  // Only IDs created in this test. Never reset the seed or the persistent volume.
  for (const id of ids) {
    expect([204, 404]).toContain((await request.delete(`/api/tarefas/${id}`, { headers: await csrf(request) })).status())
    expect((await request.get(`/api/tarefas/${id}`)).status()).toBe(404)
  }
  for (const id of projects) {
    expect([204, 404]).toContain((await request.delete(`/api/projetos/${id}`, { headers: await csrf(request) })).status())
    expect((await request.get(`/api/projetos/${id}`)).status()).toBe(404)
  }
})
async function enter(page: Page, email = 'ana@example.test', navigate = true) {
  if (navigate) await page.goto('/#tarefas')
  await page.getByLabel('Email', { exact: true }).fill(email)
  await page.getByLabel('Palavra-passe').fill(password!)
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Nova tarefa', exact: true })).toBeVisible()
}
async function save(page: Page, create: boolean) {
  const response = page.waitForResponse(r => new URL(r.url()).pathname.startsWith('/api/tarefas') && r.request().method() === (create ? 'POST' : 'PATCH'))
  await page.getByRole('button', { name: 'Guardar tarefa', exact: true }).click()
  const saved = await response; expect(saved.status()).toBe(create ? 201 : 200)
  const body = await saved.json()
  if (create) ids.push(body.id)
  return body
}

test('tarefas pessoais: CRUD, filtros sem acentos, null, reload e confirmação', async ({ page }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await enter(page)
  const title = `Preparar apresentação ${Date.now()}`

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Nova tarefa', exact: true }).click()
  await page.getByLabel('Título da tarefa').fill(title)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByLabel('Prioridade da tarefa')).toHaveValue('MEDIUM')

  // Arrange — preparar o próximo cenário/dados.
  await page.getByLabel('Descrição da tarefa (opcional)').fill('Descrição persistente')
  await page.getByLabel('Prazo da tarefa (opcional)').fill('2026-11-19')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  const created = await save(page, true)

  // Assert — verificar o resultado.
  expect(created.estado).toBe('TODO'); expect(created.prioridade).toBe('MEDIUM'); expect(created.projetoId).toBeNull()

  // Arrange — preparar o próximo cenário/dados.
  await page.getByLabel('Pesquisar título').fill(title.replace('Preparar apresentação', 'APRESENTACAO'))
  await page.getByLabel('Filtrar por projeto').selectOption('__none')

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Aplicar filtros' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByTestId('task-count')).toHaveText('Nesta lista: 1 tarefas.')

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: title, exact: true }).click()
  await page.getByRole('button', { name: 'Editar tarefa' }).click()
  await page.getByLabel('Estado da tarefa').selectOption('DONE')
  await page.getByLabel('Prioridade da tarefa').selectOption('')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await save(page, false)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: title, exact: true }).click()
  await page.reload()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: title, exact: true })).toBeVisible()
  await expect(page.getByText('Sem prioridade', { exact: true })).toBeVisible()
  expect((await (await page.request.get(`/api/tarefas/${created.id}`)).json()).estado).toBe('DONE')

  // Arrange — preparar o próximo cenário/dados.
  let deletes = 0
  page.on('request', r => { if (r.method() === 'DELETE' && r.url().endsWith(`/api/tarefas/${created.id}`)) deletes++ })

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Eliminar tarefa', exact: true }).click()
  await page.getByRole('dialog').getByRole('button', { name: 'Cancelar' }).click()

  // Assert — verificar o resultado.
  expect(deletes).toBe(0)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Eliminar tarefa', exact: true }).click()
  await page.getByRole('button', { name: 'Confirmar eliminação da tarefa' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('status').filter({ hasText: /^Tarefa eliminada\.$/ })).toBeVisible()
  await expect(page.getByRole('button', { name: title, exact: true })).toHaveCount(0)

  // Assert — verificar o resultado.
  expect(deletes).toBe(1)
})

test('E2E criação em projeto: login, lista persistente e progresso sem recarregar', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await login(request)
  const name = `Progresso tarefas ${Date.now()}`

  // Act — executar a operação em teste.
  const response = await request.post('/api/projetos', { headers: await csrf(request), data: { nome: name } })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect(response.status()).toBe(201); const project = await response.json(); projects.push(project.id)

  // Arrange — preparar o próximo cenário/dados.
  await enter(page)
  await expect(page.getByRole('button', { name: 'Terminar sessão', exact: true })).toBeVisible()

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Projetos', exact: true }).click()
  await page.getByRole('link', { name, exact: true }).click()
  await expect(page.getByRole('heading', { name, exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Nova tarefa', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByLabel('Projeto da tarefa')).toHaveValue(project.id)

  // Arrange — preparar o próximo cenário/dados.
  const title = `Concluir ${Date.now()}`
  await page.getByLabel('Título da tarefa').fill(title)

  // Act — criar pela interface; save aguarda o POST real e verifica 201.
  const created = await save(page, true)

  // Assert — tarefa única na lista, sem recarregar, associada ao projeto aberto.
  expect(created.titulo).toBe(title)
  expect(created.projetoId).toBe(project.id)
  expect(created.estado).toBe('TODO')
  expect(created.prioridade).toBe('MEDIUM')
  const card = page.getByRole('button', { name: title, exact: true })
  await expect(card).toHaveCount(1)
  await expect(card).toBeVisible()
  await expect(card).toContainText('Por fazer')
  await expect(card).toContainText('Média')

  // Act — recarregar a lista do projeto, sem recriar a tarefa.
  await page.reload()

  // Assert — UI e nova consulta à API confirmam o registo persistido.
  await expect(card).toHaveCount(1)
  await expect(card).toBeVisible()
  const stored = await page.request.get(`/api/tarefas/${created.id}`)
  expect(stored.status()).toBe(200)
  expect(await stored.json()).toEqual(created)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: title, exact: true }).click()
  await page.getByRole('button', { name: 'Editar tarefa' }).click()
  await page.getByLabel('Estado da tarefa').selectOption('DONE')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await save(page, false)

  // Assert — verificar o resultado.
  await expect.poll(async () => (await (await page.request.get(`/api/projetos/${project.id}`)).json()).progresso.percentagem).toBe(100)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByText(/100%/)).toBeVisible()
})

test('colaborador desassocia tarefa e recebe JSON null; privacidade regressa ao criador', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await login(request)
  const list = await (await request.get('/api/projetos')).json()
  const project = list.find((p: { nome: string }) => p.nome === 'Projeto partilhado da Ana')
  const title = `Desassociar ${Date.now()}`

  // Act — executar a operação em teste.
  const response = await request.post('/api/tarefas', { headers: await csrf(request), data: { titulo: title, projetoId: project.id } })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect(response.status()).toBe(201); const task = await response.json(); ids.push(task.id)

  // Arrange — preparar o próximo cenário/dados.
  await enter(page, 'bruno@example.test')

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: title, exact: true }).click()
  await page.getByRole('button', { name: 'Editar tarefa' }).click()
  await page.getByLabel('Projeto da tarefa').selectOption('')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByText('A tarefa ficará privada do criador original e perderás o acesso.')).toBeVisible()
  expect(await save(page, false)).toBeNull()
  await expect(page.getByRole('status').filter({ hasText: /deixaste de ter acesso/ })).toBeVisible()
  await expect(page.getByRole('button', { name: title, exact: true })).toHaveCount(0)
  expect((await page.request.get(`/api/tarefas/${task.id}`)).status()).toBe(403)
  expect((await (await request.get(`/api/tarefas/${task.id}`)).json()).projetoId).toBeNull()
})

test('falha conserva formulário e título só com espaços não envia pedido', async ({ page }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await enter(page)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Nova tarefa', exact: true }).click()
  await page.getByLabel('Título da tarefa').fill('   ')

  // Arrange — preparar o próximo cenário/dados.
  let writes = 0
  await page.route('**/api/tarefas', async route => {
    if (route.request().method() === 'POST') { writes++; await route.abort() } else await route.continue()
  })

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Guardar tarefa' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('alert')).toContainText('Indica um título')

  // Assert — verificar o resultado.
  expect(writes).toBe(0)

  // Arrange — preparar o próximo cenário/dados.
  await page.getByLabel('Título da tarefa').fill('X')
  await page.getByLabel('Descrição da tarefa (opcional)').fill('Conservar rascunho')

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Guardar tarefa' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('alert')).toContainText('Não foi possível contactar o serviço.')
  await expect(page.getByLabel('Título da tarefa')).toHaveValue('X')
  await expect(page.getByLabel('Descrição da tarefa (opcional)')).toHaveValue('Conservar rascunho')

  // Assert — verificar o resultado.
  expect(writes).toBe(1)

  // Arrange — preparar o próximo cenário/dados.
  await page.unroute('**/api/tarefas')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await save(page, true)
  await expect(page.getByRole('status').filter({ hasText: /^Tarefa criada\.$/ })).toBeVisible()
})

test('tarefas mantêm os campos após reinício e novo login @task-restart', async ({ page }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  test.skip(process.env.MATRIZ_TASK_RESTART_TEST !== '1', 'Reinício explícito separado.')
  test.setTimeout(100_000)
  await enter(page)
  const title = `Persistência tarefa ${Date.now()}`

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Nova tarefa', exact: true }).click()
  await page.getByLabel('Título da tarefa').fill(title)
  await page.getByLabel('Estado da tarefa').selectOption('IN_PROGRESS')
  await page.getByLabel('Prioridade da tarefa').selectOption('HIGH')
  await page.getByLabel('Prazo da tarefa (opcional)').fill('2026-11-19')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  const task = await save(page, true)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: title, exact: true }).click()
  execFileSync('docker', ['compose', 'restart', 'app'], { cwd: path.resolve(process.cwd(), '..'), stdio: 'pipe', timeout: 30_000 })
  await expect.poll(async () => {
    try { return (await page.request.get('/actuator/health', { timeout: 1000 })).status() } catch { return 0 }
  }, { timeout: 45_000, intervals: [500, 1000] }).toBe(200)
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  await page.getByLabel('Email', { exact: true }).fill('ana@example.test')
  await page.getByLabel('Palavra-passe').fill(password!)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: title, exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  const stored = await (await page.request.get(`/api/tarefas/${task.id}`)).json()

  // Assert — verificar o resultado.
  expect(stored.estado).toBe('IN_PROGRESS'); expect(stored.prioridade).toBe('HIGH'); expect(stored.prazo).toBe('2026-11-19')
})
