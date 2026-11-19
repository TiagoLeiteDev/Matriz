# Ficha 05 — Plano ativo de preparação da entrega

**19/11/2026 · base a9681f7 · plano aprovado pelo utilizador.** As tabelas anteriores da Ficha 03 abaixo são históricas; a Ficha 04 foi publicada até a9681f7. Ainda não há resultado CI nesta etapa.

| Etapa | Issue | Critério / dependências | Estado |
|---|---|---|---|
| Plano e Docker/Compose | #17 | Configuração existente, build, health, seed explícito e persistência isolada | Em curso |
| GitHub Actions | #18 | Testes/builds antes das imagens, E2E e falha controlada seguida de recuperação | Por executar |
| Versão candidata | #19 | 0.1.0-rc.1, documentação, changelog/notas e inventário | Por executar |
| Validação final | #20 | Cópia limpa/versão identificada, app, dados, logs, CI e limitações | Por executar |

Reutilizar Dockerfile/Compose e apenas PostgreSQL como serviço externo. Manter volumes principais, migrations aplicadas e regras de acesso. Para ensaios usar projeto/portas/volume próprios e COMPOSE_PROJECT_NAME consistente nos comandos/subprocessos. Sem novas funcionalidades nem alojamento público. Relatórios em verification.md, registo da IA em ai-logs/ficha-05.md. Aprovação do plano autoriza a implementação e versionamento por etapas; resultados são registados apenas após execução.

---

## Histórico preservado — Ficha 03

# Matriz — Plano de implementação e estado de entrega

**Data:** 18/11/2026. **Estado atual:** MVP funcional implementado e verificado; revisão do Exercício 9 em curso. A tabela abaixo é a referência ativa. O plano inicial e a evolução por exercício estão preservados no histórico recolhido, sem os tratar como pendências atuais.

## Decisões e estrutura atuais

COL-01–07, SEG-01, TAR-01, PESQ-01/02, COM-01, PAINEL-01, ARQ-01 e TEC-01 refletem os requisitos aprovados. Stack: Java/Spring Boot/Spring Security/PostgreSQL/JPA/Flyway e React/TypeScript/Vite/CSS. JUnit/MockMvc/Testcontainers, Playwright, ESLint e Docker/Compose implementados. GitHub Actions planeado, mas sem workflow/execução.

Backend modular em identity/projects/tasks/shared/bootstrap, frontend em features/auth/projects/tasks e api, contrato openapi.yaml e esquema V1/V2. Não existe frontend/src/components genérico: os componentes são organizados por funcionalidade. React compilado servido no Spring Boot; Vite com proxy no desenvolvimento.

| Incremento | Estado e evidência |
|---|---|
| Planeamento, reconciliação e stack | Exercício 1 concluído documentalmente; decisões em requisitos/restrições. |
| Estrutura, migrations, seed e configuração | Exercício 2 implementado; V1/V2 e seed explícito, sem reset no arranque. |
| Acesso e autorização | Exercício 3 implementado; SEG-01 e testes com duas identidades. |
| Projetos e primeiro fluxo vertical | Exercício 4 implementado; pesquisa PESQ-01, arquivo/restauro/cascata. |
| Tarefas, filtros, associações e progresso | Exercício 5 implementado; TAR-01/PESQ-02/COL-06, validações e transações. |
| Responsáveis e comentários | Exercício 6 implementado; COM-01, autoria/instante backend, múltiplos/elegibilidade. |
| Frontend, colaboradores, painel e vistas | Exercício 7 implementado; COL-07/PAINEL-01 e layout desktop/móvel. |
| Verificação consolidada | Exercício 8 concluído: nove cenários aprovados por automação; BUG-08-01 corrigido. Ver verification.md. |
| Revisão e entrega local | Exercício 9 em curso: uma refatoração aprovada de pesquisa, correção de apresentação do progresso, cópia limpa isolada e revisão documental; ver delivery-review.md. |
| CI | Não implementada/não cumprida a parte do plano que exigia pipeline e prova de falha/recuperação. Nenhum resultado de Actions inventado. |
| Outro computador, validação manual humana e apresentação | Não verificados/concluídos por testes neste computador. |

## Verificações e alterações da revisão

