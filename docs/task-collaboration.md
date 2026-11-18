# Matriz — Plano de responsáveis e comentários (Ficha 03, Exercício 6)

**Data:** 18/11/2026. **Estado:** plano aprovado pelo utilizador (“sim”); quatro incrementos implementados e verificados localmente. Issue [#7](https://github.com/TiagoLeiteDev/Matriz/issues/7). COM-01 aprovada com o plano. Sem alteração ao esquema V1/V2.

## Análise inicial

Referências consultadas: requirements.md, user-stories.md, constraints.md, database.md, architecture.md, openapi.yaml, README.md, ui/wireframe-tarefas.md e AGENTS.md; implementação de TaskAccess, TaskQueries, comandos de tarefas e esquema V1/V2.

- F15/US-18: zero ou vários responsáveis, par tarefa/utilizador único, existência/elegibilidade verificadas no backend, remoção individual sem apagar tarefa/conta.
- F16/US-19: conteúdo obrigatório/não só espaços; autor e data gerados pelo backend; consulta autorizada, persistente, incluindo leitura de arquivadas.
- F13/US-16, COL-02/04/06: proprietário/colaboradores atuais gerem recursos de tarefas de projeto; pessoais só o criador. Atribuição não concede acesso. Arquivo bloqueia escritas; transferências já removem atribuições inelegíveis atomicamente.
- F04/US-11: persistência após reabrir, reload e reinício com novo login (SEG-01).
- US-17-03/04: consulta de elegíveis suporta este exercício. Gestão de colaboradores (US-17-01/02) permanece um fluxo distinto, não incluído automaticamente neste pedido.

Já existem tabelas, PKs/FKs, índices, cascatas, perfis seguros, consultas GET de responsáveis/comentários e guardas de elegibilidade. As leituras não comprovam operações de escrita concluídas. Monólito modular/stack TEC-01 mantidos; implementação no módulo tasks, reutilizando identity/projects e a gestão de transações existente.

## Reconciliação do pedido com o contrato

1. O pedido exige comentários entre 1 e 2000 caracteres. O contrato atual referencia Texto sem máximo. Proposta COM-01: adotar o limite explícito deste novo pedido apenas para conteúdo de comentários, com schema próprio; não limitar títulos/descrições nem alterar o schema genérico Texto. Validar por pontos de código Unicode no backend e de forma equivalente no frontend; testar fronteiras 1/2000/2001 e texto suplementar. Preservar o conteúdo enviado, sem truncagem silenciosa.
2. Autor é sempre o ator da sessão. utilizadorId do responsável é uma seleção de destinatário, nunca prova de identidade/acesso: o backend verifica existência e elegibilidade atual.
3. GET comentários já ordena criado_em ASC, id ASC. Explicitar no OpenAPI essa ordenação existente (mais antigos primeiro, desempate estável), sem inventar outra.
4. Duplicado de atribuição = 409, já previsto no contrato. Remover uma atribuição inexistente = 404, dentro dos status documentados; a UI atualiza a lista e apresenta o erro, sem anunciar sucesso.

## Plano incremental para validação

| Incremento | Objetivo e alterações | Critério de conclusão e verificações |
|---|---|---|
| 1 — Responsáveis/API | Lista de elegíveis; POST/DELETE de atribuições; DTO estrito; autorização/CSRF; locks/transações coordenados com tarefas/projetos; atualização do estado das operações no OpenAPI. | PostgreSQL descartável: múltiplos, duplicado 409, inexistente 404, inelegível 403, privacidade pessoal, arquivo 409, CSRF, sem acesso, remoção individual e conservação de contas/tarefas. Rever diff antes do incremento 2. |
| 2 — Responsáveis/UI | Componente no detalhe da tarefa, lista atual, seleção só entre elegíveis, associar/remover individualmente, estados de carregamento/vazio/erro e bloqueio de pedidos repetidos. Arquivadas em leitura. | Lint/build e browser real: associar dois, remover só um, reabrir/reload, erro sem falso sucesso, sem conceder acesso por atribuição. Rever diff antes do incremento 3. |
| 3 — Comentários/API | POST de comentário, autor/instante do servidor; conteúdo específico 1–2000; manter GET e ordenar por instante/id; alinhar requisitos/OpenAPI com COM-01. | Testes 1/2000/2001, Unicode, vazio/só espaços/null/tipos/campos extra, autor/data forjados recusados, duas identidades, arquivo/privacidade/CSRF, ordem/desempate e persistência. Rever diff antes do incremento 4. |
| 4 — Comentários/UI e fecho | Lista autor/data/conteúdo e formulário de envio no detalhe, sem edição/eliminação autónoma; preservar rascunho em falha; refresh só após gravação confirmada; README/log/evidências. | Lint/build, backend completo, browser: criação, erros, reload/reabertura e teste separado com reinício/novo login para comentários e responsáveis. Validar OpenAPI e respostas reais; rever diff final. |

Cada incremento só avança após as suas verificações e revisão do diff. Se houver falhas, corrigir e repetir os testes afetados. A validação humana deste plano autoriza os incrementos descritos; nova decisão de âmbito/segurança exige esclarecimento.

## Alterações de dados

Nenhuma entidade/relação/índice novo é necessário: atribuicao_tarefa tem PK composta (tarefa_id, utilizador_id), que impede duplicados na persistência; FKs para utilizador/tarefa. comentario já tem conteúdo, autor, tarefa e instante; índices por tarefa/autor. Cascata remove dependentes ao eliminar tarefa/projeto. Não alterar V1/V2 nem apagar dados.

COM-01 será validada nos pedidos da aplicação. Não se propõe um novo CHECK máximo na BD neste incremento: isso exigiria verificar e decidir tratamento de comentários preexistentes acima do limite. Não truncar/eliminar esses dados; preservar leitura e rastreabilidade. Se a análise posterior revelar necessidade concreta de índice/migration, justificar antes de alterar.

## Operações e permissões

URLs abaixo incluem o prefixo real /api; OpenAPI usa servidor /api.

| Método/endpoint | Permissão e validação | Sucesso / erros principais |
|---|---|---|
| GET /api/tarefas/{id}/utilizadores-elegiveis | Acesso atual à tarefa; proprietário + colaboradores do projeto ou só criador da pessoal; sem hashes. | 200; 400/401/403/404/500 |
| GET /api/tarefas/{id}/responsaveis | Leitura autorizada, inclusive arquivadas. Já implementado. | 200; 400/401/403/404/500 |
| POST /api/tarefas/{id}/responsaveis | Ator pode gerir tarefa ativa; utilizadorId obrigatório, existente e elegível; impedir duplicado. | 201 com Location e Responsavel; 400/401/403/404/409/422/500 |
| DELETE /api/tarefas/{id}/responsaveis/{utilizadorId} | Ator pode gerir tarefa ativa; remover apenas o par existente. | 204; 400/401/403/404/409/500 |
| GET /api/tarefas/{id}/comentarios | Acesso atual; arquivadas em leitura; criadoEm/id ASC. Já implementado. | 200; 400/401/403/404/500 |
| POST /api/tarefas/{id}/comentarios | Ator pode gerir tarefa ativa; só conteudo no DTO; COM-01; UUID/autor/instante gerados no backend. | 201 com Location e Comentario; 400/401/403/404/409/422/500 |

Campos desconhecidos/identidade/data forjadas: 400; validação de campos: 422; falta de CSRF: 403. Acrescentar ao contrato o 400 de UUID malformado nas leituras/remoção para corresponder ao tratamento real, sem criar novas rotas. Location de comentário identifica o recurso criado; não implica um GET individual de comentário não previsto.

## Regras de implementação e interface

- Revalidar acesso/arquivo/elegibilidade com locks em ordem compatível com TaskCommands e ProjectCommands; não validar e depois escrever em contextos separados.
- Duplicados protegidos por validação de negócio e PK; pedidos concorrentes ao mesmo par não criam duplicação nem expõem erro interno de SQL.
- Transferência/eliminação não podem intercalar deixando atribuições inelegíveis ou comentários órfãos.
- Listas de utilizadores/responsáveis usam perfis mínimos já contratados; não listar contas globais para seleção nem aceitar que o cliente conceda acesso.
- Atualizar responsáveis/comentários depois de sucesso confirmado, sem reload. Desativar submissão em curso, manter entradas em falha e tratar 401; não repetir escritas automaticamente, pois falha de rede pode ter resultado desconhecido.
- Não disponibilizar controlos de escrita em tarefa arquivada; backend mantém a mesma proteção por pedidos diretos.
- Comentários renderizados como texto, não HTML fornecido pelo utilizador. Sem edição/remoção individual, notificações ou menções.

## Riscos, dependências e limites

Gestão de colaboradores ainda não implementada: para demonstrar vários elegíveis, usar o projeto partilhado do seed; fixtures descartáveis/preparadas pelos testes para restantes cenários. Este exercício não fecha US-17 integralmente.

Colaboradores removidos/transferências são risco concorrente: manter protocolo de locks e testes de integridade. Não confundir guardas existentes com implementação do fluxo de remoção de colaboradores.

Sem paginação nem metas de grandes volumes aprovadas. Escritas autenticadas exigem CSRF; testes não desativam a proteção. Reinício invalida sessão, pelo que o teste de persistência inclui novo login. E2E limpa apenas IDs próprios; nunca dados do seed ou volume.

## Documentação e Git previstos

Após aprovação: abrir issue específica antes da implementação, com este âmbito e critérios. Atualizar requisitos/histórias (COM-01), contratos/estado por operação, README com uso e testes manuais, verification.md com esperado/observado, plano global, AGENTS.md e log da ficha 03. Não afirmar testes como executados neste plano.

A implementação fica associada à issue do incremento. Commit/push/fecho serão tratados ao terminar, segundo a autorização do utilizador; a aprovação deste plano não declara antecipadamente publicação ou conclusão.


## Resultado implementado e revisão final

- Incremento 1: API de elegíveis/atribuições, JSON estrito, transações/locks e PK/duplicado 409; AssignmentTest 8/8 passou. Diff revisto antes da UI.
- Incremento 2: TaskAssignees integrado no detalhe, seleção limitada/remoção, carregamento/vazio/erro e leitura de arquivadas; lint/build e dois testes de browser passaram. Seletores corrigidos para nomes reais do seed; diff revisto antes de comentários/API.
- Incremento 3: CommentCommands/Controller e COM-01, autor/instante backend, ordem existente explícita; 28 testes de comentários/responsáveis/segurança passaram; schema com fronteiras Unicode válido. Diff revisto antes de comentários/UI.
- Incremento 4: TaskComments no detalhe, autor/data/conteúdo seguro como texto, contador Unicode e formulário preservado em falha; 66 testes backend, lint/build, 18 testes normais de Chrome e 1 teste separado de reinício passaram. Quatro testes especiais ignorados na execução normal, não contados como passados. OpenAPI 0.6.0 válido, 20/24 operações implementadas; 7 respostas reais de colaboração conformes e DELETE 204.

Revisão final: apenas rotas específicas abertas; autorização/arquivo na transação; nenhum autor/instante do cliente; sem HTML injetado; dados históricos preservados, sem migration; sem gestão de colaboradores adicionada implicitamente. Links e git diff --check válidos. Baseline da demonstração conservada: 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições. Falhas e correções instrumentais descritas em verification.md.

US-18 e US-19 suportadas; US-17-03/04 (elegíveis) suportadas. US-17-01/02 (gerir colaboradores), painel, Kanban/calendário, CI e entrega noutro computador permanecem pendentes. COM-01 aplica-se a novos pedidos, não restringe retroativamente texto histórico. Sem paginação/metas de grandes volumes ou edição/eliminação autónoma de comentários. Uma falha de rede pode ter resultado desconhecido; consultar antes de repetir.

README documenta seleção/associação/remoção, comentários, validações, fluxos manuais e comandos de teste/reinício. Evidências em verification.md e interações em ai-logs/ficha-03.md. Registo Git do Exercício 6 autorizado; commit e confirmação de publicação/fecho na issue #7.
