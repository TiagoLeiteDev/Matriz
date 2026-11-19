# Matriz

**Versão candidata: 0.1.0-rc.1.** [Changelog](CHANGELOG.md) · [Notas de versão](docs/release-notes.md).

Aplicação colaborativa de gestão de projetos e tarefas, desenvolvida no curso AI-Powered Software Engineering.

## Funcionalidades e estado

- Iniciar sessão, consultar perfil e terminar sessão; contas fictícias por seed.
- Criar/pesquisar/editar/arquivar/restaurar/eliminar projetos autorizados.
- Gerir colaboradores pelo proprietário, incluindo em projetos arquivados; remover acesso limpa atribuições inelegíveis atomicamente.
- Criar/editar/eliminar tarefas pessoais privadas ou tarefas de projetos acessíveis; transferir/desassociar com autorização.
- Estados TODO/IN_PROGRESS/DONE e prioridades LOW/MEDIUM/HIGH ou Sem prioridade. Na criação, prioridade omitida = MEDIUM; null explícito conserva ausência.
- Lista, Kanban e calendário, pesquisa sem maiúsculas/acentos, filtros combinados e progresso dos projetos.
- Vários responsáveis elegíveis e comentários com autor/data do backend; conteúdo novo de 1–2000 pontos de código Unicode, não apenas espaços.
- Painel com projetos ativos acessíveis e contagens que incluem tarefas pessoais e excluem arquivados, independentemente dos filtros da lista.