Refatoração única aprovada: SearchText.fold partilhado por ProjectQueries/TaskQueries, mantendo exatamente NFD/remoção de marcas/Locale.ROOT. Passo isolado com 75 testes backend passados. Uma primeira edição incompleta foi recusada pela compilação e corrigida antes desses resultados; não contabilizar essa tentativa como teste passado.

Correção pontual separada: formato português do progresso, após reprodução do critério CA-US-09-05 (API 33.3 correto; UI mostrava ponto em vez de vírgula). Sem alterar cálculo/contrato. Novo teste de entrega cobre 25%, 33,3%, 50%, 100%, exclusão de pessoal e transferência.

Cópia limpa: clone remoto c9bcdcc em pasta ignorada, dependências por lockfiles e projeto Compose/portas/volume próprios; fontes da refatoração e correção sobrepostas explicitamente porque ainda não publicadas. Não confundir a árvore final local com um commit já no GitHub. Resultados reais no relatório, incluindo o que ficou por executar.

## Entrega e Git

Artefactos: frontend/backend/testes/dependências, Wrapper, Docker/Compose, V1/V2, seed, .env.example, README, AGENTS, OpenAPI, requisitos/histórias/modelo/arquitetura/UI, este plano, verification.md e registos de IA. Excluir .env, node_modules, target/dist, relatórios, clones e temporários.

Repositório único, com issues por incremento. Exercício 9 na issue #10. Divisão em três commits autorizada em 19/11/2026; confirmação de publicação na issue #10. Entrega prevista 19/11/2026 às 17h, Lisboa; não declarar apresentação/CI concluídas pela revisão local.


<details>
<summary>Histórico preservado — plano inicial e evolução por exercício</summary>

﻿# Matriz — Plano de implementação colaborativa

**Data:** 18/11/2026. **Estado:** Exercício 1 da ficha 03 concluído ao nível de análise e planeamento, com stack TEC-01 aprovada pelo utilizador. Incremento 1 (Exercício 2) implementado e verificado: estrutura, persistência, migrations, seed e arranque. Incremento 2 (Exercício 3) implementa acesso e consultas protegidas; Incremento 3 (Exercício 4) integra CRUD de projetos; Incremento 4 (Exercício 5) implementa tarefas, filtros e transferências; Exercício 6 implementa responsáveis/comentários; Exercício 7 integra colaboradores, painel e três vistas; verificação final/entrega ainda pendentes. Decisões específicas ainda abertas são verificadas antes do incremento dependente; não estão implicitamente aprovadas.

## Base validada

- COL-01–06: âmbito colaborativo, propriedade/colaboradores, cascata, tarefas pessoais privadas, sessão/cookie e limpeza atómica de atribuições inelegíveis.
- ARQ-01: monólito modular; identidade/acesso, projetos e tarefas.
- TEC-01: Java 21, Spring Boot, Maven, Spring Security, PostgreSQL, Spring Data JPA, Flyway; React/TypeScript/Vite e CSS simples; JUnit/Spring Boot Test/MockMvc, Testcontainers/PostgreSQL, Playwright/TypeScript, ESLint, Docker/Compose e GitHub Actions.
- OpenAPI 3.1 colaborativo com 24 operações, validado documentalmente. Não é API implementada; confirmar convenções assinaladas antes dos casos de uso dependentes.

## Estrutura prevista e responsabilidades

Os caminhos seguintes são planeados; não existem ainda como código.

