# Matriz — Gestão de tarefas (Ficha 03, Exercício 5)

**Data:** 18/11/2026. **Estado:** API/interface implementadas; evidências em [verification.md](verification.md). Rastreabilidade: [issue #6](https://github.com/TiagoLeiteDev/Matriz/issues/6). Registo Git autorizado pelo utilizador; commit e confirmação de publicação na issue #6.

## Decisões humanas

O prompt exigia projeto obrigatório e enumerações diferentes do contrato. As respostas confirmaram:

- COL-04 mantida: tarefas pessoais privadas do criador e tarefas de projetos autorizados.
- TAR-01: TODO/IN_PROGRESS/DONE e LOW/MEDIUM/HIGH ou null; estado inicial TODO. Prioridade omitida numa nova tarefa = MEDIUM; null explícito = Sem prioridade.
- PESQ-02: pesquisa parcial literal do título ignorando maiúsculas e acentos.
- Migração: Em Revisão→IN_PROGRESS, Crítica→HIGH; restantes por equivalência. Prioridades vazias existentes conservadas.

Título obrigatório, não vazio/não só espaços; sem máximos aprovados. Não foram importados os limites 3–150/2000 do exemplo. Descrição, projeto, prazo e prioridade opcionais; datas passadas permitidas. A UI apresenta Por fazer/Em curso/Concluída e Baixa/Média/Alta.

## Migração V2

[V2__task_states_and_priorities.sql](../backend/src/main/resources/db/migration/V2__task_states_and_priorities.sql) altera CHECKs/defaults sem editar V1 nem recriar tabelas:

| Antes | Depois |
|---|---|
| Por Fazer | TODO |
| Em Curso / Em Revisão | IN_PROGRESS |
| Concluída | DONE |
| Baixa / Média | LOW / MEDIUM |
| Alta / Crítica | HIGH |
| Prioridade null | null, sem preenchimento retroativo |

IDs, criador, relações e comentários conservados. Flyway aplica a V2 no arranque; seed não é repetido para repor dados. O teste de migração usa PostgreSQL descartável: aplica V1, introduz 20 combinações antigas mais uma tarefa pessoal e relações, aplica V2, verifica valores/relações/defaults/CHECKs e segunda execução sem migrations adicionais.

## Contratos implementados

| Operação | Resultado |
|---|---|
| POST /api/tarefas | titulo obrigatório; descricao/estado/prioridade/prazo/projetoId opcionais. UUID/criador definidos no backend. 201 com Location e DTO. |
| GET /api/tarefas | Lista autorizada com filtros por interseção, sem paginação/ordem garantida. 200 com array. |
| GET /api/tarefas/{id} | 200 com DTO autorizado; leitura por ID de arquivadas permitida. |
| PATCH /api/tarefas/{id} | Pelo menos um campo; omitidos conservam, null apenas em opcionais. 200 com DTO ou JSON literal null após perda de acesso na desassociação. |
| DELETE /api/tarefas/{id} | Confirmação explícita na UI; remove comentários/atribuições atomicamente, conserva projeto/contas. 204 após commit. |

DTO: id, titulo, descricao, estado, prioridade, prazo, projetoId, criadorId. Sem entidades JPA/hashes expostos ou identidade imposta pelo cliente. OpenAPI 0.5.0: 24 operações documentadas, 16 implementadas.

Validação de campos: 422; JSON/estrutura/campos proibidos, UUID de caminho ou filtros malformados: 400; sem sessão: 401; acesso/CSRF: 403; inexistente: 404; arquivo/conflito concorrente de associação: 409; falha interna: 500 genérico. UUID inválido num campo do corpo é 422. Escritas exigem CSRF.

## Filtros

q, projetoId ou semProjeto=true, estado, prioridade ou semPrioridade=true, prazoDe e prazoAte. Pesquisa normalizada depois de limitar acesso; datas inclusivas excluem tarefas sem prazo. Booleanos apenas true/false; intervalo invertido ou combinações incompatíveis devolvem 400. Pesquisa vazia não restringe. Projeto sem acesso no filtro devolve array vazio. Sem paginação.

A lista exclui projetos arquivados e não existe includeArchived aprovado. Tarefas pessoais só aparecem ao criador. A leitura autorizada por ID pode mostrar uma tarefa arquivada sem ações de alteração.

## Acesso, transações e transferência

Proprietário/colaboradores atuais gerem tarefas do projeto; pessoais só o criador. Arquivo de origem/destino bloqueia todas as escritas, incluindo desassociação/eliminação.

Comandos transacionais bloqueiam projetos em ordem estável antes da tarefa, coordenando arquivo/eliminação. Associação/acesso são revalidados após o lock; mudança concorrente de associação produz 409 sem gravação parcial. PATCH funde os campos omitidos com os valores atuais bloqueados.

Transferência exige acesso à origem/destino ativo, preserva criador/comentários e remove atribuições inelegíveis na mesma transação (COL-06). Desassociar conserva só atribuições ao criador e torna a tarefa privada. Se o ator for outro, recebe JSON null e a UI retira a tarefa da sua lista. Falha real na limpeza foi induzida só em BD descartável para comprovar rollback integral.

## Interface e rastreabilidade

Navegação Projetos/Tarefas; lista global ou dentro do projeto, filtros, detalhe, formulário e confirmação. Carregamento/vazio/sucesso/erro explícitos. Formulário preservado na falha, bloqueio de submissões simultâneas, sem repetição automática de escritas. Operações confirmadas atualizam lista, contagens identificadas da lista filtrada e progresso do projeto sem reload. Progresso usa DONE/total.

| Fluxo | Requisitos / User Stories |
|---|---|
| Criar/editar e opcionais | F02/F06; US-03/04 |
| Estados livres/reabertura | F03; US-05 |
| Lista e filtros | F05/F11; US-06/14 (Kanban/calendário pendentes) |
| Progresso | F07; US-09 |
| Transferir/desassociar | F08; US-10; COL-06 |
| Persistência | F04; US-11 |
| Eliminação confirmada | F10; US-13 |
| Privacidade/sessão/acesso | F12/F13; US-15/16 |

## Validação e limites

50 testes backend passaram (33 anteriores + 16 de tarefas + 1 de migração); lint/build e 14 cenários normais de browser passaram. Reinício e evidências detalhadas em verification.md. E2E limpa só IDs que criou; não apaga seed/volume.

Sem paginação ou desempenho medido para grandes volumes; pesquisa normalizada em memória no conjunto autorizado. Sem ETag para impedir edições com base em formulários antigos. Falha de rede após envio pode ter resultado desconhecido: consultar antes de repetir.

Kanban, calendário, painel integral, gestão de colaboradores, escrita de comentários/responsáveis permanecem nos incrementos próprios. Contagens desta lista não resolvem PD-06 do painel. Não há novas decisões de âmbito pendentes para este incremento.


## Atualização — Exercício 6

Responsáveis/elegíveis e comentários integrados na API e no detalhe da tarefa, mantendo o modelo de seis entidades e V1/V2. COM-01: novos comentários de 1–2000 pontos de código Unicode, não só espaços; autor/data do backend; ordem criadoEm/id ASC. Responsáveis múltiplos com par único, remoção individual e elegibilidade revalidada no backend. Arquivo bloqueia escritas; pessoais privadas. Contratos/limites em [task-collaboration.md](task-collaboration.md). As menções anteriores a escrita de comentários/responsáveis pendente ficam superadas por este incremento; gestão de colaboradores, painel e Kanban/calendário continuam pendentes. Ver verification.md para evidências, sem confundir plano com teste executado.
