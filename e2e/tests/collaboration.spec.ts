import { test, expect } from '@playwright/test'
import type { APIRequestContext, Page } from '@playwright/test'
import { execFileSync } from 'node:child_process'
import path from 'node:path'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD com a palavra-passe fictícia do seed.')
let ids: string[] = []
test.beforeEach(() => { ids = [] })
async function csrf(request: APIRequestContext) { return { 'X-CSRF-Token': (await (await request.get('/api/sessao/csrf')).json()).token } }
async function login(request: APIRequestContext) {
  expect((await request.post('/api/sessao', { headers: await csrf(request), data: { email: 'ana@example.test', palavraPasse: password } })).status()).toBe(200)
}
async function fixture(request: APIRequestContext, personal = false) {
  await login(request)
  const projects = await (await request.get('/api/projetos')).json()
  const project = projects.find((p: { nome: string }) => p.nome === 'Projeto partilhado da Ana')
  const response = await request.post('/api/tarefas', { headers: await csrf(request), data: {
    titulo: `Colaboração ${Date.now()}`, projetoId: personal ? null : project.id,
  } })
  expect(response.status()).toBe(201)
  const task = await response.json(); ids.push(task.id); return task
}
async function enter(page: Page, task: string, email = 'bruno@example.test') {
  await page.goto(`/#tarefas/${task}`)
  await page.getByLabel('Email', { exact: true }).fill(email)
  await page.getByLabel('Palavra-passe').fill(password!)
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page.getByRole('heading', { name: 'Responsáveis', exact: true })).toBeVisible()
}
test.afterEach(async ({ request }) => {
  if (!ids.length) return
  await login(request)
  for (const id of ids) expect([204, 404]).toContain((await request.delete(`/api/tarefas/${id}`, { headers: await csrf(request) })).status())
})
test('responsáveis: colaborador associa dois, remove um e conserva o outro após reload', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  const task = await fixture(request)
  await enter(page, task.id)
  const section = page.getByRole('region', { name: 'Responsáveis da tarefa', exact: true })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByText('Sem responsáveis.', { exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  await section.getByLabel('Utilizador elegível').selectOption({ label: 'Ana Demo (ana@example.test)' })

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Associar responsável' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('button', { name: 'Remover responsável Ana Demo', exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  await section.getByLabel('Utilizador elegível').selectOption({ label: 'Bruno Demo (bruno@example.test)' })

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Associar responsável' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByText('Todos os utilizadores elegíveis já estão associados.')).toBeVisible()

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Remover responsável Ana Demo', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('button', { name: 'Remover responsável Ana Demo', exact: true })).toHaveCount(0)

  // Act — executar a operação em teste.
  await page.reload()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('button', { name: 'Remover responsável Bruno Demo', exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  const actual = await (await request.get(`/api/tarefas/${task.id}/responsaveis`)).json()

  // Assert — verificar o resultado.
  expect(actual).toHaveLength(1); expect(actual[0].utilizador.email).toBe('bruno@example.test')
})
test('responsável pessoal: só criador elegível e falha conserva seleção sem falso sucesso', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  const task = await fixture(request, true)
  await enter(page, task.id, 'ana@example.test')
  const section = page.getByRole('region', { name: 'Responsáveis da tarefa', exact: true })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByLabel('Utilizador elegível').locator('option')).toHaveCount(2)

  // Arrange — preparar o próximo cenário/dados.
  await section.getByLabel('Utilizador elegível').selectOption({ label: 'Ana Demo (ana@example.test)' })
  let writes = 0
  await page.route(`**/api/tarefas/${task.id}/responsaveis`, async route => {
    if (route.request().method() === 'POST') { writes++; await route.abort() } else await route.continue()
  })

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Associar responsável' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('alert')).toContainText('Não foi possível contactar o serviço.')
  await expect(section.getByLabel('Utilizador elegível')).toHaveValue(task.criadorId)
  await expect(section.getByText('Responsável associado.', { exact: true })).toHaveCount(0)

  // Assert — verificar o resultado.
  expect(writes).toBe(1)

  // Arrange — preparar o próximo cenário/dados.
  await page.unroute(`**/api/tarefas/${task.id}/responsaveis`)

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Associar responsável' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('button', { name: 'Remover responsável Ana Demo', exact: true })).toBeVisible()
})

test('comentário: Unicode, validação, erro conserva rascunho, autoria/data e conteúdo seguro após reload', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  const task = await fixture(request)
  await enter(page, task.id)
  const section = page.getByRole('region', { name: 'Comentários da tarefa', exact: true })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByText('Sem comentários.', { exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  let writes = 0
  page.on('request', r => { if (r.method() === 'POST' && r.url().endsWith(`/api/tarefas/${task.id}/comentarios`)) writes++ })
  await section.getByLabel('Conteúdo do comentário').fill('   ')

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Adicionar comentário' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('alert')).toContainText('1 e 2000')

  // Arrange — preparar o próximo cenário/dados.
  await section.getByLabel('Conteúdo do comentário').fill('😀'.repeat(2001))

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Adicionar comentário' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByText('2001/2000 caracteres', { exact: true })).toBeVisible()

  // Assert — verificar o resultado.
  expect(writes).toBe(0)

  // Arrange — preparar o próximo cenário/dados.
  const draft = '<img src=x onerror=alert(1)> Informação do Bruno'
  await section.getByLabel('Conteúdo do comentário').fill(draft)
  await page.route(`**/api/tarefas/${task.id}/comentarios`, async route => {
    if (route.request().method() === 'POST') await route.abort(); else await route.continue()
  })

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Adicionar comentário' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByRole('alert')).toContainText('Não foi possível contactar o serviço.')
  await expect(section.getByLabel('Conteúdo do comentário')).toHaveValue(draft)
  await expect(section.getByText('Comentário adicionado.', { exact: true })).toHaveCount(0)

  // Assert — verificar o resultado.
  expect(writes).toBe(1)

  // Arrange — preparar o próximo cenário/dados.
  await page.unroute(`**/api/tarefas/${task.id}/comentarios`)
  const result = page.waitForResponse(r => r.request().method() === 'POST' && r.url().endsWith(`/api/tarefas/${task.id}/comentarios`))

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Adicionar comentário' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  const response = await result; expect(response.status()).toBe(201); const comment = await response.json()

  // Assert — verificar o resultado.
  expect(comment.autor.email).toBe('bruno@example.test')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByLabel('Conteúdo do comentário')).toHaveValue('')
  await expect(section.getByText(draft, { exact: true })).toBeVisible()
  await expect(section.locator('time')).toHaveAttribute('datetime', comment.criadoEm)
  await expect(section.locator('img')).toHaveCount(0)

  // Arrange — preparar o próximo cenário/dados.
  await section.getByLabel('Conteúdo do comentário').fill('😀'.repeat(2000))

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByText('2000/2000 caracteres', { exact: true })).toBeVisible()

  // Act — executar a operação em teste.
  await section.getByRole('button', { name: 'Adicionar comentário' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByLabel('Conteúdo do comentário')).toHaveValue('')

  // Act — executar a operação em teste.
  await page.reload()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(section.getByText(draft, { exact: true })).toBeVisible()
  await expect(section.locator('ol > li')).toHaveCount(2)

  // Arrange — preparar o próximo cenário/dados.
  const stored = await (await request.get(`/api/tarefas/${task.id}/comentarios`)).json()

  // Assert — verificar o resultado.
  expect(stored[0].conteudo).toBe(draft); expect(stored[1].conteudo).toBe('😀'.repeat(2000))
})

test('tarefa arquivada permite ler responsáveis/comentários mas não mostra ações de escrita', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await login(request)

  // Act — executar a operação em teste.
  const projectResponse = await request.post('/api/projetos', { headers: await csrf(request), data: { nome: `Arquivo colaboração ${Date.now()}` } })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect(projectResponse.status()).toBe(201); const project = await projectResponse.json()

  // Arrange — preparar o próximo cenário/dados.
  try {
    const taskResponse = await request.post('/api/tarefas', { headers: await csrf(request), data: { titulo: 'Consultar arquivo', projetoId: project.id } })
    expect(taskResponse.status()).toBe(201); const task = await taskResponse.json()
    const profile = await (await request.get('/api/sessao')).json()
    expect((await request.post(`/api/tarefas/${task.id}/responsaveis`, { headers: await csrf(request), data: { utilizadorId: profile.id } })).status()).toBe(201)
    expect((await request.post(`/api/tarefas/${task.id}/comentarios`, { headers: await csrf(request), data: { conteudo: 'Preservado no arquivo' } })).status()).toBe(201)
    expect((await request.patch(`/api/projetos/${project.id}`, { headers: await csrf(request), data: { arquivado: true } })).status()).toBe(200)
    await enter(page, task.id, 'ana@example.test')
    const comments = page.getByRole('region', { name: 'Comentários da tarefa', exact: true })
    const assignees = page.getByRole('region', { name: 'Responsáveis da tarefa', exact: true })
    await expect(comments.getByText('Preservado no arquivo', { exact: true })).toBeVisible()
    await expect(assignees.getByText('Ana Demo (ana@example.test)', { exact: true })).toBeVisible()
    await expect(comments.getByRole('button', { name: 'Adicionar comentário' })).toHaveCount(0)
    await expect(assignees.getByLabel('Utilizador elegível')).toHaveCount(0)
    await expect(assignees.getByRole('button', { name: /Remover responsável/ })).toHaveCount(0)
  } finally {
    // Only this newly created project and its own dependents; never touch the shared seed project.
    expect((await request.delete(`/api/projetos/${project.id}`, { headers: await csrf(request) })).status()).toBe(204)
  }
})