| Caminho/componente previsto | Responsabilidade |
|---|---|
| backend/pom.xml e Maven Wrapper | Dependências/versionamento e build reproduzível. |
| backend/src/main/java/.../identity | Sessão, perfil, logout e integração Spring Security/CSRF. |
| backend/src/main/java/.../projects | Projetos, propriedade, colaboradores, arquivo e cascata. |
| backend/src/main/java/.../tasks | Tarefas, privacidade, estado, associações, atribuições e comentários. |
| backend/src/main/java/.../shared | Contrato de erros e coordenação realmente partilhada; evitar camadas sem necessidade. |
| backend/src/main/resources/application*.yml | Configuração sem credenciais reais, perfis e ligação PostgreSQL. |
| backend/src/main/resources/db/migration | Esquema Flyway, integridade, índices justificados e evolução versionada. |
| backend/src/main/java/.../bootstrap | Seed fictício de demonstração idempotente, sem apagar dados no arranque. |
| backend/src/test | Validação, autorização, persistência e regressões. |
| frontend/package.json e package-lock.json | Dependências, versões e scripts reais de build/verificação. |
| frontend/src/api | Cliente da API, cookie/CSRF e tratamento uniforme de respostas/erros. |
| frontend/src/features/auth, projects, tasks | Ecrãs/fluxos e componentes de domínio reutilizáveis. |
| frontend/src/components e styles | Componentes de apresentação e adaptação do layout. |
| e2e/ e configuração Playwright | Fluxos de utilizador com fixtures isoladas e evidências de falha. |
| Dockerfile e .dockerignore | Build multi-stage: frontend compilado servido pelo Spring Boot na imagem final. |
| compose.yaml e .env.example | Dois serviços finais: aplicação e PostgreSQL com volume/healthchecks. |
| .github/workflows/ci.yml | Testes, builds e construção Docker, com permissões mínimas. |

Não há obrigação de criar uma biblioteca ou projeto por módulo. O frontend executa no browser; servir ficheiros pelo Spring Boot mantém a unidade de entrega. Em desenvolvimento, Vite pode correr separadamente, encaminhando /api para o backend.

## Incrementos

| Incremento | Ficheiros/componentes previstos | Dependências/gate | Critérios e verificação | Estado |
|---|---|---|---|---|
| 0 — Consolidar base | Documentação, reconciliação, contrato, stack e este plano. | Decisões COL/ARQ/TEC. | Revisão documental, 24 operações válidas, rastreabilidade e pendências explícitas. | Concluído: Exercício 1 |
| 1 — Estrutura e persistência | pom/Wrapper, estrutura backend/frontend, configuração, migrations, seed e BD de desenvolvimento. | Docker disponível; fixar versões e tipos físicos antes do esquema. | Exercício 2: build/arranque, ligação PostgreSQL, migrations repetíveis, dois utilizadores e projetos com acessos distintos; segundo arranque não apaga/duplica dados. Registar comandos reais no README. | Concluído: Exercício 2; evidências em verification.md |
| 2 — Acesso | identity, segurança, perfil/logout, CSRF, login e testes. | Incremento 1; validar nomes do cookie/cabeçalho, duração e configurações por ambiente. | US-15/16: credenciais válidas/inválidas, sessão ausente/expirada, perfil sem hash, logout invalidante e CSRF. Testes com duas identidades. | Implementado e verificado: Exercício 3; limites em security.md |
| 3 — Primeiro fluxo completo de projetos | projects, controllers/DTOs, listagem/formulário, colaboradores básicos e testes. | 1/2; definir correspondência da pesquisa antes de testes detalhados. | US-01/02/17/21: autenticar, criar projeto, listar e consultar após recarregar/reiniciar backend. Sem acesso alheio; proprietário inferido. | Concluído para projetos: Exercício 4; gestão de colaboradores permanece no incremento próprio |
| 4 — Tarefas e consultas | tasks, forms/lista, estado, associação e integração. | 3; validar limites de texto e detalhes de DTO. | US-03–14: criar e consultar sem recarregar, campos válidos/inválidos, privacidade e autorização, transferência com limpeza atómica, filtros e persistência real. | Implementado e verificado no Exercício 5: lista/CRUD/filtros/transferência/progresso; Kanban/calendário no incremento 6 |
| 5 — Colaboração e operações destrutivas | Elegíveis/responsáveis, comentários, arquivo/restauro e cascata. | 3/4; política COL-02/03/06 aprovada. | US-12/13/17–20: sem duplicados, autor/data do backend, perda de acesso imediata, arquivo bloqueia alterações, cascata sem órfãos, cancelamento e rollback. | Responsáveis/comentários implementados no Exercício 6; arquivo/cascata nos Exercícios 4/5. Colaboradores integrados no Exercício 7 |
| 6 — Interface e painel completos | Lista/Kanban/calendário, progresso, contagens, erros e estados. | 3–5; confirmar âmbito das contagens e navegação do calendário. | US-06–09/14/22: mesmas tarefas nas vistas, contagens autorizadas não limitadas à página, atualização após gravação, formulário preservado e prevenção de submissão repetida. | Implementado no Exercício 7; evidências em verification.md |
| 7 — Qualidade e entrega reproduzível | Testes, E2E, Docker/Compose, CI, docs e revisão. | Incrementos anteriores com verificações reais. | Três testes mínimos e fluxo Playwright; cópia limpa, persistência após recriar container, pipeline falha com teste controlado e volta a passar; revisão de segredos e demonstração até 5 min. | Não iniciado |

