# Ficha prática 04 — Registo de utilização de IA

## Exercício 1 — Plano de testes com IA

**Data:** 19/11/2026. **Ferramenta:** ChatGPT, através do Codex. **Base analisada:** 212a4f6.

### Pedido

O utilizador pediu o início da Ficha 04 e anexou um prompt estruturado para definir um plano de testes do MVP: consultar instruções/requisitos/critérios/arquitetura/OpenAPI/estrutura e testes existentes; criar matriz com rastreabilidade, pré-condições, dados, esperado, nível e prioridade; incluir título inválido, criação/consulta e acesso indevido; rever sugestões de IA. Proibiu modificar implementação, implementar ou executar testes nesta fase.

### Trabalho realizado e resposta

Leitura do enunciado da Ficha 04 e inspeção estática dos artefactos e testes existentes. Produzido [plano de testes](../test-plan.md) com 16 cenários, regras confirmadas, dados fictícios, isolamento e inventário da cobertura existente. Distinguidos testes unitários propostos de integração existente com Spring/MockMvc/PostgreSQL e testes E2E Playwright.

### Decisões e controlo humano

A IA propôs reutilizar a cobertura e preencher a lacuna de testes unitários puros de TaskInput. Rejeitou importar limites 3–150/2000 dos exemplos, projeto obrigatório e proibição de colaboradores gerirem tarefas. Corrigiu a classificação dos testes existentes e distinguiu reload/nova sessão de reinício. Estas são decisões da proposta para revisão, não confirmação humana de novas regras. A tabela completa e justificações estão no plano.

### Verificação e limites

Apenas inspeção documental/estática e verificação de diff/formato. Nenhum teste foi implementado ou executado; nenhum resultado histórico foi apresentado como execução atual. Não houve alteração de produção, testes, dependências ou ambiente. Plano aguarda revisão do utilizador antes dos exercícios de implementação. Sem commit/push neste incremento.


## Exercício 2 — Testes unitários

**Data:** 19/11/2026. **Ferramenta:** ChatGPT/Codex.

**Pedido:** implementar e executar testes unitários de uma regra existente, com caso válido, inválido e fronteira; consultar plano/requisitos/arquitetura/convenções; não alterar produção; registar resultados reais em verification.md preservando o histórico.

**Plano apresentado e realizado:** testar diretamente TaskInput.create/patch com JUnit/AssertJ/ObjectMapper, sem Spring/BD. Criada TaskInputTest com cinco métodos, dois parametrizados: 11 casos ao todo. Títulos válidos e de um carácter, cinco variantes inválidas na criação, três na edição, e omissão na edição. Sem novos limites nem dependências.

**Decisão da IA:** manter asserções centradas no título e no campo da exceção; não exigir mensagem integral, códigos HTTP ou persistência num teste unitário. Omissão de PATCH não é equivalente a null explícito. A fronteira escolhida é um carácter não branco, pois não existe máximo aprovado. Defaults de T02 continuam fora destas novas asserções. Nenhum resultado foi corrigido artificialmente nem regra alterada para obter sucesso.

**Execução real:** ./backend/mvnw.cmd -f backend/pom.xml -Dtest=TaskInputTest test → BUILD SUCCESS, 11 aprovados, 0 falhas, 0 erros, 0 ignorados. Não houve correção após execução; sem defeito identificado na regra selecionada. Relatórios locais Surefire, comando e limitações em [verification.md](../verification.md). Integração/E2E não executados neste exercício. Sem commit/push.


## Pedido adicional — Organização AAA (19/11/2026)

O utilizador pediu organizar os testes existentes em Arrange, Act e Assert, com comentários, e aplicar a convenção no futuro. Plano apresentado: comentários/blocos sem mudar cenários/asserções, com ciclos AAA adicionais nos percursos e Act & Assert nos pedidos/helpers e exceções que combinam as fases.

Aplicado nos 10 ficheiros de testes Java e 7 Playwright; convenção persistida em AGENTS.md. Comparação com a cópia anterior confirmou instruções/ordem/asserções preservadas, excluindo apenas comentários adicionados e linhas vazias. Não houve alteração de produção.