test('responsáveis e comentários persistem após reinício e novo login @collaboration-restart', async ({ page, request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  test.skip(process.env.MATRIZ_COLLAB_RESTART_TEST !== '1', 'Reinício explícito, execução separada.')
  test.setTimeout(100_000)
  const task = await fixture(request)
  await enter(page, task.id)
  const assignees = page.getByRole('region', { name: 'Responsáveis da tarefa', exact: true })
  const comments = page.getByRole('region', { name: 'Comentários da tarefa', exact: true })
  for (const name of ['Ana Demo (ana@example.test)', 'Bruno Demo (bruno@example.test)']) {
    await assignees.getByLabel('Utilizador elegível').selectOption({ label: name })
    await assignees.getByRole('button', { name: 'Associar responsável' }).click()
    await expect(assignees.getByLabel('Utilizador elegível')).toHaveValue('')
  }
  await comments.getByLabel('Conteúdo do comentário').fill('Dados conservados após reinício')

  // Act — executar a operação em teste.
  await comments.getByRole('button', { name: 'Adicionar comentário' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(comments.getByText('Dados conservados após reinício', { exact: true })).toBeVisible()

  // Act — executar a operação em teste.
  execFileSync('docker', ['compose', 'restart', 'app'], { cwd: path.resolve(process.cwd(), '..'), stdio: 'pipe', timeout: 30_000 })
  await expect.poll(async () => {
    try { return (await page.request.get('/actuator/health', { timeout: 1000 })).status() } catch { return 0 }
  }, { timeout: 45_000, intervals: [500, 1000] }).toBe(200)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await page.request.get('/api/sessao')).status()).toBe(401)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Iniciar sessão', exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  await enter(page, task.id)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(assignees.getByRole('button', { name: /Remover responsável/ })).toHaveCount(2)
  await expect(comments.getByText('Dados conservados após reinício', { exact: true })).toBeVisible()
  await expect(comments.locator('time')).toHaveCount(1)

  // Arrange — preparar o próximo cenário/dados.
  await login(request)
  const stored = await (await request.get(`/api/tarefas/${task.id}/comentarios`)).json()

  // Assert — verificar o resultado.
  expect(stored[0].autor.email).toBe('bruno@example.test')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await (await request.get(`/api/tarefas/${task.id}/responsaveis`)).json())).toHaveLength(2)
})
