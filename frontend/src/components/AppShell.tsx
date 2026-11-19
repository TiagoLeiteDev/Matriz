import type { ReactNode } from 'react'
import type { UserProfile } from '../api/client'

const sections = [
  { id: 'painel', label: 'Painel', description: 'O teu trabalho, numa só vista', icon: '▦' },
  { id: 'projetos', label: 'Projetos', description: 'Organiza o trabalho em equipa', icon: '▤' },
  { id: 'tarefas', label: 'Tarefas', description: 'Um passo de cada vez', icon: '✓' },
] as const

export default function AppShell({ user, route, busy, message, onRefresh, onLogout, children }: {
  user: UserProfile; route: string; busy: boolean; message: string;
  onRefresh: () => void; onLogout: () => void; children: ReactNode
}) {
  const active = sections.find(section => route.startsWith(`#${section.id}`)) ?? sections[0]
  const initials = user.nome.trim().split(/\s+/).slice(0, 2).map(part => Array.from(part)[0]).join('')
  return <div className="app-shell">
    <a className="skip-link" href="#main-content" onClick={event => {
      event.preventDefault(); document.getElementById('main-content')?.focus()
    }}>Saltar para o conteúdo</a>
    <aside className="sidebar">
      <a className="brand" href="#painel" aria-label="Matriz — painel"><span className="brand-mark" aria-hidden="true">m</span><span>Matriz<span className="brand-caption">O teu espaço de trabalho</span></span></a>
      <p className="nav-label">ESPAÇO DE TRABALHO</p>
      <nav aria-label="Navegação principal">{sections.map(section => <button key={section.id}
        aria-current={active.id === section.id ? 'page' : undefined}
        onClick={() => { window.location.hash = section.id }}>
        <span className="nav-icon" aria-hidden="true">{section.icon}</span>{section.label}
        <span className="nav-arrow" aria-hidden="true">↗</span>
      </button>)}</nav>
      <div className="sidebar-note"><span className="availability-dot" aria-hidden="true" />Organiza. Prioriza. Concretiza.<p>Mais clareza para o que vem a seguir.</p></div>
    </aside>
    <div className="app-body">
      <header className="topbar">
        <div className="page-context"><h1>{active.label}</h1><p>{active.description}</p></div>
        <section className="account" aria-label="Conta atual">
          <span className="avatar" aria-hidden="true">{initials}</span>
          <div><h2 id="profile-title">Olá, {user.nome}</h2><p>{user.email}</p></div>
          <div className="account-actions"><button onClick={onRefresh} disabled={busy}>Atualizar perfil</button><button onClick={onLogout} disabled={busy}>Terminar sessão</button></div>
        </section>
      </header>
      <main id="main-content" className="workspace" tabIndex={-1}>
        {message && <p role="status">{message}</p>}
        {children}
      </main>
      <footer className="app-footer"><span>Matriz · Gestão de projetos e tarefas</span><span>A sessão termina após 60 minutos de inatividade.</span></footer>
    </div>
  </div>
}
