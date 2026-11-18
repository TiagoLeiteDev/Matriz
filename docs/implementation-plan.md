# Matriz — Plano de implementação colaborativa

**Data:** 18/11/2026. **Estado:** Exercício 1 da ficha 03 concluído ao nível de análise e planeamento, com stack TEC-01 aprovada pelo utilizador. Incremento 1 (Exercício 2) implementado e verificado: estrutura, persistência, migrations, seed e arranque. Incremento 2 (Exercício 3) implementa acesso e consultas protegidas; Incremento 3 (Exercício 4) integra CRUD de projetos; tarefas/colaboração de escrita e restante MVP ainda por implementar. Decisões específicas ainda abertas são verificadas antes do incremento dependente; não estão implicitamente aprovadas.

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
| 4 — Tarefas e consultas | tasks, forms/lista, estado, associação e integração. | 3; validar limites de texto e detalhes de DTO. | US-03–14: criar e consultar sem recarregar, campos válidos/inválidos, privacidade e autorização, transferência com limpeza atómica, filtros e persistência real. | Não iniciado |
| 5 — Colaboração e operações destrutivas | Elegíveis/responsáveis, comentários, arquivo/restauro e cascata. | 3/4; política COL-02/03/06 aprovada. | US-12/13/17–20: sem duplicados, autor/data do backend, perda de acesso imediata, arquivo bloqueia alterações, cascata sem órfãos, cancelamento e rollback. | Não iniciado |
| 6 — Interface e painel completos | Lista/Kanban/calendário, progresso, contagens, erros e estados. | 3–5; confirmar âmbito das contagens e navegação do calendário. | US-06–09/14/22: mesmas tarefas nas vistas, contagens autorizadas não limitadas à página, atualização após gravação, formulário preservado e prevenção de submissão repetida. | Não iniciado |
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
| 3/4 | Projetos resolvidos: contrato sem máximos e PESQ-01. Para tarefas, manter pendências e não importar limites pedagógicos. |
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
