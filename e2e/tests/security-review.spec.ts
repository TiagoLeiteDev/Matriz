import { test, expect } from '@playwright/test'
import type { APIRequestContext } from '@playwright/test'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD fictícia do seed.')

async function csrf(client: APIRequestContext) {
  const response = await client.get('/api/sessao/csrf')
  expect(response.status()).toBe(200)
  return { 'X-CSRF-Token': (await response.json()).token }
}
async function login(client: APIRequestContext, email: string) {
  const response = await client.post('/api/sessao', {
    headers: await csrf(client), data: { email, palavraPasse: password },
  })
  expect(response.status()).toBe(200)
}
async function json(client: APIRequestContext, path: string) {
  const response = await client.get(path)
  expect(response.status()).toBe(200)
  return response.json()
}

test('security review: duas identidades recusam IDs alheios sem alterar recursos', async ({ playwright, baseURL }) => {
  // Arrange — sessões independentes e fixtures privadas novas para ambas as contas.
  const clients = await Promise.all([playwright.request.newContext({ baseURL }), playwright.request.newContext({ baseURL })])
  const projects: { client: APIRequestContext; id: string }[] = []
  try {
    const fixtures = []
    for (const [index, email] of ['ana@example.test', 'bruno@example.test'].entries()) {
      const client = clients[index]
      await login(client, email)
      const projectResponse = await client.post('/api/projetos', {
        headers: await csrf(client), data: { nome: `Revisão acesso ${index} ${Date.now()}` },
      })
      expect(projectResponse.status()).toBe(201)
      const project = await projectResponse.json()
      projects.push({ client, id: project.id })
      const taskResponse = await client.post('/api/tarefas', {
        headers: await csrf(client), data: { titulo: 'Recurso protegido', projetoId: project.id },
      })
      expect(taskResponse.status()).toBe(201)
      fixtures.push({ project, task: await taskResponse.json() })
    }
    for (const ownerIndex of [0, 1]) {
      // Arrange — snapshot autorizado antes de manipular identificadores com a outra conta.
      const owner = clients[ownerIndex], attacker = clients[1 - ownerIndex]
      const { project, task } = fixtures[ownerIndex]
      const projectPath = `/api/projetos/${project.id}`, taskPath = `/api/tarefas/${task.id}`
      const paths = [projectPath, `${projectPath}/colaboradores`, taskPath,
        `${taskPath}/comentarios`, `${taskPath}/responsaveis`, `${taskPath}/utilizadores-elegiveis`]
      const before = await Promise.all(paths.map(path => json(owner, path)))
      const attempts = [
        { method: 'GET', path: projectPath }, { method: 'GET', path: taskPath },
        { method: 'PATCH', path: projectPath, data: { nome: 'Intrusão' } },
        { method: 'DELETE', path: projectPath },
        { method: 'PATCH', path: taskPath, data: { estado: 'DONE' } },
        { method: 'DELETE', path: taskPath },
      ]
      for (const attempt of attempts) {
        // Act — sessão válida e CSRF dessa sessão para atingir a autorização do recurso.
        const response = await attacker.fetch(attempt.path, {
          method: attempt.method, headers: await csrf(attacker),
          ...('data' in attempt ? { data: attempt.data } : {}),
        })

        // Assert — problema 403, sem DTO privado nem sucesso de uma escrita.
        expect(response.status()).toBe(403)
        expect(response.headers()['content-type']).toContain('application/problem+json')
        const problem = await response.json()
        expect(problem).toMatchObject({ type: 'about:blank', status: 403, codigo: 'ACESSO_NEGADO' })
        expect(typeof problem.title).toBe('string'); expect(problem.title.length).toBeGreaterThan(0)
        expect(typeof problem.detail).toBe('string'); expect(problem.detail.length).toBeGreaterThan(0)
        expect(Object.keys(problem).sort()).toEqual(['codigo', 'detail', 'status', 'title', 'type'])
        expect(await Promise.all(paths.map(path => json(owner, path)))).toEqual(before)
      }
      // Act — pesquisar diretamente pelas listagens com a identidade sem acesso.
      const projectList = await json(attacker, '/api/projetos')
      const taskList = await json(attacker, '/api/tarefas')

      // Assert — as listagens também excluem os identificadores protegidos.
      expect(projectList.some((row: { id: string }) => row.id === project.id)).toBe(false)
      expect(taskList.some((row: { id: string }) => row.id === task.id)).toBe(false)
    }
  } finally {
    // Cleanup — tentar limpar todas as fixtures próprias, mesmo se uma limpeza falhar.
    const results = await Promise.allSettled(projects.map(async ({ client, id }) => {
      expect((await client.delete(`/api/projetos/${id}`, { headers: await csrf(client) })).status()).toBe(204)
      expect((await client.get(`/api/projetos/${id}`)).status()).toBe(404)
    }))
    await Promise.all(clients.map(client => client.dispose()))
    for (const result of results) if (result.status === 'rejected') throw result.reason
  }
})
