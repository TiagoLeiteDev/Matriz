export class ApiError extends Error {
  constructor(public status: number, message: string) { super(message) }
}

export type UserProfile = { id: string; nome: string; email: string }

// Tokens stay in memory and are fetched again before each session mutation.
// Never automatically replay a write after a network failure or 401/403.
export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`/api${path}`, {
    ...options, credentials: 'same-origin', cache: 'no-store',
    headers: { Accept: 'application/json', ...options.headers },
  })
  if (!response.ok) {
    let message = 'Não foi possível concluir o pedido. Tenta novamente.'
    try {
      const problem: { detail?: string } = await response.json()
      if (problem.detail) message = problem.detail
    } catch { /* The server may be unavailable or return a non-JSON error. */ }
    throw new ApiError(response.status, message)
  }
  return response.status === 204 ? undefined as T : response.json() as Promise<T>
}

async function csrf(): Promise<string> { return (await api<{ token: string }>('/sessao/csrf')).token }

export async function login(email: string, palavraPasse: string): Promise<UserProfile> {
  const token = await csrf()
  return api<UserProfile>('/sessao', {
    method: 'POST', headers: { 'Content-Type': 'application/json', 'X-CSRF-Token': token },
    body: JSON.stringify({ email, palavraPasse }),
  })
}

export async function logout(): Promise<void> {
  const token = await csrf()
  await api<void>('/sessao', { method: 'DELETE', headers: { 'X-CSRF-Token': token } })
}
