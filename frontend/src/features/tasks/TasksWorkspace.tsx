import { useEffect, useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { api, ApiError, mutate } from '../../api/client'
import type { Project } from '../projects/types'
import type { Task } from './types'
import { states, priorities } from './types'
import TaskForm from './TaskForm'
import DeleteTaskDialog from './DeleteTaskDialog'
import TaskAssignees from './TaskAssignees'
import TaskComments from './TaskComments'
import TaskViews from './TaskViews'
import { currentMonth } from './types'
import type { TaskView } from './TaskViews'

const empty = { q: '', project: '', state: '', priority: '', from: '', to: '' }
function hashTask() { return window.location.hash.startsWith('#tarefas/') ? window.location.hash.slice('#tarefas/'.length) : null }

export default function TasksWorkspace({ userId, projectId, readOnly = false, onChanged, onSessionExpired }: {
  userId: string; projectId?: string; readOnly?: boolean; onChanged?: () => void; onSessionExpired: () => void
}) {
  const [id, setId] = useState<string | null>(() => projectId ? null : hashTask())
  const [tasks, setTasks] = useState<Task[]>([])
  const [projects, setProjects] = useState<Project[]>([])
  const [selected, setSelected] = useState<Task | null>(null)
  const [archived, setArchived] = useState(false)
  const [form, setForm] = useState<'create' | 'edit' | null>(null)
  const [draft, setDraft] = useState(empty)
  const [filters, setFilters] = useState(empty)
  const [revision, setRevision] = useState(0)
  const [loading, setLoading] = useState(true)
  const [confirm, setConfirm] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [view, setView] = useState<TaskView>('list')
  const [month, setMonth] = useState(currentMonth)
  const inFlight = useRef(false)
  useEffect(() => {
    if (projectId) return
    const change = () => { setId(hashTask()); setLoading(true); setRevision(n => n + 1); setForm(null); setConfirm(false); setError('') }
    window.addEventListener('hashchange', change)
    return () => window.removeEventListener('hashchange', change)
  }, [projectId])
  useEffect(() => {
    if (readOnly) return
    const controller = new AbortController()
    const params = new URLSearchParams({ q: filters.q })
    const project = projectId ?? filters.project
    if (project === '__none') params.set('semProjeto', 'true')
    else if (project) params.set('projetoId', project)
    if (filters.state) params.set('estado', filters.state)
    if (filters.priority === '__none') params.set('semPrioridade', 'true')
    else if (filters.priority) params.set('prioridade', filters.priority)
    if (filters.from) params.set('prazoDe', filters.from)
    if (filters.to) params.set('prazoAte', filters.to)
    Promise.all([api<Project[]>('/projetos', { signal: controller.signal }),
      api<Task[]>(`/tarefas?${params}`, { signal: controller.signal }),
      id ? api<Task>(`/tarefas/${encodeURIComponent(id)}`, { signal: controller.signal }) : Promise.resolve(null)])
      .then(async ([list, rows, task]) => {
        let isArchived = false
        if (task?.projetoId) {
          const parent = list.find(p => p.id === task.projetoId) ?? await api<Project>(`/projetos/${task.projetoId}`, { signal: controller.signal })
          isArchived = parent.arquivado
        }
        if (controller.signal.aborted) return
        setProjects(list); setTasks(rows); setSelected(task); setArchived(isArchived)
      }).catch((failure: unknown) => {
        if (controller.signal.aborted) return
        if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
        else setError(failure instanceof Error ? failure.message : 'Não foi possível consultar tarefas.')
      }).finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [projectId, id, filters, revision, readOnly, onSessionExpired])
  function navigate(taskId: string | null) {
    setLoading(true); setError(''); setForm(null); setConfirm(false)
    if (projectId) { setId(taskId); setRevision(n => n + 1); return }
    const target = taskId ? `#tarefas/${taskId}` : '#tarefas'
    if (window.location.hash === target) { setId(taskId); setRevision(n => n + 1) } else window.location.hash = target
  }
  function saved(task: Task | null) {
    setSuccess(task === null ? 'Tarefa guardada. A tarefa ficou privada do criador original e deixaste de ter acesso.' : form === 'edit' ? 'Tarefa atualizada.' : 'Tarefa criada.')
    onChanged?.(); navigate(null)
  }
  async function remove() {
    if (!selected || inFlight.current) return
    inFlight.current = true; setBusy(true); setError(''); setSuccess('')
    try { await mutate<void>(`/tarefas/${selected.id}`, 'DELETE'); setConfirm(false); setSuccess('Tarefa eliminada.'); onChanged?.(); navigate(null) }
    catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível eliminar a tarefa.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  function filter(event: SubmitEvent<HTMLFormElement>) { event.preventDefault(); setLoading(true); setError(''); setFilters({ ...draft }); setRevision(n => n + 1) }
  if (readOnly) return <section aria-label="Gestão de tarefas"><h3>Tarefas</h3><p>Projeto arquivado: as tarefas estão protegidas contra alterações.</p></section>
  return <section className="tasks" aria-label="Gestão de tarefas">
    <button onClick={() => navigate(null)}>Lista de tarefas</button>
    {success && <p role="status">{success}</p>}
    {form ? <TaskForm key={form === 'edit' ? selected!.id : 'new'} task={form === 'edit' ? selected! : undefined} projects={projects}
      defaultProject={projectId} userId={userId} onSaved={saved} onCancel={() => setForm(null)} onSessionExpired={onSessionExpired} /> : <>
      {loading ? <p role="status">A carregar tarefas…</p> : error && !confirm ? <><p role="alert">{error}</p><button onClick={() => { setLoading(true); setError(''); setRevision(n => n + 1) }}>Tentar novamente</button></> : id && selected ? <article>
        <h3>{selected.titulo}</h3><p className="description">{selected.descricao || 'Sem descrição.'}</p>
        <dl><dt>Estado</dt><dd>{states[selected.estado]}</dd><dt>Prioridade</dt><dd>{selected.prioridade ? priorities[selected.prioridade] : 'Sem prioridade'}</dd>
          <dt>Prazo</dt><dd>{selected.prazo || 'Sem prazo'}</dd><dt>Projeto</dt><dd>{selected.projetoId ? projects.find(p => p.id === selected.projetoId)?.nome ?? 'Projeto arquivado' : 'Tarefa pessoal'}</dd></dl>
        {archived ? <p>Projeto arquivado: apenas consulta.</p> : <div className="actions"><button onClick={() => { setSuccess(''); setForm('edit') }}>Editar tarefa</button>
          <button onClick={() => { setError(''); setSuccess(''); setConfirm(true) }}>Eliminar tarefa</button></div>}
        <TaskAssignees key={selected.id} taskId={selected.id} readOnly={archived} onSessionExpired={onSessionExpired} />
        <TaskComments key={selected.id} taskId={selected.id} readOnly={archived} onSessionExpired={onSessionExpired} />
      </article> : <>
        <h3>Tarefas</h3><button onClick={() => { setSuccess(''); setForm('create') }}>Nova tarefa</button>
        <form className="task-filters" onSubmit={filter}>
          <label htmlFor="task-search">Pesquisar título</label><input id="task-search" type="search" value={draft.q} onChange={e => setDraft({ ...draft, q: e.target.value })} />
          {!projectId && <><label htmlFor="task-project-filter">Filtrar por projeto</label><select id="task-project-filter" value={draft.project} onChange={e => setDraft({ ...draft, project: e.target.value })}>
            <option value="">Todos os acessíveis</option><option value="__none">Sem projeto (pessoais)</option>{projects.map(p => <option key={p.id} value={p.id}>{p.nome}</option>)}</select></>}
          <label htmlFor="task-state-filter">Filtrar por estado</label><select id="task-state-filter" value={draft.state} onChange={e => setDraft({ ...draft, state: e.target.value })}>
            <option value="">Todos os estados</option>{Object.entries(states).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select>
          <label htmlFor="task-priority-filter">Filtrar por prioridade</label><select id="task-priority-filter" value={draft.priority} onChange={e => setDraft({ ...draft, priority: e.target.value })}>
            <option value="">Todas as prioridades</option><option value="__none">Sem prioridade</option>{Object.entries(priorities).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select>
          <label htmlFor="task-from">Prazo desde</label><input id="task-from" type="date" value={draft.from} onChange={e => setDraft({ ...draft, from: e.target.value })} />
          <label htmlFor="task-to">Prazo até</label><input id="task-to" type="date" value={draft.to} onChange={e => setDraft({ ...draft, to: e.target.value })} />
          <div className="actions"><button type="submit">Aplicar filtros</button><button type="button" onClick={() => { setDraft(empty); setFilters({ ...empty }); setLoading(true); setError(''); setRevision(n => n + 1) }}>Limpar filtros</button></div>
        </form>
        <p data-testid="task-count">Nesta lista: {tasks.length} tarefas.</p>
        <p>{Object.entries(states).map(([state, label]) => `${label}: ${tasks.filter(t => t.estado === state).length}`).join(' · ')}</p>
        <TaskViews tasks={tasks} view={view} onView={setView} month={month} onMonth={setMonth} onOpen={navigate} />
      </>}
    </>}
    {confirm && selected && <DeleteTaskDialog title={selected.titulo} busy={busy} error={error} onCancel={() => { setConfirm(false); setError('') }} onConfirm={remove} />}
  </section>
}
