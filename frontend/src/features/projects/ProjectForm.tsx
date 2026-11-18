import { useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { ApiError, mutate } from '../../api/client'
import type { Project } from './types'

export default function ProjectForm({ project, onSaved, onCancel, onSessionExpired }: {
  project?: Project; onSaved: (project: Project) => void; onCancel: () => void; onSessionExpired: () => void
}) {
  const [name, setName] = useState(project?.nome ?? '')
  const [description, setDescription] = useState(project?.descricao ?? '')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const inFlight = useRef(false)
  const nameInput = useRef<HTMLInputElement>(null)
  async function submit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (inFlight.current) return
    if (!name.trim()) { setError('Indica um nome que não seja vazio ou apenas espaços.'); nameInput.current?.focus(); return }
    inFlight.current = true; setBusy(true); setError('')
    try {
      const saved = await mutate<Project>(project ? `/projetos/${project.id}` : '/projetos', project ? 'PATCH' : 'POST',
        { nome: name, descricao: description === '' ? null : description })
      onSaved(saved)
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível guardar o projeto.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  return <section aria-labelledby="project-form-title">
    <h2 id="project-form-title">{project ? 'Editar projeto' : 'Novo projeto'}</h2>
    <form onSubmit={submit}>
      <label htmlFor="project-name">Nome do projeto</label>
      <input id="project-name" ref={nameInput} required value={name} disabled={busy} onChange={event => setName(event.target.value)} />
      <label htmlFor="project-description">Descrição (opcional)</label>
      <textarea id="project-description" value={description} disabled={busy} onChange={event => setDescription(event.target.value)} rows={5} />
      {error && <p role="alert">{error}</p>}
      <div className="actions"><button type="submit" disabled={busy}>{busy ? 'A guardar…' : 'Guardar projeto'}</button>
        <button type="button" disabled={busy} onClick={onCancel}>Cancelar</button></div>
    </form>
  </section>
}
