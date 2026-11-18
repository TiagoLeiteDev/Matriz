import { useEffect, useRef } from 'react'

export default function DeleteTaskDialog({ title, busy, error, onCancel, onConfirm }: {
  title: string; busy: boolean; error: string; onCancel: () => void; onConfirm: () => void
}) {
  const ref = useRef<HTMLDialogElement>(null)
  const cancel = useRef<HTMLButtonElement>(null)
  useEffect(() => { const dialog = ref.current; dialog?.showModal(); cancel.current?.focus(); return () => dialog?.close() }, [])
  return <dialog ref={ref} aria-labelledby="delete-task-title" onCancel={e => { e.preventDefault(); if (!busy) onCancel() }}>
    <h3 id="delete-task-title">Eliminar tarefa?</h3><p><strong>{title}</strong></p>
    <p>A tarefa, os seus comentários e as atribuições de responsáveis serão eliminados definitivamente. O projeto e as contas dos utilizadores serão conservados.</p>
    {error && <p role="alert">{error}</p>}
    <div className="actions"><button ref={cancel} disabled={busy} onClick={onCancel}>Cancelar</button>
      <button disabled={busy} onClick={onConfirm}>{busy ? 'A eliminar…' : 'Confirmar eliminação da tarefa'}</button></div>
  </dialog>
}
