# Matriz — Revisão final e evidências de entrega

**Data:** 18/11/2026. **Exercício:** ficha 03, exercício 9. **Base publicada:** c9bcdcc. **Issue:** [#10](https://github.com/TiagoLeiteDev/Matriz/issues/10). Revisão local concluída; registo e publicação autorizados em 19/11/2026, com confirmação na issue #10. Relatório e critérios em [delivery-review.md](delivery-review.md). A matriz dos nove cenários do Exercício 8 está preservada no histórico abaixo.

## Resultados executados nesta revisão

| Verificação | Resultado real |
|---|---|
| Estado inicial Git | main em c9bcdcc, status/diff limpos antes da revisão. |
| Refatoração única aprovada | SearchText.fold extraído, preservando NFD/remoção de marcas/Locale.ROOT; 75 testes backend passaram após correção de uma edição inicial incompleta. Não contabilizar a compilação inicial falhada como teste aprovado. |
| Backend `./backend/mvnw.cmd -f backend/pom.xml verify` | 75 testes, 0 falhas/erros/ignorados, já com a refatoração. |
| Dependências em clone limpo | frontend/e2e instalados por npm ci dos lockfiles, sem copiar node_modules; npm não reportou vulnerabilidades nessa execução. Isto não é auditoria de segurança universal. |
| Frontend lint/build | Código 0 na cópia e após correção pontual de apresentação; imagem integrada compilada com fontes finais. |
| Docker em cópia limpa isolada | Build, V1/V2, seed explícito e up --wait concluídos; app healthy em 18082 e DB em 55433, com rede/volume próprios. |
| Playwright na cópia isolada | **25 passaram**, 4 especiais ignorados, 0 falhas na suite final (37,3 s). Inclui nova regressão de progresso. Não repetir o resultado do Exercício 8 como se fosse execução nova desses quatro especiais. |
| Progresso/CA-US-09 | Verificados na API/UI 25%, 33,3%, 50%, 100%, exclusão de pessoal e transferência com origem 0%/destino 100%. |
| Recriação de containers | down sem -v e up de db/app no projeto isolado; novo login recuperou projeto arquivado, tarefa, comentário com mesmo autor/instante e atribuição. Tarefa eliminada continuou 404; fixtures próprias removidas. |
| Contrato | OpenAPI 0.7.0 válido; 24 mappings correspondem às 24 operações, normalizando só nomes dos parâmetros de caminho. 17 respostas reais da cópia conformes aos schemas. Sem alteração ao contrato. |
| Integridade final da cópia | 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições; apenas V1/V2 success=true. |
| Segredos/entrega | Scan por padrões no HEAD e em 13 commits, sem ficheiros sinalizados; .env/gerados não versionados; .gitignore/.dockerignore revistos. Limitação: deteção por padrões/inspeção não prova ausência absoluta de segredos não reconhecidos. |

### Cópia limpa e comandos

Obtida por `git clone --no-hardlinks https://github.com/TiagoLeiteDev/Matriz.git tmp/delivery-review-20261118`, no commit c9bcdcc. Configuração nova a partir de .env.example, apenas fictícia, com APP_PORT=18082, DB_PORT=55433, DB_URL do host ajustado e APP_ALLOWED_ORIGINS correspondentes. As fontes da refatoração e da correção ainda não publicadas foram sobrepostas explicitamente; 61 ficheiros de fontes/teste comparados com a árvore final, equivalentes após normalização de fins de linha. Não afirmar que estas alterações já pertencem ao clone remoto publicado.

Na cópia, os comandos foram os do README com projeto isolado:

```powershell
docker compose -p matriz-delivery-review config --quiet
docker compose -p matriz-delivery-review up -d db --wait
docker compose -p matriz-delivery-review build app seed
docker compose -p matriz-delivery-review --profile tools run --rm seed
docker compose -p matriz-delivery-review up -d app --wait
npm.cmd --prefix frontend ci
npm.cmd --prefix frontend run lint
npm.cmd --prefix frontend run build
npm.cmd --prefix e2e ci
. ./scripts/Load-LocalEnv.ps1
$env:PLAYWRIGHT_CHANNEL = 'chrome'
$env:MATRIZ_BASE_URL = 'http://127.0.0.1:18082'
npm.cmd --prefix e2e test
# Persistência, depois de preparar fixtures próprias pela API:
docker compose -p matriz-delivery-review down
docker compose -p matriz-delivery-review up -d db app --wait
```

As flags dos quatro testes especiais foram definidas como 0 no shell da suite normal. O backend verify foi executado na árvore principal, já com a refatoração; a imagem da cópia usa as mesmas fontes. Scripts temporários de revisão, fora da entrega, fizeram chamadas HTTP de preparação/consulta/cleanup e validação JSON Schema sem imprimir credenciais. Instalação limpa realizada **no mesmo computador**, com Docker Linux/Windows e Chrome instalado; não equivale a validar outro computador, outro SO ou download sem qualquer cache de imagens/Maven.

## BUG-09-01 — Separador decimal do progresso

Reproduzido antes de alterar a apresentação: uma tarefa DONE em três, API=33.3; UI mostrava 33.3%, mas CA-US-09-05 exige 33,3%. A causa era interpolação direta de Number sem locale. [Captura anterior](ui/bug-09-progress-locale-before.png). Diagnóstico/proposta apresentados ao utilizador. Correção limitada em ProjectsWorkspace/Dashboard para toLocaleString('pt-PT', máximo uma decimal), sem mudar cálculo/API. Novo delivery-review.spec.ts falhou antes e passou na suite final, cobrindo também percentagens inteiras e transferência. Não é uma segunda refatoração; é correção pontual de critério já confirmado.

## Três exemplos reais de utilização da IA

| Objetivo do pedido | Decisão tomada pela análise humana/IA | Sugestão ou implementação corrigida/adaptada/rejeitada | Resultado obtido e fonte |
|---|---|---|---|
| Fechar a regra de colaboração em projeto arquivado (Ex. 7). | O utilizador escolheu permitir ao proprietário adicionar/remover colaboradores em arquivo; aplicar COL-06 sem abrir escritas de tarefas arquivadas. | A recomendação da IA de consultar apenas e exigir restauro foi **rejeitada** pelo utilizador. | COL-07 implementada e verificada por ProjectCollaborationTest e frontend-integration.spec.ts. Pergunta/resposta em ai-logs/ficha-03.md, Ex. 7. |
| Verificar persistência/criação noutra sessão (Ex. 8). | Tratar a falha como navegação após analisar o código e o teste, sem assumir perda de dados. | A implementação inicial gerada com apoio da IA mantinha a tarefa selecionada apenas em estado local no projeto; foi **corrigida** para URL com projeto/tarefa e restauro no reload. | BUG-08-01 reproduzido, corrigido e coberto por mvp-verification.spec.ts; o caso voltou a passar na cópia limpa desta revisão. Fonte: log e histórico de verification.md. |
| Reduzir duplicação da pesquisa sem alterar regras (Ex. 9). | Utilizador aprovou uma única extração para SearchText.fold, em passo isolado, mantendo o algoritmo e executando regressão. | A primeira edição automática confundiu a chaveta da expressão regular com o fim do método; compilação detetou o erro. A edição foi **corrigida** com remoção delimitada pelas linhas do método, sem mudar algoritmo. | 75 testes backend passaram após correção; pesquisas de projetos/tarefas sem acentos também passaram na suite browser da cópia final. Fonte: ai-logs/ficha-03.md, Ex. 9, e diff dos três ficheiros. |

Estes exemplos correspondem a interações/code paths reais desta conversa e aos registos versionados; não foram inventados pedidos ou resultados.

## Critérios não aprovados por esta evidência

CI/GitHub Actions e prova de pipeline falhar/recuperar: **Não cumprido** (sem workflow). Execução noutro computador, verificações funcionais manuais humanas, auditoria WCAG/carga/browsers e apresentação/validação pelo formador: **Não verificados**. As 22 histórias estão representadas e há 88 critérios documentados, mas não foi executado um cenário isolado para cada um dos 88: não marcar todos individualmente como aprovados. Exemplos de cobertura apenas parcial de variantes: CA-US-05-01 (nove transições no backend, não todas nas duas vistas UI), CA-US-12-03 (restauro nas três vistas com/sem prazo) e CA-US-14-05 (fixture exata combinada em todas as vistas). Sem falha funcional confirmada nessas variantes; falta evidência específica completa.

A execução local e os artefactos obrigatórios estão preparados nos limites do ensaio. A entrega integral continua condicionada às ações do relatório, incluindo autorização de commits/push. Testes automáticos e inspeção técnica não são apresentados como testes manuais humanos.


<details>
<summary>Histórico preservado — verificação consolidada do Exercício 8 e anteriores</summary>

# Matriz — Verificação consolidada do MVP

**Data:** 18/11/2026. **Exercício:** ficha 03, exercício 8. **Base:** commit `71dbf47` (exercício 7 publicado). **Rastreabilidade:** [issue #9](https://github.com/TiagoLeiteDev/Matriz/issues/9). **Estado:** nove cenários aprovados por verificações automatizadas, incluindo reinício/expiração; BUG-08-01 corrigido com regressão. Registo Git autorizado; confirmação de publicação na issue #9.

## Implementação analisada e aplicabilidade

Monólito modular Java 21/Spring Boot/Spring Security/Maven, PostgreSQL/JPA/Flyway, React/TypeScript/Vite/CSS e Docker Compose. Nove classes de testes backend (75 testes), cinco ficheiros Playwright existentes e três testes novos em mvp-verification.spec.ts. Consultados AGENTS, requisitos/histórias, restrições, plano, README e implementação real; contrato ativo OpenAPI 0.7.0 com 24 operações.

**Os nove cenários são aplicáveis e têm funcionalidades implementadas.** Não se confundem contagens documentais, testes executados e entrega concluída. Neste conjunto não há cenário funcional classificado como Não aplicável ou Não implementado. Registo público, recuperação de palavra-passe, edição/eliminação individual de comentários e exposição pública não fazem parte do MVP aprovado. CI/entrega e ensaios mais amplos não são dados como concluídos por estes resultados.

Regras usadas: COL-02/04/06/07, SEG-01 (60 minutos de inatividade; reinício/logout invalidam sessão), TAR-01 (três estados/prioridades, null opcional), PESQ-01/02, COM-01 (1–2000 pontos de código Unicode) e PAINEL-01 (pessoais + projetos ativos acessíveis, excluir arquivados). Sem máximos de título/descrição copiados dos exemplos pedagógicos.

## Matriz dos nove cenários — esperado e observado

As linhas seguintes são verificações **automatizadas**, por MockMvc/PostgreSQL real descartável e/ou Playwright sobre a aplicação real. Os passos usam fixtures fictícias próprias; não executar alterações destrutivas nos dados do seed.

| Caso / objetivo e reprodução | Resultado esperado | Resultado observado / evidência | Estado |
|---|---|---|---|
| 01 — Acesso. Entrar com Ana; tentar palavra-passe errada/conta ausente; pedir recurso sem sessão; terminar sessão; aguardar expiração na instância isolada. | Login válido 200/perfil seguro; credenciais erradas e sem sessão 401; CSRF/origem inválida 403; sessão revogada perde acesso; UI regressa ao login. | SecurityTest 12 passou; auth.spec.ts passou nos cenários normais e no teste separado de 65 s sem pedidos numa instância de 1 minuto. Política real de 60 minutos verificada pelo teste de servlet. | Aprovado |
| 02 — Projetos. Criar projeto, pesquisar por parte do nome sem acentos/maiúsculas, editar/reabrir e cancelar/confirmar eliminação; tentar gestão por colaborador/outro utilizador. | Dados persistem; só proprietário gere; pesquisa só no conjunto autorizado; cancelamento não envia DELETE e confirmação remove o projeto/dependentes. | ProjectCrudTest 12 e projects.spec.ts normais passaram. Projeto criado/editado na UI também consultado noutra sessão em mvp-verification.spec.ts. | Aprovado |
| 03 — Persistência de tarefas. Criar tarefa pela interface, consultar sem reload; reabrir/recarregar; reiniciar app e voltar a autenticar; consultar os mesmos campos. | Tarefa/estado/prioridade/prazo persistem em PostgreSQL; UI apresenta gravação confirmada; sessão anterior inválida após restart. | Criação/reload/nova sessão passaram; teste separado de reinício de tarefas passou, conservando IN_PROGRESS/HIGH/prazo após novo login. | Aprovado |
| 04 — Validação. Enviar título vazio, só espaços Unicode, null/tipo inválido, estados/prioridades anteriores, data impossível, UUID inválido e campos de identidade; comparar registo antes/depois. | 422 para validação, 400 para campos proibidos; nenhum dado confirmado alterado ou dependente criado; UI conserva entradas na falha. | Novo teste “pedidos inválidos não alteram dados...” passou: task DTO inalterado após cada PATCH inválido, comentários/atribuições vazios. TaskCrudTest, CommentTest e testes de formulários passaram. | Aprovado |
| 05 — Estado/prioridade. Editar estado/prioridade/prazo; filtrar por projeto/estado/prioridade; alternar lista/Kanban/calendário e voltar ao painel. | Mesmos dados autorizados; transições livres e progresso atualizado; painel usa PAINEL-01, não filtros da lista; prazos mudam de dia/mês. | TaskCrudTest cobre as nove transições; tasks.spec.ts/frontend-integration.spec.ts passaram, incluindo HIGH, mudança para DONE, calendário e contagens. | Aprovado |
| 06 — Responsáveis. Associar dois elegíveis; repetir par, inclusive concorrentemente; remover um; tentar inexistente/inelegível/pessoal alheia. | Par único, duplicado 409, inexistente 404, inelegível/acesso 403; só a atribuição selecionada é removida; contas/tarefa conservadas. | AssignmentTest 8 passou, incluindo concorrência 201/409 e uma só linha. collaboration.spec.ts confirmou múltiplos, remoção individual e reload; novo teste confirmou limpeza após revogação. | Aprovado |
| 07 — Comentários. Enviar conteúdo válido/inválido e autor/data forjados; reabrir/nova sessão; reiniciar e voltar a consultar. | Conteúdo, autor da sessão e instante do servidor persistem; limite COM-01 e ordem criadoEm/id; arquivo/sem acesso bloqueiam escrita. | CommentTest 8 e cenários normais passaram; novo teste confirmou conteúdo/autor/data noutra sessão. Teste separado conservou comentário e dois responsáveis após reinício/novo login. | Aprovado |
| 08 — Autorização por IDs. Bruno usa IDs de projeto/tarefa/pessoal privados da Ana para GET e dez escritas; depois recebe acesso e perde-o sem novo login. | Pedidos diretos recusados 403; listas não revelam recursos privados; revogação tem efeito na sessão atual; limpar atribuições e preservar autoria. | Novo teste com sete leituras e dez escritas alheias passou; concessão 201, consulta 200, remoção 204 e nova consulta/escrita 403. Comentário mantém autor; responsáveis ficam vazios. SecurityTest e restantes testes de recursos passaram. | Aprovado |
| 09 — Eliminação/erros. Cancelar/confirmar DELETE; provocar falha de cascata/limpeza em BD descartável; abortar comunicação de formulários. | Cascata atómica sem órfãos ou contas apagadas; rollback em falha; erro genérico sem SQL; sem falso sucesso, perda de entradas ou repetição automática. | ProjectCrudTest/TaskCrudTest/ProjectCollaborationTest/CommentTest passaram com falhas reais induzidas; testes UI de projetos/tarefas/responsáveis/comentários conservaram entradas e só reenviaram por ação explícita. | Aprovado |

Fontes dos testes: [backend](../backend/src/test/java/pt/matriz), [Playwright](../e2e/tests), [três novos testes](../e2e/tests/mvp-verification.spec.ts). Evidência técnica local: backend/target/surefire-reports (JUnit XML/texto); e2e/playwright-report (suite normal); e2e/test-results e tmp/e2e-* (execuções separadas). Relatórios gerados não são versionados; este resumo e o teste de regressão ficam no repositório.

## Comandos e resultados reais desta execução

PowerShell na raiz do repositório; Docker Desktop Linux disponível, app principal em 8080 e PostgreSQL com volume conservado. Usadas as contas fictícias do seed e a DEMO_PASSWORD local, sem a imprimir ou guardar nos relatórios.

| Comando | Observado |
|---|---|
| `./backend/mvnw.cmd -f backend/pom.xml verify` | 75 testes, 0 falhas/erros/ignorados. Backend/migrations não alterados pela correção de UI posterior; não repetir esta execução como se tivesse ocorrido após uma alteração de backend. |
| `npm.cmd --prefix frontend run lint` e `npm.cmd --prefix frontend run build` | Código 0, também após a correção final. |
| `docker compose build app seed` e `docker compose up -d app --wait` | Imagem com a correção integrada; app/db saudáveis. Seed não executado. |
| `. ./scripts/Load-LocalEnv.ps1`; `PLAYWRIGHT_CHANNEL=chrome`; `npm.cmd --prefix e2e test` | Primeira execução: 23 passaram, 1 falhou e 4 especiais ignorados; BUG-08-01 identificado. Repetição após correção: **24 passaram**, 0 falhas, 4 especiais ignorados. |
| Três percursos separados com `--grep restart` | **3 passaram** (46,5 s): projetos, tarefas e responsáveis/comentários, com reinício real e nova autenticação. |
| Instância temporária + `npm.cmd --prefix e2e run test:expiry -- --reporter=line --output=../tmp/e2e-expiry` | **1 passou** após 65 s sem pedidos, timeout temporário 1 minuto. A instância foi parada/removida; app principal mantém 60 minutos. |
| `git diff --check` | Sem erros de whitespace na revisão da correção; verificação final no fecho. |

Comandos exatos para repetir os reinícios (reiniciam explicitamente app; não usar durante outra suite sobre a mesma instância):

```powershell
. ./scripts/Load-LocalEnv.ps1
$env:PLAYWRIGHT_CHANNEL = 'chrome'
$env:MATRIZ_BASE_URL = 'http://127.0.0.1:8080'
$env:MATRIZ_RESTART_TEST = '1'
$env:MATRIZ_TASK_RESTART_TEST = '1'
$env:MATRIZ_COLLAB_RESTART_TEST = '1'
npm.cmd --prefix e2e test -- --grep restart --reporter=line --output=../tmp/e2e-restarts
```

Expiração: criada com `docker compose run -d --no-deps --name matriz-session-verification -p 127.0.0.1:18081:8080 app --server.servlet.session.timeout=1m --matriz.security.allowed-origins=http://127.0.0.1:18081`; confirmado healthy por docker inspect. No shell do teste: MATRIZ_BASE_URL=http://127.0.0.1:18081 e MATRIZ_SHORT_SESSION_TEST=1. No fim: `docker stop matriz-session-verification` e `docker rm matriz-session-verification`, sem volumes. A política real não foi reduzida. O comando de reinícios com alternativa regex contendo `|` falhou na interpretação por npm.cmd/cmd no Windows; substituído por `--grep restart`, que seleciona os três testes. Essa falha instrumental não conta como teste aprovado.

### Comandos de referência da suite normal e da conferência final

```powershell
. ./scripts/Load-LocalEnv.ps1
$env:PLAYWRIGHT_CHANNEL = 'chrome'
npm.cmd --prefix e2e test

docker compose exec -T db psql -U matriz_demo -d matriz -c "SELECT count(*) AS utilizadores FROM utilizador; SELECT count(*) AS projetos FROM projeto; SELECT count(*) AS tarefas FROM tarefa; SELECT count(*) AS comentarios FROM comentario; SELECT count(*) AS atribuicoes FROM atribuicao_tarefa; SELECT version, success FROM flyway_schema_history;"
docker compose ps
```

Validação documental adicional: OpenAPIV31SpecValidator confirmou OpenAPI 0.7.0 válido. Contrato, backend e migrations sem alteração neste exercício. A verificação não introduziu funcionalidades novas.

## BUG-08-01 — Detalhe da tarefa perde seleção ao recarregar dentro do projeto

- **Reprodução:** criar projeto/tarefa na UI; abrir tarefa no detalhe do projeto; associar responsável e adicionar comentário; recarregar. Primeiro teste falhou ao procurar o comentário no detalhe.
- **Esperado:** restaurar o detalhe aberto e voltar a consultar os dados persistidos.
- **Observado:** a interface regressava à lista de tarefas do projeto; o detalhe não era reconstruído. [Captura anterior à correção](ui/bug-08-task-reload-before.png).
- **Causa confirmada no código:** em TasksWorkspace, id inicial era null quando projectId existia, a navegação de tarefa era apenas setId local e o listener de hash era ignorado para esse contexto. ProjectsWorkspace interpretava todo o sufixo do endereço como ID do projeto. Não foi demonstrada corrupção/perda de dados em PostgreSQL; a falha era de navegação/seleção.
- **Diagnóstico e proposta apresentados ao utilizador antes de modificar a aplicação.** Correção limitada a ProjectsWorkspace.tsx e TasksWorkspace.tsx: endereço `#projetos/{projetoId}/tarefas/{tarefaId}`, parsing separado e restauro da seleção, pai mantém contexto ao mudar tarefa, navegação explícita do projeto refresca os dados. Tarefa de outro projeto não é apresentada no contexto errado. API, regras de negócio, segurança e esquema não alterados.
- **Regressão:** terceiro teste novo verifica URL, reload, lista/voltar no browser, edição do projeto a partir do detalhe da tarefa e consulta de projeto/tarefa/comentário/responsável por outra sessão; passou na repetição. Suite normal completa passou 24/24 aplicáveis. Prioridade média, corrigido.

## Testes automatizados versus verificações manuais

75 testes backend e cenários Playwright são automatizados; pedidos diretos à API, consultas de contagens e inspeção de relatórios são verificações técnicas executadas por ferramentas. Não foram feitas verificações funcionais manuais por um humano neste exercício: **Não executado**, sem as contabilizar como aprovadas. Os passos da matriz servem para uma eventual repetição manual. Capturas/lint/revisão de código não substituem os testes funcionais.

## Integridade, conclusão e limites

Contagens iniciais: 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições. Testes browser criam e removem somente os seus IDs; TRUNCATE/triggers/falhas de SQL apenas em bases Testcontainers descartáveis. Conferência final: as mesmas contagens iniciais, app/db healthy e apenas Flyway V1/V2 success=true. A instância temporária de expiração já foi removida. Sem novas migrations ou funcionalidades acrescentadas; correção de navegação limitada ao bug reproduzido.

Os resultados sustentam o comportamento aprovado nos nove cenários e a integridade/persistência testadas, incluindo reinício. Estado final: 9 Aprovados, 0 Falhou e 0 Não executado na matriz automatizada; testes funcionais manuais humanos continuam Não executados. Permanecem fora desta conclusão: CI, ensaio de cópia limpa/noutro computador, apresentação/entrega, auditoria WCAG, cobertura universal de browsers e ensaios de desempenho/carga. Limitações conhecidas mantidas: sem paginação/ETag e pesquisa/contagens no conjunto autorizado em memória. Sem bugs funcionais adicionais confirmados nesta execução; isto não prova ausência de qualquer defeito.


<details>
<summary>Histórico preservado — verificações dos exercícios anteriores</summary>

﻿# Matriz — Plano e evidências de verificação

**Data:** 18/11/2026. **Estado:** base técnica, acesso e gestão de projetos (Exercícios 2–4) verificados. Tarefas, colaboração de escrita e restantes fluxos do MVP ainda não concluídos. As secções por exercício preservam a sequência de evidências.

| Caso | Resultado esperado | Resultado observado | Evidência |
|---|---|---|---|
| V01 Acesso | Credenciais válidas permitem sessão, inválidas são rejeitadas; perfil e logout funcionam. | Passou no Exercício 3, API e Chrome. | SecurityTest e e2e/tests/auth.spec.ts. |
| V02 Projetos | Criar/pesquisar/editar/eliminar respeita propriedade e cascata. | Passou no Exercício 4: API/UI, arquivo/restauro, pesquisa e cascata/rollback. | ProjectCrudTest, projects.spec.ts e OpenAPI. |
| V03 Persistência | Tarefa surge sem recarregar e mantém-se após reinício. | Projetos passaram em reload/reinício; fluxo de tarefas ainda não executado. | projects.spec.ts @restart; tarefas pendentes. |
| V04 Validação | Vazio/só espaços e valores inválidos rejeitados no backend. | Passou para projetos no Exercício 4; DTOs de tarefas ainda pendentes. | ProjectCrudTest e formulário no Chrome. |
| V05 Estados/filtros | Alterações atualizam tarefa, filtros, progresso e contagens. | Não executado. | Pendente. |
| V06 Responsáveis | Múltiplos elegíveis, sem duplicação; remoção e limpeza atómicas. | Não executado. | Pendente. |
| V07 Comentários | Conteúdo, autor e data confiáveis persistem e são autorizados. | Não executado. | Pendente. |
| V08 Autorização | Segundo utilizador não acede a dados alheios por IDs manipulados; tarefas pessoais privadas. | Passou nas consultas e nas escritas de projetos; restantes escritas futuras ainda pendentes. | SecurityTest e Playwright; limites em security.md. |
| V09 Eliminação/erro | Cascata sem órfãos, cancelar sem pedido, erro sem falso sucesso/perda do formulário. | Passou para projetos, incluindo rollback por falha de BD; gestão autónoma de tarefas pendente. | ProjectCrudTest e projects.spec.ts. |
| V10 Sessão/CSRF | Cookie, logout, invalidação e proteção de operações de escrita. | Passou em login/logout, expiração/reinício e flags do cookie; escritas de negócio ainda pendentes. | SecurityTest, Playwright, test:restart e perfil prod local. |

## Testes automatizados mínimos da entrega — estado atual

1. Regra de validação: nome obrigatório/não só espaços passou nos projetos (Exercício 4); título de tarefa ainda pendente.
2. Autorização: dois utilizadores e recursos não acessíveis verificados em leitura e CRUD de projetos.
3. Criação/consulta posterior: projeto com PostgreSQL real, nova sessão, reload e reinício verificados no Exercício 4.

Registar comandos, ambiente, resultados e limitações reais. Não substituir testes de aplicação por validação de OpenAPI. Acrescentar regressão para bugs reproduzidos e corrigidos.

## Exemplos de Human-in-the-Loop já observados

| Objetivo da IA | Decisão do utilizador/programador | Verificação |
|---|---|---|
| Definir âmbito inicial | Aprovou uso individual; depois autorizou reconciliação colaborativa em COL-01. | Matriz de diferenças e atualização documental; não equivale a teste de execução. |
| Modelar associação Projeto/Tarefa | Aprovou tarefas independentes e correção da relação não identificadora. | Revisão semântica e renderização Mermaid. |
| Preparar API | Proposta inicial sem autenticação foi superada pelo novo âmbito; utilizador aprovou sessão/cookie. | Revisão do contrato e validação automática; autorização real ainda por testar. |

## Verificações documentais

A execução e resultados da validação de OpenAPI, referências e imagens ficam registados no log da ficha 3 após serem efetivamente executados. O registo seguinte corresponde à fase documental anterior à implementação. Os comandos reais da base técnica foram acrescentados no Exercício 2 ao README.

### Resultado da revisão documental — 18/11/2026

OpenAPI 3.1 validado: 24 operações e 12 exemplos válidos; schemas rejeitam campos de identidade indevidos e entradas obrigatórias inválidas. 22 histórias/88 critérios únicos; fontes ER sincronizadas e imagens ER/login/tarefas inspecionadas. Referências locais válidas e git diff --check sem erros. Ferramentas de validação isoladas em tmp, sem dependências da aplicação adicionadas. Todos os cenários de execução V01–V10 continuam Não executado.

## Exercício 2 — evidências de execução, 18/11/2026

Ambiente: Windows 11/PowerShell, JDK Temurin 21.0.9, Maven 3.9.16 (incluindo Wrapper), Node 24.16.0, Docker Desktop Engine 29.1.3/Linux. A imagem final utiliza Java 21.0.13 e PostgreSQL 17.9. As versões transitivas são fixadas pelo parent/lockfile; imagens identificadas por digest no Dockerfile/Compose.

| Verificação | Comando / evidência | Observado |
|---|---|---|
| Compilação e testes Java | `./backend/mvnw.cmd -B -ntp -f backend/pom.xml verify` | BUILD SUCCESS; 9 testes, 0 falhas, 0 erros, 0 ignorados. PostgreSQL real descartável via Testcontainers. |
| Wrapper | `./backend/mvnw.cmd -f backend/pom.xml validate` e `mvnw.cmd --version` | BUILD SUCCESS; Maven 3.9.16/Java 21; comando da raiz funcional. |
| Dependências frontend | `npm.cmd install`, seguido de `npm ci` no build Docker | Lockfile gerado e instalação Linux reproduzida; auditoria da instalação: 0 vulnerabilidades reportadas nessa execução. Não constitui auditoria integral de segurança. |
| Frontend | `npm.cmd run lint` e `npm.cmd run build` em frontend | Sem erros/avisos na versão final; TypeScript/Vite compilam a página técnica. |
| Compose | `docker compose config --quiet` | Configuração válida, sem imprimir valores resolvidos. |
| Arranque nativo | `. ./scripts/Load-LocalEnv.ps1` e `java -jar backend/target/matriz-0.1.0-SNAPSHOT.jar` | Conexão JDBC, V1 aplicada numa BD vazia, Hibernate valida seis entidades; health UP. |
| Ausência de seed no arranque | Consulta contagem de utilizador após primeiro arranque normal | 0 utilizadores; histórico Flyway version=1/success=true. |
| Seed no host | Mesmo JAR com `--matriz.demo.seed-only=true` | Termina com código 0; 2 utilizadores, 2 projetos, 3 tarefas, 1 colaboração, 2 atribuições, 1 comentário. |
| Build integrado | `docker compose build app seed` | Imagens construídas; React compilado copiado para static e servido pelo Spring Boot. target/node_modules do host excluídos do contexto. Testes são saltados nesta etapa de empacotamento, tendo sido executados separadamente. |
| Seed repetido na imagem | `docker compose --profile tools run --rm seed` | Termina com código 0; mantém as mesmas contagens, sem duplicar fixtures. |
| App integrada | `docker compose up -d app --wait` | App e BD healthy, execução da app como utilizador não root. |
| HTTP integrado | GET / e dois assets (/assets/*.js e *.css) | HTTP 200 para página, JavaScript e CSS. Verificação HTTP, não teste visual/E2E do browser. |
| Persistência no reinício | `docker compose restart db app`; `docker compose up -d db app --wait` | Antes/depois: 2/2/3/1/2/1 nas seis tabelas; health UP. O container da BD também foi recriado ao fixar o digest, conservando o volume e os dados. |
| Exclusão Git | `git check-ignore .env backend/target frontend/node_modules frontend/dist` | Todos ignorados. Configuração versionável só com valores fictícios. |

### Testes de InfrastructureTest

1. Migration versionada aplicada uma vez, reexecução sem migrations pendentes e sem seed automático.
2. Seis entidades, fixtures com acessos distintos, tarefa pessoal e hash BCrypt verificável.
3. Seed repetido conserva título editado, comentário/colaboração removidos e hash original.
4. Colisão de email na inicialização faz rollback e conserva o registo anterior.
5. CHECKs de espaços/enumerações, FK e unicidade de atribuição rejeitam dados inválidos.
6. Cascata de projeto elimina só os dependentes e conserva contas, outro projeto e tarefa pessoal.
7. Gravação/consulta SQL conserva data passada e campos opcionais.
8. MockMvc: health UP sem detalhes; pedidos GET/POST de negócio bloqueados. Não comprova login ou autorização por recurso.

9. Regressão de colisão de ID de projeto: rollback conserva proprietário/dados e não concede participação nem deixa contas fictícias parciais.

Relatório gerado: backend/target/surefire-reports/pt.matriz.InfrastructureTest.txt (não versionado); resumo manual de smoke: tmp/verification/foundation-smoke.json (não versionado). Os resultados observados estão preservados nesta tabela.

### Revisão crítica e limites

- Na revisão crítica foi removida a tolerância a colisões de IDs na primeira criação de fixtures; a transação passa a falhar em vez de reutilizar um projeto existente. Teste de regressão acrescentado; versão final com nove testes passou.
- O build preliminar com dependency:go-offline foi interrompido por demorar no descarregamento de dependências desnecessárias. O Dockerfile final resolve apenas o build necessário, com cache BuildKit; a construção final passou.
- O aviso de Fast Refresh inicial foi resolvido separando App.tsx de main.tsx. ESLint foi atualizado para uma versão suportada compatível com os plugins; a versão final passou sem avisos.
- JUnit/Mockito em Java 21 emitiu aviso sobre carregamento dinâmico de agente; não provocou falhas. Rever configuração de agente numa futura atualização de JDK/testes.
- Não houve teste de clonagem noutro computador; houve build Linux em containers sem artefactos de build do host. Não declarar estes resultados como equivalentes.
- Sem login, autorização por recurso, CRUD, limpeza COL-06, regras de arquivo, UI funcional, E2E ou pipeline CI implementados. V01–V10 mantêm o estado não executado de ponta a ponta.
- As restrições SQL não substituem validação dos DTOs nem políticas de acesso. Os três testes funcionais mínimos da entrega continuam por concluir nos fluxos reais, apesar dos nove testes de infraestrutura aprovados.

## Exercício 3 — acesso e segurança, 18/11/2026

**Política humana SEG-01:** 60 minutos de inatividade, sem lembrar-me; reinício do backend e logout exigem novo login. A proposta inicial de 30 minutos foi rejeitada. A configuração efetiva de 60 minutos foi verificada no ServletContext de um Tomcat real; não foi substituída pela configuração curta de teste.

| Verificação | Comando / evidência | Resultado observado |
|---|---|---|
| Backend completo | `./backend/mvnw.cmd -B -ntp -f backend/pom.xml verify` | BUILD SUCCESS; **21 testes**, 0 falhas, 0 erros, 0 ignorados: 9 de infraestrutura e 12 de segurança. PostgreSQL real descartável. |
| Identidade e credenciais | SecurityTest | Login válido roda ID da sessão; principal autenticado fica sem password/credenciais. Perfil apenas id/nome/email. Email inexistente e palavra-passe errada: mesma resposta 401. |
| CSRF/origem | SecurityTest e Playwright | Login/logout exigem CSRF; token de outra sessão ou anterior ao login é recusado; origem hostil é recusada mesmo com CSRF correto. |
| Logout e ausência de sessão | SecurityTest/Chrome | Sessão invalidada, cookie expirado, perfil 401 após logout. Cookies ausentes/desconhecidos não autorizam consultas. |
| Acesso aos recursos | SecurityTest/Chrome | Proprietário/colaborador leem projeto partilhado; Ana não lê projeto privado do Bruno; Bruno não lê tarefa pessoal da Ana/comentários/responsáveis. |
| Permissões atuais | SecurityTest | Remover participação revoga novas leituras sem novo login; criador histórico/atribuição isolada não concedem acesso. Guardas de proprietário, arquivo, destino e elegibilidade testadas. |
| Identidade forjada e validação | SecurityTest | Campo extra proprietarioId no login rejeitado; utilizadorId em query não troca o perfil; email/formato/UUID inválidos recusados; recurso inexistente 404. |
| Política efetiva | SecurityTest com servidor Servlet real | Timeout=60 minutos, tracking apenas COOKIE, nome matriz_session e HttpOnly verificados. |
| Frontend | `npm.cmd --prefix frontend run lint` e `npm.cmd --prefix frontend run build` | Sem erros/avisos; build concluído. Interface de login/perfil/logout, sem mocks ou armazenamento persistente de tokens. |
| Browser normal | `npm.cmd --prefix e2e test`, Chrome headless | **5 passaram; 1 ignorado por desenho** (timeout curto). Login, reload, logout, erro, duas identidades, privacidade, CSRF/origem e mensagem após revogação. |
| Expiração real | `npm.cmd --prefix e2e run test:expiry` com MATRIZ_SHORT_SESSION_TEST=1 e baseURL 18081 | **1 passou**: instância temporária com timeout=1m, 65 segundos sem pedidos; Atualizar perfil recebe 401 e mostra login com mensagem. Não foram esperados 60 minutos na instância principal. |
| Reinício do backend | `npm.cmd --prefix e2e run test:restart` | Login antes=200; perfil com a sessão anterior depois do reinício=401. BD/volume mantidos. |
| Cookie real no browser | Playwright | HttpOnly=true, SameSite=Lax, cookie de sessão sem expiração persistente; ID muda ao autenticar. Sem localStorage/sessionStorage. |
| Cookie do perfil prod | GET /api/sessao/csrf em container temporário com perfil prod | Secure=true, HttpOnly=true, SameSite=Lax. Verificação de cabeçalho local; não houve implantação/TLS público. |
| Build e execução | `docker compose build app seed`; `docker compose up -d app --wait` | App/BD healthy; frontend e backend integrados. V1 não alterada. |
| Contrato | OpenAPI 3.1 + JSON Schema com formatos | Especificação válida; 24 operações, 8 implementadas e 16 planeadas. Servidor /api, 12 exemplos válidos e 7 respostas JSON reais validadas contra os schemas; logout 204. |

### Testes de segurança (12)

1. Timeout real do Servlet, cookie nome/HttpOnly e tracking apenas por cookie.
2. Login, rotação da sessão, remoção de credenciais/hash do principal e perfil seguro.
3. Credenciais erradas e conta inexistente com a mesma mensagem genérica.
4. CSRF obrigatório no login e não transferível entre sessões.
5. Token CSRF pré-login inválido após autenticação; logout revoga sessão e expira cookie.
6. Sem sessão ou cookie desconhecido: todas as consultas protegidas recusadas.
7. Dois utilizadores, projetos com acessos distintos, tarefa pessoal e dependentes privados.
8. Revogação imediata de participação, mesmo com autoria histórica/atribuição existente.
9. Políticas de proprietário/colaborador, arquivo, elegibilidade pessoal e destino de transferência.
10. Identidade extra rejeitada, perfil não alterável por query, JSON/campos/UUIDs e recurso inexistente.
11. Origem hostil recusada com CSRF válido.
12. Escritas futuras permanecem bloqueadas mesmo para utilizador autenticado com CSRF.

### Revisão crítica e limites do exercício

- Os oito endpoints implementados estão enumerados em security.md e no contrato. Não há criação/edição/eliminação funcional de projetos/tarefas, colaboração de escrita, limpeza COL-06, painel ou vistas completas.
- Inferência de proprietário/criador/autor é uma obrigação dos casos de uso de criação futuros. CurrentIdentity já fornece a identidade confiável; não se afirma teste de uma escrita ainda inexistente. Responsável selecionado não é confundido com ator autenticado.
- Guardas por recurso foram testadas e aplicadas às leituras; cada nova escrita exige aplicação transacional e respetivos testes, incluindo concorrência/limpeza de atribuições.
- O teste real de expiração usa configuração curta isolada; o teste de servidor real verifica a política de 60 minutos. O teste de restart demonstra a perda da autenticação, sem apagar dados.
- Cookies Secure foram verificados no perfil prod, mas HTTPS/reverse proxy/certificados e implantação pública não foram executados. Sem rate limiting, monitorização ou gestão global de contas no MVP atual.
- A interface trata 401 nos fluxos existentes; os próximos ecrãs devem reutilizar essa política. Não há reenvio automático de escritas, polling de sessão ou lembrar-me.
- Traces/vídeo de rede desativados. Relatórios e screenshots de falha ficam ignorados pelo Git. Apenas contas e dados fictícios utilizados.

## Exercício 4 — gestão de projetos, 18/11/2026

**PESQ-01:** o utilizador rejeitou pesquisa que distingue acentos e confirmou correspondência por parte do nome, ignorando maiúsculas e acentos. Sem paginação e sem copiar máximos dos exemplos para o contrato.

| Verificação | Evidência | Resultado observado |
|---|---|---|
| Backend completo | `./backend/mvnw.cmd -B -ntp -f backend/pom.xml verify` | BUILD SUCCESS: **33 testes**, 0 falhas/erros/ignorados (9 infraestrutura + 12 segurança + 12 projetos). |
| Criação/persistência | ProjectCrudTest | 201/Location/DTO; proprietário da sessão; arquivado=false; consulta noutra sessão conserva dados; nome de 1 carácter e textos acima de 100/1000 aceites. |
| Validação/identidade | ProjectCrudTest | Nome vazio/branco/null/tipo errado e descrição inválida 422; JSON/campos extra de identidade 400; sem dados alterados. |
| PATCH | ProjectCrudTest | Omitidos conservam; null limpa descrição; vazio de descrição aceite; booleanos/null/nome inválidos recusados; proprietário imutável. |
| Listagem/pesquisa | ProjectCrudTest/Chrome | Só acessíveis, sem duplicação de proprietário/colaborador; revogação imediata. APRESENTACAO encontra apresentação; %, _ e aspas são literais. Arquivo true/false estrito; parâmetros inválidos 400. |
| Permissões/CSRF | ProjectCrudTest | Colaborador/não membro não editam/eliminam; todas as escritas exigem CSRF; ausência de sessão recusada. |
| Arquivo/restauro | ProjectCrudTest/Chrome | Preserva tarefas/dependentes; alterna listas ativa/arquivo; metadados conservados. |
| Cascata | ProjectCrudTest | Remove só projeto/tarefas/comentários/atribuições/colaborações relacionados; conserva contas, outro projeto e tarefa pessoal. Seed explícito não faz reaparecer projeto eliminado. |
| Falha real de cascata | Trigger de falha apenas no PostgreSQL descartável | DELETE devolve 500 genérico; todas as seis tabelas conservam contagens originais, sem eliminação parcial ou detalhes SQL no erro. Trigger/função de teste removidos. |
| Frontend | lint + TypeScript/Vite build | Passaram sem avisos na versão final. Ajustado cleanup do ref do dialog e navegação para evitar estado de carregamento sem novo pedido ao mudar hash. |
| Browser normal | `npm.cmd --prefix e2e test`, Chrome headless | **10 passaram; 2 ignorados por desenho** (expiração curta e reinício separados). Inclui 5 de acesso e 5 de projetos. |
| Eliminação UI | projects.spec.ts | Dialog explica cascata; Cancelar envia 0 DELETE, confirmar envia 1; 204 e ausência posterior. |
| Falha de comunicação | projects.spec.ts com abort de POST no teste | Mensagem de comunicação em português; formulário conserva nome/descrição, não anuncia sucesso nem reenvia; corrigida a causa, guardar passa e cria apenas o registo esperado. A app não contém mocks. |
| Percurso vertical com reinício | MATRIZ_RESTART_TEST=1 + `npm.cmd --prefix e2e run test:projects-restart` | **1 passou**: login/criação/reload/reinício real de app/novo login/mesmo projeto e descrição. Sem intervenção SQL manual para o fluxo. |
| Contrato | OpenAPI 3.1 e JSON Schema | Especificação válida: 24 operações, 12 implementadas. Cinco respostas reais de projetos/erro validadas contra schemas; DELETE 204. |
| Conservação de demonstração | Contagens SQL após cleanup dos testes | 2 utilizadores, 2 projetos, 3 tarefas e 1 comentário originais mantidos. Cleanup só dos IDs criados pelos testes. |
| Build integrado | Docker Compose build/up | App e PostgreSQL healthy, React servido pelo Spring Boot, sem alterar V1 ou apagar volume. |

### ProjectCrudTest — 12 casos

1. Criação infere proprietário, Location e consulta persistida com nova sessão.
2. Não impõe máximos pedagógicos nem trim de texto válido.
3. Corpos inválidos/campos proibidos sem alteração de dados.
4. PATCH/omissão/null, tipos estritos e proprietário imutável.
5. Listagem autorizada, sem duplicação e revogação de colaboração.
6. Pesquisa parcial sem acentos/maiúsculas, wildcards/aspas literais e vazio.
7. Arquivo/restauro e filtro booleano estrito, conservando dependentes.
8. Colaborador e utilizador alheio não editam/eliminam.
9. CSRF em todas as escritas e proteção de pedidos não autenticados.
10. Cascata sem órfãos, sem apagar contas/outros projetos/tarefa pessoal e sem restauro pelo seed.
11. Falha real de cascata faz rollback integral e não expõe erro de BD.
12. IDs malformados/inexistentes não alteram dados.

### Revisão, correções e limites

O primeiro comando normal de browser teve 1 falha num seletor de teste demasiado amplo: havia dois role=status (sucesso e carregamento) após DELETE. Corrigido o seletor para a mensagem exata; repetição passou 10/10 cenários normais. Não foi necessário alterar a API ou eliminar dados para ultrapassar essa falha.

O teste de reinício é explícito/separado por alterar o estado de app durante a execução. O de expiração curta foi executado no Exercício 3 e não foi repetido neste incremento, cuja política de sessão não mudou. Não somar testes ignorados como passados. Relatórios JUnit/Playwright são gerados fora do Git; resumo de resultados preservado aqui.

Permanecem limitações documentadas: sem paginação/garantias para grandes volumes; pesquisa normalizada no conjunto autorizado em memória; sem versão/ETag para rejeitar edições baseadas em dados antigos; só operações de projeto concluídas. Gestão de tarefas/colaboração de escrita/limpeza COL-06, vistas, painel e CI/entrega integral continuam pendentes. Rastreabilidade dos commits e publicação dos exercícios 3/4 registada nas issues #4/#5 e no checkpoint do log.

## Verificação da separação Git — 18/11/2026

A pedido do utilizador, o acesso e os projetos foram separados em dois commits. A versão intermédia de acesso foi materializada numa pasta isolada a partir da base do Exercício 2, sem alterar os ficheiros finais: 21 testes backend passaram, lint/build do frontend passaram e OpenAPI com oito operações implementadas foi validado. Os links locais dessa versão também foram verificados. O commit 0c1351e referencia #4.

As fontes finais foram comparadas por SHA-256 com a cópia anterior à separação e conservaram o conteúdo verificado no Exercício 4 (33 testes backend e cenários de browser documentados). O commit seguinte contém apenas a evolução de projetos/documentação, com Refs #5. Nenhum teste foi apresentado como execução nova da versão final nesta operação de Git; usou-se a evidência existente e a igualdade dos ficheiros de código. Credenciais/padrões e ficheiros gerados foram revistos antes dos commits. A confirmação de publicação é registada nas issues após o push.


## Exercício 5 — Gestão de tarefas — 18/11/2026

### Resultados executados

| Verificação | Esperado | Observado |
|---|---|---|
| backend/mvnw.cmd -f backend/pom.xml verify | Regressão e casos de tarefas com PostgreSQL descartável. | 50 testes, 0 falhas/erros/ignorados: InfrastructureTest 9, SecurityTest 12, ProjectCrudTest 12, TaskCrudTest 16, TaskMigrationTest 1. |
| npm.cmd --prefix frontend run lint / run build | ESLint, TypeScript e bundle válidos. | Ambos código 0. |
| docker compose build app seed | Imagens com frontend/backend e V2. | Build concluído. Seed não executado para repor dados. |
| docker compose up -d app --wait | Aplicar V2 sem apagar o volume. | app/db saudáveis; Flyway V1 e V2 success=true. |
| npm.cmd --prefix e2e test, Chrome | Regressão e novos percursos, com fixtures próprias. | 14 passaram; 3 ignorados por desenho (expiração/reinício de projetos/reinício de tarefas). |
| MATRIZ_TASK_RESTART_TEST=1, npm.cmd --prefix e2e run test:tasks-restart | Tarefa persiste, sessão antiga invalida-se e novo login consulta campos. | 1 passou; estado IN_PROGRESS, prioridade HIGH e prazo 2026-11-19 conservados após reinício. |
| OpenAPIV31SpecValidator + JSON Schema 2020-12 | Contrato válido; exemplos e respostas reais conformes. | OpenAPI 0.5.0 válido, 5 exemplos válidos; listagem real da Ana e os seus 2 detalhes autorizados válidos. 16/24 operações implementadas. |
| Contagens da demonstração antes/depois | Preservar tarefas preexistentes e nulls, sem resíduos dos testes. | Antes: 3 tarefas, 3 prioridades null, V1. Depois: 3 tarefas TODO, 3 prioridades null, 1 comentário e 2 atribuições; V1/V2 aplicadas. |

O comando de validação local da API precisou de execução autorizada fora do sandbox devido a WinError 10013; repetido com sucesso. Docker também precisou de acesso ao motor local. Não foi necessário desativar segurança da aplicação.

### Cobertura backend acrescentada

TaskCrudTest: criação/defaults/Location e leitura noutra sessão; privacidade pessoal; validações estritas/sem máximos arbitrários; omissão/null em PATCH; nove transições livres e progresso; listagem autorizada/revogação; filtros combinados literais/sem acentos e datas inclusivas; filtros inválidos; referências/acesso/arquivo; transferência e limpeza de atribuições; desassociação com resposta JSON literal null; rollback numa falha induzida da limpeza; cascata sem apagar projeto/contas; CSRF/ausência de sessão/IDs inexistentes.

TaskMigrationTest: cria V1 numa BD descartável, insere 20 combinações de quatro estados antigos por quatro prioridades mais null, e uma tarefa pessoal adicional. Conserva IDs/relações/criador/autoria/comentário/atribuições, verifica o mapeamento humano, nulls, defaults novos, CHECKs e segunda execução sem reaplicar V2. Não usa o volume da demonstração.

### Percursos reais de navegador acrescentados

1. Criar tarefa pessoal (TODO/MEDIUM), pesquisar APRESENTACAO, filtrar Sem projeto, editar para DONE/Sem prioridade, reload, cancelar eliminação sem DELETE e confirmar com um só DELETE.
2. Criar projeto/tarefa e concluir; progresso muda para 100% na interface sem reload.
3. Bruno desassocia tarefa criada pela Ana no projeto partilhado: PATCH 200 com JSON null, mensagem compreensível, tarefa desaparece e GET de Bruno passa a 403; Ana mantém acesso.
4. Título só com espaços não envia pedido. Falha de comunicação conserva título/descrição, sem falso sucesso; nova tentativa explícita guarda a tarefa.
5. Percurso separado de reinício e reautenticação, descrito na tabela.

Os testes E2E removem apenas os IDs próprios registados; não eliminam tarefas/projetos do seed ou dados arbitrários. O teste de expiração curta e o reinício específico de projetos não foram repetidos neste incremento: a política de sessão não mudou, o teste normal de projetos passou e o reinício foi exercitado com tarefas. Não somar ignorados como passados.

### Limites atuais

Ainda não concluídos: gestão de colaboradores, escrita de comentários/responsáveis, Kanban/calendário, painel integral/PD-06, CI e validação de entrega noutro computador. Sem paginação, sem desempenho medido para grandes volumes, pesquisa normalizada em memória no conjunto autorizado e sem ETag para edições com formulário antigo. Não foi publicada a aplicação na Internet. O registo Git foi posteriormente autorizado; a confirmação de publicação fica na issue #6.


## Exercício 6 — Responsáveis e comentários — 18/11/2026

Plano em task-collaboration.md aprovado pelo utilizador, incluindo COM-01. Issue #7 aberta antes da implementação. Sem novas tabelas, índices ou migrations; V1/V2 preservadas e seed não repetido.

| Incremento/verificação | Resultado esperado | Resultado observado |
|---|---|---|
| 1: AssignmentTest | Múltiplos/remoção individual, existência/elegibilidade, privacidade, arquivo, CSRF e concorrência. | 8 passaram, 0 falhas/erros/ignorados; pedidos concorrentes dão 201/409 e uma só atribuição. |
| 2: frontend lint/build e browser de responsáveis | UI real, seleção limitada, erros sem falso sucesso, reload. | Lint/build passaram; 2 testes Chrome passaram após corrigir nomes dos seletores para Ana Demo/Bruno Demo. |
| 3: CommentTest + AssignmentTest + SecurityTest | COM-01, autor/data, permissões, ordem e regressão. | 28 passaram (8+8+12), 0 falhas/erros/ignorados. |
| 4: backend verify completo | Regressão de infraestrutura/segurança/projetos/tarefas/migração/colaboração. | 66 passaram, 0 falhas/erros/ignorados. |
| 4: frontend lint/build e imagem integrada | ESLint/TypeScript/bundle e Docker válidos. | Código 0; app/db healthy. |
| 4: npm.cmd --prefix e2e test (Chrome) | Regressão normal, incluindo interface de comentários e arquivo. | 18 passaram; 4 ignorados por desenho (expiração e três percursos de reinício separados). |
| OpenAPI 0.6.0/JSON Schema | Contrato válido; requests/responses conformes. | 20/24 operações implementadas; 5 exemplos válidos; COM-01 validada com fronteiras Unicode; 7 respostas reais de colaboração válidas em fixture própria, DELETE atribuição 204 e cleanup concluído. Listagem/detalhes de tarefas continuam válidos. |
| Reinício de colaboração | Dois responsáveis e comentário conservados após restart e novo login. | 1 passou (18,1 s): sessão anterior 401, Atualizar perfil mostra login, novo login recupera os dois responsáveis e comentário com autor/data preservados. |

### Cobertura e observações

- AssignmentTest: listas elegíveis autorizadas e pessoais só do criador; múltiplos persistem noutra sessão; remover um não apaga o outro/contas/tarefa; duplicado=409; inexistente=404; inelegível/acesso cruzado/revogado=403; arquivo=409 nas escritas/leitura permitida; JSON estrito/campos forjados; CSRF; concorrência com duas sessões e um só par persistido.
- CommentTest: UUID/autor/instante do servidor e conteúdo preservado noutra sessão; 1/2000/2001 caracteres e emojis suplementares; vazio/espaços Unicode/null/tipos/campos forjados; acesso pessoal/privado/revogado, arquivo e CSRF; ordem cronológica com desempate por ID; leitura histórica acima do limite sem truncar; trigger de falha em BD descartável comprova ausência de gravação e erro sem detalhes SQL.
- Browser: associar dois como colaborador/remover um/reload; pessoal mostra só criador; falha preserva seleção; comentário só com espaços/2001 emojis não envia pedido; erro de rede preserva rascunho sem falso sucesso/reenvio; POST válido identifica autor/data; 2000 emojis aceites; HTML apresentado como texto sem criar imagem; reload conserva comentários; tarefa arquivada mantém leitura sem botões de escrita.
- Cada incremento teve testes e revisão de diff antes do seguinte. Revisões verificaram rotas específicas, CSRF, identidade do backend, lock projeto/tarefa, atualização após commit, ausência de novas entidades/migrations e respeito pelo âmbito. Diff final verificado com git diff --check.
- Correções instrumentais: seletores de nomes do seed; invocação Maven com lista de testes teve vírgulas sem aspas no PowerShell e foi repetida corretamente. O primeiro teste de reinício confirmou 401 mas ficou à espera do login ao navegar para o mesmo fragmento, sem atualizar o estado da interface; corrigido o percurso para Atualizar perfil, repetição passou. Estes erros não foram contabilizados como testes passados.
- Os testes usam apenas contas fictícias e fixtures próprias; falhas/rollback e TRUNCATE pertencem apenas às bases Testcontainers descartáveis. Nenhum reset do volume, truncagem de comentários históricos ou alteração do seed para fazer passar testes.

### Limites

Gestão de colaboradores (US-17-01/02) ainda não implementada; consulta de elegíveis (US-17-03/04), responsáveis (US-18) e comentários (US-19) cobertos neste incremento. Faltam painel/PD-06, Kanban/calendário, CI e ensaio de entrega noutro computador. Sem paginação ou metas de grande volume. COM-01 limita novos pedidos na API, não apaga/limita retroativamente os comentários históricos na BD. Sem edição/eliminação autónoma de comentários. Uma falha de rede após envio pode ter resultado desconhecido: consultar antes de repetir.

No fecho da implementação, o incremento estava local. O utilizador autorizou posteriormente commit/push; a confirmação de publicação fica na issue #7.


**Conferência final do Exercício 6:** dois utilizadores, dois projetos, três tarefas, um comentário e duas atribuições preexistentes conservados após cleanup; Flyway apenas V1/V2 success=true. Links locais dos ficheiros alterados válidos; diff sem erros de whitespace; ficheiros de ambiente/gerados e migrations fora do conjunto alterado. Apenas artefactos próprios criados nos testes foram removidos.


## Exercício 7 — Frontend e integração — 18/11/2026

Enunciado consultado: 10 - Ficha Prática 03.pdf, página 5. Plano em frontend-integration.md. Decisões humanas PAINEL-01 (pessoais + projetos ativos acessíveis, excluindo arquivados) e COL-07 (proprietário gere colaboradores mesmo arquivado) registadas antes da implementação. Issue #8 aberta antes do código.

| Verificação/incremento | Esperado | Observado |
|---|---|---|
| Colaboradores: ProjectCollaborationTest + SecurityTest | Proprietário, referências, duplicados, CSRF, arquivo, revogação/limpeza atómica e rollback. | 17 passaram (5+12), zero falhas/erros/ignorados. |
| Painel: DashboardTest | Contagens autorizadas, pessoais/arquivo, três estados, vazio, revogação e parâmetro explícito. | 4 passaram após corrigir parâmetro ausente de 500 para 400; teste de regressão mantido. |
| backend/mvnw.cmd -f backend/pom.xml verify | Regressão completa PostgreSQL descartável. | 75 testes, 0 falhas/erros/ignorados: 66 anteriores + 5 colaboradores + 4 painel. |
| frontend lint/build | ESLint, TypeScript e bundle válidos. | Código 0; aviso Fast Refresh inicial resolvido movendo a função utilitária para types.ts. |
| Docker build/up --wait | App integrada, sem apagar dados/volume. | Build concluído e app/db healthy. Seed não repetido; nenhuma migration nova. |
| npm.cmd --prefix e2e test (Chrome) | Regressão com ecrã inicial Painel e novos fluxos. | 21 passaram, 4 especiais ignorados por desenho (expiração e reinícios separados). |
| frontend-integration.spec.ts, após ajuste móvel | Painel, colaboradores em arquivo e vistas/filtros/prazo; legibilidade móvel. | 3 passaram; sem overflow horizontal a 360 px, agenda móvel visível com título completo. Não somar esta repetição como testes adicionais à suite de 21. |
| MATRIZ_RESTART_TEST=1, test:projects-restart | Navegação para detalhe e persistência após restart e novo login. | 1 passou (19,9 s); projeto/descrição conservados. |
| OpenAPIV31SpecValidator/JSON Schema | Contrato e respostas reais conformes. | OpenAPI 0.7.0 válido, 24 operações implementadas; 5 exemplos válidos, 5 respostas novas reais de painel/colaboradores conformes, DELETE colaborador/projeto 204. Fixture própria removida. Listagem/detalhes de tarefas também conformes. |
| Conferência da BD após cleanup | Dados originais conservados. | 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições; Flyway V1/V2 success=true, sem versões adicionais. |

### Percursos e revisão visual

1. Painel inicial corresponde à API; editar tarefa para DONE atualiza contagens ao voltar, arquivar retira projeto/tarefas, pessoal continua incluída. Painel não usa filtros da lista.
2. Ana adiciona Bruno numa fixture própria; Bruno acede mas não recebe controlos de gestão de colaboradores. Responsável e comentário criados; remoção de Bruno em arquivo revoga acesso e elimina só atribuição, preservando comentário/autoria. Nova adição em arquivo também funciona.
3. Três tarefas em três estados aparecem nas colunas corretas; tarefa sem prazo mantém-se no Kanban; filtro HIGH reutilizado no calendário; alterar prazo para outro mês e estado para DONE atualiza as vistas sem reload.
4. Capturas desktop e 360 px inspecionadas. A primeira captura móvel mostrou títulos comprimidos na grelha; ajustada a apresentação para grelha com contagens e agenda legível abaixo, mantendo os mesmos dados. Lint/build, imagem integrada e os três testes afetados repetidos com sucesso. Capturas finais em docs/ui/frontend-kanban-desktop.png e frontend-calendar-mobile.png.

### Revisão do diff e rastreabilidade

- Incremento 1 reutiliza o lock de projeto e transação com COL-06; contas/autoria preservadas. Incremento 2 usa snapshot REPEATABLE_READ entre perfil/projetos/tarefas; nova API não aceita identidade do cliente. Diff e validações revistos antes das vistas.
- Vistas reutilizam dados e filtros reais, sem endpoints/mocks próprios; datas sem conversão UTC. Layout reutiliza formulários, cliente /api, mensagens e proteção CSRF. Primeiro ecrã agora é Painel; o teste antigo de projetos foi atualizado para selecionar Projetos explicitamente, preservando deep links.
- Schema físico/stack/V1/V2 mantidos. Backend conta o conjunto autorizado completo, sem depender de quantos cartões/linhas são apresentados. Sem paginação aprovada, sem garantias novas de desempenho.
- Ficheiros de ambiente/gerados/temporários excluídos; capturas contêm apenas fixtures fictícias. Testes eliminam somente os próprios IDs, nunca o seed/volume. Não repetir as verificações especiais ignoradas como se tivessem passado: apenas o reinício de projetos foi executado neste incremento; os restantes têm evidência dos incrementos anteriores.

### Limites e próximos passos

A interface suporta as funcionalidades aprovadas, mas isto não declara encerrada a ficha 03 ou a entrega. Exercício 8: auditoria consolidada dos cenários da ficha e evidências. CI, cópia limpa/noutro computador e preparação de apresentação permanecem pendentes. Testados Chrome desktop e largura 360 px; não há auditoria WCAG, cobertura universal de browsers ou ensaios de grandes volumes. Pesquisa/painel sem paginação, contagens calculadas a partir do conjunto autorizado em memória e sem ETags para formulários antigos. Sem drag-and-drop no Kanban (não obrigatório), registo público ou exposição pública.

No fecho da implementação, o Exercício 7 estava local. O utilizador autorizou posteriormente commit/push; a confirmação de publicação fica na issue #8.

</details>

</details>


## Conferência operacional final do Exercício 9

Imagem principal reconstruída e app atualizada com as fontes verificadas; health healthy em 8080. Seed não repetido, nenhuma migration nova; conferência por leitura conservou 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições, apenas V1/V2. Containers/rede da cópia isolada removidos por down sem -v; volume separado conservado para inspeção, pasta de ensaio ignorada. Não houve nova execução dos 25 testes na app principal: a suite foi executada na cópia com fontes equivalentes, conforme descrito. No fecho da revisão local ainda não existia commit/push; o utilizador autorizou posteriormente o registo em 19/11/2026. Confirmação de publicação na issue #10.


## Ficha 04 — Exercício 2: testes unitários do título

**Data:** 19/11/2026. **Base Git:** 212a4f6 com plano/testes locais ainda sem commit. **Regra:** título textual obrigatório, não vazio nem só espaços; não há mínimo de três caracteres nem máximo aprovado. Fontes: requirements.md (Tarefas), CA-US-03-03, CA-US-04-04 e semântica PATCH de openapi.yaml. Unidade: TaskInput.create/patch, sem alterações de produção.

Ficheiro: `backend/src/test/java/pt/matriz/TaskInputTest.java`. JUnit Jupiter/Test parametrizado, AssertJ e ObjectMapper já disponíveis no projeto. Instanciação direta, sem Spring, MockMvc, PostgreSQL, Docker, rede ou serviços na execução dos testes. Casos independentes, Arrange/Act/Assert; exceções verificadas por tipo e prefixo do campo, sem fixar a mensagem completa.

| Caso | Plano / critério | Entrada | Esperado e observado | Execuções |
|---|---|---|---|---|
| UT-01 válido | T02, US-03 | titulo="Preparar apresentação" | Título aceite e preservado | 1 aprovada |
| UT-02 fronteira | T02, regra título não vazio | titulo="X" | Um carácter não branco aceite e preservado | 1 aprovada |
| UT-03 inválidos na criação | T01, CA-US-03-03 | Ausente, null, 12, vazio, três espaços | RequestValidationException identificando titulo, em cada variante | 5 aprovadas |
| UT-04 inválidos na edição | T03, CA-US-04-04 | null, vazio, três espaços | RequestValidationException identificando titulo, em cada variante | 3 aprovadas |
| UT-05 omissão na edição | T03, contrato PATCH | Apenas estado=DONE | hasTitle=false, titulo=null, hasState=true e estado=DONE | 1 aprovada |

Comando executado na raiz, PowerShell:

```powershell
./backend/mvnw.cmd -f backend/pom.xml -Dtest=TaskInputTest test
```

**Resultado real:** BUILD SUCCESS; Tests run: 11, Failures: 0, Errors: 0, Skipped: 0. Maven 3.9.16, Temurin JDK 21.0.9, Surefire 3.5.6/JUnit Platform; tempo total Maven 16,674 s, classe 0,884 s. Evidência local regenerável: backend/target/surefire-reports/pt.matriz.TaskInputTest.txt e TEST-pt.matriz.TaskInputTest.xml (gerados, fora do Git).

Não houve falhas de configuração, correções aos testes após execução ou defeito identificado na regra selecionada. Não foi necessário alterar produção, dependências ou serviços. O teste de omissão verifica a representação do PATCH, não persistência de campos: isso pertence à integração. O caso válido é fronteira inferior de conteúdo não vazio; não inventa máximo de comprimento. Não se testou equivalência de whitespace Unicode entre UI/backend.

Âmbito: apenas a classe selecionada foi executada. Não foram executados testes de integração/E2E nem a suite backend completa neste exercício. T01/T03 foram cobertos; T02 foi coberto quanto ao título válido/fronteira, sem novas asserções dos defaults/opcionais, já representadas nos testes de integração existentes. O resultado não aprova automaticamente toda a matriz ou o MVP.
