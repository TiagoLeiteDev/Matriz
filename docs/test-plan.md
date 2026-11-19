# Matriz — Plano de testes

**Ficha 04, exercício 1 · 19/11/2026 · Base analisada: 212a4f6.**
**Estado:** plano proposto para revisão humana. Nenhum teste implementado ou executado neste exercício; existência de código de teste não significa aprovação do cenário. Resultados históricos permanecem em [verification.md](verification.md).

## Fontes e âmbito

Fontes: [AGENTS.md](../AGENTS.md), [requisitos](requirements.md), [User Stories](user-stories.md), [restrições](constraints.md), [arquitetura](architecture.md), [modelo](database.md), [tarefas](tasks.md), [OpenAPI 0.7.0](../openapi.yaml) e enunciado da Ficha 04, exercício 1. Inspeção estática de TaskInput, TaskCommands, TaskAccess, TaskQueries, TaskForm/TasksWorkspace, testes backend e Playwright.

Foco: validação, criação/consulta persistente e permissões de tarefas. Comentários, responsáveis, filtros e arquivo entram apenas como riscos relacionados já aprovados. Não constitui certificação integral das 22 histórias/88 critérios, auditoria de segurança completa nem teste de carga.

### Regras confirmadas

- F02/F06, US-03/04: título obrigatório, textual, não vazio/não só espaços. Sem mínimo de três caracteres ou máximo de comprimento aprovado. Descrição, projeto, prioridade e prazo opcionais; prazo passado permitido.
- TAR-01: estado omitido = TODO; prioridade omitida = MEDIUM; null explícito em prioridade = Sem prioridade. Estados TODO/IN_PROGRESS/DONE; prioridades LOW/MEDIUM/HIGH ou null.
- COL-02/04, F13, US-16: proprietário e colaboradores atuais gerem tarefas do projeto; pessoais só do criador. Atribuição por si só não concede acesso. Identidade inferida da sessão.
- COL-06: transferência/desassociação limpa atribuições inelegíveis atomicamente e preserva criador/comentários. Arquivo bloqueia escritas de tarefas/dependentes.
- Contrato: POST /api/tarefas → 201 e Location; GET /api/tarefas/{id} → 200; PATCH → 200; DELETE → 204. Validação de campos → 422; sem sessão → 401; sem permissão/CSRF → 403; recurso inexistente → 404; arquivo → 409. Pedidos estruturalmente proibidos → 400.
- Falha de gravação não confirma sucesso; UI conserva entradas e impede submissões repetidas durante o pedido.

## Dados e isolamento

A e B são contas fictícias; PA é projeto ativo de A, PB projeto privado de B. Por defeito B não colabora em PA; conceder colaboração explicitamente apenas nos casos positivos. TP é tarefa pessoal de A; TA pertence a PA. IDs são obtidos durante a preparação, não presumidos nem substituídos por IDs alheios à fixture.

Integração: JUnit/MockMvc com PostgreSQL Testcontainers, migrations reais e preparação isolada por teste. Não usar a base principal. TRUNCATE e falhas induzidas só em containers descartáveis. Testes unitários não iniciam Spring/Docker/BD.

E2E: aplicação real e PostgreSQL de teste com configuração/portas/volume próprios; Playwright, seletores existentes por label/role, dados únicos e limpeza apenas dos IDs criados. Sessões separadas para A/B, CSRF válido nas escritas de autorização para evitar um falso positivo de segurança. Não apagar seed/volume principal. Reinício é execução separada e explicitamente autorizada sobre a instância isolada.

## Matriz de cenários

Cada variante indicada deve ter asserção própria. Alta = integridade, privacidade ou percurso essencial; Média = proteção complementar de regra já aprovada. Não foram selecionados casos de prioridade baixa neste incremento.

