import { test, expect } from '@playwright/test'
import { collectCleanupFailures, cleanupError } from '../helpers/cleanup'

test('cleanup: falha no primeiro recurso não impede restantes nem oculta a falha', async () => {
  // Arrange — simular falha sem chamar APIs ou alterar uma base de dados.
  const calls: string[] = [], original = new Error('Falha simulada')
  const steps = ['tarefa1', 'tarefa2', 'projeto1'].map(name => ({ name, run: async () => {
    calls.push(name)
    if (name === 'tarefa1') throw original
  } }))

  // Act — executar o mesmo helper usado pelo afterEach real.
  const failures = await collectCleanupFailures(steps)
  const error = cleanupError(failures, false)

  // Assert — ordem conservada, restantes tentados e falha sinalizada.
  expect(calls).toEqual(['tarefa1', 'tarefa2', 'projeto1'])
  expect(failures).toEqual([{ name: 'tarefa1', error: original }])
  expect(error).toBeInstanceOf(AggregateError)
  expect(error!.errors).toEqual([original])
})

test('cleanup: caminho sem falhas não produz erro', async () => {
  // Arrange — simular uma limpeza bem-sucedida, sem serviços.
  const calls: string[] = []

  // Act — executar e avaliar a limpeza.
  const failures = await collectCleanupFailures([{ name: 'tarefa', run: async () => { calls.push('tarefa') } }])

  // Assert — todas as operações foram tentadas sem erro agregado.
  expect(calls).toEqual(['tarefa'])
  expect(failures).toEqual([])
  expect(cleanupError(failures, false)).toBeUndefined()
})

test('cleanup: conservar falha original do teste sem a substituir pela limpeza', async () => {
  // Arrange — o runner já registou uma falha no corpo do teste.
  const failure = new Error('Falha simulada na limpeza')

  // Act — recolher falha adicional e aplicar política de preservação.
  const failures = await collectCleanupFailures([{ name: 'tarefa', run: async () => { throw failure } }])
  const error = cleanupError(failures, true)

  // Assert — conservar diagnóstico adicional sem novo throw sobre o erro original.
  expect(failures).toEqual([{ name: 'tarefa', error: failure }])
  expect(error).toBeUndefined()
})
