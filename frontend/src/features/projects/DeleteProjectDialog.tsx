import { useEffect, useRef } from 'react'

export default function DeleteProjectDialog({ name, busy, error, onCancel, onConfirm }: {
  name: string; busy: boolean; error: string; onCancel: () => void; onConfirm: () => void
}) {
  const dialog = useRef<HTMLDialogElement>(null)
  const cancel = useRef<HTMLButtonElement>(null)
  useEffect(() => {
    const current = dialog.current
    current?.showModal(); cancel.current?.focus()
    return () => current?.close()
  }, [])
  return <dialog ref={dialog} aria-labelledby="delete-title" onCancel={event => { event.preventDefault(); if (!busy) onCancel() }}>
    <h2 id="delete-title">Eliminar projeto?</h2><p><strong>{name}</strong></p>
    <p>O projeto e todas as suas tarefas, comentários, atribuições de responsáveis e colaborações serão eliminados definitivamente.
      As contas dos utilizadores serão conservadas.</p>
    {error && <p role="alert">{error}</p>}
    <div className="actions"><button ref={cancel} disabled={busy} onClick={onCancel}>Cancelar</button>
      <button disabled={busy} onClick={onConfirm}>{busy ? 'A eliminar…' : 'Confirmar eliminação'}</button></div>
  </dialog>
}
