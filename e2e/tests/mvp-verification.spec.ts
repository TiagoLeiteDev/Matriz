import { test, expect } from '@playwright/test'
import type { APIRequestContext } from '@playwright/test'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD do seed.')
const baseURL = process.env.MATRIZ_BASE_URL || 'http://127.0.0.1:8080'
let projects: string[] = [], personal: string[] = []
test.beforeEach(() => { projects = []; personal = [] })
async function csrf(request: APIRequestContext) { return { 'X-CSRF-Token': (await (await request.get('/api/sessao/csrf')).json()).token } }
async function login(request: APIRequestContext, email = 'ana@example.test') {
  const response = await request.post('/api/sessao', { headers: await csrf(request), data: { email, palavraPasse: password } })
  expect(response.status()).toBe(200); return response.json()
}
async function fixture(request: APIRequestContext) {
  await login(request)
  const response = await request.post('/api/projetos', { headers: await csrf(request), data: { nome: `Verificação MVP ${Date.now()}` } })
  expect(response.status()).toBe(201); const project = await response.json(); projects.push(project.id)
  const saved = await request.post('/api/tarefas', { headers: await csrf(request), data: { titulo: 'Tarefa da verificação', projetoId: project.id } })
  expect(saved.status()).toBe(201); return { project, task: await saved.json() }
}
test.afterEach(async ({ request }) => {
  if (!projects.length && !personal.length) return
  await login(request)
  // Own fixtures only. Never remove existing projects/tasks or reset the seed volume.
  for (const id of projects) expect([204, 404]).toContain((await request.delete(`/api/projetos/${id}`, { headers: await csrf(request) })).status())
  for (const id of personal) expect([204, 404]).toContain((await request.delete(`/api/tarefas/${id}`, { headers: await csrf(request) })).status())
})

test('verificação: pedidos inválidos não alteram dados confirmados nem criam dependentes', async ({ request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  const { task } = await fixture(request)
  for (const data of [{ titulo: '' }, { titulo: '\u2003\u00a0' }, { titulo: null }, { titulo: true },
    { estado: 'Em Revisão' }, { prioridade: 'Crítica' }, { prazo: '2026-02-30' }, { projetoId: 'invalid' }]) {
    expect((await request.patch(`/api/tarefas/${task.id}`, { headers: await csrf(request), data })).status()).toBe(422)
    expect(await (await request.get(`/api/tarefas/${task.id}`)).json()).toEqual(task)
  }

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await request.patch(`/api/tarefas/${task.id}`, { headers: await csrf(request), data: { criadorId: task.criadorId } })).status()).toBe(400)

  // Arrange — preparar o próximo cenário/dados.
  for (const conteudo of ['', ' \n\t', 'x'.repeat(2001)])
    expect((await request.post(`/api/tarefas/${task.id}/comentarios`, { headers: await csrf(request), data: { conteudo } })).status()).toBe(422)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await request.post(`/api/tarefas/${task.id}/responsaveis`, { headers: await csrf(request), data: { utilizadorId: 'invalid' } })).status()).toBe(422)
  expect(await (await request.get(`/api/tarefas/${task.id}/comentarios`)).json()).toEqual([])
  expect(await (await request.get(`/api/tarefas/${task.id}/responsaveis`)).json()).toEqual([])
})