| ID | Requisito / critério | Cenário e pré-condições | Entradas / ação | Resultado esperado verificável | Nível | Prioridade / motivo |
|---|---|---|---|---|---|---|
| T01 | F02; CA-US-03-03 | Validar título inválido sem infraestrutura | TaskInput.create com titulo ausente, null, número 12, vazio e três espaços, em casos separados | RequestValidationException com campo titulo; não devolver Create válido. HTTP não é responsabilidade deste teste | Unitário | Alta — impede dados inválidos |
| T02 | F02; CA-US-03-01/05; TAR-01 | Caso válido e fronteira mínima confirmada | titulo="X", estado/prioridade omitidos; variante "Preparar apresentação" | Create conserva título, estado TODO, prioridade MEDIUM; opcionais restantes null. Um carácter não branco é válido | Unitário | Alta — evita importar limites não aprovados |
| T03 | F02; CA-US-04-04/01 | Validação de edição isolada | TaskInput.patch com titulo="", "   " e null; variante apenas estado="DONE" | Três títulos rejeitados com campo titulo; estado isolado aceite com hasTitle=false, sem exigir título omitido | Unitário | Alta — distingue omissão de valor inválido |
| T04 | F02; CA-US-03-03/04-04 | API e BD isolada; A autenticado, CSRF válido | POST com cada título inválido de T01; PATCH de TA com título vazio | 422 e erro associado a titulo; contagem de tarefas não aumenta; título e restantes dados de TA conservados | Integração | Alta — comprova fronteira HTTP e ausência de escrita |
| T05 | F02/F06; CA-US-03-02/04/05 | A autorizado em PA ativo | POST {titulo:"Preparar apresentação", descricao:"Plano", projetoId:PA, prazo:"2020-01-01"}, omitindo estado/prioridade | 201; Location=/api/tarefas/{id}; DTO com ID, criador A, PA, título/descrição/prazo exatos, TODO/MEDIUM; uma linha correspondente na BD | Integração | Alta — criação real e defaults |
| T06 | F04; CA-US-04-01; CA-US-11-01 | Tarefa criada em T05, dentro do mesmo teste composto | Novo pedido GET pelo ID/Location com nova sessão autenticada de A; leitura SQL independente | 200; mesmos ID, criador, projeto e campos guardados; linha persistida correspondente. Uma nova sessão não comprova reinício | Integração | Alta — consulta posterior à persistência |
| T07 | F06; CA-US-03-04; CA-US-04-03; TAR-01 | A autenticado | Criar pessoal com prioridade:null; editar tarefa com descrição/prioridade/prazo null; omitir título | Null explícito conservado, tarefa pessoal projetoId=null; PATCH conserva título/criador; consulta posterior confirma limpeza dos opcionais | Integração | Média — semântica dos opcionais |
| T08 | F13; CA-US-16-01 | A tem TA em PA; B sem colaboração, ambos autenticados | B faz GET/PATCH/DELETE de TA; PATCH válido com CSRF válido; lista com filtro PA | GET/PATCH/DELETE 403 sem DTO de TA; lista 200 vazia; consulta de A e BD confirmam tarefa intacta | Integração | Alta — IDOR em projeto privado |
| T09 | F13; CA-US-16-03; COL-04 | A tem TP; B pode até colaborar em PA | B GET/PATCH/DELETE de TP; GET responsáveis/comentários de TP; escritas com CSRF válido | 403 sem dados privados; TP e dependentes inalterados; colaboração num projeto não concede acesso pessoal | Integração | Alta — privacidade pessoal |
| T10 | F02/F13; CA-US-16-02; COL-02 | B é colaborador atual de PA ativo | B cria e consulta tarefa em PA e altera estado para DONE | 201/200/200; criador da nova tarefa é B; estado persistido DONE. Não exigir propriedade para gerir tarefas | Integração | Alta — controlo positivo da autorização |
| T11 | F12/F13; US-15/16; SEG-01 | TA existente | GET sem sessão; PATCH autenticado sem CSRF; GET UUID inexistente como A | 401, 403 e 404 respetivamente; nenhuma escrita nem dados internos/credenciais nos erros | Integração | Alta — distinguir autenticação, CSRF e inexistência |
| T12 | F09; CA-US-12-05/06 | PA arquivado e A autorizado | GET TA; POST em PA; PATCH/DELETE TA com CSRF válido | Leitura 200; escritas 409; dados/dependentes conservados. Não confundir com proibição de gerir colaboradores em arquivo | Integração | Alta — proteção do arquivo |
| T13 | F08; CA-US-10-03/04; COL-06 | TA criada por A, com B colaborador e ambos atribuídos | B desassocia TA por PATCH projetoId:null | 200 com JSON literal null; TA fica privada de A; B passa a 403; só atribuição elegível de A fica; criador/comentários conservados na mesma operação | Integração | Alta — perda de acesso e integridade |
| T14 | F02/F04; CA-US-03-02; CA-US-11-01 | PA de teste ativo, login pela UI | Autenticar → abrir PA → criar tarefa com título único → verificar lista → abrir detalhe → reload | Uma criação POST 201; tarefa visível sem reload inicial; detalhe após reload conserva título e projeto; consulta API confirma ID. Sem mocks do percurso | E2E | Alta — percurso exigido pela Ficha 04 |
| T15 | F02/F04; CA-US-03-03; CA-US-11-03 | Formulário aberto | Submeter três espaços; depois título válido e descrição com falha de rede controlada | Título inválido: mensagem e zero POST. Falha: sem sucesso, título/descrição mantidos, submissão reativada; não inferir rollback de falha de rede | E2E | Alta — validação e recuperação do utilizador |
| T16 | F04; CA-US-11-01; SEG-01 | Instância isolada; tarefa própria criada por UI | Reiniciar apenas backend de teste, aguardar health, autenticar de novo e consultar tarefa | Sessão anterior inválida; após novo login, campos e ID preservados, sem recriação da tarefa | E2E separado | Média — persistência após reinício, distinta de reload |

