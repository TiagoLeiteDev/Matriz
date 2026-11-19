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


## Exercícios 2/3 — Docker/Compose e evidência local (19/11/2026)

Base a9681f7/1933486, configuração existente e pipeline fe5ad97. Java21/Maven verify: **95 passaram, 0 falhas/erros/ignorados**, 2 min 56 s. Frontend lint/build passaram. Compose matriz-ficha05-local com app18086/BD55436/rede/volume próprios: config --quiet, build app seed, migrations V1/V2, seed explícito e health passaram. Suite normal Playwright: **30 passaram, 4 especiais ignorados**, 1,1 min; não aprovar os ignorados automaticamente.

Probe com fixtures próprias: projeto arquivado, tarefa com responsável/comentário/autor/instante e tarefa eliminada. docker compose down sem -v e up -d db app --wait recriaram containers conservando o volume. Novo login confirmou os mesmos IDs/campos, arquivo, comentário, atribuição e 404 da tarefa eliminada. Probe limpou apenas os IDs criados. SQL baseline final: 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário, 2 atribuições. Últimos 80 logs de arranque sem ERROR/Exception na pesquisa realizada; V2 já aplicada, nenhuma migration nova. Não equivale a auditoria de todos os logs.

Variáveis no mesmo processo: COMPOSE_PROJECT_NAME=matriz-ficha05-local, APP_PORT=18086, DB_PORT=55436, APP_ALLOWED_ORIGINS e MATRIZ_BASE_URL=http://127.0.0.1:18086, PLAYWRIGHT_CHANNEL=chrome; DEMO_PASSWORD carregada sem impressão. App principal/volume não alterados. Evidências geradas locais fora do Git. Scripts de probe temporários não fazem parte da aplicação entregue.

Pipeline criada e publicada em fe5ad97; execução main https://github.com/TiagoLeiteDev/Matriz/actions/runs/44525278059 ainda em curso ao registar esta etapa. Falha deliberada 0446cb0 em branch ci/ficha05-failure-demo: run https://github.com/TiagoLeiteDev/Matriz/actions/runs/44525427870 concluiu failure no passo backend; construir imagens ficou skipped. O teste AAAFailureDemonstrationTest foi removido em 1485bbc; recuperação publicada e aguarda resultado. Falha não entrou em main, sem reescrever histórico.


## Exercício 5 — Candidata 0.1.0-rc.1 preparada

Manifests Maven/frontend/E2E e lockfiles alinhados sem mudar versões de dependências. Maven finalName=matriz e Dockerfile copia matriz.jar, eliminando dependência do nome da versão no caminho. README/AGENTS/arquitetura, CHANGELOG.md, notas da candidata e manifesto de 159 ficheiros atualizados. Maven package -DskipTests passou, manifest do JAR confirmou Implementation-Version 0.1.0-rc.1; não apresentado como nova execução de testes. A tag só será criada após validação final/CI. A linha vazia externa no Dockerfile mantém-se fora do index; alterações funcionais do Dockerfile limitadas ao caminho do JAR.


## Troubleshooting da primeira CI

Run main 44525278059: backend/lint/build/imagens/arranque passaram, passo E2E falhou. Resumo report_unavailable: mistura de reporter list/json na mesma saída não produziu JSON isolado para o parser. Corrigido workflow para PLAYWRIGHT_JSON_OUTPUT_FILE, conforme documentação oficial, sem redirecionamento misto; resumo tolera texto posterior e inclui apenas título/localização estáticos dos testes falhados (não payloads/erros/cookies). A causa funcional da falha E2E ainda não é conhecida nesta etapa; será investigada com a nova evidência. Não corrigir código da aplicação por hipótese.


## Diagnóstico E2E em CI e sincronização limitada

Run 44526657622: 95 backend passaram; E2E 28 passaram/2 falharam/4 ignorados. Resumo corrigido identificou mvp-verification.spec.ts:158 (comentário após reload) e projects.spec.ts:159 (segundo login após logout). Inspeção mostrou navegação imediata após click logout, sem esperar a invalidacão; corrigido teste para esperar heading login e GET perfil401 antes de navegar. No reload, esperar resposta real de comentários200 e verificar conteúdo persistido antes da asserção UI. Mantidas todas as asserções, sem sleeps, retries, aumento de timeout ou alteração de produção. Falha reload pode ser sincronização/ambiente; não se declara causa funcional confirmada. CLI de reinícios passou a Node direta para evitar interpretação de pipe pelo npm.cmd no Windows; Linux usa a mesma invocação.
