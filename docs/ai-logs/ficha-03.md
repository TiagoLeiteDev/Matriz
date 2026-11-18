# Ficha Prática 03 — Registo de trabalho e decisões

**Início:** 18/11/2026. **Ferramenta:** Codex. **Estado:** Exercício 1 concluído em análise/planeamento com TEC-01 aprovada; Exercício 2 não iniciado.

## 1. Análise do enunciado

Lida integralmente a ficha 10 - Ficha Prática 03.pdf e consultados os prompts do arquivo 09 - Anexos.zip. O enunciado exige conciliar diferenças antes de implementar, escolher stack/persistência, completar contratos e planear incrementos. O âmbito integral inclui autenticação/autorização, responsáveis múltiplos, comentários, pesquisa de projetos e eliminação de projetos.

## 2. Autorização de mudança de âmbito

Pedido do utilizador: “ok vamos então alterar o que já foi feito até agora para estar compatível com o âmbito colaborativo pedido”. A decisão COL-01 substitui o âmbito individual anterior, mantendo as funcionalidades previamente aprovadas que não conflituam.

### Respostas específicas do utilizador

| Questão | Resposta | Decisão |
|---|---|---|
| Proprietário gere projeto/colaboradores; proprietário e colaboradores gerem tarefas, responsáveis e comentários. | Sim, adotar esta política. | COL-02 |
| Eliminar projeto e dependentes. | Eliminar tudo, com confirmação explícita. | COL-03 |
| Tarefas independentes. | Manter, privadas do utilizador que as cria. | COL-04 |
| Sessão no servidor e cookie, deixando biblioteca para a stack. | Sim, sessão no servidor e cookie. | COL-05 |
| Atribuições que se tornam inelegíveis por transferência/remoção de acesso. | Remover as atribuições inelegíveis na mesma operação. | COL-06 |

Foi explicado o significado de sessão/cookie e das opções remover atribuições vs bloquear a operação antes das duas últimas respostas. Nenhuma escolha de stack foi feita por implicação.

## 3. Preservação do histórico

Copiados 14 artefactos anteriores para docs/history/mvp-individual-v1, sem apagar o conteúdo histórico dos logs 1/2. Os documentos ativos passam a indicar COL-01 e as decisões que substituem a versão individual.

## 4. Alterações documentais

- Requisitos F01–F19 e acesso por recurso, tarefas pessoais privadas, cascata, elegibilidade e sessão.
- Backlog ampliado para 22 histórias, mantendo US-01–US-14 adaptadas e acrescentando US-15–US-22; critérios são especificação, não testes aprovados.
- Arquitetura mantém monólito modular com identidade/acesso, projetos e tarefas; comentários/atribuições pertencem a Tarefas.
- Modelo com UTILIZADOR, PROJETO, TAREFA, COLABORACAO_PROJETO, ATRIBUICAO_TAREFA e COMENTARIO, com criador/proprietário/autor confiáveis e relações de acesso.
- Contrato OpenAPI colaborativo com sessão/CSRF, perfil/logout, projetos, colaboradores, tarefas, elegíveis, responsáveis, comentários e painel. Naming/DTOs e detalhes de proteção são propostas técnicas a rever, não código implementado.
- Wireframes de tarefa colaborativa e login, com utilizador, responsáveis, comentários e ações do proprietário. Nova disposição é proposta, não aprovação UI-01 retroativa.
- Criados scope-reconciliation.md, implementation-plan.md e verification.md; revistos constraints, AGENTS, README e architecture-review.

## 5. Limites e próximos passos

Ainda por escolher stack, persistência, tipos físicos, migrations/seed, biblioteca e duração da sessão, detalhes de cookie/CSRF, limites de texto, pesquisa de projetos/acentos e âmbito final das contagens. O contrato propõe explicitar a inclusão de tarefas pessoais por parâmetro do painel; não se atribui aprovação implícita dessa convenção ao utilizador.

Não foram instaladas dependências da aplicação, criados frontend/backend, executadas migrations ou feitos commits/push nesta atividade. A ficha 3 não está concluída; a preparação documental permite rever a base e escolher tecnologias antes da implementação incremental.

## 6. Validações documentais executadas

