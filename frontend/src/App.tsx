import { useEffect, useState } from 'react'
export default function App() {
  const [status, setStatus] = useState('A verificar…')
  useEffect(() => {
    const controller = new AbortController()
    fetch('/actuator/health', { signal: controller.signal })
      .then(async response => {
        if (!response.ok) throw new Error('Serviço indisponível')
        const body: { status: string } = await response.json()
        setStatus(body.status === 'UP' ? 'Serviço disponível' : 'Serviço indisponível')
      })
      .catch((error: unknown) => {
        if (!(error instanceof DOMException && error.name === 'AbortError')) setStatus('Serviço indisponível')
      })
    return () => controller.abort()
  }, [])
  return <main><h1>Matriz</h1><p>Base técnica do projeto.</p><p role="status">{status}</p>
    <p>A interface funcional será desenvolvida nos próximos incrementos.</p></main>
}