Cada incremento deve entregar um comportamento verificado antes de prosseguir. A interface mínima do login/projetos acompanha o primeiro fluxo completo; não se adia toda a integração para o final.

## Primeiro percurso a aceitar

Iniciar sessão → criar projeto → vê-lo na listagem → consultar detalhe → recarregar interface → reiniciar backend e autenticar quando necessário → confirmar os dados guardados. Utilizador sem acesso não pode consultar o projeto por alteração do identificador.

## Verificações e evidência

- Unidade: regras de validação/negócio com JUnit.
- API: Spring Boot Test/MockMvc, incluindo autenticação/autorização e erros.
- Integração real: PostgreSQL isolado por Testcontainers; criação seguida de consulta e limpeza independente.
- E2E: Playwright/TypeScript, seletores estáveis, sem esperas fixas, evidências quando falha.
- Frontend: ESLint e build TypeScript/Vite; testes de componentes apenas onde necessários e justificados.
- Operação: Compose config/build/up, health/readiness, volume persistente e execução a partir de cópia limpa.
- CI: GitHub Actions com testes antes de build Docker, logs e resultados reais.

Registar comandos, esperado/observado/evidências e limitações em verification.md. Os nomes de comandos e versões só passam a instruções de execução após verificação na implementação. Não se apresentam verificações planeadas como testes passados.

## Pendências por incremento

| Antes de | Decisão restante |
|---|---|
| 1 | Resolvido no Exercício 2: versões fixadas, UUID/text/date/timestamptz, V1 Flyway e seed explícito transacional. Ver setup.md. |
| 2 | Resolvido: SEG-01 (60 minutos), cookie/CSRF e perfil prod; evidências em verification.md. HTTPS real/publicação não executados. |
| 3/4 | Projetos resolvidos: contrato sem máximos e PESQ-01. Tarefas resolvidas em TAR-01/PESQ-02; sem máximos de texto, sem importar limites pedagógicos. |
| 6 | Composição das contagens, formato/navegação do calendário e ordem inicial. |
| 7 | Comandos efetivos, portas, imagens fixadas e evidência de execução noutro ambiente/CI. |

Estas pendências não alteram o âmbito ou a stack já aprovados. Devem ser resolvidas/revistas antes da funcionalidade dependente, não ignoradas nem tratadas como autorização genérica para novas regras.

## Prazo e limites

Entrega em 19/11/2026 às 17h, hora de Lisboa. No fecho original do plano não existia código. O Exercício 2 implementou a base técnica, sem concluir funcionalidades de negócio. O estado documental não comprova viabilidade do calendário ou aplicação pronta. Desenvolvimento, verificações, documentação e apresentação estão incluídos no prazo.

[Requisitos](requirements.md) · [Restrições](constraints.md) · [Arquitetura](architecture.md) · [User Stories](user-stories.md) · [Verificação](verification.md) · [Log](ai-logs/ficha-03.md)

## Revisão após Exercício 2 — 18/11/2026

Incremento 1 concluído: build Java e frontend, BD PostgreSQL, seis tabelas/mapeamentos JPA, V1 Flyway, demonstração explícita e arranque integrado. Nove testes de infraestrutura passaram; imagem construída sem usar target/node_modules do host; health, assets e reinício com conservação dos dados verificados. README/setup/verification registam os comandos e os limites reais.

Próximo: incremento 2, identidade/acesso. Antes de o disponibilizar, resolver a política de duração de sessão e as convenções de cookie/CSRF; depois implementar login/perfil/logout e testes com duas identidades. Em seguida, primeiro fluxo completo de projetos (incremento 3), e só depois tarefas/colaboração/vistas. Não interpretar o bloqueio total atual da API como autorização por recurso implementada.

## Revisão após Exercício 3