T05 e T06 são passos de um único teste independente: criar, guardar ID, consultar e verificar BD, sem dependência entre métodos ou ordem da suite.

## Inventário e reutilização

Inspeção de código, sem execução nesta fase:

| Artefacto existente | Cobertura observada no código | Decisão para os exercícios seguintes |
|---|---|---|
| backend/src/test/java/pt/matriz/TaskCrudTest.java | Títulos inválidos, criação/defaults/nova sessão, pessoais, PATCH, transições, filtros, arquivo, transferência, cascata e rollback com PostgreSQL | Reutilizar e reforçar asserções concretas apenas onde necessário; não duplicar toda a classe |
| backend/src/test/java/pt/matriz/SecurityTest.java | Sessão/CSRF, acessos distintos, pessoais e revogação de colaboração | Reutilizar como complemento de T08–T11; verificar cenários existentes antes de adicionar |
| AssignmentTest, CommentTest, ProjectCollaborationTest, DashboardTest | Dependentes e colaboração/contagens | Manter como regressão complementar; fora do mínimo de nova implementação |
| e2e/tests/tasks.spec.ts | CRUD pessoal, progresso, desassociação, formulário em falha e reinício opt-in | Reutilizar T15/T16; confirmar que o percurso de projeto T14 tem todas as asserções requeridas |
| e2e/tests/mvp-verification.spec.ts e delivery-review.spec.ts | Navegação/reload de tarefa em projeto e progresso | Preservar regressões; não substituir testes essenciais por verificação de percentagens |
| TaskInput.create/patch | Validação pura, chamável com JsonNode sem contexto Spring | Lacuna: não foi encontrada classe de teste unitário isolado dedicada; propor T01–T03 no exercício 2 |

@SpringBootTest + MockMvc + Testcontainers é integração, mesmo quando se valida um único campo. Os resultados históricos de 75 testes backend e 25 E2E da revisão anterior não são novas execuções da Ficha 04, nem aprovação automática desta matriz.

## Seleção e sequência proposta

1. Exercício 2: T01–T03 com JUnit/AssertJ e ObjectMapper locais, sem Spring/BD; válido, inválido e fronteira de um carácter. Não alterar produção para obter testes verdes.
2. Exercício 3: consolidar T04–T06 e T08/T09 na cobertura existente; fluxo criado/consultado com BD real e negativo verificável. Inspecionar asserções antes de decidir alterações.
3. Exercício 4: consolidar T14; reutilizar T15 e executar T16 apenas separadamente se incluído na verificação autorizada. Sem esperas fixas.
4. Exercício 5: revisão de código/segurança e cenário real de autorização com duas contas; relatório quality-security-review.md nessa fase. T07/T10–T13 são regressões complementares prioritárias já parcialmente representadas.

