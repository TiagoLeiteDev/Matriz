# Matriz — Plano e evidências de verificação

**Data:** 18/11/2026. **Estado:** aplicação não implementada; cenários de execução ainda não realizados. Este documento começa como plano, não relatório de testes aprovados.

| Caso | Resultado esperado | Resultado observado | Evidência |
|---|---|---|---|
| V01 Acesso | Credenciais válidas permitem sessão, inválidas são rejeitadas; perfil e logout funcionam. | Não executado. | Pendente. |
| V02 Projetos | Criar/pesquisar/editar/eliminar respeita propriedade e cascata. | Não executado. | Pendente. |
| V03 Persistência | Tarefa surge sem recarregar e mantém-se após reinício. | Não executado. | Pendente. |
| V04 Validação | Vazio/só espaços e valores inválidos rejeitados no backend. | Não executado. | Pendente. |
| V05 Estados/filtros | Alterações atualizam tarefa, filtros, progresso e contagens. | Não executado. | Pendente. |
| V06 Responsáveis | Múltiplos elegíveis, sem duplicação; remoção e limpeza atómicas. | Não executado. | Pendente. |
| V07 Comentários | Conteúdo, autor e data confiáveis persistem e são autorizados. | Não executado. | Pendente. |
| V08 Autorização | Segundo utilizador não acede a dados alheios por IDs manipulados; tarefas pessoais privadas. | Não executado. | Pendente. |
| V09 Eliminação/erro | Cascata sem órfãos, cancelar sem pedido, erro sem falso sucesso/perda do formulário. | Não executado. | Pendente. |
| V10 Sessão/CSRF | Cookie, logout, invalidação e proteção de operações de escrita. | Não executado. | Pendente. |

## Testes automatizados mínimos a implementar

1. Regra de validação (nome/título obrigatório e não só espaços).
2. Autorização com dois utilizadores e recurso não acessível.
3. Criação e consulta posterior à persistência real.

Registar comandos, ambiente, resultados e limitações reais. Não substituir testes de aplicação por validação de OpenAPI. Acrescentar regressão para bugs reproduzidos e corrigidos.

## Exemplos de Human-in-the-Loop já observados

| Objetivo da IA | Decisão do utilizador/programador | Verificação |
|---|---|---|
| Definir âmbito inicial | Aprovou uso individual; depois autorizou reconciliação colaborativa em COL-01. | Matriz de diferenças e atualização documental; não equivale a teste de execução. |
| Modelar associação Projeto/Tarefa | Aprovou tarefas independentes e correção da relação não identificadora. | Revisão semântica e renderização Mermaid. |
| Preparar API | Proposta inicial sem autenticação foi superada pelo novo âmbito; utilizador aprovou sessão/cookie. | Revisão do contrato e validação automática; autorização real ainda por testar. |

## Verificações documentais

A execução e resultados da validação de OpenAPI, referências e imagens ficam registados no log da ficha 3 após serem efetivamente executados. Não há comandos de arranque/testes da aplicação porque a stack ainda não foi escolhida.

### Resultado da revisão documental — 18/11/2026

OpenAPI 3.1 validado: 24 operações e 12 exemplos válidos; schemas rejeitam campos de identidade indevidos e entradas obrigatórias inválidas. 22 histórias/88 critérios únicos; fontes ER sincronizadas e imagens ER/login/tarefas inspecionadas. Referências locais válidas e git diff --check sem erros. Ferramentas de validação isoladas em tmp, sem dependências da aplicação adicionadas. Todos os cenários de execução V01–V10 continuam Não executado.
