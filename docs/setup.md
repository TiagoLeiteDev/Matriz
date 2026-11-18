# Matriz — Base técnica e persistência (Ficha 03, Exercício 2)

## Estrutura e responsabilidades

```text
Matriz/
├── backend/
│   ├── pom.xml, mvnw, mvnw.cmd, .mvn/wrapper/  # build Java 21/Maven
│   └── src/
│       ├── main/java/pt/matriz/
│       │   ├── MatrizApplication.java
│       │   ├── identity/                     # utilizador, bloqueio inicial e BCrypt
│       │   ├── projects/                     # projeto e colaboração
│       │   ├── tasks/                        # tarefa, atribuição e comentário
│       │   └── bootstrap/DemoSeeder.java     # inicialização explícita e transacional
│       ├── main/resources/
│       │   ├── application.yml
│       │   └── db/migration/V1__initial_schema.sql
│       └── test/java/pt/matriz/InfrastructureTest.java
├── frontend/
│   ├── package.json, package-lock.json        # dependências fixadas
│   ├── vite.config.ts, tsconfig.json, eslint.config.js
│   └── src/                                 # página técnica, sem mocks de negócio
├── scripts/Load-LocalEnv.ps1
├── .env.example, compose.yaml, Dockerfile, .dockerignore
└── docs/                                    # decisões, contrato, plano e evidências
```

É um monólito modular, com um build/backend e módulos por responsabilidade. Os UUIDs de referência são mapeados como campos nas entidades JPA; as FKs e cascatas pertencem ao esquema. As entidades não são respostas REST e não há controladores de negócio neste incremento. Não foram criadas camadas vazias nem uma aplicação por módulo.

## Dependências

| Área | Dependência | Finalidade |
|---|---|---|
| Runtime | Java 21, Spring Boot 3.5.16, Web | Aplicação e servidor HTTP; base para os contratos aprovados. |
| Runtime | Spring Data JPA/Hibernate, driver PostgreSQL | Mapeamento das seis entidades e validação do esquema existente. |
| Runtime | Flyway core + PostgreSQL | Aplicar e validar migrations ordenadas; sem recriação automática. |
| Runtime | Spring Security | Bloqueio preventivo de operações e BCrypt; login fica no incremento seguinte. |
| Runtime | Bean Validation | Base para validação de pedidos nos próximos fluxos. |
| Operação | Actuator (apenas health) | Verificar disponibilidade e ligação à BD; sem detalhes públicos. |
| Interface | React/React DOM 19.3.1 | Página técnica real; interface funcional ainda por desenvolver. |
| Build | Maven Wrapper 3.9.16 / Wrapper 3.3.4 | Execução independente do Maven global; downloads iniciais exigem rede. |
| Build frontend | Node 24, Vite 8.3.8, plugin React 6.3.0, TypeScript 5.9.3 | Build e desenvolvimento com proxy local. |
| Desenvolvimento | ESLint 10.15.0, typescript-eslint, plugins hooks/refresh e tipos | Análise estática; versões exatas em package-lock.json. |
| Testes | Spring Boot Test/JUnit, MockMvc, Spring Security Test | Testar arranque, health e bloqueio da superfície de negócio. |
| Testes | Testcontainers PostgreSQL/JUnit | BD real descartável, separada do volume de demonstração. |
| Operação | Docker/Compose, PostgreSQL 17.9 | Aplicação e BD persistente; serviço seed temporário e explícito. |

Versões transitivas Java são geridas pelo parent Spring Boot; consultar `backend/pom.xml`. Imagens Docker de build/runtime e PostgreSQL fixadas por digest. Playwright e GitHub Actions continuam previstos no plano; não foram implementados neste exercício.

