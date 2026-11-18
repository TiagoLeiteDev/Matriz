import { useCallback, useEffect, useState } from 'react'
import { api, ApiError, logout } from './api/client'
import type { UserProfile } from './api/client'
import LoginForm from './features/auth/LoginForm'
import ProjectsWorkspace from './features/projects/ProjectsWorkspace'

export default function App() {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState('')
  const sessionExpired = useCallback(() => {
    setUser(null); setMessage('A tua sessão terminou. Inicia sessão novamente.')
  }, [])
  useEffect(() => {
    const controller = new AbortController()
    api<UserProfile>('/sessao', { signal: controller.signal })
      .then(setUser)
      .catch((failure: unknown) => {
        if (controller.signal.aborted) return
        setMessage(failure instanceof ApiError && failure.status === 401
          ? 'Inicia sessão para continuar.' : 'Não foi possível contactar o serviço. Tenta novamente.')
      })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [])
  function failure(error: unknown) {
    if (error instanceof ApiError && error.status === 401) {
      setUser(null); setMessage('A tua sessão terminou. Inicia sessão novamente.')
    } else setMessage(error instanceof Error ? error.message : 'Não foi possível concluir a operação.')
  }
  async function refresh() {
    if (busy) return
    setBusy(true); setMessage('')
    try { setUser(await api<UserProfile>('/sessao')); setMessage('Perfil atualizado.') }
    catch (error: unknown) { failure(error) }
    finally { setBusy(false) }
  }
  async function exit() {
    if (busy) return
    setBusy(true); setMessage('')
    try { await logout(); setUser(null); setMessage('Sessão terminada.') }
    catch (error: unknown) { failure(error) }
    finally { setBusy(false) }
  }
  return <main>
    <h1>Matriz</h1>
    {loading ? <p role="status">A verificar sessão…</p> : user ? <section aria-labelledby="profile-title">
      <h2 id="profile-title">Olá, {user.nome}</h2><p>{user.email}</p>
      <p>A sessão termina após 60 minutos de inatividade.</p>
      {message && <p role="status">{message}</p>}
      <div className="actions"><button onClick={refresh} disabled={busy}>Atualizar perfil</button>
        <button onClick={exit} disabled={busy}>Terminar sessão</button></div>
      <ProjectsWorkspace user={user} onSessionExpired={sessionExpired} />
    </section> : <LoginForm message={message} onLogin={profile => { setUser(profile); setMessage('') }} />}
  </main>
}
