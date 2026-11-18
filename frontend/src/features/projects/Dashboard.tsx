import { useEffect, useState } from 'react'
import { api, ApiError } from '../../api/client'
import type { UserProfile } from '../../api/client'
import type { Project } from './types'
import { states } from '../tasks/types'
type Summary = { utilizador: UserProfile; projetos: Project[]; contagens: Record<keyof typeof states, number>; incluiIndependentes: boolean }
export default function Dashboard({ onSessionExpired }: { onSessionExpired: () => void }) {
  const [data, setData] = useState<Summary | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [revision, setRevision] = useState(0)
  useEffect(() => {
    const controller = new AbortController()
    api<Summary>('/painel?incluirIndependentes=true', { signal: controller.signal }).then(setData).catch((failure: unknown) => {
      if (controller.signal.aborted) return
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível consultar o painel.')
    }).finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [revision, onSessionExpired])
  return <section aria-label="Painel inicial"><h2>Painel inicial</h2>
    <button onClick={() => { setLoading(true); setError(''); setRevision(n => n + 1) }}>Atualizar painel</button>
    {loading ? <p role="status">A carregar painel…</p> : error ? <p role="alert">{error}</p> : data && <>
      <p>Tarefas pessoais e de projetos ativos a que tens acesso. Exclui projetos arquivados; não depende dos filtros da lista.</p>
      <dl className="summary">{Object.entries(states).map(([state, label]) => <div key={state}><dt>{label}</dt><dd data-testid={`count-${state}`}>{data.contagens[state as keyof typeof states]}</dd></div>)}</dl>
      <p data-testid="dashboard-total">Total: {Object.values(data.contagens).reduce((a, b) => a + b, 0)} tarefas.</p>
      <a href="#tarefas">Consultar tarefas</a><h3>Projetos ativos acessíveis</h3>
      {data.projetos.length === 0 ? <p>Sem projetos ativos.</p> : <ul className="project-list">{data.projetos.map(project => <li key={project.id}>
        <a href={`#projetos/${project.id}`}>{project.nome}</a><span>{project.progresso.percentagem}%</span>
      </li>)}</ul>}
    </>}
  </section>
}
