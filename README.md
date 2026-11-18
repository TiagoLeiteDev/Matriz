# Matriz

Aplicação colaborativa de gestão de projetos e tarefas, desenvolvida no curso AI-Powered Software Engineering.

## Estado atual

Base documental revista para o âmbito integral da ficha 03, autorizado em COL-01. Monólito modular mantido; autenticação, autorização, colaboradores, responsáveis e comentários incluídos. **Não existe aplicação implementada nem comandos de arranque/testes definidos.** Exercício 1 da ficha 03 concluído em análise/planeamento; stack e tecnologias de persistência aprovadas em TEC-01. O contrato e os layouts expandidos são propostas técnicas para revisão.

## Tecnologias aprovadas

Java 21, Spring Boot, Maven, Spring Security (sessão/cookie), PostgreSQL, Spring Data JPA e Flyway. Frontend React/TypeScript/Vite com CSS simples. JUnit, Spring Boot Test/MockMvc, Testcontainers/PostgreSQL, Playwright/TypeScript e ESLint. Docker/Compose e GitHub Actions para execução e verificações.

Entrega prevista: React compilado servido pelo Spring Boot, com PostgreSQL num segundo serviço Compose. Versões exatas e comandos reais serão fixados/verificados na implementação.

## Funcionalidades do MVP

- Sessão no servidor com cookie, consulta do utilizador autenticado e logout.
- Projetos com proprietário/colaboradores, pesquisa por nome, arquivo/restauro e eliminação confirmada em cascata.
- Tarefas de projetos autorizados e tarefas pessoais independentes privadas do criador.
- Estados Por Fazer, Em Curso, Em Revisão e Concluída; prioridades/prazos opcionais.
- Lista, Kanban e calendário; pesquisa/filtros; progresso por projeto e painel com contagens autorizadas.
- Vários responsáveis elegíveis por tarefa, sem duplicação, e comentários com autor/data definidos no backend.
- Privacidade verificada no backend; remoção de acesso/transferência remove atribuições inelegíveis na mesma operação.
- Persistência real após recarregar/reiniciar; seed de demonstração não destrutivo e execução reproduzível noutro computador.

Sem gestão global de organizações/equipas, convites, registo público, recuperação de palavra-passe, notificações, subtarefas, modelos, recorrência ou estimativas.

## Documentação ativa

| Documento | Conteúdo |
|---|---|
| [Requisitos](docs/requirements.md) | Âmbito, política de acesso e F01–F19. |
| [User Stories](docs/user-stories.md) | 22 histórias e 88 critérios, incluindo autorização e colaboração. |
| [Restrições](docs/constraints.md) | COL-01–06, prazo, segurança e decisões pendentes. |
| [Reconciliação](docs/scope-reconciliation.md) | Diferenças entre fichas e decisões substituídas. |
| [Arquitetura](docs/architecture.md) | Monólito modular e responsabilidades. |
| [Modelo e ER](docs/database.md) | Seis entidades, relações e regras. |
| [OpenAPI](openapi.yaml) | Proposta colaborativa de contratos, sem implementação. |
| [Wireframes](docs/ui/wireframe-tarefas.md) | Tarefas colaborativas e início de sessão. |
| [Revisão](docs/architecture-review.md) | Rastreabilidade e limitações da base colaborativa. |
| [Plano](docs/implementation-plan.md) | Incrementos e verificações do Exercício 1 da ficha 03. |
| [Verificação](docs/verification.md) | Cenários planeados, ainda não executados na aplicação. |
| [Registo da ficha 3](docs/ai-logs/ficha-03.md) | Autorizações e trabalho documental atual. |
| [AGENTS](AGENTS.md) | Instruções para agentes e segurança. |

## Histórico

[Artefactos do MVP individual](docs/history/mvp-individual-v1/LEITURA.md) preservados. Os logs [ficha 1](docs/ai-logs/ficha-01.md) e [ficha 2](docs/ai-logs/ficha-02.md) mantêm as decisões históricas, superadas onde indicado por COL-01. Não usar o antigo contrato sem login como base atual.

## Instalação, execução e testes

Pendentes de implementação e verificação com a stack aprovada. Não há comandos, contas reais ou resultados de testes da aplicação a anunciar. O README final incluirá dependências, configuração, migrations/seed, contas fictícias, arranque e testes verificados numa cópia limpa.

## Entrega

19/11/2026 às 17h, hora de Lisboa. Incluir frontend/backend, testes, dependências, esquema/migrations, seed, configuração de exemplo sem segredos, documentação atualizada e URL GitHub acessível ao formador. Demonstração de 1–2 fluxos em até cinco minutos, com tecnologias, metodologia/IA, desafio e aprendizagem.
