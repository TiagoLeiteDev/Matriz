import { useCallback, useEffect, useState } from 'react'
import { api, ApiError, logout } from './api/client'
import type { UserProfile } from './api/client'
import LoginForm from './features/auth/LoginForm'
import ProjectsWorkspace from './features/projects/ProjectsWorkspace'
import TasksWorkspace from './features/tasks/TasksWorkspace'
import Dashboard from './features/projects/Dashboard'
import AppShell from './components/AppShell'

export default function App() {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState('')
  const [route, setRoute] = useState(() => window.location.hash)
  useEffect(() => {
    const change = () => setRoute(window.location.hash)
    window.addEventListener('hashchange', change)
    return () => window.removeEventListener('hashchange', change)
  }, [])
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
  if (loading) return <main className="session-loading"><span className="brand-mark" aria-hidden="true">m</span><h1>Matriz</h1><p role="status">A verificar sessão…</p></main>
  if (user) return <AppShell user={user} route={route} busy={busy} message={message} onRefresh={refresh} onLogout={exit}>
    {route.startsWith('#tarefas') ? <TasksWorkspace userId={user.id} onSessionExpired={sessionExpired} /> : route.startsWith('#projetos') ? <ProjectsWorkspace user={user} onSessionExpired={sessionExpired} /> : <Dashboard onSessionExpired={sessionExpired} />}
  </AppShell>
  return <main className="login-layout">
    <div className="login-story"><div className="brand"><span className="brand-mark" aria-hidden="true">m</span><h1>Matriz</h1></div>
      <div><p className="eyebrow">MENOS DISPERSÃO. MAIS DIREÇÃO.</p><h2>Um lugar para<br />dar forma<br />às tuas ideias<span>.</span></h2><p>Organiza projetos, acompanha tarefas e constrói o próximo passo com a tua equipa.</p></div>
      <p className="login-signature">Organiza. Prioriza. Concretiza.</p>
    </div>
    <div className="login-panel"><p className="eyebrow">O TEU ESPAÇO DE TRABALHO</p><LoginForm message={message} onLogin={profile => { setUser(profile); setMessage('') }} /></div>
  </main>
}
