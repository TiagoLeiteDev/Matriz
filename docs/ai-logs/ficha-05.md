# Ficha prática 05 — DevOps e documentação

## Exercício 1 — Análise e plano aprovado (19/11/2026)

**Ferramenta:** ChatGPT/Codex. **Base:** a9681f7. O utilizador pediu análise técnica sem alterações; a análise foi apresentada na conversa e o utilizador autorizou depois "podes prosseguir com o plano".

Factos: monólito modular Java21/Spring Boot3.5.16 + React19.3.1/TypeScript5.9.3/Vite8.3.8, PostgreSQL17.9, Flyway; Docker multi-stage e Compose existentes; Node24.16.0/Maven3.9.16 nas imagens; JUnit/MockMvc/Testcontainers/Playwright1.65.1/ESLint. CI, changelog e notas de versão ainda ausentes na base. A linha vazia local no Dockerfile foi preservada.

Plano aprovado: issues #17 base/Compose, #18 CI/falha-recuperação, #19 versão candidata/documentação, #20 validação final; reutilizar imagens/arquitetura/serviços existentes, testes antes de construir imagens, ambiente isolado, GitHub Actions push/PR e versão proposta 0.1.0-rc.1. Sem novas funcionalidades ou publicação pública da aplicação.

As actions foram consultadas nos repositórios oficiais e SHAs confirmados por git ls-remote. Docker documenta que COMPOSE_PROJECT_NAME prevalece sobre name: no Compose: será usada a mesma identidade nos subprocessos de reinício. Playwright recomenda instalar browser/dependências no runner. Referências: https://docs.docker.com/compose/how-tos/project-name/ ; https://github.com/actions/setup-node/tree/v6 ; https://github.com/actions/setup-java/tree/v5 ; https://playwright.dev/docs/ci .

Esta secção regista o plano e fontes, não resultados de CI ainda inexistentes. As evidências de execução serão acrescentadas após cada etapa.


## Exercício 4 — Pipeline preparada (estado inicial)

.github/workflows/ci.yml criado com actions oficiais fixadas por SHA (checkout v6/setup-node v6/setup-java v5/upload-artifact v4), permissions contents:read e credenciais de checkout não persistidas. Trigger push/PR/manual; Java21/Node24.16.0, lockfiles, lint/build, Maven verify antes das imagens, Compose próprio, suite normal e três reinícios em etapa separada. COMPOSE_PROJECT_NAME acompanha os subprocessos. Expiração real continua separada, sem reduzir a sessão normal.

scripts/ci-summary.py exporta apenas contagens, omitindo payloads/cookies/ambiente dos relatórios originais; artefactos com retenção de sete dias. Ensaio local com marcadores fictícios confirmou contagens e omissão dos dados. Estrutura YAML/triggers/permissões/17 passos verificados; lint/build locais passaram. Suite backend e Compose/E2E locais estão em execução; nenhum resultado Actions declarado nesta etapa. Resultados finais serão acrescentados.
