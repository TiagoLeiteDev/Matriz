export type CleanupStep = { name: string; run: () => Promise<void> }
export type CleanupFailure = { name: string; error: unknown }

// Sequential execution preserves task-before-project ordering without stopping on failure.
export async function collectCleanupFailures(steps: CleanupStep[]): Promise<CleanupFailure[]> {
  const failures: CleanupFailure[] = []
  for (const step of steps) {
    try { await step.run() } catch (error: unknown) { failures.push({ name: step.name, error }) }
  }
  return failures
}

export function cleanupError(failures: CleanupFailure[], testAlreadyFailed: boolean): AggregateError | undefined {
  if (!failures.length || testAlreadyFailed) return undefined
  return new AggregateError(failures.map(failure => failure.error), 'Falhou a limpeza de fixtures próprias.')
}
