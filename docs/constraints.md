# Matriz — Restrições e decisões do MVP colaborativo

**Versão:** 2.0, 18/11/2026. A base individual foi substituída por COL-01. Consultar scope-reconciliation.md para o histórico.

## Decisões confirmadas

| ID | Decisão |
|---|---|
| COL-01 | Cumprir o âmbito colaborativo integral da ficha 03. |
| COL-02 | Proprietário gere projeto e colaboradores; proprietário/colaboradores gerem tarefas, responsáveis elegíveis e comentários. |
| COL-03 | Eliminação de projeto/dependentes em cascata, com confirmação explícita e sem apagar contas. |
| COL-04 | Manter tarefas independentes, privadas do criador. |
| COL-05 | Autenticação por sessão no servidor e cookie. Spring Security aprovado em TEC-01; política/configuração concretizadas em SEG-01 e security.md. |
| COL-06 | Remover atribuições que se tornem inelegíveis na mesma operação que remove acesso ou transfere/desassocia tarefa. |
| TAR-01 | Exercício 5: TODO/IN_PROGRESS/DONE e LOW/MEDIUM/HIGH; inicial TODO e prioridade omitida MEDIUM. Null explícito = Sem prioridade. Migração: Em Revisão → IN_PROGRESS, Crítica → HIGH; restantes por equivalência; vazios existentes preservados. Confirmado pelo utilizador. |
| PESQ-02 | Tarefas: parte literal do título, ignorando maiúsculas e acentos, como projetos. Tarefas pessoais mantidas (COL-04). Confirmado no Exercício 5. |
| PESQ-01 | Projetos: pesquisa por parte do nome, ignorando maiúsculas e acentos; vazio não restringe o conjunto autorizado/arquivo. Escolha expressa do utilizador no Exercício 4. |
| SEG-01 | 60 minutos de inatividade, sem lembrar-me; reinício e logout exigem novo login. Sessão em memória, cookie e CSRF concretizados no Exercício 3; ver security.md. |
| ARQ-01 | Manter monólito modular, acrescentando identidade/acesso às responsabilidades existentes. |
| TEC-01 | Java 21/Spring Boot/Maven/Spring Security, PostgreSQL/JPA/Flyway, React/TypeScript/Vite/CSS, JUnit/MockMvc/Testcontainers/Playwright/ESLint, Docker/Compose/GitHub Actions. |

O pedido geral autorizou a mudança de âmbito. COL-02–04 e COL-05–06 foram confirmadas em respostas específicas. Os detalhes de contrato/layout são propostas documentais de implementação, não implementação concluída.

## Restrições de entrega

- Entrega em 19/11/2026 às 17h, hora de Lisboa, incluindo desenvolvimento, testes, configuração, documentação e apresentação. Não foi autorizado alterar o prazo.
- Frontend e backend reais, persistência após recarregar/reiniciar; sem mocks nas funcionalidades concluídas.
- Contas de demonstração e pelo menos dois utilizadores/projetos para verificar acessos distintos. Seed repetível sem apagar dados em cada arranque.
- Contratos completos necessários aos fluxos; a regra de apenas duas operações dizia respeito ao exercício da ficha 2, não ao desenvolvimento integral da ficha 3.
- Plano em implementation-plan.md e evidência esperado/observado em verification.md.
- Pelo menos três testes automatizados: validação, autorização, criação com consulta à persistência. Regressão para bugs corrigidos.
- README e AGENTS atualizados com comandos reais quando existirem; execução a partir de cópia limpa, GitHub acessível ao formador e demonstração de até cinco minutos.

## Segurança

- Identidade derivada da sessão; proprietário, criador, autor e data de comentário definidos no backend.
- Autorizar recursos concretos em todos os pontos de entrada, incluindo pedidos diretos com IDs alterados. Login não substitui autorização.
- Palavras-passe locais apenas em hash com biblioteca apropriada; não inventar algoritmo de autenticação/criptografia.
- Cookie de sessão HttpOnly, política SameSite e Secure em HTTPS, invalidação no logout e proteção CSRF devem ser concretizados com a biblioteca escolhida. As convenções do contrato são proposta de desenho técnico.
- Não devolver hashes, credenciais ou detalhes internos. Segredos reais fora do repositório e configuração de exemplo fictícia.
- O uso de sessão/cookie exige proteção contra CSRF e configuração coerente de origem/CORS; não usar SameSite como única garantia universal.
- Exposição pública não aprovada. Não confundir execução noutro computador com publicação em rede.

