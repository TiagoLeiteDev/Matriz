import { test, expect } from '@playwright/test'

const password = process.env.DEMO_PASSWORD
if (!password) throw new Error('Configura DEMO_PASSWORD com a palavra-passe fictícia do seed.')

for (const viewport of [{ width: 1365, height: 900 }, { width: 360, height: 800 }]) {
  test(`estrutura SPA: navegação, histórico e layout a ${viewport.width}px`, async ({ page }) => {
    // Arrange — sessão real e dimensão de ecrã; sem criar ou alterar dados de negócio.
    await page.setViewportSize(viewport)
    await page.goto('/')
    await page.getByLabel('Email', { exact: true }).fill('ana@example.test')
    await page.getByLabel('Palavra-passe').fill(password!)
    await page.getByRole('button', { name: 'Entrar', exact: true }).click()
    const navigation = page.getByRole('navigation', { name: 'Navegação principal' })
    await expect(page.getByRole('region', { name: 'Painel inicial' })).toBeVisible()
    await page.evaluate(() => {
      (window as Window & { __matrizNavigationProbe?: string }).__matrizNavigationProbe = 'same-document'
    })

    // Act — navegar entre áreas e voltar usando o histórico do browser.
    await navigation.getByRole('button', { name: 'Projetos', exact: true }).click()
    await expect(page.getByRole('region', { name: 'Gestão de projetos' })).toBeVisible()
    await expect(navigation.getByRole('button', { name: 'Projetos', exact: true })).toHaveAttribute('aria-current', 'page')
    await navigation.getByRole('button', { name: 'Tarefas', exact: true }).click()
    await expect(page.getByRole('region', { name: 'Gestão de tarefas' })).toBeVisible()
    await page.goBack()

    // Assert — seleção, histórico, sessão e documento preservados; sem overflow horizontal.
    await expect(page).toHaveURL(/#projetos$/)
    await expect(navigation.getByRole('button', { name: 'Projetos', exact: true })).toHaveAttribute('aria-current', 'page')
    await expect(page.getByRole('heading', { name: 'Olá, Ana Demo' })).toBeVisible()
    expect(await page.evaluate(() => (window as Window & { __matrizNavigationProbe?: string }).__matrizNavigationProbe)).toBe('same-document')
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true)

    // Act — reabrir a rota atual e terminar a sessão explicitamente.
    await page.reload()
    await expect(page.getByRole('region', { name: 'Gestão de projetos' })).toBeVisible()
    await page.getByRole('button', { name: 'Terminar sessão' }).click()

    // Assert — logout retira a navegação autenticada e mostra o login.
    await expect(page.getByRole('heading', { name: 'Iniciar sessão' })).toBeVisible()
    await expect(navigation).toHaveCount(0)
  })
}
