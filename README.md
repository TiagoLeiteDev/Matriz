# Matriz

Aplicação colaborativa de gestão de projetos e tarefas, desenvolvida no curso AI-Powered Software Engineering.

## Estado atual

Exercícios 1–5 da ficha 03 implementados nos respetivos incrementos: base técnica, acesso e gestão de projetos. Já é possível iniciar sessão, criar/listar/pesquisar/consultar/editar/arquivar/restaurar/eliminar projetos pela interface, com PostgreSQL real e autorização por recurso. Tarefas: CRUD, lista, filtros, associação/transferência e progresso integrados no Exercício 5. **Colaboração de escrita, painel, Kanban e calendário continuam nos exercícios seguintes.** Sessão SEG-01: 60 minutos de inatividade, sem lembrar-me; reinício do backend e logout exigem novo login. Rastreabilidade Git: acesso na issue #4 e projetos na issue #5; publicação e commits referenciados nas próprias issues.

## Tecnologias aprovadas

Java 21, Spring Boot, Maven, Spring Security (sessão/cookie), PostgreSQL, Spring Data JPA e Flyway. Frontend React/TypeScript/Vite com CSS simples. JUnit, Spring Boot Test/MockMvc, Testcontainers/PostgreSQL, Playwright/TypeScript e ESLint. Docker/Compose e GitHub Actions para execução e verificações.

Entrega prevista: React compilado servido pelo Spring Boot, com PostgreSQL num segundo serviço Compose. Versões exatas em backend/pom.xml, frontend/package-lock.json e Dockerfile; configuração e dependências descritas em [Base técnica](docs/setup.md).

## Funcionalidades do MVP

- Sessão no servidor com cookie, consulta do utilizador autenticado e logout.
- Projetos com proprietário/colaboradores, pesquisa por nome, arquivo/restauro e eliminação confirmada em cascata.
- Tarefas de projetos autorizados e tarefas pessoais independentes privadas do criador.
- Estados TODO/IN_PROGRESS/DONE (Por fazer/Em curso/Concluída); prioridades LOW/MEDIUM/HIGH ou null, MEDIUM por omissão em novas tarefas; prazos opcionais.
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
| [OpenAPI](openapi.yaml) | API em /api; 16 operações implementadas e restantes marcadas como planeadas. |
| [Wireframes](docs/ui/wireframe-tarefas.md) | Tarefas colaborativas e início de sessão. |
| [Revisão](docs/architecture-review.md) | Rastreabilidade e limitações da base colaborativa. |
| [Plano](docs/implementation-plan.md) | Incrementos e verificações do Exercício 1 da ficha 03. |
| [Verificação](docs/verification.md) | Resultados de infraestrutura e cenários funcionais ainda pendentes. |
| [Tarefas](docs/tasks.md) | CRUD, filtros PESQ-02, estados TAR-01, migração V2 e transferências COL-06. |
| [Projetos](docs/projects.md) | CRUD, pesquisa PESQ-01, permissões, transações e limites do Exercício 4. |
| [Segurança](docs/security.md) | Política SEG-01, ciclo de sessão, identidade, matriz de autorização e limites. |
| [Base técnica](docs/setup.md) | Estrutura, dependências, variáveis, persistência, migrations, seed e riscos. |
| [Registo da ficha 3](docs/ai-logs/ficha-03.md) | Autorizações e trabalho documental atual. |
| [AGENTS](AGENTS.md) | Instruções para agentes e segurança. |

## Histórico

[Artefactos do MVP individual](docs/history/mvp-individual-v1/LEITURA.md) preservados. Os logs [ficha 1](docs/ai-logs/ficha-01.md) e [ficha 2](docs/ai-logs/ficha-02.md) mantêm as decisões históricas, superadas onde indicado por COL-01. Não usar o antigo contrato sem login como base atual.

## Instalação, execução e testes

### Arranque com Docker Compose (PowerShell, na raiz)

Requisitos: Git e Docker Desktop com motor Linux em execução. A primeira construção descarrega as imagens e dependências; requer Internet. Não é necessário Java/Node no host para esta opção.

```powershell
# Apenas na primeira configuração; não substituir um .env existente.
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
# Rever os valores fictícios locais; não usar estas palavras-passe em produção.
docker compose config --quiet
docker compose up -d db --wait
docker compose build app seed
# Aplica migrations e carrega a demonstração uma única vez; termina com código 0.
docker compose --profile tools run --rm seed
docker compose up -d app --wait
Invoke-RestMethod http://127.0.0.1:8080/actuator/health
```

