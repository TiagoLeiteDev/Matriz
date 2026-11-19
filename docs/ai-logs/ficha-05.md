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


## Causa confirmada do falso positivo no comentário

Run 44528619364: 29 E2E passaram/1 falhou/4 ignorados; logout corrigido. Comentário GET200 mas conteúdo ausente após reload. Probe temporário no clone (apenas diagnóstico, sem commit) executou o seletor antes de submeter e devolveu COMMENT_SELECTOR_PROBE ['TEXTAREA']: getByText encontrava o rascunho, não um comentário persistido. Teste corrigido para exigir POST201, ID/conteúdo e listitem real; reload valida o mesmo ID na resposta e na UI. Acrescentada asserção de que o rascunho não aparece na lista antes do envio. Não houve alteração de produção nem diagnóstico de perda de dados. O probe será removido do clone.


## Exercício 6 — Validação final e decisões de entrega 0.1.0-rc.1 (19/11/2026)

**Fonte/versão:** main d6abb5c9f2634e5bda68b0b3bf25ac9860f354ab; manifests 0.1.0-rc.1, JAR estável matriz.jar e contrato funcional OpenAPI 0.7.0. O fecho documental/tag será confirmado na issue #20 com o respetivo SHA, sem reescrever histórico.

### CI real e falha/recuperação

- Main [run 44529912559](https://github.com/TiagoLeiteDev/Matriz/actions/runs/44529912559) **success**, job 135580175683: lint/build frontend, **95 backend (0 falhas/erros/ignorados)**, build das imagens, Compose/seed/health, **30 normais (4 especiais ignorados)** e **3 reinícios separados** aprovados. Resumo seguro publicado; cleanup aprovado. Não contabilizar quatro ignorados como aprovados; expiração não corre neste workflow.
- Falha controlada [run 44525427870](https://github.com/TiagoLeiteDev/Matriz/actions/runs/44525427870), SHA0446cb0, branch ci/ficha05-failure-demo: 96 testes/1 falha deliberada, backend failure, imagens skipped. Teste removido no commit1485bbc. Ensaios intermédios da recuperação detetaram problemas de E2E/relatório, descritos abaixo. [Run 44530492017](https://github.com/TiagoLeiteDev/Matriz/actions/runs/44530492017), SHAfa50155, concluiu **success**; teste deliberadamente falhado ausente do HEAD. A branch de demonstração tem a base anterior da app, sem mudança da lógica de negócio; não foi integrada em main.
- Primeiras tentativas main44525278059 e44526657622 falharam na etapa E2E. Relatório misturado com stdout não era legível; corrigido para ficheiro JSON próprio. Novos dados identificaram logout seguido de navegação antes do fim e getByText a encontrar o rascunho no textarea. Probe confirmou TEXTAREA antes do envio. Testes corrigidos para esperar login/perfil401, POST201 e listitem/ID real; sem sleeps/retries/aumento de timeout nem correção de produção por hipótese. Run44528619364 tinha29 passes/1falha antes do seletor final; o resultado verde acima corresponde à correção efetiva.

### Clone limpo e execução independente

`git clone --no-hardlinks https://github.com/TiagoLeiteDev/Matriz.git tmp/ficha05-clean`, configuração nova copiada de .env.example e sem overlay de fontes locais. Clone atualizado por fetch/checkout detached para d6abb5c; git status limpo. Projeto Compose **matriz-ficha05-clean**, app18087/BD55437/rede/volume próprios. COMPOSE_PROJECT_NAME definido no processo e herdado pelos reinícios. App principal/volume não alterados.

Comandos executados no clone: config --quiet; build app seed; up -d db --wait; --profile tools run --rm seed; up -d app --wait; npm --prefix e2e ci. Browser Chromium159.0.8154.6/v1255 instalado pelo Playwright1.65.1; sem canal Chrome no ensaio final.

```powershell
. ./scripts/Load-LocalEnv.ps1
$env:COMPOSE_PROJECT_NAME = 'matriz-ficha05-clean'
$env:APP_PORT = '18087'
$env:DB_PORT = '55437'
$env:APP_ALLOWED_ORIGINS = 'http://127.0.0.1:18087'
$env:MATRIZ_BASE_URL = 'http://127.0.0.1:18087'
$env:CI = 'true'
Remove-Item Env:PLAYWRIGHT_CHANNEL -ErrorAction SilentlyContinue
$env:PLAYWRIGHT_JSON_OUTPUT_FILE = Join-Path (Get-Location) 'e2e-normal.json'
npm.cmd --prefix e2e test -- --reporter=json
```

Resultado final **30 passaram/4 ignorados em35,0s**. Três reinícios separados passaram em54,8s, com flags MATRIZ_RESTART_TEST/MATRIZ_TASK_RESTART_TEST/MATRIZ_COLLAB_RESTART_TEST=1. Invocação Node direta a partir de e2e: `node node_modules/@playwright/test/cli.js test --grep '@restart|@task-restart|@collaboration-restart' --reporter=json`. Primeira tentativa pelo npm.cmd no Windows interpretou o pipe como shell e falhou antes dos testes; corrigida invocação, não a aplicação.

Os dois cenários de sincronização foram repetidos três vezes cada (6 passaram/13,6s) antes da correção final do seletor; a suite normal completa foi novamente executada depois desta correção, com30 passes. Probe temporário do seletor removido do clone; não integra a versão entregue.

Expiração real separada: override temporário JSON no diretório ignorado tmp, app SERVER_SERVLET_SESSION_TIMEOUT=1m, flag MATRIZ_SHORT_SESSION_TEST=1, apenas auth.spec.ts --grep @expiry. **1 passou após65s sem pedidos**, execução1,1min; finalmente restaurado Compose sem override e health saudável, política normal60min reposta. Não confundir timeout de teste com política de produção.

Persistência final: probe com projeto arquivado/tarefa/atribuição/comentário autor/instante e tarefa eliminada; down sem -v/up recriou containers. Novo login confirmou IDs/campos/arquivo/dependentes e tarefa eliminada404; fixtures removidas. SQL final: **2 utilizadores, 2 projetos, 3 tarefas, 1 comentário, 2 atribuições**. V1/V2 success, sem migration nova. Container uid100/gid101 (matriz), app.jar mostra0.1.0-rc.1; logs de arranque inspecionados sem erro crítico encontrado no âmbito pesquisado.

### Documentação, segurança e limites

README/AGENTS/arquitetura/plano, changelog/notas e manifesto159ficheiros atualizados. Versões dos manifests/lockfiles coerentes, links locais conferidos e git diff --check sem erros. Pesquisa limitada no conjunto versionado por padrões de chaves privadas/tokens GitHub/AWS: nenhum ficheiro sinalizado; não é certificação universal nem scanner de dependências. .env/gerados/dependências/relatórios/clones fora do Git/imagem.

Workflow usa actions oficiais fixadas por SHA, contents:read, checkout sem credenciais persistidas, projeto/valores fictícios de CI, testes antes das imagens e resumo sem payloads/cookies/ambiente. Resumo inclui contagens e identificação estática de falhas, não relatórios brutos. Push de branches/PR/manual verifica CI; tags de identificação não duplicam a mesma execução.

Não executados: apresentação/aceitação manual do formador, publicação pública/HTTPS/registry, outros browsers/sistemas fora dos ensaios, testes de carga/acessibilidade ou pentest integral. GitHub runner Linux e clone Windows são execuções reais em ambientes distintos; não equivalem a validação universal. Notas da candidata referenciam a confirmação final da tag/publicação na issue #20.