Execuções reais: Maven verify → 86 aprovados/0 falhas/0 erros/0 ignorados; Playwright → 25 aprovados/4 especiais ignorados. Primeira tentativa de E2E bloqueada pela rede do sandbox, interrompida e repetida com permissão; execução final passou. Resultados, comandos e avisos em verification.md. Sem commit/push.


## Exercício 3 — Integração da API e persistência (19/11/2026)

**Ferramenta:** ChatGPT/Codex. **Pedido:** implementar e executar criação/consulta real de tarefa e cenário negativo, com autenticação/contrato, BD isolada, limpeza e repetição, preservando os testes/documentos e sem mudar produção para obter sucesso.

**Plano apresentado/decisão:** reforçar o fluxo existente em TaskCrudTest em vez de o duplicar; acrescentar negativo focado com erro contratual e snapshots da BD. Reutilizar Spring Boot Test, MockMvc, JUnit, AssertJ, Testcontainers/Flyway/seed e login/CSRF reais. Comentários AAA explícitos. Sem framework, regra, endpoint ou limite novo.

**Implementação:** createDefaultsIdentityAndPersistenceInNewSession verifica oito campos, Location, nova sessão e SQL; whitespaceTitleReturnsProblemAndPreservesDatabase verifica 422 application/problem+json, VALIDACAO, campo titulo e seis tabelas intactas. Fluxo positivo usa título com acentos, descrição, projeto autorizado e prazo passado permitido; defaults TODO/MEDIUM.

**Resultados reais:** comando ./backend/mvnw.cmd -f backend/pom.xml -Dtest=TaskCrudTest test executado duas vezes, cada uma com novo PostgreSQL descartável. Ambas BUILD SUCCESS, 17 aprovados, zero falhas/erros/ignorados; tempos Maven 40,224 s e 29,950 s. Sem correção de testes ou produção necessária. Limites: MockMvc não valida servidor HTTP externo; não executados E2E nem suite backend integral neste exercício. Detalhes e evidências em verification.md. Sem commit/push.


## Exercício 4 — E2E (19/11/2026)

**Ferramenta:** ChatGPT/Codex. **Pedido:** automatizar autenticar → abrir projeto → criar tarefa → verificar listagem, com seletores estáveis, ambiente/dados fictícios isolados, limpeza, repetição e resultados reais, sem alterações de produção.

**Decisão da IA/plano apresentado:** reforçar o teste existente de criação/progresso em tasks.spec.ts em vez de duplicar o fluxo; adicionar presença única na lista, rótulos, reload, consulta posterior do mesmo ID/DTO e confirmação GET 404 na limpeza. Comentários AAA. Projeto preparado pela API, percurso de autenticação e criação de tarefa pela UI real.

**Execução:** Compose separado matriz-ficha04-e2e, app18083/BD55434/volume próprio, build/migrations/seed explícito/health. Playwright Chrome com --grep 'E2E criação em projeto' --repeat-each=2 → 2 passaram em 7,5 s, zero falhas/ignorados. afterEach limpou só IDs próprios e SQL confirmou baseline 2 utilizadores/2 projetos/3 tarefas/1 comentário/2 atribuições. Containers/rede isolados removidos com down sem -v, volume conservado. Sem falhas para investigar ou screenshots/traces necessários; sem alterar produção/contrato/dependências. Limites e comandos em verification.md; restantes suites não reexecutadas neste exercício. Sem commit/push.


## Registo Git — Ficha 04, exercícios 1–4 e organização AAA

O utilizador autorizou a criação de issues e commits em 19/11/2026. Registo retroativo explícito, sem reescrever o histórico: #11 plano, #12 unitários, #13 organização AAA, #14 integração, #15 E2E. Commits separados com Refs; os checkpoints anteriores "sem commit/push" descrevem o momento de cada exercício. Este pedido não inclui push; confirmação dos SHAs locais nas respetivas issues, abertas até publicação. A separação foi preparada no index sem substituir as fontes finais da pasta de trabalho; operações/asserções finais e evidências das execuções anteriores conservadas. Não houve nova execução de testes apenas para este registo Git. Exercício 5 ainda por realizar.