Abrir http://127.0.0.1:8080 para iniciar sessão. O health devolve `{"status":"UP"}` quando o serviço e a BD estão disponíveis. Login/perfil/logout, gestão de projetos e tarefas em lista estão disponíveis; painel integral, Kanban/calendário e colaboração de escrita continuam por implementar. `seed` é uma ferramenta temporária, não um terceiro serviço permanente.

As migrations correm no arranque antes do seed. Para aplicar apenas o esquema, omitir o comando seed e iniciar app. Para carregar a demonstração mais tarde, executar explicitamente o mesmo comando seed. Uma nova execução não repõe dados editados/eliminados nem altera palavras-passe. Falhas/colisões fazem rollback; consultar os logs sem apagar dados para contornar o problema.

Contas fictícias: `ana@example.test` e `bruno@example.test`; palavra-passe inicial definida por `DEMO_PASSWORD` em `.env` (exemplo: `MatrizDemoOnly42!`). O hash é BCrypt. Usar estas contas no ecrã de login; não há registo público. Ana é proprietária do projeto partilhado e Bruno colaborador; o segundo projeto é privado do Bruno. Há também uma tarefa pessoal privada da Ana.

### Desenvolvimento no host (PowerShell)

Requisitos adicionais: JDK 21 e Node 24. Maven 3.9.16 é descarregado pelo Wrapper; não é necessário instalar Maven globalmente. Os dois métodos de arranque usam a mesma BD local, mas o backend local e app do Compose não devem ocupar a mesma porta ao mesmo tempo.

```powershell
docker compose stop app
docker compose up -d db --wait
. ./scripts/Load-LocalEnv.ps1
# Build do backend; os testes requerem Docker e usam uma BD descartável.
./backend/mvnw.cmd -f backend/pom.xml verify
# Arranque normal: migrations, sem seed.
java -jar backend/target/matriz-0.1.0-SNAPSHOT.jar
```

Para executar apenas o seed no host, parar primeiro o backend e executar, no mesmo ambiente configurado:

```powershell
java -jar backend/target/matriz-0.1.0-SNAPSHOT.jar --matriz.demo.seed-only=true
```

Noutro terminal, instalar/executar o frontend:

```powershell
cd frontend
npm.cmd ci
npm.cmd run dev
```

