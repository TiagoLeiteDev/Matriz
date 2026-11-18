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
  await page.request.get('/api/sessao/csrf')
  const old = (await page.context().cookies()).find(cookie => cookie.name === 'matriz_session')?.value
  await enter(page)
  await expect(page.getByRole('heading', { name: 'Olá, Ana Demo' })).toBeVisible()
  const cookie = (await page.context().cookies()).find(cookie => cookie.name === 'matriz_session')!
  expect(cookie.httpOnly).toBe(true); expect(cookie.sameSite).toBe('Lax'); expect(cookie.expires).toBe(-1)
  expect(cookie.value === old).toBe(false)
  await page.reload()
  await expect(page.getByRole('heading', { name: 'Olá, Ana Demo' })).toBeVisible()
  expect(await page.evaluate(() => localStorage.length + sessionStorage.length)).toBe(0)
  await page.getByRole('button', { name: 'Terminar sessão' }).click()
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  expect((await page.request.get('/api/sessao')).status()).toBe(401)
})

test('credenciais inválidas apresentam erro sem autenticar', async ({ page }) => {
  await page.goto('/')
  await page.getByLabel('Email', { exact: true }).fill('ana@example.test')
  await page.getByLabel('Palavra-passe').fill('incorrect-password')
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page.getByRole('alert')).toContainText('Credenciais inválidas')
  await expect(page.getByLabel('Email', { exact: true })).toHaveValue('ana@example.test')
  expect((await page.request.get('/api/sessao')).status()).toBe(401)
})

test('dois utilizadores respeitam acessos distintos e privacidade pessoal', async ({ page, browser, baseURL }) => {
  await enter(page)
  expect((await page.request.get(`/api/projetos/${shared}`)).status()).toBe(200)
  expect((await page.request.get(`/api/projetos/${privateProject}`)).status()).toBe(403)
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
  await enter(page)
  const response = await page.request.get('/api/sessao/csrf')
  const { token } = await response.json()
  expect((await page.request.delete('/api/sessao', { headers: { 'X-CSRF-Token': token } })).status()).toBe(204)
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  await expect(page.getByRole('status')).toContainText('A tua sessão terminou')
})

test('login sem CSRF ou com origem hostil é recusado', async ({ request }) => {
  const data = { email: 'ana@example.test', palavraPasse: password }
  expect((await request.post('/api/sessao', { data })).status()).toBe(403)
  const { token } = await (await request.get('/api/sessao/csrf')).json()
  expect((await request.post('/api/sessao', { data, headers: {
    'X-CSRF-Token': token, Origin: 'https://attacker.example',
  } })).status()).toBe(403)
})

test('expiração por inatividade real regressa ao login @expiry', async ({ page }) => {
  test.skip(process.env.MATRIZ_SHORT_SESSION_TEST !== '1', 'Executar apenas contra instância isolada com timeout de 1 minuto.')
  test.setTimeout(100_000)
  await enter(page)
  // Intentional idle interval: polling would refresh the session and invalidate the test.
  await new Promise(resolve => setTimeout(resolve, 65_000))
  await page.getByRole('button', { name: 'Atualizar perfil' }).click()
  await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
  await expect(page.getByRole('status')).toContainText('A tua sessão terminou')
})