Os nove cenários funcionais foram verificados no Exercício 8. A revisão de entrega do Exercício 9 está descrita em [delivery-review.md](docs/delivery-review.md), com resultados e pendências efetivos. O workflow [Matriz CI](https://github.com/TiagoLeiteDev/Matriz/actions/workflows/ci.yml) verifica testes, builds e execução Compose; resultados reais e prova de falha/recuperação são registados em [verification.md](docs/verification.md). Não confundir testes locais com CI ou ensaio noutro computador.

Sem organizações/equipas globais, registo público, recuperação de palavra-passe, notificações, recorrência ou edição/eliminação autónoma de comentários. Publicação na Internet não preparada/aprovada.

## Tecnologias

Java 21, Spring Boot 3.5.16, Maven Wrapper 3.9.16, Spring Security, PostgreSQL 17.9, JPA e Flyway. React/TypeScript/Vite/CSS. JUnit/MockMvc/Testcontainers/PostgreSQL, Playwright/TypeScript e ESLint. Docker Compose compila o React e serve-o no Spring Boot, com PostgreSQL num segundo serviço. Versões/imagens fixadas em pom.xml, lockfiles e Dockerfile/compose.yaml.

## Instalação com Docker — opção de demonstração

Pré-requisitos: Git, Docker Desktop com motor Linux e Internet para descarregar imagens/dependências. Não exige Java ou Node no computador para construir/arrancar esta opção.

```powershell
git clone https://github.com/TiagoLeiteDev/Matriz.git
cd Matriz
# Não substituir um .env existente.
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
# Rever configuração fictícia antes de arrancar.
docker compose config --quiet
docker compose up -d db --wait
docker compose build app seed
# Migrations + seed explícito, apenas na preparação da demonstração.
docker compose --profile tools run --rm seed
docker compose up -d app --wait
Invoke-RestMethod http://127.0.0.1:8080/actuator/health
```

Abrir [http://localhost:8080](http://localhost:8080). Health esperado: `{"status":"UP"}`. As dependências de build são instaladas pela imagem; `seed` é uma ferramenta temporária, não um serviço permanente.

### Configuração

`.env.example` contém apenas valores fictícios. `.env` fica fora do Git e da imagem. Variáveis: POSTGRES_DB/USER/PASSWORD, DB_URL/USER/PASSWORD para o backend no host, DEMO_PASSWORD para o seed, APP_PORT/DB_PORT para portas Compose e APP_ALLOWED_ORIGINS para origens autorizadas. Não imprimir nem versionar segredos reais.

Portas padrão: app 8080, PostgreSQL 55432, apenas em 127.0.0.1. Se estiverem ocupadas, alterar APP_PORT/DB_PORT e APP_ALLOWED_ORIGINS em `.env`; para desenvolver no host, ajustar também DB_URL e o proxy Vite. O Compose fornece DB_URL interno para o serviço db.

Para ensaiar uma segunda instalação, usar portas diferentes e `docker compose -p matriz-delivery-review ...` em **todos** os comandos, incluindo seed/restart/down. Esse nome separa containers, rede e volume; não misturar comandos de projetos diferentes.

### Migrations e dados de demonstração

Flyway aplica V1/V2 no arranque; Hibernate apenas valida. Nunca editar migrations aplicadas ou apagar o volume para contornar uma falha. O arranque normal não executa seed. Repetir o seed não repõe edições/remoções nem muda palavras-passe; colisões fazem rollback.

Contas fictícias: `ana@example.test` e `bruno@example.test`; palavra-passe inicial é DEMO_PASSWORD definida no primeiro seed (o exemplo público fictício é `MatrizDemoOnly42!`, sem utilização em produção). Guardada como BCrypt. Ana possui o projeto partilhado, Bruno é colaborador e possui outro projeto privado; há uma tarefa pessoal da Ana. Seed inicial: 2 utilizadores, 2 projetos, 3 tarefas, 1 colaboração, 2 atribuições e 1 comentário.

V2 converte estados/prioridades anteriores e conserva prioridades null preexistentes. Novos registos usam os defaults aprovados. Seed e migrations estão versionados no backend.

## Desenvolvimento no host

Requisitos adicionais: JDK 21, Node 24, Docker para PostgreSQL/Testcontainers. Maven é descarregado pelo Wrapper.

```powershell
# Evitar app Compose e backend do host na mesma porta.
docker compose stop app
docker compose up -d db --wait
. ./scripts/Load-LocalEnv.ps1
./backend/mvnw.cmd -f backend/pom.xml verify
java -jar backend/target/matriz.jar
```

Noutro terminal:

```powershell
cd frontend
npm.cmd ci
npm.cmd run dev
```

Vite normalmente em 127.0.0.1:5173, com proxy /api e /actuator/health para 8080; consultar o endereço efetivo no terminal. O JAR produzido diretamente pelo Maven não integra o React; a imagem Docker integra-o. Para seed no host, com backend parado e ambiente carregado: `java -jar backend/target/matriz.jar --matriz.demo.seed-only=true`.

Em Linux/macOS usar `sh backend/mvnw -f backend/pom.xml verify` e npm/npx sem `.cmd`, carregando as variáveis no shell. Os ensaios registados foram feitos em Windows/Docker Linux; não afirmar validação de outros sistemas.

## Testes e diagnóstico

```powershell
./backend/mvnw.cmd -f backend/pom.xml verify
npm.cmd --prefix frontend ci
npm.cmd --prefix frontend run lint
npm.cmd --prefix frontend run build
npm.cmd --prefix e2e ci
# Uma vez: instalar Chromium, ou usar Chrome instalado como abaixo.
cd e2e
npx.cmd playwright install chromium
cd ..
. ./scripts/Load-LocalEnv.ps1
# Se usar Chrome instalado; omitir esta variável para Chromium do Playwright.
$env:PLAYWRIGHT_CHANNEL = 'chrome'
npm.cmd --prefix e2e test
```

App/BD devem estar disponíveis e o seed preparado. Para outra porta: `$env:MATRIZ_BASE_URL = 'http://127.0.0.1:18082'`. DEMO_PASSWORD tem de corresponder ao primeiro seed. Testes API/browser usam fixtures próprias e removem só esses IDs; backend usa PostgreSQL descartável, nunca o volume da demonstração. Relatórios em backend/target/surefire-reports, e2e/playwright-report e e2e/test-results, fora do Git; traces/vídeos desativados para não guardar credenciais/cookies.

Os quatro testes especiais ficam ignorados na suite normal. [verification.md](docs/verification.md) tem comandos para três reinícios separados e expiração real em instância temporária de um minuto. Não reduzir silenciosamente os **60 minutos** da aplicação principal. Testes automatizados não equivalem a verificações manuais humanas.

```powershell
docker compose ps
docker compose logs --tail 80 app
docker compose exec -T db pg_isready -U matriz_demo -d matriz
docker compose exec -T db psql -U matriz_demo -d matriz -c "SELECT version, description, success FROM flyway_schema_history;"
# Reiniciar sem apagar dados.
docker compose restart db app
# Remover containers/rede conservando o volume.
docker compose down
docker compose up -d db app --wait
```

Conta/BD SQL dos exemplos são fictícias; adaptar se alteradas. Não usar `down -v`: elimina o volume. Falha de rede após uma escrita pode ter resultado desconhecido; consultar o estado antes de repetir. Formulários conservam entradas em erro e não repetem escritas automaticamente.

## Utilização e segurança

Após login, navegar em Painel/Projetos/Tarefas. Proprietário gere projeto/colaboradores; proprietário e colaboradores gerem tarefas de projetos ativos. Tarefa pessoal só do criador. Arquivar conserva dados e bloqueia escritas das tarefas/dependentes; proprietário pode gerir colaboradores em arquivo (COL-07). Eliminações têm confirmação; cancelar não envia DELETE. Remoção de colaborador/transferência conserva autoria dos comentários e remove atribuições inelegíveis atomicamente.

Alternar Lista/Kanban/Calendário mantém filtros. Calendário mensal, segunda-feira primeiro, prazo como data sem conversão UTC; tarefas sem prazo ficam na lista/Kanban. Em ecrã pequeno há grelha com contagens e agenda legível. O detalhe de tarefa em projeto usa `#projetos/{id}/tarefas/{id}` e restaura após reload; global usa `#tarefas/{id}`. Painel consulta dados confirmados ao regressar/atualizar, sem polling.

Sessão no servidor, 60 minutos de inatividade, sem lembrar-me; reinício/logout exigem novo login. Cookie matriz_session HttpOnly/SameSite=Lax; Secure no perfil prod/HTTPS. CSRF em todas as escritas, incluindo login/logout; token renovado após autenticação. Identidade/autor/data do backend, nunca impostos pelo cliente. Não há armazenamento persistente de credenciais/tokens no frontend. Perfil prod não instala TLS: infraestrutura HTTPS tem de ser preparada; Compose é local.

## Artefactos e entrega

- Frontend/backend completos, pom.xml/Wrapper, package.json/lockfiles e testes.
- V1/V2, seed explícito, Dockerfile/compose.yaml, `.env.example` e helper de ambiente.
- [AGENTS](AGENTS.md), [requisitos](docs/requirements.md), [22 histórias/88 critérios](docs/user-stories.md), [restrições](docs/constraints.md).
- [Arquitetura](docs/architecture.md), [modelo](docs/database.md), [OpenAPI 3.1](openapi.yaml), [wireframes/capturas](docs/ui/wireframe-tarefas.md).
- [Plano](docs/implementation-plan.md), [verificações](docs/verification.md), [revisão de entrega](docs/delivery-review.md) e [registo de IA](docs/ai-logs/ficha-03.md).

Histórico individual em docs/history/mvp-individual-v1 e logs das fichas 01/02, sem o tratar como especificação ativa. A entrega prevista é 19/11/2026 às 17h, Lisboa; apresentação/validação humana e CI têm estado explícito no relatório final. Não há publicação automática ou commits implícitos.


## Ficha 04 — testes focados e revisão

Plano em [docs/test-plan.md](docs/test-plan.md), execuções em [docs/verification.md](docs/verification.md) e revisão em [docs/quality-security-review.md](docs/quality-security-review.md).

```powershell
# Unitários sem serviços externos.
./backend/mvnw.cmd -f backend/pom.xml -Dtest=TaskInputTest test
# Integração: Docker disponível; PostgreSQL Testcontainers descartável.
./backend/mvnw.cmd -f backend/pom.xml -Dtest=TaskCrudTest test
# E2E/API: preparar instância isolada e ambiente conforme verification.md.
. ./scripts/Load-LocalEnv.ps1
$env:PLAYWRIGHT_CHANNEL = 'chrome'
$env:MATRIZ_BASE_URL = 'http://127.0.0.1:18083'
npm.cmd --prefix e2e test -- tests/tasks.spec.ts --grep 'E2E criação em projeto' --repeat-each=2
npm.cmd --prefix e2e test -- tests/security-review.spec.ts --repeat-each=2
npm.cmd --prefix frontend run lint
```

A porta 18083 refere-se à instância de ensaio; iniciar os serviços antes dos testes. O relatório documenta Q01/R01 e decisões pendentes; não confundir verificações dirigidas com auditoria integral.


Regressões do exercício 5: `npm.cmd --prefix e2e test -- tests/cleanup.spec.ts` não necessita de aplicação/BD. Para UI Unicode e autorização, com a instância isolada pronta e ambiente configurado: `npm.cmd --prefix e2e test -- tests/cleanup.spec.ts tests/tasks.spec.ts tests/security-review.spec.ts`. O teste de reinício continua opt-in e fica ignorado no comando normal.


## Integração contínua e versão candidata

.github/workflows/ci.yml corre em push, pull_request e execução manual, com Java21/Node24.16.0 e actions fixadas por SHA. Instala pelos lockfiles, executa lint/build e Maven verify antes de construir as imagens; prepara Compose/seed fictício, executa Playwright normal e três reinícios separados. permissions contents:read; sem credenciais de checkout persistidas ou secrets de produção. Expiração real mantém execução separada (não reduzir os 60 minutos da aplicação normal).

COMPOSE_PROJECT_NAME identifica o ambiente em todos os comandos, incluindo subprocessos de reinício. Em CI usa o ID da execução; localmente escolher nome próprio, APP_PORT/DB_PORT e APP_ALLOWED_ORIGINS compatíveis. A variável deve estar presente no terminal que lança Playwright. Não misturar com a instância principal.

scripts/ci-summary.py gera artifacts/ci-summary.json apenas com contagens de testes; a pipeline publica esse resumo durante sete dias. Não publica .env, relatórios brutos, cookies, payloads, screenshots ou traces. O build Docker salta testes internamente porque os mesmos são obrigatórios na etapa anterior da CI.

A versão candidata está nos manifests; o JAR usa nome estável backend/target/matriz.jar. A identificação Git final e evidências ficam em docs/release-notes.md/verification.md. A preparação não publica a aplicação ou imagens num registry.