- OpenAPI 3.1 validado por openapi-spec-validator 0.9.0: 24 operações, operationIds únicos, dois pontos públicos (CSRF pré-login e login); os restantes declaram sessão exigida.
- Todos os métodos de alteração declaram cabeçalho CSRF. Isto não comprova a implementação da proteção.
- Doze exemplos validados contra os respetivos schemas. Verificações positivas/negativas de nome/título/conteúdo, enumerações, datas e campos opcionais passaram nos schemas.
- Pedidos com proprietarioId, criadorId ou autorId indevidos são rejeitados pelos DTOs de escrita; os DTOs públicos não contêm hashes.
- 22 histórias e 88 critérios únicos; 19 funcionalidades mapeadas no backlog e cobertura conceptual documentada.
- Fonte Mermaid do documento corresponde ao .mmd. ERD de seis entidades renderizado em SVG/PNG com código 0 e imagem inspecionada.
- Wireframes colaborativo e login inspecionados: legíveis, sem cortes ou sobreposições relevantes.
- Links locais dos documentos ativos e logs verificados. A fotografia histórica mantém os caminhos originais e as respetivas limitações indicadas em LEITURA.md.
- git diff --check sem erros de whitespace; aviso de normalização LF/CRLF no log histórico não é erro de conteúdo.

**Limitação:** não existem testes executados da aplicação, transações, verificação de hashes ou autorização real. A base está atualizada para colaboração e pronta para revisão técnica e seleção de stack. Não é conclusão da ficha 03.

## 7. Escolha de stack e fecho do Exercício 1 — TEC-01

**Data:** 18/11/2026. Após recomendar a stack e rever todos os materiais do curso (14 PDFs, 709 páginas extraídas, 28 ficheiros dos três anexos ZIP e AI.txt), o utilizador autorizou: **“ok vamos fechar o exercicio 1 da ficha 03 então com essa sugestão”**.

### Decisão aprovada

Java 21/Spring Boot/Maven, Spring Security com sessão/cookie, PostgreSQL/Spring Data JPA/Flyway, React/TypeScript/Vite/CSS simples. Testes JUnit/Spring Boot Test/MockMvc, Testcontainers/PostgreSQL, Playwright/TypeScript e ESLint. Docker/Compose e GitHub Actions.

Estratégia: desenvolvimento com Vite e backend locais; entrega com React compilado servido pelo Spring Boot e PostgreSQL em segundo serviço Compose com persistência. Não introduzir FastAPI/Pytest apenas para reproduzir exemplos do curso. AI.txt é um percurso separado, não requisito de incluir IA na Matriz.

### Artefactos atualizados

architecture.md regista TEC-01 e justificação; constraints.md distingue decisões resolvidas de detalhes pendentes; implementation-plan.md especifica caminhos/componentes, dependências, critérios e verificações de cada incremento. README/AGENTS e referências de estado atualizados. Não foi criada estrutura de código, instalado backend/frontend, iniciada BD ou executado build da aplicação nesta atividade.

### Checklist do Exercício 1

| Pedido | Evidência/estado |
|---|---|
| Rever artefactos e conciliar âmbito | COL-01–06 e scope-reconciliation.md. Concluído documentalmente. |
| Confirmar linguagem/frameworks/base de dados | TEC-01 em architecture.md. Aprovado. |
| Consolidar regras de acesso | requirements.md e COL-02/04/06. Documentado. |
| Completar contrato para os fluxos | OpenAPI colaborativo com 24 operações, validado. Convenções e detalhes pendentes identificados para revisão antes do incremento dependente. |
| Criar plano com sequência/componentes/aceitação/verificação | implementation-plan.md concretizado com estrutura prevista e gates por incremento. |

**Estado:** Exercício 1 concluído no âmbito de planeamento. Não significa que todas as decisões de pormenor estejam resolvidas: versões, tipos físicos, limites de texto, detalhes de sessão, pesquisa e contagens constam dos gates do plano. Não iniciar o comportamento dependente por suposição nem confundir o plano com implementação concluída.

**Próximo passo:** Exercício 2 — preparar estrutura e persistência, fixar versões, esquema/migrations e seed, executar/validar arranque e registar comandos reais. Commit/push e implementação não realizados neste fecho documental.

## Rastreabilidade Git e issues — 18/11/2026

A pedido do utilizador, foram criadas retroativamente duas issues para organizar o trabalho documental já realizado:

- [Issue #1](https://github.com/TiagoLeiteDev/Matriz/issues/1): consolidação histórica das fichas 01/02. Referencia o commit original ca85963, já publicado, e o commit dbfb2e0 com a fotografia documental individual em docs/history/mvp-individual-v1.
- [Issue #2](https://github.com/TiagoLeiteDev/Matriz/issues/2): documentação ativa do MVP colaborativo e planeamento do Exercício 1 da ficha 03. O commit deste registo referencia esta issue.

Não foi reescrito o histórico existente. A criação destas issues é posterior ao trabalho documental; não demonstra planeamento prévio. Os novos commits ficam locais, pendentes de push. A implementação da aplicação e os seus testes continuam por executar.