Referências técnicas consultadas para sessão e CSRF: https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html e https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html . Não substituem as decisões funcionais do projeto.

## Pendências reais

| ID | Tema | Estado |
|---|---|---|
| DT-01 | Linguagem/frameworks e stack. | Resolvido em TEC-01; versões concretizadas em pom.xml, package-lock.json e imagens Docker fixadas no Exercício 2. |
| DT-02 | Persistência, tipos físicos, migrations, sessão e seed. | Tipos físicos UUID/text/date/timestamptz, migration V1, mapeamentos JPA e seed explícito implementados no Exercício 2; sessão em memória e política SEG-01 implementadas no Exercício 3. |
| DT-03 | Instalação, arranque, ambiente de execução e distribuição. | Base local/Compose implementada com React compilado no Spring Boot e PostgreSQL; comandos e evidências em README/setup/verification. Entrega funcional, CI e validação noutro computador continuam pendentes. |
| DT-04 | Contrato API colaborativo. | 24 operações documentadas e validadas. Base de planeamento concluída; Exercícios 3/4/5/6 implementam 20 operações (acesso, consultas, CRUD de projetos/tarefas, responsáveis e comentários), com servidor /api e estado por operação; restantes fluxos continuam planeados. Manter contrato alinhado com cada incremento. |
| PD-01 | Limites de texto. | Projetos seguem exatamente o contrato: nome não vazio/não só espaços e descrição opcional, sem máximos. Não adotar 3–100/1000 dos exemplos. Restantes campos seguem o contrato de cada incremento. |
| PD-02 | Falhas de gravação. | Regra aprovada: erro explícito, conservar dados confirmados e formulário; transações/cascata/rollback implementados e verificados para projetos no Exercício 4 e tarefas no Exercício 5; restantes escritas pendentes. |
| PD-03 | Desempenho, acessibilidade e larguras-alvo. | Interface adaptável pedida na ficha; testes/metas ainda por definir. |
| PD-04 | Correspondência de pesquisa/acentos. | Projetos resolvidos por PESQ-01 (parcial, sem maiúsculas/acentos). Tarefas resolvidas por PESQ-02: parte do título sem maiúsculas/acentos. |
| PD-05 | Ordem inicial e navegação do calendário. | Desenho ainda por detalhar. |
| PD-06 | Âmbito exato das contagens do painel. | Incluir só conjunto autorizado; definir explicitamente tarefas independentes/arquivadas antes de implementar. |
| PD-07 | Expiração/duração de sessão e pormenores cookie/CSRF. | Resolvido no Exercício 3: SEG-01, cookie matriz_session HttpOnly/SameSite=Lax/Secure no perfil prod; CSRF por sessão e X-CSRF-Token. Ver security.md e evidências. |

A antiga pendência de aceitação do âmbito individual deixou de ser aplicável: o utilizador decidiu alinhar com o colaborativo. Isto não afirma revisão pelo formador dos detalhes da política.

## Fora do âmbito

Gestão global de organizações/equipas, convites, registo público, recuperação de palavra-passe, notificações, subtarefas, recorrência, modelos, estimativas e estados próprios de projeto. As tarefas pessoais e calendário/Kanban não são retirados.

[Requisitos](requirements.md) · [Modelo](database.md) · [Plano](implementation-plan.md) · [Log](ai-logs/ficha-03.md)


## COM-01 — Comentários (Exercício 6)

Plano aprovado pelo utilizador: novos comentários têm conteúdo obrigatório de 1 a 2000 pontos de código Unicode, não apenas espaços. Autor e instante gerados pelo backend; só conteudo aceite no pedido. Sem truncar dados históricos nem limitar títulos/descrições. Ordem de consulta criadoEm/id ascendente; leitura autorizada de arquivadas, escrita bloqueada. Ver task-collaboration.md e OpenAPI 0.6.0.
