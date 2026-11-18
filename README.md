# Matriz

Aplicação colaborativa de gestão de projetos e tarefas, desenvolvida no curso AI-Powered Software Engineering.

## Estado atual

Exercício 1 da ficha 03 concluído. Exercício 2 prepara a base executável: Spring Boot, PostgreSQL, migrations Flyway, seis entidades JPA, seed explícito, testes de infraestrutura e página técnica React. **Login, autorização por recurso e funcionalidades de gestão ainda não estão implementados.** A API de negócio permanece bloqueada; o OpenAPI é o contrato para os próximos incrementos.

## Tecnologias aprovadas

Java 21, Spring Boot, Maven, Spring Security (sessão/cookie), PostgreSQL, Spring Data JPA e Flyway. Frontend React/TypeScript/Vite com CSS simples. JUnit, Spring Boot Test/MockMvc, Testcontainers/PostgreSQL, Playwright/TypeScript e ESLint. Docker/Compose e GitHub Actions para execução e verificações.

Entrega prevista: React compilado servido pelo Spring Boot, com PostgreSQL num segundo serviço Compose. Versões exatas em backend/pom.xml, frontend/package-lock.json e Dockerfile; configuração e dependências descritas em [Base técnica](docs/setup.md).

## Funcionalidades do MVP

- Sessão no servidor com cookie, consulta do utilizador autenticado e logout.
- Projetos com proprietário/colaboradores, pesquisa por nome, arquivo/restauro e eliminação confirmada em cascata.
- Tarefas de projetos autorizados e tarefas pessoais independentes privadas do criador.
- Estados Por Fazer, Em Curso, Em Revisão e Concluída; prioridades/prazos opcionais.
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
| [OpenAPI](openapi.yaml) | Contratos colaborativos; operações de negócio ainda não implementadas. |
| [Wireframes](docs/ui/wireframe-tarefas.md) | Tarefas colaborativas e início de sessão. |
| [Revisão](docs/architecture-review.md) | Rastreabilidade e limitações da base colaborativa. |
| [Plano](docs/implementation-plan.md) | Incrementos e verificações do Exercício 1 da ficha 03. |
| [Verificação](docs/verification.md) | Resultados de infraestrutura e cenários funcionais ainda pendentes. |
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

Abrir http://127.0.0.1:8080 para a página técnica. O health devolve `{"status":"UP"}` quando o serviço e a BD estão disponíveis. Não existe ainda ecrã de login ou CRUD funcional. `seed` é uma ferramenta temporária, não um terceiro serviço permanente.

As migrations correm no arranque antes do seed. Para aplicar apenas o esquema, omitir o comando seed e iniciar app. Para carregar a demonstração mais tarde, executar explicitamente o mesmo comando seed. Uma nova execução não repõe dados editados/eliminados nem altera palavras-passe. Falhas/colisões fazem rollback; consultar os logs sem apagar dados para contornar o problema.

Contas fictícias: `ana@example.test` e `bruno@example.test`; palavra-passe inicial definida por `DEMO_PASSWORD` em `.env` (exemplo: `MatrizDemoOnly42!`). O hash é BCrypt. As contas serão utilizadas quando a autenticação for implementada. Ana é proprietária do projeto partilhado e Bruno colaborador; o segundo projeto é privado do Bruno. Há também uma tarefa pessoal privada da Ana.

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

Não usar `docker compose down -v` para um reinício: elimina o volume. O esquema evolui por novas migrations; não editar uma migration já aplicada. `.env`, caches, `node_modules` e `target` ficam fora do Git. A configuração atual é local; CI, E2E e publicação não foram concluídos.

## Entrega

19/11/2026 às 17h, hora de Lisboa. Incluir frontend/backend, testes, dependências, esquema/migrations, seed, configuração de exemplo sem segredos, documentação atualizada e URL GitHub acessível ao formador. Demonstração de 1–2 fluxos em até cinco minutos, com tecnologias, metodologia/IA, desafio e aprendizagem.
