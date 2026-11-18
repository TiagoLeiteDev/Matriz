import { useEffect, useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { api, ApiError, mutate } from '../../api/client'
import type { UserProfile } from '../../api/client'

export default function ProjectMembers({ projectId, owner, onChanged, onSessionExpired }: {
  projectId: string; owner: boolean; onChanged: () => void; onSessionExpired: () => void
}) {
  const [rows, setRows] = useState<UserProfile[]>([])
  const [email, setEmail] = useState('')
  const [revision, setRevision] = useState(0)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const inFlight = useRef(false)
  useEffect(() => {
    const controller = new AbortController()
    api<UserProfile[]>(`/projetos/${projectId}/colaboradores`, { signal: controller.signal }).then(setRows).catch((failure: unknown) => {
      if (controller.signal.aborted) return
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível consultar colaboradores.')
    }).finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [projectId, revision, onSessionExpired])
  async function change(user?: string) {
    if (inFlight.current) return
    inFlight.current = true; setBusy(true); setError(''); setSuccess('')
    try {
      await mutate(`/projetos/${projectId}/colaboradores${user ? `/${user}` : ''}`, user ? 'DELETE' : 'POST', user ? undefined : { email })
      setEmail(''); setSuccess(user ? 'Colaborador removido; acesso e atribuições revogados.' : 'Colaborador adicionado.')
      setLoading(true); setRevision(n => n + 1); onChanged()
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível alterar colaboradores.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  function submit(event: SubmitEvent<HTMLFormElement>) { event.preventDefault(); void change() }
  return <section aria-label="Colaboradores do projeto"><h3>Colaboradores</h3>
    {success && <p role="status">{success}</p>}
    {error && <><p role="alert">{error}</p><button disabled={busy} onClick={() => { setLoading(true); setError(''); setRevision(n => n + 1) }}>Atualizar colaboradores</button></>}
    {loading ? <p role="status">A carregar colaboradores…</p> : rows.length === 0 ? <p>Sem colaboradores adicionais.</p> : <ul>{rows.map(user => <li key={user.id}>
      {user.nome} ({user.email}) {owner && <button disabled={busy} aria-label={`Remover colaborador ${user.nome}`} onClick={() => void change(user.id)}>Remover</button>}
    </li>)}</ul>}
    {owner && <form onSubmit={submit}>
      <p>Adicionar uma conta existente. Remover revoga o acesso e as atribuições deste projeto; conserva comentários e contas.</p>
      <label htmlFor="member-email">Email do colaborador</label><input id="member-email" type="email" required value={email} disabled={busy} onChange={e => setEmail(e.target.value)} />
      <button type="submit" disabled={busy}>Adicionar colaborador</button>
    </form>}
  </section>
}
