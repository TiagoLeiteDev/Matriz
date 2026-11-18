import { useEffect, useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { api, ApiError, mutate } from '../../api/client'

type User = { id: string; nome: string; email: string }
type Assignment = { utilizador: User }
export default function TaskAssignees({ taskId, readOnly, onSessionExpired }: {
  taskId: string; readOnly: boolean; onSessionExpired: () => void
}) {
  const [rows, setRows] = useState<Assignment[]>([])
  const [eligible, setEligible] = useState<User[]>([])
  const [selected, setSelected] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [revision, setRevision] = useState(0)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const inFlight = useRef(false)
  useEffect(() => {
    const controller = new AbortController()
    Promise.all([api<Assignment[]>(`/tarefas/${taskId}/responsaveis`, { signal: controller.signal }),
      api<User[]>(`/tarefas/${taskId}/utilizadores-elegiveis`, { signal: controller.signal })])
      .then(([assigned, users]) => { if (!controller.signal.aborted) { setRows(assigned); setEligible(users) } })
      .catch((failure: unknown) => {
        if (controller.signal.aborted) return
        if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
        else setError(failure instanceof Error ? failure.message : 'Não foi possível consultar responsáveis.')
      }).finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [taskId, revision, onSessionExpired])
  async function change(user: string, remove: boolean) {
    if (inFlight.current) return
    inFlight.current = true; setBusy(true); setError(''); setSuccess('')
    try {
      await mutate<Assignment | void>(`/tarefas/${taskId}/responsaveis${remove ? `/${user}` : ''}`, remove ? 'DELETE' : 'POST', remove ? undefined : { utilizadorId: user })
      setSelected(''); setSuccess(remove ? 'Responsável removido.' : 'Responsável associado.')
      setLoading(true); setRevision(n => n + 1)
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível alterar responsáveis.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  function add(event: SubmitEvent<HTMLFormElement>) { event.preventDefault(); if (selected) void change(selected, false) }
  const available = eligible.filter(user => !rows.some(row => row.utilizador.id === user.id))
  return <section aria-label="Responsáveis da tarefa">
    <h4>Responsáveis</h4>
    {success && <p role="status">{success}</p>}
    {error && <><p role="alert">{error}</p><button disabled={busy} onClick={() => { setError(''); setLoading(true); setRevision(n => n + 1) }}>Atualizar responsáveis</button></>}
    {loading ? <p role="status">A carregar responsáveis…</p> : <>
      {rows.length === 0 ? <p>Sem responsáveis.</p> : <ul>{rows.map(({ utilizador: user }) => <li key={user.id}>
        <span>{user.nome} ({user.email})</span>{!readOnly && <button disabled={busy} aria-label={`Remover responsável ${user.nome}`} onClick={() => void change(user.id, true)}>Remover</button>}
      </li>)}</ul>}
      {!readOnly && <form onSubmit={add}>
        <label htmlFor="assignee">Utilizador elegível</label><select id="assignee" value={selected} disabled={busy} onChange={e => setSelected(e.target.value)} required>
          <option value="">Selecionar utilizador</option>{available.map(user => <option key={user.id} value={user.id}>{user.nome} ({user.email})</option>)}
        </select>
        <button disabled={busy || !selected} type="submit">Associar responsável</button>
        {available.length === 0 && <p>Todos os utilizadores elegíveis já estão associados.</p>}
      </form>}
    </>}
  </section>
}
