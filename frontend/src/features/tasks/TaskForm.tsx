import { useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { ApiError, mutate } from '../../api/client'
import type { Project } from '../projects/types'
import { states, priorities } from './types'
import type { Task } from './types'

export default function TaskForm({ task, projects, defaultProject, userId, onSaved, onCancel, onSessionExpired }: {
  task?: Task; projects: Project[]; defaultProject?: string; userId: string;
  onSaved: (task: Task | null) => void; onCancel: () => void; onSessionExpired: () => void
}) {
  const [title, setTitle] = useState(task?.titulo ?? '')
  const [description, setDescription] = useState(task?.descricao ?? '')
  const [state, setState] = useState(task?.estado ?? 'TODO')
  const [priority, setPriority] = useState(task ? task.prioridade ?? '' : 'MEDIUM')
  const [deadline, setDeadline] = useState(task?.prazo ?? '')
  const [project, setProject] = useState(task ? task.projetoId ?? '' : defaultProject ?? '')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const inFlight = useRef(false)
  async function submit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (inFlight.current) return
    if (!/\P{White_Space}/u.test(title)) { setError('Indica um título que não seja vazio ou apenas espaços.'); return }
    inFlight.current = true; setBusy(true); setError('')
    try {
      onSaved(await mutate<Task | null>(task ? `/tarefas/${task.id}` : '/tarefas', task ? 'PATCH' : 'POST', {
        titulo: title, descricao: description === '' ? null : description, estado: state,
        prioridade: priority || null, prazo: deadline || null, projetoId: project || null,
      }))
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível guardar a tarefa.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  return <section aria-labelledby="task-form-title"><h3 id="task-form-title">{task ? 'Editar tarefa' : 'Nova tarefa'}</h3>
    <form onSubmit={submit}>
      <label htmlFor="task-title">Título da tarefa</label><input id="task-title" required disabled={busy} value={title} onChange={e => setTitle(e.target.value)} />
      <label htmlFor="task-description">Descrição da tarefa (opcional)</label><textarea id="task-description" rows={4} disabled={busy} value={description} onChange={e => setDescription(e.target.value)} />
      <label htmlFor="task-project">Projeto da tarefa</label><select id="task-project" disabled={busy} value={project} onChange={e => setProject(e.target.value)}>
        <option value="">Sem projeto (pessoal)</option>{projects.map(p => <option key={p.id} value={p.id}>{p.nome}</option>)}
      </select>
      {!project && <p>{task && task.criadorId !== userId ? 'A tarefa ficará privada do criador original e perderás o acesso.' : 'Esta tarefa pessoal só pode ser consultada por ti.'}</p>}
      {task && project !== (task.projetoId ?? '') && <p>As atribuições de responsáveis sem acesso ao destino serão removidas.</p>}
      <label htmlFor="task-state">Estado da tarefa</label><select id="task-state" disabled={busy} value={state} onChange={e => setState(e.target.value as Task['estado'])}>
        {Object.entries(states).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
      </select>
      <label htmlFor="task-priority">Prioridade da tarefa</label><select id="task-priority" disabled={busy} value={priority} onChange={e => setPriority(e.target.value)}>
        <option value="">Sem prioridade</option>{Object.entries(priorities).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
      </select>
      <label htmlFor="task-deadline">Prazo da tarefa (opcional)</label><input id="task-deadline" type="date" disabled={busy} value={deadline} onChange={e => setDeadline(e.target.value)} />
      {error && <p role="alert">{error}</p>}
      <div className="actions"><button disabled={busy} type="submit">{busy ? 'A guardar…' : 'Guardar tarefa'}</button>
        <button disabled={busy} type="button" onClick={onCancel}>Cancelar</button></div>
    </form></section>
}