test('verificação: IDs alheios e sessão revogada não permitem ler ou escrever recursos', async ({ request, playwright }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  const { project, task } = await fixture(request)

  // Act — executar a operação em teste.
  const saved = await request.post('/api/tarefas', { headers: await csrf(request), data: { titulo: 'Pessoal da verificação' } })

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect(saved.status()).toBe(201); const own = await saved.json(); personal.push(own.id)

  // Arrange — preparar o próximo cenário/dados.
  const other = await playwright.request.newContext({ baseURL })
  try {
    const bruno = await login(other, 'bruno@example.test')
    for (const path of [`/api/projetos/${project.id}`, `/api/projetos/${project.id}/colaboradores`, `/api/tarefas/${task.id}`,
      `/api/tarefas/${task.id}/comentarios`, `/api/tarefas/${task.id}/responsaveis`, `/api/tarefas/${task.id}/utilizadores-elegiveis`, `/api/tarefas/${own.id}`])
      expect((await other.get(path)).status(), path).toBe(403)
    const attempts = [
      { method: 'PATCH', path: `/api/projetos/${project.id}`, data: { nome: 'Intrusão' } },
      { method: 'DELETE', path: `/api/projetos/${project.id}` },
      { method: 'POST', path: '/api/tarefas', data: { titulo: 'Intrusão', projetoId: project.id } },
      { method: 'PATCH', path: `/api/tarefas/${task.id}`, data: { estado: 'DONE' } },
      { method: 'DELETE', path: `/api/tarefas/${task.id}` },
      { method: 'POST', path: `/api/tarefas/${task.id}/comentarios`, data: { conteudo: 'Intrusão' } },
      { method: 'POST', path: `/api/tarefas/${task.id}/responsaveis`, data: { utilizadorId: bruno.id } },
      { method: 'DELETE', path: `/api/tarefas/${task.id}/responsaveis/${bruno.id}` },
      { method: 'POST', path: `/api/projetos/${project.id}/colaboradores`, data: { email: 'bruno@example.test' } },
      { method: 'DELETE', path: `/api/projetos/${project.id}/colaboradores/${bruno.id}` },
    ]
    for (const attempt of attempts) expect((await other.fetch(attempt.path, { method: attempt.method, headers: await csrf(other), ...('data' in attempt ? { data: attempt.data } : {}) })).status(), attempt.path).toBe(403)
    expect(await (await request.get(`/api/tarefas/${task.id}`)).json()).toEqual(task)
    expect((await (await other.get('/api/projetos')).json()).some((p: { id: string }) => p.id === project.id)).toBe(false)
    expect((await (await other.get('/api/tarefas')).json()).some((t: { id: string }) => [task.id, own.id].includes(t.id))).toBe(false)
    expect((await request.post(`/api/projetos/${project.id}/colaboradores`, { headers: await csrf(request), data: { email: bruno.email } })).status()).toBe(201)
    expect((await other.get(`/api/tarefas/${task.id}`)).status()).toBe(200)
    expect((await request.post(`/api/tarefas/${task.id}/responsaveis`, { headers: await csrf(request), data: { utilizadorId: bruno.id } })).status()).toBe(201)
    expect((await other.post(`/api/tarefas/${task.id}/comentarios`, { headers: await csrf(other), data: { conteudo: 'Autoria após revogação' } })).status()).toBe(201)
    expect((await request.delete(`/api/projetos/${project.id}/colaboradores/${bruno.id}`, { headers: await csrf(request) })).status()).toBe(204)
    expect((await other.get(`/api/tarefas/${task.id}`)).status()).toBe(403)
    expect((await other.patch(`/api/tarefas/${task.id}`, { headers: await csrf(other), data: { estado: 'DONE' } })).status()).toBe(403)
    expect(await (await request.get(`/api/tarefas/${task.id}/responsaveis`)).json()).toEqual([])
    expect((await (await request.get(`/api/tarefas/${task.id}/comentarios`)).json())[0].autor.id).toBe(bruno.id)
  } finally { await other.dispose() }
})

