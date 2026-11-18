import { useState } from 'react'
import type { SubmitEvent } from 'react'
import { login } from '../../api/client'
import type { UserProfile } from '../../api/client'

export default function LoginForm({ onLogin, message }: { onLogin: (user: UserProfile) => void; message: string }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  async function submit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (busy) return
    setBusy(true); setError('')
    try {
      const user = await login(email, password)
      setPassword(''); onLogin(user)
    } catch (failure: unknown) {
      setError(failure instanceof Error ? failure.message : 'Não foi possível iniciar sessão.')
    } finally { setBusy(false) }
  }
  return <section aria-labelledby="login-title">
    <h2 id="login-title">Iniciar sessão</h2>
    {message && <p role="status">{message}</p>}
    <form onSubmit={submit}>
      <label htmlFor="email">Email</label>
      <input id="email" name="email" type="email" autoComplete="username" required value={email}
        onChange={event => setEmail(event.target.value)} disabled={busy} />
      <label htmlFor="password">Palavra-passe</label>
      <input id="password" name="password" type="password" autoComplete="current-password" required value={password}
        onChange={event => setPassword(event.target.value)} disabled={busy} />
      {error && <p role="alert">{error}</p>}
      <button type="submit" disabled={busy}>{busy ? 'A entrar…' : 'Entrar'}</button>
    </form>
  </section>
}
