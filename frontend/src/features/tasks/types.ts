export const states = { TODO: 'Por fazer', IN_PROGRESS: 'Em curso', DONE: 'Concluída' } as const
export const priorities = { LOW: 'Baixa', MEDIUM: 'Média', HIGH: 'Alta' } as const
export type Task = { id: string; titulo: string; descricao: string | null; estado: keyof typeof states;
  prioridade: keyof typeof priorities | null; prazo: string | null; projetoId: string | null; criadorId: string }
