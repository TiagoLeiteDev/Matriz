const { request } = require('@playwright/test')
const { execFileSync } = require('node:child_process')
const path = require('node:path')

async function main() {
  if (!process.env.DEMO_PASSWORD) throw new Error('Configura DEMO_PASSWORD com a palavra-passe fictícia do seed.')
  const api = await request.newContext({ baseURL: process.env.MATRIZ_BASE_URL || 'http://127.0.0.1:8080' })
  try {
    const { token } = await (await api.get('/api/sessao/csrf')).json()
    const login = await api.post('/api/sessao', {
      headers: { 'X-CSRF-Token': token },
      data: { email: 'ana@example.test', palavraPasse: process.env.DEMO_PASSWORD },
    })
    if (login.status() !== 200) throw new Error('Login prévio ao reinício falhou.')
    execFileSync('docker', ['compose', 'restart', 'app'], {
      cwd: path.resolve(__dirname, '../..'), stdio: 'pipe', timeout: 30_000,
    })
    let available = false
    for (let attempt = 0; attempt < 45; attempt++) {
      try {
        if ((await api.get('/actuator/health', { timeout: 1000 })).status() === 200) { available = true; break }
      } catch { /* Connection may be unavailable while the process restarts. */ }
      await new Promise(resolve => setTimeout(resolve, 1000))
    }
    if (!available) throw new Error('Backend indisponível após reinício.')
    const status = (await api.get('/api/sessao')).status()
    if (status !== 401) throw new Error('A sessão anterior continuou válida após reinício.')
    console.log(JSON.stringify({ loginBeforeRestart: 200, profileAfterRestart: status }))
  } finally { await api.dispose() }
}
main().catch(error => { console.error(error.message); process.exitCode = 1 })
