import type { UserProfile } from '../../api/client'

export type Project = {
  id: string; nome: string; descricao: string | null; arquivado: boolean; proprietario: UserProfile
  progresso: { totalTarefas: number; tarefasConcluidas: number; percentagem: number }
}
