import { useEffect, useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { api, ApiError, mutate } from '../../api/client'
import type { UserProfile } from '../../api/client'
import type { Project } from './types'
import ProjectForm from './ProjectForm'
import DeleteProjectDialog from './DeleteProjectDialog'

function selectedId() { return window.location.hash.startsWith('#projetos/') ? window.location.hash.slice('#projetos/'.length) : null }

export default function ProjectsWorkspace({ user, onSessionExpired }: { user: UserProfile; onSessionExpired: () => void }) {
  const [id, setId] = useState(selectedId)
  const [projects, setProjects] = useState<Project[]>([])
  const [selected, setSelected] = useState<Project | null>(null)
  const [form, setForm] = useState<'create' | 'edit' | null>(null)
  const [query, setQuery] = useState('')
  const [appliedQuery, setAppliedQuery] = useState('')
  const [archived, setArchived] = useState(false)
  const [revision, setRevision] = useState(0)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [confirmDelete, setConfirmDelete] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const inFlight = useRef(false)
  useEffect(() => {
    const change = () => { setId(selectedId()); setLoading(true); setRevision(value => value + 1); setForm(null); setConfirmDelete(false); setError('') }
    window.addEventListener('hashchange', change)
    return () => window.removeEventListener('hashchange', change)
  }, [])
  useEffect(() => {
    const controller = new AbortController()
    const params = new URLSearchParams({ arquivado: String(archived), q: appliedQuery })
    const request = id ? api<Project>(`/projetos/${encodeURIComponent(id)}`, { signal: controller.signal })
      .then(setSelected) : api<Project[]>(`/projetos?${params}`, { signal: controller.signal }).then(setProjects)
    request.catch((failure: unknown) => {
      if (controller.signal.aborted) return
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível consultar os projetos.')
    }).finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [id, archived, appliedQuery, revision, onSessionExpired])
  function navigate(projectId: string | null) {
    setLoading(true); setError(''); setForm(null)
    const target = projectId ? `#projetos/${projectId}` : '#projetos'
    if (window.location.hash === target) { setId(projectId); setRevision(value => value + 1) }
    else window.location.hash = target
  }
  function search(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault(); setLoading(true); setError(''); setAppliedQuery(query); setRevision(value => value + 1)
  }
  function saved(project: Project) {
    setSuccess(form === 'edit' ? 'Projeto atualizado.' : 'Projeto criado.'); setSelected(project); navigate(project.id)
  }
  async function changeArchive() {
    if (!selected || inFlight.current) return
    inFlight.current = true; setBusy(true); setError(''); setSuccess('')
    try {
      const updated = await mutate<Project>(`/projetos/${selected.id}`, 'PATCH', { arquivado: !selected.arquivado })
      setSelected(updated); setSuccess(updated.arquivado ? 'Projeto arquivado.' : 'Projeto restaurado.')
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível atualizar o projeto.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  async function remove() {
    if (!selected || inFlight.current) return
    inFlight.current = true; setBusy(true); setError(''); setSuccess('')
    try {
      await mutate<void>(`/projetos/${selected.id}`, 'DELETE')
      setConfirmDelete(false); setSelected(null); setSuccess('Projeto eliminado.'); navigate(null)
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível eliminar o projeto.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  const owner = selected?.proprietario.id === user.id
  return <section className="projects" aria-label="Gestão de projetos">
    <nav aria-label="Projetos"><a href="#projetos" onClick={event => { event.preventDefault(); navigate(null) }}>Projetos</a></nav>
    {success && <p role="status">{success}</p>}
    {form ? <ProjectForm key={form === 'edit' ? selected!.id : 'new'} project={form === 'edit' ? selected! : undefined}
      onSaved={saved} onCancel={() => setForm(null)} onSessionExpired={onSessionExpired} /> : <>
      {loading ? <p role="status">A carregar projetos…</p> : error && !confirmDelete ? <>
        <p role="alert">{error}</p><button onClick={() => { setLoading(true); setError(''); setRevision(value => value + 1) }}>Tentar novamente</button>
      </> : id && selected ? <article aria-labelledby="project-title">
        <h2 id="project-title">{selected.nome}</h2><p className="description">{selected.descricao || 'Sem descrição.'}</p>
        <dl><dt>Proprietário</dt><dd>{selected.proprietario.nome}</dd><dt>Situação</dt><dd>{selected.arquivado ? 'Arquivado' : 'Ativo'}</dd>
          <dt>Progresso</dt><dd>{selected.progresso.totalTarefas === 0 ? '0% — Sem tarefas' : `${selected.progresso.percentagem}% (${selected.progresso.tarefasConcluidas}/${selected.progresso.totalTarefas})`}</dd></dl>
        {owner ? <div className="actions"><button disabled={busy} onClick={() => { setSuccess(''); setForm('edit') }}>Editar projeto</button>
          <button disabled={busy} onClick={changeArchive}>{selected.arquivado ? 'Restaurar projeto' : 'Arquivar projeto'}</button>
          <button disabled={busy} onClick={() => { setError(''); setSuccess(''); setConfirmDelete(true) }}>Eliminar projeto</button></div>
          : <p>Tens acesso de colaboração a este projeto.</p>}
      </article> : <>
        <h2>Projetos</h2><button onClick={() => { setSuccess(''); setForm('create') }}>Criar projeto</button>
        <form className="project-search" onSubmit={search}><label htmlFor="project-search">Pesquisar por nome</label>
          <input id="project-search" type="search" value={query} onChange={event => setQuery(event.target.value)} />
          <button type="submit">Pesquisar</button>
          <button type="button" onClick={() => { setQuery(''); setAppliedQuery(''); setLoading(true); setRevision(value => value + 1) }}>Limpar pesquisa</button>
        </form>
        <label className="archive-filter"><input type="checkbox" checked={archived} onChange={event => { setArchived(event.target.checked); setLoading(true); setError('') }} /> Mostrar projetos arquivados</label>
        {projects.length === 0 ? <p>Nenhum projeto encontrado.</p> : <ul className="project-list">{projects.map(project => <li key={project.id}>
          <a href={`#projetos/${project.id}`} onClick={event => { event.preventDefault(); navigate(project.id) }}>{project.nome}</a>
          <span>{project.proprietario.id === user.id ? 'Proprietário' : 'Colaborador'} · {project.progresso.percentagem}%</span>
        </li>)}</ul>}
      </>}
    </>}
    {confirmDelete && selected && <DeleteProjectDialog name={selected.nome} busy={busy} error={error}
      onCancel={() => { setConfirmDelete(false); setError('') }} onConfirm={remove} />}
  </section>
}
