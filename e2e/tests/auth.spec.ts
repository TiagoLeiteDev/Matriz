import { test, expect } from '@playwright/test'
import type { Page } from '@playwright/test'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD com a palavra-passe fictícia do seed.')
const shared = '00000000-0000-4000-8000-000000000011'
const privateProject = '00000000-0000-4000-8000-000000000012'
const personalTask = '00000000-0000-4000-8000-000000000023'

async function enter(page: Page, email = 'ana@example.test') {
  await page.goto('/')
  await page.getByLabel('Email', { exact: true }).fill(email)
  await page.getByLabel('Palavra-passe').fill(password!)
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Terminar sessão' })).toBeVisible()
}

test('login, perfil persistente no reload, cookie protegido e logout', async ({ page }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await page.request.get('/api/sessao/csrf')
  const old = (await page.context().cookies()).find(cookie => cookie.name === 'matriz_session')?.value
  await enter(page)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Olá, Ana Demo' })).toBeVisible()

  // Arrange — preparar o próximo cenário/dados.
  const cookie = (await page.context().cookies()).find(cookie => cookie.name === 'matriz_session')!

  // Assert — verificar o resultado.
  expect(cookie.httpOnly).toBe(true); expect(cookie.sameSite).toBe('Lax'); expect(cookie.expires).toBe(-1)
  expect(cookie.value === old).toBe(false)

  // Act — executar a operação em teste.
  await page.reload()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Olá, Ana Demo' })).toBeVisible()
  expect(await page.evaluate(() => localStorage.length + sessionStorage.length)).toBe(0)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Terminar sessão' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  expect((await page.request.get('/api/sessao')).status()).toBe(401)
})

test('credenciais inválidas apresentam erro sem autenticar', async ({ page }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  await page.goto('/')
  await page.getByLabel('Email', { exact: true }).fill('ana@example.test')
  await page.getByLabel('Palavra-passe').fill('incorrect-password')

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('alert')).toContainText('Credenciais inválidas')
  await expect(page.getByLabel('Email', { exact: true })).toHaveValue('ana@example.test')
  expect((await page.request.get('/api/sessao')).status()).toBe(401)
})

test('dois utilizadores respeitam acessos distintos e privacidade pessoal', async ({ page, browser, baseURL }) => {
  // Act & Assert — executar o percurso/pedido e verificar o resultado.
  await enter(page)

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await page.request.get(`/api/projetos/${shared}`)).status()).toBe(200)
  expect((await page.request.get(`/api/projetos/${privateProject}`)).status()).toBe(403)

  // Arrange — preparar o próximo cenário/dados.
  const other = await browser.newContext({ baseURL })
  try {
    const second = await other.newPage()
    await enter(second, 'bruno@example.test')
    expect((await second.request.get(`/api/projetos/${shared}`)).status()).toBe(200)
    expect((await second.request.get(`/api/projetos/${privateProject}`)).status()).toBe(200)
    for (const suffix of ['', '/comentarios', '/responsaveis']) {
      expect((await second.request.get(`/api/tarefas/${personalTask}${suffix}`)).status()).toBe(403)
    }
    await second.getByRole('button', { name: 'Terminar sessão' }).click()
  } finally { await other.close() }
})

test('sessão revogada encaminha para login com mensagem compreensível', async ({ page }) => {
  // Act & Assert — executar o percurso/pedido e verificar o resultado.
  await enter(page)
  const response = await page.request.get('/api/sessao/csrf')
  const { token } = await response.json()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await page.request.delete('/api/sessao', { headers: { 'X-CSRF-Token': token } })).status()).toBe(204)

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  await expect(page.getByRole('status')).toContainText('A tua sessão terminou')
})

test('login sem CSRF ou com origem hostil é recusado', async ({ request }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  const data = { email: 'ana@example.test', palavraPasse: password }

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await request.post('/api/sessao', { data })).status()).toBe(403)

  // Arrange — preparar o próximo cenário/dados.
  const { token } = await (await request.get('/api/sessao/csrf')).json()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  expect((await request.post('/api/sessao', { data, headers: {
    'X-CSRF-Token': token, Origin: 'https://attacker.example',
  } })).status()).toBe(403)
})

test('expiração por inatividade real regressa ao login @expiry', async ({ page }) => {
  // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
  test.skip(process.env.MATRIZ_SHORT_SESSION_TEST !== '1', 'Executar apenas contra instância isolada com timeout de 1 minuto.')
  test.setTimeout(100_000)
  await enter(page)
  // Intentional idle interval: polling would refresh the session and invalidate the test.
  await new Promise(resolve => setTimeout(resolve, 65_000))

  // Act — executar a operação em teste.
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()

  // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  await expect(page.getByRole('status')).toContainText('A tua sessão terminou')
})