Para futuras execuções registar por ID: commit, comando, ambiente, esperado, observado, evidência, estado (aprovado/falhou/não executado) e limitações. Critério de seleção: requisitos confirmados, impacto de falha e evidência que cada nível acrescenta. CI é opcional na Ficha 04; não é requisito deste exercício.

## Revisão crítica das sugestões da IA

Decisões desta proposta, ainda sujeitas à revisão do utilizador; não são apresentadas como aprovações humanas já recebidas:

| Sugestão inicial considerada | Adaptação/rejeição | Justificação |
|---|---|---|
| Usar título de 3–150 caracteres e descrição até 2000 do exemplo pedagógico | Rejeitada | PD-01 e contrato não aprovam esses limites; "X" é caso válido de fronteira |
| Tratar qualquer utilizador não proprietário como proibido | Corrigida | Colaborador atual gere tarefas; T08 exige ausência real de colaboração e T10 é controlo positivo |
| Tornar projeto obrigatório | Rejeitada | COL-04 mantém tarefas pessoais privadas |
| Chamar unitários aos testes atuais de validação via MockMvc | Corrigida | Iniciam Spring e BD real; unitários de TaskInput constituem nível distinto |
| Devolver sempre 404 para recurso alheio | Rejeitada | Contrato aprovado distingue 403 sem acesso e 404 inexistente; este exercício não altera política |
| Validar sucesso só por HTTP 201 ou presença de texto na página | Reforçada | T05/T06 verificam campos, identidade, Location, consulta e BD; T14 liga UI ao ID criado |
| Considerar nova sessão/reload como prova de reinício | Corrigida | T16 é separado; novo login e consulta não demonstram reinício por si só |
| Criar nova suite duplicando todos os cenários anteriores | Rejeitada | Reutilizar testes e acrescentar apenas lacunas/asserções úteis |

## Inconsistências, riscos e decisões pendentes

- Não foi confirmada nesta inspeção uma divergência funcional nos cenários essenciais entre regras documentadas, TaskInput/TaskCommands e testes lidos. Isto não substitui execução nem revisão de toda a aplicação.
- Documentação de arquitetura/tarefas contém estados históricos de exercícios antigos, superados por secções posteriores e pelo contrato 0.7.0. Não interpretar "pendente" histórico como regra atual, nem corrigir documentação fora deste incremento.
- A UI usa String.trim e o backend uma classe Unicode de whitespace. São mecanismos diferentes; equivalência para todos os caracteres Unicode não foi demonstrada. Casos ASCII vazio/espaços são obrigatórios; tratamento de caracteres Unicode limítrofes requer comparação com o contrato antes de fixar novos resultados esperados. Não alterar regras nesta fase.
- Nenhum máximo aprovado significa não inventar um teste obrigatório de rejeição por comprimento. Testes representativos de texto longo não equivalem a prometer capacidade ilimitada.
- Plano aguarda revisão humana, sobretudo seleção do mínimo a acrescentar sem duplicação. Não há dúvida crítica que impeça propor os cenários ASCII, criação/consulta e autorização acima.
- Todos os cenários desta matriz têm estado de execução **não executado neste exercício**. Ficheiros de testes/produção, ambiente, dependências e configuração permanecem inalterados.


## Atualização de execução — Exercício 2 (19/11/2026)

O pedido seguinte do utilizador autorizou implementar/executar os unitários do título. TaskInputTest cobre T01, T03 e a parte de título válido/fronteira de T02: 11 execuções aprovadas, zero falhas/erros/ignorados. Defaults/opcionais de T02 não fazem parte destas novas asserções. Os estados "não executado" acima referem-se ao momento de elaboração do exercício 1; os restantes cenários não tiveram nova execução. Comando, dados, resultados e limites em [verification.md](verification.md), secção Ficha 04 — Exercício 2. Nenhuma regra de negócio foi alterada.
