# Changelog

## Não publicado

### Interface

- Estrutura comum AppShell após o login, menu lateral Painel/Projetos/Tarefas e cabeçalho com conta/logout.
- Login, cartões de contagens/progresso, filtros e detalhes modernizados, com adaptação a ecrãs pequenos.
- Navegação SPA por fragmentos preservada, incluindo histórico, reload e detalhe de projeto/tarefa.

### Verificação

- Dois testes AAA de navegação no mesmo documento, sessão/histórico e layout desktop/móvel.
- Regressão local: 32 testes Playwright aprovados, quatro especiais ignorados; lint/build aprovados.
- Documentação e capturas em docs/frontend-modernization.md; distribuição Docker e CI acompanhadas na issue #21.

Regras funcionais, API, backend e migrations preservados. A tag v0.1.0-rc.1 continua a identificar a candidata anterior.

## 0.1.0-rc.1 — 2026-11-19

Primeira versão candidata do MVP colaborativo. Código funcional e decisões das fichas 01–04 preservados; preparação de execução/entrega da Ficha 05.

### Funcionalidades

- Sessão protegida/CSRF e autorização por recurso; tarefas pessoais privadas.
- Projetos, colaboradores, tarefas, responsáveis e comentários persistentes.
- Pesquisa/filtros, progresso, painel, lista/Kanban/calendário e arquivo/restauro.

### Qualidade e execução

- Testes unitários, integração PostgreSQL Testcontainers, E2E e segurança; convenção AAA.
- Cleanup robusto e título Unicode alinhado com a regra do backend.
- Docker multi-stage, utilizador sem privilégios, imagens base fixadas, Compose/volume/healthchecks e seed explícito.
- GitHub Actions: testes e builds antes das imagens, E2E/reinícios isolados e resumo de contagens sem payloads.
- Nome estável do JAR e documentação/versionamento da candidata.

### Limitações

Candidata de demonstração local; sem alojamento público, publicação de imagens, registo público ou recuperação de palavra-passe. Testes automatizados não certificam universalmente segurança, acessibilidade, carga ou outros sistemas. Resultados de execução e limitações efetivas em docs/verification.md.