Acesso implementado com SEG-01 e oito operações do contrato: quatro de sessão/CSRF e quatro consultas de recursos existentes para verificar autorização. Identidade atual e políticas de proprietário, participação, privacidade pessoal, arquivo e elegibilidade no backend. Ecrã de login/perfil/logout e tratamento de sessão inválida integrados. Resultados de Java/browser, expiração e reinício em verification.md.

Próximo: Exercício 4 / incremento 3 — criação, listagem, consulta, edição e eliminação de projetos, com pesquisa e confirmações. A consulta por ID já tem uma base protegida; ainda é necessário o fluxo completo e as restantes operações. Antes da pesquisa, fechar correspondência por nome; não impor limites de texto arbitrários. Escritas futuras reutilizam identidade e políticas em casos de uso transacionais, com testes de autorização e persistência por operação.

## Revisão após Exercício 4

CRUD de projetos, arquivo/restauro, pesquisa PESQ-01 e interface integrados. Primeiro percurso completo confirmado: login → criar → listar/consultar/editar → reload → reinício de app → novo login → mesmo projeto/descrição. Cancelamento sem DELETE, cascata e rollback testados. Resultados em verification.md.

Próximo: Exercício 5, gestão de tarefas. Reutilizar CurrentIdentity e guardas, implementar estados/prioridade/filtros/persistência e aplicar acesso/arquivo nos casos de uso. Não confundir leitura de tarefas existente com CRUD concluído. Gestão de colaboradores, responsáveis/comentários de escrita e limpeza COL-06 permanecem nos incrementos dependentes. Exercícios 3/4 separados em commits associados às issues #4/#5; publicação verificável nesses registos.


## Revisão após Exercício 5

Incremento 4 concluído para lista/CRUD/filtros de tarefas, estados TAR-01, pesquisa PESQ-02, transferência/desassociação com limpeza COL-06 e progresso integrado. V2 aplicada sem substituir V1 nem preencher prioridades null preexistentes. 50 testes backend, lint/build, 14 cenários normais de browser e 1 teste separado de reinício passaram. OpenAPI 0.5.0 válido com 16/24 operações implementadas.

Próximo incremento: colaboração de escrita (colaboradores, responsáveis e comentários), conforme o exercício seguinte. Kanban/calendário e painel/PD-06 permanecem no incremento 6; não declarar US-07/08/17/18/19/22 concluídas com este trabalho. Registo Git autorizado pelo utilizador; commit e confirmação de publicação na issue #6.


## Revisão após Exercício 6

Responsáveis/elegíveis e comentários implementados na API/UI segundo plano aprovado e COM-01. V1/V2 e modelo preservados. Ver task-collaboration.md e verification.md para execução por incremento e evidências. Gestão de colaboradores (US-17-01/02) ainda pendente; elegíveis (US-17-03/04), responsáveis (US-18) e comentários (US-19) implementados. Painel/PD-06, Kanban/calendário, CI e entrega noutro computador permanecem pendentes. Registo Git autorizado pelo utilizador; commit e confirmação de publicação na issue #7.


## Revisão após Exercício 7

Frontend integrado com gestão de colaboradores COL-07, painel PAINEL-01, Lista/Kanban/calendário e layout adaptável. API 0.7.0 com 24 operações implementadas; sem novas entidades/migrations. Evidências em frontend-integration.md/verification.md. Exercício 8: auditoria final dos cenários da ficha e evidências; qualidade/CI/entrega continuam pendentes. Issue #8: registo Git autorizado, confirmação de publicação na própria issue.


## Revisão após Exercício 8

Verificação consolidada realizada: nove cenários automatizados aprovados, 75 testes backend, 24 cenários normais Playwright, três reinícios e uma expiração isolada passaram; lint/build passaram. Criados três testes relevantes de validação/autorização/criação com consulta posterior. BUG-08-01 identificado, diagnóstico apresentado antes de alterar a aplicação e correção limitada à navegação do detalhe de tarefa em projeto, com regressão. Dados originais conservados; backend/contrato/migrations preservados. Detalhe em verification.md.

A auditoria funcional deste exercício está concluída localmente; isto não conclui CI, cópia limpa/noutro computador, apresentação/entrega ou verificações manuais humanas. Registo Git autorizado; confirmação de publicação/fecho na issue #9. Consultar o exercício seguinte da ficha antes de alargar o trabalho.

</details>