Referências de compatibilidade consultadas: [Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [Vite](https://vite.dev/guide/). A resolução e os builds reais confirmam os artefactos utilizados; não se afirma que sejam sempre as versões mais recentes.

## Configuração e segredos

| Variável | Responsabilidade | Exemplo fictício |
|---|---|---|
| POSTGRES_DB | Nome da BD criada pelo container | matriz |
| POSTGRES_USER | Conta técnica local do PostgreSQL | matriz_demo |
| POSTGRES_PASSWORD | Palavra-passe técnica local; mudar fora da demonstração | LocalDatabaseDemoOnly42 |
| DB_PORT | Porta da BD no host, ligada apenas a 127.0.0.1 | 55432 |
| DB_URL | JDBC para backend executado no host; Compose usa o DNS db internamente | jdbc:postgresql://127.0.0.1:55432/matriz |
| DB_USER | Conta JDBC para execução no host | matriz_demo |
| DB_PASSWORD | Palavra-passe JDBC para execução no host | LocalDatabaseDemoOnly42 |
| DEMO_PASSWORD | Palavra-passe fictícia inicial das duas contas, apenas no seed | MatrizDemoOnly42! |
| APP_PORT | Porta pública local do Compose / servidor local | 8080 |
| APP_BIND_ADDRESS | Interface de escuta do processo; Compose define 0.0.0.0 dentro do container | 127.0.0.1 no host |

`.env.example` contém apenas valores fictícios. `.env` é ignorado pelo Git e excluído do contexto Docker. Não há fallback de credenciais no application.yml: a ligação exige variáveis. Não imprimir `docker compose config` com valores resolvidos ou as variáveis em relatórios. Usar `docker compose config --quiet` para validar.

Compose lê `.env`; Spring Boot executado diretamente não o lê automaticamente. Em PowerShell, usar `. ./scripts/Load-LocalEnv.ps1`; em shell POSIX, carregar um ficheiro local de confiança com `set -a; . ./.env; set +a`. A ferramenta PowerShell interpreta pares NOME=valor, sem executar o conteúdo.

Em produção, injetar segredos pelo ambiente/gestor da plataforma, usar uma conta de BD dedicada com privilégios mínimos e proteger ligações conforme o ambiente. Os valores publicados não servem para produção. O Compose fornecido liga portas ao loopback e não publica a aplicação na Internet. TLS, política de sessão, cookies e autorização funcional continuam nos incrementos dependentes.

## Persistência física

| Entidade | Tabela | Chave e principais campos |
|---|---|---|
| Utilizador | utilizador | UUID id, text nome/email/password_hash; email único. |
| Projeto | projeto | UUID id/proprietario_id, text nome/descricao, boolean arquivado=false. |
| Tarefa | tarefa | UUID id/criador_id/projeto_id opcional, text titulo/descricao/estado/prioridade opcional, date prazo opcional. |
| Colaboração | colaboracao_projeto | PK composta (projeto_id, utilizador_id), ambas FKs UUID. |
| Atribuição | atribuicao_tarefa | PK composta (tarefa_id, utilizador_id), ambas FKs UUID. |
| Comentário | comentario | UUID id/tarefa_id/autor_id, text conteudo, timestamptz criado_em. |

UUID é uma escolha técnica deste incremento para concretizar os identificadores opacos aprovados; novos IDs serão gerados no backend. A demonstração utiliza UUIDs fixos para repetibilidade. Text evita impor limites de negócio ainda não aprovados. Estados e prioridades conservam exatamente os valores aprovados, incluindo acentos. O esquema não normaliza emails nem define pesquisa sem acentos: essas convenções continuam pendentes antes dos fluxos correspondentes.

As relações seguem database.md: utilizador 1:N projetos e tarefas; projeto 0..1 por tarefa; N:M colaboradores/projeto e responsáveis/tarefa; tarefa e utilizador 1:N comentários. FKs para utilizadores impedem eliminar contas referenciadas. Eliminar projeto remove tarefas e colaborações; eliminar tarefa remove comentários/atribuições. Sem cascade para contas ou projetos alheios.

CHECKs protegem textos obrigatórios e enumerações; PKs impedem duplicação das associações; índices das FKs suportam consultas/cascata. A BD não implementa sozinha autorização, arquivo ou elegibilidade dos responsáveis. Essas regras exigem casos de uso transacionais e testes posteriores. Não expor o acesso SQL ao cliente.

## Migrations

Flyway aplica `V1__initial_schema.sql`: utilizador → projeto → tarefa → colaboração/atribuição/comentário → índices. Todas as dependências são criadas antes das respetivas FKs. `flyway_schema_history` é uma tabela técnica, não uma nova entidade funcional.

O arranque executa migrations pendentes e valida checksums; Hibernate usa apenas `ddl-auto=validate`. Flyway clean está desativado. Depois de publicada/aplicada, não editar V1 para evoluir o esquema: criar V2, V3, etc. Não há migration de dados fictícios na cadeia de produção.

Comando de migrations sem seed: executar o JAR normalmente. Não é necessário instalar uma CLI Flyway ou introduzir um segundo mecanismo de configuração. Migrations e seed também ocorrem no comando seed, nessa ordem, numa BD inicialmente vazia.

## Dados de demonstração

| Conta | Relações e cenário |
|---|---|
| ana@example.test | Proprietária do Projeto partilhado da Ana; criadora de tarefa pessoal privada; sem acesso ao projeto privado do Bruno. |
| bruno@example.test | Colaborador do projeto da Ana; proprietário do Projeto privado do Bruno. |

Três tarefas: uma partilhada com dois responsáveis e comentário do Bruno; uma no projeto privado do Bruno; uma pessoal da Ana. A autoria/data são determinadas pelo seed técnico, não fornecidas por utilizadores da API. Contas usam a palavra-passe DEMO_PASSWORD, armazenada com BCrypt. Não há login implementado neste incremento: são fixtures para os testes de acesso seguintes, não prova de autorização funcional.

O seed só existe quando `--matriz.demo.seed-only=true` é passado explicitamente. O processo encerra após a transação. Arranque normal não repõe dados. Um advisory lock serializa seeds concorrentes. A primeira execução cria tudo atomicamente; uma colisão faz rollback e não apaga dados existentes. Quando as duas contas fixas já existem com os emails esperados, nova execução não altera nada, incluindo dados editados/eliminados, palavras-passe e participações. Estado parcial ou IDs ocupados com emails diferentes produz erro explícito. Não restaurar automaticamente os dados nem apagar o volume para contornar o erro.

## Comandos e verificação

Consultar o [README](../README.md) para os comandos de instalação, configuração, build, migrations, seed, arranque e diagnóstico. Os resultados executados e os limites ficam em [verification.md](verification.md). O plano revisto encontra-se em [implementation-plan.md](implementation-plan.md).

## Riscos e próximas etapas

| Risco/lacuna | Mitigação / dependência |
|---|---|
| Confundir esquema e fixtures com aplicação funcional | Não há CRUD/login concluído; a página é técnica e operações de negócio ficam bloqueadas. |
| FKs não impedem responsável inelegível ou operação sobre arquivo | Implementar autorização por recurso e transações COL-06 antes de disponibilizar escrita. |
| Apagar volume ou alterar V1 perde dados/produz divergência | Manter volume, migrations incrementais e backups na operação real. |
| Repetir seed após edições | Seed não restaura fixtures já inicializadas; não é uma ferramenta de reset. |
| Exposição de valores de exemplo | Loopback, .gitignore/.dockerignore e segredos próprios em ambientes reais. |
| Builds iniciais dependem de rede e Docker em execução | Wrapper, lockfile, imagens fixadas e mensagens de erro; downloads/cache não fazem parte do Git. |
| Políticas de sessão, pesquisa e limites ainda não decididas | Resolver antes dos incrementos de autenticação e CRUD, sem impor regras pelo esquema. |
| Prontidão em produção e CI ainda não demonstradas | Exercício 2 valida a base local; CI, testes E2E, TLS e entrega final permanecem no plano. |
