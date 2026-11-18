import { useEffect, useRef, useState } from 'react'
import type { SubmitEvent } from 'react'
import { api, ApiError, mutate } from '../../api/client'

type Comment = { id: string; conteudo: string; autor: { id: string; nome: string; email: string }; criadoEm: string }
export default function TaskComments({ taskId, readOnly, onSessionExpired }: {
  taskId: string; readOnly: boolean; onSessionExpired: () => void
}) {
  const [rows, setRows] = useState<Comment[]>([])
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [revision, setRevision] = useState(0)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const inFlight = useRef(false)
  useEffect(() => {
    const controller = new AbortController()
    api<Comment[]>(`/tarefas/${taskId}/comentarios`, { signal: controller.signal })
      .then(data => { if (!controller.signal.aborted) setRows(data) })
      .catch((failure: unknown) => {
        if (controller.signal.aborted) return
        if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
        else setError(failure instanceof Error ? failure.message : 'Não foi possível consultar comentários.')
      }).finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [taskId, revision, onSessionExpired])
  const length = Array.from(content).length
  async function submit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (inFlight.current) return
    setSuccess('')
    if (length < 1 || length > 2000 || !Array.from(content).some(char => !/\p{White_Space}/u.test(char))) {
      setError('Indica entre 1 e 2000 caracteres, não apenas espaços.'); return
    }
    inFlight.current = true; setBusy(true); setError('')
    try {
      await mutate<Comment>(`/tarefas/${taskId}/comentarios`, 'POST', { conteudo: content })
      setContent(''); setSuccess('Comentário adicionado.'); setLoading(true); setRevision(n => n + 1)
    } catch (failure: unknown) {
      if (failure instanceof ApiError && failure.status === 401) onSessionExpired()
      else setError(failure instanceof Error ? failure.message : 'Não foi possível adicionar o comentário.')
    } finally { inFlight.current = false; setBusy(false) }
  }
  return <section aria-label="Comentários da tarefa">
    <h4>Comentários</h4><p>Mais antigos primeiro.</p>
    {success && <p role="status">{success}</p>}
    {error && <><p role="alert">{error}</p><button disabled={busy} onClick={() => { setError(''); setLoading(true); setRevision(n => n + 1) }}>Atualizar comentários</button></>}
    {loading ? <p role="status">A carregar comentários…</p> : rows.length === 0 ? <p>Sem comentários.</p> : <ol className="comments">{rows.map(comment => <li key={comment.id}>
      <p>{comment.autor.nome} · <time dateTime={comment.criadoEm} title={comment.criadoEm}>{new Date(comment.criadoEm).toLocaleString('pt-PT')}</time></p>
      <p className="description">{comment.conteudo}</p>
    </li>)}</ol>}
    {!readOnly && <form onSubmit={submit}>
      <label htmlFor="comment-content">Conteúdo do comentário</label><textarea id="comment-content" rows={4} required disabled={busy} value={content} onChange={e => setContent(e.target.value)} aria-describedby="comment-length" />
      <p id="comment-length">{length}/2000 caracteres</p><button disabled={busy} type="submit">Adicionar comentário</button>
    </form>}
  </section>
}
