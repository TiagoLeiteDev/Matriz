> **Relatório histórico da Ficha 03.** O estado de CI/entrega técnica aqui datado de 18/11 foi posteriormente atualizado na Ficha 05. Consultar [verification.md](verification.md), [notas da candidata](release-notes.md) e [issue #20](https://github.com/TiagoLeiteDev/Matriz/issues/20). Preservam-se os resultados e limitações do ensaio original.

# Matriz — Revisão final e preparação da entrega

**Data:** 18/11/2026. **Exercício:** ficha 03, exercício 9. **Base publicada:** c9bcdcc. **Issue:** [#10](https://github.com/TiagoLeiteDev/Matriz/issues/10). Registo e publicação autorizados pelo utilizador em 19/11/2026; confirmação dos commits e do push na issue #10.

## Conclusão

O MVP executou a partir de uma cópia limpa isolada e a árvore final testada passou as verificações descritas abaixo. Os artefactos obrigatórios estão preparados para entrega local, com as ressalvas indicadas. **Não se declara a entrega integral concluída:** CI não implementada, validação noutro computador/apresentação/aprovação humana ainda não verificadas, com publicação desta revisão acompanhada na issue #10. Não há aprovação individual automática dos 88 critérios apenas por passar a suite.

## Âmbito e estado real do repositório

Inicialmente main em c9bcdcc, git status/diff limpos. Repositório único com frontend, backend, testes, contratos, migrations, configuração e documentação; issues por incremento. Fontes, esquema e regras comparados com os requisitos, 22 User Stories/88 critérios e decisões COL/TAR/PESQ/COM/SEG/PAINEL. A arquitetura modular e a stack foram preservadas.

A revisão não acrescentou funcionalidades. Mudanças de aplicação: uma refatoração aprovada de pesquisa e uma correção pontual de apresentação do progresso. Backend/controllers/DTOs/contrato/migrations não mudaram nas suas regras. A primeira extração automática de método falhou na compilação, foi corrigida e só a execução final passou; erro registado, não ocultado como sucesso.

## Inconsistências identificadas antes de atualizar documentos

| ID | Evidência anterior | Atualização realizada |
|---|---|---|
| DOC-01 | README incluía GitHub Actions entre tecnologias usadas, mas .github/workflows/ci.yml não existe. | Distinguir stack implementada de CI planeada; assinalar critério de pipeline não cumprido. |
| DOC-02 | README tinha legenda de verificações “ainda pendentes” e instruções dispersas por exercícios já concluídos. | README atual reorganizado por instalação/configuração/execução/testes/uso/entrega; histórico fica nos logs e documentos específicos. |
| DOC-03 | AGENTS acumulava regras históricas, incluindo colaboradores pendentes, apesar da implementação posterior. | Instruções ativas consolidadas, comandos reais, COL-07 e responsabilidades/transações atuais. |
| DOC-04 | Plano dizia que caminhos não existiam e API não estava implementada; qualidade “Não iniciado” apesar dos testes concluídos. | Tabela ativa atualizada e histórico preservado em secção recolhida; separar verificação local de CI/entrega pendentes. |
| DOC-05 | Modelo dizia que política de sessões/limites continuava pendente. | Referir decisões atuais SEG-01/TAR-01/COM-01, preservando as seis entidades e V1/V2. |
| DOC-06 | Não havia os três exemplos breves de IA pedidos para esta revisão. | Tabela real em verification.md, com objetivos, decisões, partes corrigidas/rejeitadas e resultados/fontes. |

## Única refatoração prioritária — aprovada e executada isoladamente

**Problema:** ProjectQueries e TaskQueries repetiam a mesma normalização de pesquisa. **Proposta:** shared/SearchText.fold, mantendo exatamente Normalizer.NFD, remoção de marcas e Locale.ROOT; os chamadores mantêm o tratamento anterior de null e a filtragem autorizada antes da pesquisa.

**Benefício:** um único ponto para a política PESQ-01/02, sem criar camadas/frameworks. **Risco:** divergência acidental em Unicode ou filtragem; mitigado pelo diff mínimo e testes reais de pesquisa/acesso existentes. **Aprovação:** o utilizador respondeu “Aprovar a refatoração isolada”. Primeiro passo alterou só ProjectQueries.java, TaskQueries.java e SearchText.java, antes das atualizações documentais/correção de UI. **Resultado:** 75 testes backend passaram após corrigir o fragmento deixado na primeira edição; pesquisa sem maiúsculas/acentos e caracteres literais voltou a passar na suite de browser da cópia final. Não foi aplicada outra refatoração.

## Correção pontual BUG-09-01

Critério CA-US-09-05: uma tarefa concluída em três deve apresentar 33,3%. A API devolvia corretamente 33.3, mas a UI interpolava o número sem locale e mostrava ponto. Reproduzido com teste que falhou antes da alteração; [captura](ui/bug-09-progress-locale-before.png). Diagnóstico e proposta apresentados antes de editar a aplicação.

Correção apenas em ProjectsWorkspace/Dashboard: formatação pt-PT com máximo uma decimal. Não muda cálculo, DTOs ou OpenAPI. delivery-review.spec.ts passou depois, verificando 25%, 33,3%, 50%, 100%, tarefas pessoais fora do progresso e transferência com origem 0%/destino 100%. É correção de requisito existente, não nova funcionalidade nem segunda refatoração.

## Código, segurança e contrato

Revisão das políticas CurrentIdentity/ProjectAccess/TaskAccess, comandos transacionais, ordem de locks projeto/tarefa, COL-06 e cascatas. Identidade/autor/data inferidos no backend; seleção de responsável verificada; arquivo e tarefas pessoais respeitados. Clientes usam /api, CSRF/cookies e estados de loading/vazio/erro/sucesso, preservam entradas e não repetem escritas automaticamente. Entidades JPA não são expostas diretamente; perfis/DTOs sem hashes.

OpenAPI 0.7.0 validado. Inventário de 24 operações comparado com 24 mappings, normalizando nomes de parâmetros de caminho: nenhum endpoint só no contrato ou só no código. Conferidas formas de DTO, enumerações, null/omissão, códigos de sucesso/erros principais, CSRF, parâmetro explícito do painel e regras de colaboração. **17 respostas reais** da instalação isolada conformes aos schemas; escritas/autorização/validação exercitadas pelas suites. Isto não é geração automática do contrato nem prova de todas as combinações possíveis de erros HTTP.

.gitignore/.dockerignore revistos: .env, node_modules, target/dist, relatórios/clones/temporários excluídos. Sem .env real ou gerados versionados; configuração de exemplo e contas são fictícias. Pesquisa por padrões no HEAD e em **13 commits**, sem ficheiros sinalizados. Não imprimir valores/token/hash durante revisão. Esta inspeção não garante deteção de qualquer formato de segredo desconhecido.

## Instalação limpa e persistência

Clone remoto real em tmp/delivery-review-20261118, SHA c9bcdcc, sem copiar dependências/ambiente locais. Criado .env a partir de .env.example com dados fictícios e portas próprias (18082/55433); projeto Compose matriz-delivery-review, rede e volume próprios.

Como não foi autorizado publicar commits, foram sobrepostos explicitamente os ficheiros aprovados da refatoração e da correção/teste, para ensaiar a árvore final. **61 ficheiros de fontes/teste** equivalentes à árvore principal após normalizar fins de linha. Não se apresenta esse overlay como um novo commit já remoto.

Executados npm ci pelos lockfiles, lint/build, Docker build, config --quiet, db --wait, seed explícito V1/V2 e app --wait. 25 testes normais Playwright passaram contra 18082 (quatro especiais ignorados). Depois criadas fixtures próprias pela API: projeto arquivado/tarefa/responsável/comentário e tarefa eliminada. Executados down sem -v e up de db/app; novo login confirmou dados, autor/instante e atribuição iguais, arquivo preservado e eliminação ainda 404. Fixtures removidas; baseline final: 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições; apenas V1/V2 success=true.

Comandos e resultados em [verification.md](verification.md). O ensaio foi no mesmo computador Windows/Docker Linux, com caches de ferramentas/imagens disponíveis; não é validação em equipamento/OS diferente. O README já contém os passos atuais e como isolar outro projeto/portas.

## Critérios de conclusão

| Critério | Estado | Evidência / limite |
|---|---|---|
| Código frontend/backend, dependências e testes no mesmo repositório | Aprovado | Estrutura e manifest revistos; fontes finais compilaram e foram exercitadas. |
| Migrations, seed e configuração fictícia | Aprovado | V1/V2 aplicadas em volume novo; seed explícito; .env.example e Wrapper/lockfiles presentes. |
| README, AGENTS, plano e verification atuais | Aprovado na revisão | Inconsistências identificadas antes de editar; comandos operacionais ensaiados com projeto/portas isolados. |
| OpenAPI e endpoints | Aprovado nas verificações | 24 mappings/24 operações, schemas/DTOs principais e 17 respostas reais conformes. |
| Funcionalidades, acesso e estados da interface | Aprovado nos cenários executados | 75 testes backend e 25 Playwright; nove cenários do Ex. 8 preservados, nova regressão de progresso. |
| Persistência/recriação de containers | Aprovado | Fixture arquivada/dados/dependentes conservados e eliminada não reaparece; novo login. |
| Segredos/temporários no conjunto de entrega | Aprovado na inspeção | Zero padrões sinalizados no âmbito pesquisado; gerados/ambiente excluídos. Não é certificação universal. |
| Cópia limpa no mesmo computador | Aprovado com ressalva explícita | Clone remoto + overlay local identificado; fontes finais equivalentes. Repetir clone do SHA final depois de publicar é recomendável. |
| GitHub Actions e demonstração de pipeline falhar/recuperar | Não cumprido | Não existe workflow nem execução. Não implementado como nova tarefa nesta revisão. |
| Commit/push desta revisão | Autorizado em 19/11/2026 | Execução em três commits; confirmação de publicação e SHAs na issue #10. |
| Aprovação individual de todos os 88 critérios | Não verificado exaustivamente | Existem evidências para funções/grupos; não executada cada variante isoladamente. Ver exemplos de cobertura parcial em verification.md. |
| Outro computador/OS e acesso do formador ao repositório | Não verificado | Clone feito pelo ambiente atual, não pelo formador/equipamento externo. |
| Validação funcional manual humana e apresentação até 5 min | Não verificado | Automação/inspeção não equivalem a aceitação humana ou demonstração oral. |
| Acessibilidade/carga e browsers universais | Não verificado | Apenas verificações desktop/360 px e Chrome nos cenários documentados. |

## Artefactos preparados

Inventário completo em [delivery-manifest.txt](delivery-manifest.txt). Inclui frontend/backend, testes, pom/Wrapper/properties, package.json/lockfiles, V1/V2, seed, Dockerfile/Compose, .env.example/helper, README/AGENTS, OpenAPI, plano/verificações/relatório, requisitos/histórias/restrições, arquitetura/modelo/ERD, wireframes/capturas e logs reais de IA. Histórico documental identificado como tal. Não inclui .git, .env, dependências instaladas, builds, relatórios gerados, clone de revisão ou scripts temporários de ensaio.

## Divisão de commits — autorizada em 19/11/2026

1. `refactor: partilhar normalizacao de pesquisa` — SearchText/ProjectQueries/TaskQueries, `Refs #10`.
2. `fix: apresentar progresso com formato portugues` — ProjectsWorkspace/Dashboard, novo teste e captura BUG-09-01, `Refs #10`.
3. `docs: concluir revisao local e preparar entrega` — README/AGENTS, plano/modelo, verification, relatório/manifest e log, `Refs #10`.

Registo/publicação autorizados em 19/11/2026; confirmação na issue #10. Antes de entrega: confirmar acesso do formador, realizar demonstração/validação humana e tratar CI se exigida pelo plano/avaliação. Não foram reescritos commits nem criados commits automaticamente. Datas e pendências não implicam autorização de publicação pública da aplicação.


## Conferência operacional final do Exercício 9

Imagem principal reconstruída e app atualizada com as fontes verificadas; health healthy em 8080. Seed não repetido, nenhuma migration nova; conferência por leitura conservou 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições, apenas V1/V2. Containers/rede da cópia isolada removidos por down sem -v; volume separado conservado para inspeção, pasta de ensaio ignorada. Não houve nova execução dos 25 testes na app principal: a suite foi executada na cópia com fontes equivalentes, conforme descrito. No fecho da revisão local ainda não existia commit/push; o utilizador autorizou posteriormente o registo em 19/11/2026. Confirmação de publicação na issue #10.
