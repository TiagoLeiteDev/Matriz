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