test('verificação: criação na interface é consultável com os mesmos dados numa sessão independente', async ({ page, request, playwright }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await page.goto('/')
  await page.getByLabel('Email', { exact: true }).fill('ana@example.test'); await page.getByLabel('Palavra-passe').fill(password!)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await page.getByRole('button', { name: 'Projetos', exact: true }).click()
  await page.getByRole('button', { name: 'Criar projeto', exact: true }).click()

  // Arrange — preparar o próximo cenário/dados.
  let name = `Persistência consolidada ${Date.now()}`
  await page.getByLabel('Nome do projeto').fill(name)
  const created = page.waitForResponse(r => r.request().method() === 'POST' && r.url().endsWith('/api/projetos'))

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Guardar projeto', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  const projectResponse = await created; expect(projectResponse.status()).toBe(201); const project = await projectResponse.json(); projects.push(project.id)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Nova tarefa', exact: true }).click()
  await page.getByLabel('Título da tarefa').fill('Tarefa persistida noutra sessão')
  await page.getByLabel('Descrição da tarefa (opcional)').fill('Dados reais conservados')
  await page.getByLabel('Prioridade da tarefa').selectOption('HIGH')
  await page.getByLabel('Prazo da tarefa (opcional)').fill('2026-11-19')

  // Arrange — preparar o próximo cenário/dados.
  const taskSaved = page.waitForResponse(r => r.request().method() === 'POST' && r.url().endsWith('/api/tarefas'))

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Guardar tarefa', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  const taskResponse = await taskSaved; expect(taskResponse.status()).toBe(201); const task = await taskResponse.json()

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: task.titulo, exact: true }).click()

  // Assert — verificar o resultado.
  expect(new URL(page.url()).hash).toBe(`#projetos/${project.id}/tarefas/${task.id}`)

  // Arrange — preparar o próximo cenário/dados.
  const assignees = page.getByRole('region', { name: 'Responsáveis da tarefa', exact: true })
  await assignees.getByLabel('Utilizador elegível').selectOption({ label: 'Ana Demo (ana@example.test)' })

  // Act — executar a operação em teste.
  await assignees.getByRole('button', { name: 'Associar responsável' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(assignees.getByRole('button', { name: 'Remover responsável Ana Demo', exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  const comments = page.getByRole('region', { name: 'Comentários da tarefa', exact: true })
  await comments.getByLabel('Conteúdo do comentário').fill('Persistência verificada')
  // Assert — o rascunho no textarea ainda não é um comentário guardado.
  await expect(comments.getByRole('listitem').getByText('Persistência verificada', { exact: true })).toHaveCount(0)

  // Arrange — observar a gravação real antes de permitir um reload.
  const commentSaved = page.waitForResponse(response => response.request().method() === 'POST'
    && new URL(response.url()).pathname === `/api/tarefas/${task.id}/comentarios`)

  // Act — executar a operação em teste.
  await comments.getByRole('button', { name: 'Adicionar comentário', exact: true }).click()
  const commentResponse = await commentSaved
  expect(commentResponse.status()).toBe(201)
  const persistedComment = await commentResponse.json()
  expect(persistedComment.conteudo).toBe('Persistência verificada')

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(comments.getByRole('listitem').getByText('Persistência verificada', { exact: true })).toBeVisible()
  // Arrange — observar a consulta real de comentários na nova página.
  const commentsAfterReload = page.waitForResponse(response => response.request().method() === 'GET'
    && new URL(response.url()).pathname === `/api/tarefas/${task.id}/comentarios`)

  // Act — recarregar e aguardar a resposta da API, sem espera temporizada.
  await page.reload()
  const restoredComments = await commentsAfterReload

  // Assert — exigir consulta autorizada, dados persistidos e apresentação na UI.
  expect(restoredComments.status()).toBe(200)
  expect((await restoredComments.json()).some((row: { id: string; conteudo: string }) => row.id === persistedComment.id && row.conteudo === 'Persistência verificada')).toBe(true)
  await expect(comments.getByRole('listitem').getByText('Persistência verificada', { exact: true })).toBeVisible()

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Lista de tarefas', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('button', { name: task.titulo, exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  await page.goBack()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(comments.getByRole('listitem').getByText('Persistência verificada', { exact: true })).toBeVisible()

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Editar projeto', exact: true }).click()
  name += ' revisto'
  await page.getByLabel('Nome do projeto').fill(name)
  await page.getByRole('button', { name: 'Guardar projeto', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name, exact: true })).toBeVisible()

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: task.titulo, exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(comments.getByRole('listitem').getByText('Persistência verificada', { exact: true })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  const fresh = await playwright.request.newContext({ baseURL })
  try {
    const profile = await login(fresh)
    const stored = await (await fresh.get(`/api/tarefas/${task.id}`)).json()
    expect(stored).toEqual(task); expect(stored.prioridade).toBe('HIGH'); expect(stored.projetoId).toBe(project.id)
    expect((await (await fresh.get(`/api/projetos/${project.id}`)).json()).nome).toBe(name)
    const rows = await (await fresh.get(`/api/tarefas/${task.id}/comentarios`)).json()
    expect(rows).toHaveLength(1); expect(rows[0].conteudo).toBe('Persistência verificada'); expect(rows[0].autor.id).toBe(profile.id)
    expect(Number.isNaN(Date.parse(rows[0].criadoEm))).toBe(false)
    const assigned = await (await fresh.get(`/api/tarefas/${task.id}/responsaveis`)).json()
    expect(assigned).toHaveLength(1); expect(assigned[0].utilizador.id).toBe(profile.id)
    await page.getByRole('button', { name: 'Terminar sessão', exact: true }).click()
    expect((await fresh.get(`/api/tarefas/${task.id}`)).status()).toBe(200)
  } finally { await fresh.dispose() }
  await login(request)
})
