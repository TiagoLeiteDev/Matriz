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