Vite mostra o endereço local (normalmente http://127.0.0.1:5173); encaminha `/api` e `/actuator/health` para o backend em 8080. Se mudares a porta do backend, ajustar o proxy. O JAR produzido diretamente pelo Maven não inclui o frontend; o Dockerfile integra o build React no Spring Boot.

Em Linux/macOS, usar `sh backend/mvnw -f backend/pom.xml verify` e `npm` em vez de `npm.cmd`; carregar variáveis no shell antes de executar o JAR. O helper PowerShell destina-se a pares `NOME=valor` sem aspas; não interpreta código.

### Verificações e diagnóstico

```powershell
./backend/mvnw.cmd -f backend/pom.xml verify
npm.cmd --prefix frontend run lint
npm.cmd --prefix frontend run build
docker compose ps
docker compose logs --tail 80 app
docker compose exec -T db pg_isready -U matriz_demo -d matriz
# Apenas contagens e histórico de migrations; nunca consultar hashes para demonstração.
docker compose exec -T db psql -U matriz_demo -d matriz -c "SELECT version, description, success FROM flyway_schema_history;"
docker compose exec -T db psql -U matriz_demo -d matriz -c "SELECT count(*) FROM utilizador; SELECT count(*) FROM projeto; SELECT count(*) FROM tarefa;"
```

Os exemplos SQL usam a conta/BD fictícias de `.env.example`; adaptar se forem alteradas. Após seed inicial: dois utilizadores, dois projetos, três tarefas, uma colaboração, duas atribuições e um comentário. Relatórios JUnit ficam em `backend/target/surefire-reports/` (não versionados); evidência resumida em [verification.md](docs/verification.md).

```powershell
# Reiniciar sem apagar dados.
docker compose restart db app
# Parar e remover containers/rede, conservando o volume.
docker compose down
# Voltar a executar com os dados conservados.
docker compose up -d db app --wait
```

Não usar `docker compose down -v` para um reinício: elimina o volume. O esquema evolui por novas migrations; não editar uma migration já aplicada. `.env`, caches, `node_modules` e `target` ficam fora do Git. A configuração atual é local; os testes E2E de acesso foram acrescentados no Exercício 3. CI, entrega integral e publicação continuam pendentes.

## Autenticação e sessão — Exercício 3

1. Abrir a aplicação e introduzir uma das contas fictícias com a DEMO_PASSWORD configurada no primeiro seed.
2. O cliente pede GET /api/sessao/csrf, mantém o cookie de sessão e envia POST /api/sessao com JSON `{email, palavraPasse}` e cabeçalho X-CSRF-Token. A palavra-passe é verificada com BCrypt; a resposta contém apenas id/nome/email.
3. O backend roda o identificador da sessão e o token CSRF. Guarda a autenticação na sessão em memória; o cliente obtém novo CSRF antes de uma nova escrita. Não há JWT, localStorage, sessionStorage ou lembrar-me.
4. GET /api/sessao devolve o perfil da identidade autenticada. A identidade nunca vem de um utilizadorId/proprietarioId/autorId enviado pelo cliente.
5. A sessão expira após **60 minutos de inatividade**. Reiniciar o backend exige novo login; não existe persistência de sessões. Cada pedido com sessão válida conta como atividade. A interface não faz polling automático.
6. Terminar sessão envia DELETE /api/sessao com CSRF atual, invalida a sessão no servidor e expira matriz_session. A sessão anterior deixa de autenticar.
7. Um 401 devolve a interface ao login com mensagem compreensível. Uma escrita sem CSRF pode devolver 403 antes de a sessão ser verificada. O cliente não repete escritas automaticamente.

Cookie matriz_session: HttpOnly, SameSite=Lax, Path=/, apenas cookie (sem URL rewriting), sem Max-Age persistente. Fechar o browser não substitui logout: alguns browsers restauram cookies de sessão. Todas as respostas de segurança ficam sem cache. A API devolve application/problem+json, sem credenciais ou detalhes internos.

**Produção HTTPS:** ativar o perfil `prod` (por exemplo, variável SPRING_PROFILES_ACTIVE=prod no ambiente de implantação) e definir APP_ALLOWED_ORIGINS com as origens HTTPS exatas da interface. O perfil ativa Secure no cookie. TLS tem de ser configurado na infraestrutura; esta configuração não cria um servidor HTTPS ou publicação automática. Não confiar em forwarded headers de origens arbitrárias. O Compose fornecido continua local, com portas em 127.0.0.1; não é uma configuração de publicação.

APP_ALLOWED_ORIGINS tem valores locais de exemplo em .env.example. O Compose encaminha esta variável; ao executar no host, usar o helper de ambiente. Se mudares portas/origens de desenvolvimento, ajustar a lista e o proxy Vite. A lista de origens é uma verificação adicional, não substitui CSRF e não ativa CORS permissivo.

### Superfície atualmente disponível

- Públicas: GET /api/sessao/csrf e POST /api/sessao (login exige CSRF).
- Com sessão: GET/DELETE /api/sessao.
- Com sessão e autorização por recurso: GET /api/projetos/{id}, GET /api/tarefas/{id}, GET /api/tarefas/{id}/comentarios e GET /api/tarefas/{id}/responsaveis.
- CRUD de projetos foi acrescentado no Exercício 4, conforme a secção seguinte. CRUD/listagem de tarefas foram acrescentados no Exercício 5. Restantes operações de colaboração/painel continuam bloqueadas.

O OpenAPI tem servidor relativo /api e x-implementation-status por operação. [security.md](docs/security.md) documenta a matriz e a distinção entre ator autenticado e responsável selecionado entre elegíveis.

### Testes de segurança e browser

```powershell
./backend/mvnw.cmd -f backend/pom.xml verify
npm.cmd --prefix frontend run lint
npm.cmd --prefix frontend run build
npm.cmd --prefix e2e ci
# Instalar o Chromium do Playwright, uma vez, ou usar Chrome já instalado.
cd e2e
npx.cmd playwright install chromium
cd ..
. ./scripts/Load-LocalEnv.ps1
npm.cmd --prefix e2e test
```

Para usar Chrome instalado, definir `$env:PLAYWRIGHT_CHANNEL = 'chrome'` antes do teste. Os testes precisam de app/BD em execução e do seed inicial. DEMO_PASSWORD deve corresponder à palavra-passe do primeiro seed; repetir seed não a altera. Em Linux/macOS, usar npm/npx e carregar variáveis no shell. Os testes de browser usam apenas contas fictícias e não apagam dados; os testes Java usam BD descartável.

O teste de expiração real é separado: por defeito fica ignorado para não aguardar os 60 minutos da aplicação. Para executá-lo contra uma instância temporária com prazo reduzido apenas de teste:

```powershell
docker compose run -d --no-deps --name matriz-session-test -p 127.0.0.1:18081:8080 app --server.servlet.session.timeout=1m --matriz.security.allowed-origins=http://127.0.0.1:18081
# Aguardar health UP em http://127.0.0.1:18081/actuator/health antes de executar.
$env:MATRIZ_BASE_URL = 'http://127.0.0.1:18081'
$env:MATRIZ_SHORT_SESSION_TEST = '1'
npm.cmd --prefix e2e run test:expiry
# No fim, parar/remover apenas o container temporário criado para este teste; sem volumes.
docker stop matriz-session-test
docker rm matriz-session-test
Remove-Item Env:MATRIZ_SHORT_SESSION_TEST, Env:MATRIZ_BASE_URL
```

O teste aguarda 65 segundos sem pedidos, de forma intencional: polling renovaria a atividade e invalidaria o cenário. Nunca reduzir silenciosamente o prazo da demonstração. Relatório HTML e screenshots de falha ficam em e2e/playwright-report e e2e/test-results (ignorados pelo Git); traces/vídeo desativados para não guardar cookies/credenciais de rede.

Para comprovar a invalidação no reinício (este comando reinicia explicitamente app, sem apagar BD/volume):

```powershell
npm.cmd --prefix e2e run test:restart
```

Os resultados reais e as limitações, incluindo a ausência de uma implantação HTTPS pública, constam de [verification.md](docs/verification.md).

## Gestão de projetos — Exercício 4

Após login, a secção Projetos mostra apenas projetos de que és proprietário ou colaborador. A listagem começa nos ativos; Mostrar projetos arquivados muda para o arquivo.

1. Escolher Criar projeto, indicar nome obrigatório e descrição opcional e Guardar projeto.
2. Confirmar o detalhe, proprietário inferido, situação e progresso; voltar a Projetos e abrir o projeto na lista.
3. Pesquisar por parte do nome: **sem distinguir maiúsculas nem acentos** (PESQ-01). Por exemplo, APRESENTACAO encontra Preparar apresentação. Pesquisa vazia/Limpar pesquisa remove a restrição; o filtro de arquivo mantém-se. Não há paginação no contrato.
4. Como proprietário, Editar projeto altera nome/descrição. Omitir um campo em PATCH conserva o valor; null limpa apenas descricao. Na UI, limpar a descrição envia null. Nome de um carácter é válido, nome vazio/só espaços é recusado. Não foram copiados limites 3–100/1000 dos exemplos.
5. Arquivar/Restaurar projeto muda a visibilidade sem apagar tarefas/dependentes. O progresso continua derivado de todas as tarefas do projeto; sem tarefas, 0% e Sem tarefas.
6. Eliminar projeto abre uma confirmação que explica a remoção definitiva de projeto/tarefas/comentários/atribuições/colaborações. Cancelar não envia DELETE. Só Confirmar eliminação executa o pedido; contas, outros projetos e tarefas pessoais são conservados.
7. Um colaborador pode consultar o projeto, mas não tem os controlos de gestão. A API recusa também pedidos diretos de edição/eliminação por colaboradores ou utilizadores sem acesso.

Operações disponíveis: POST/GET /api/projetos e GET/PATCH/DELETE /api/projetos/{id}. Criação devolve 201, Location e DTO; consultas/edição devolvem 200; eliminação 204 após commit. Validação 422, pedido malformado/campos proibidos/UUID inválido 400, ausência de sessão 401, permissão/CSRF 403, recurso inexistente 404 e falha interna genérica 500. Todas as escritas exigem CSRF. Proprietário/ID não são aceites como campos de criação/edição.

Os dados ficam no PostgreSQL com volume persistente; a V1 não mudou. As cascatas são transacionais: uma falha não confirma eliminação parcial. Formulários conservam entradas em falha e não anunciam sucesso nem repetem escritas automaticamente. Uma falha de comunicação após envio não garante que a operação tenha sido revertida; consultar o estado antes de tentar novamente se o resultado for desconhecido.

### Verificação manual do primeiro percurso completo

- Iniciar sessão como ana@example.test e criar um projeto com nome/descrição próprios.
- Voltar à lista, abrir o projeto, editar e recarregar a página; verificar os valores guardados.
- Executar `docker compose restart app`, aguardar health UP, Atualizar perfil e iniciar sessão novamente. O projeto/descrição mantêm-se, incluindo o detalhe indicado pelo fragmento #projetos/{id}.
- Pedir eliminação, cancelar e verificar que o projeto permanece. Pedir novamente e confirmar; verificar a ausência na lista.
- Entrar como bruno@example.test: consultar o projeto partilhado da Ana, sem controlos de gestão. Como Ana, o projeto privado do Bruno não aparece.

### Testes automatizados de projetos

```powershell
./backend/mvnw.cmd -f backend/pom.xml verify
npm.cmd --prefix frontend run lint
npm.cmd --prefix frontend run build
. ./scripts/Load-LocalEnv.ps1
$env:PLAYWRIGHT_CHANNEL = 'chrome' # Se usares Chrome já instalado; alternativa Chromium do Playwright.
npm.cmd --prefix e2e test
# Percurso separado que reinicia app explicitamente e volta a autenticar.
$env:MATRIZ_RESTART_TEST = '1'
npm.cmd --prefix e2e run test:projects-restart
Remove-Item Env:MATRIZ_RESTART_TEST
```

O comando normal ignora por desenho os testes de expiração curta e de reinício; executar cada um separadamente nos ambientes indicados. Os testes de projetos criam fixtures pela API/UI e removem apenas os IDs que registaram; não eliminam os projetos do seed. Os testes de falha/cascata usam PostgreSQL descartável no backend. Ver [evidências](docs/verification.md).

## Entrega

19/11/2026 às 17h, hora de Lisboa. Incluir frontend/backend, testes, dependências, esquema/migrations, seed, configuração de exemplo sem segredos, documentação atualizada e URL GitHub acessível ao formador. Demonstração de 1–2 fluxos em até cinco minutos, com tecnologias, metodologia/IA, desafio e aprendizagem.

## Gestão de tarefas — Exercício 5

Após login, escolher Tarefas ou abrir um projeto ativo. Nova tarefa permite indicar título obrigatório, descrição, projeto (ou pessoal), estado, prioridade e prazo. Guardar confirma a persistência antes de atualizar a lista/progresso; abrir uma tarefa permite editar ou eliminar com confirmação. Cancelar a eliminação não envia pedidos.

Aplicar filtros combina título sem maiúsculas/acentos, projeto/Sem projeto, estado, prioridade/Sem prioridade e datas inclusivas. Limpar filtros repõe o conjunto autorizado. As contagens identificam apenas essa lista filtrada; não representam o painel ainda pendente. As listas excluem projetos arquivados; leitura autorizada por ID continua possível.

A V2 corre no arranque: conserva IDs, relações e prioridades null existentes; converte os estados/prioridades anteriores segundo TAR-01. Não editar V1 nem apagar o volume. Novas tarefas sem prioridade enviada assumem MEDIUM; enviar null mantém Sem prioridade. Transferências conservam criador/comentários e removem atribuições inelegíveis atomicamente. Ao desassociar uma tarefa criada por outro utilizador pode perder-se acesso; a UI informa essa consequência.

Teste separado de persistência de tarefas, com reinício explícito de app (sessão anterior invalida-se):

```powershell
. ./scripts/Load-LocalEnv.ps1
$env:PLAYWRIGHT_CHANNEL = 'chrome'
$env:MATRIZ_TASK_RESTART_TEST = '1'
npm.cmd --prefix e2e run test:tasks-restart
Remove-Item Env:MATRIZ_TASK_RESTART_TEST
```

Issue do incremento: [#6](https://github.com/TiagoLeiteDev/Matriz/issues/6). Ver [contratos e limites](docs/tasks.md) e [evidências](docs/verification.md).
