# Ficha Prática 03 — Registo de trabalho e decisões

**Início:** 18/11/2026. **Ferramenta:** Codex. **Estado:** Exercícios 1–4 implementados nos respetivos incrementos; acesso SEG-01 e projetos PESQ-01 verificados. Tarefas, colaboração de escrita e restante MVP ainda por implementar. Rastreabilidade Git no checkpoint de publicação abaixo. As secções anteriores mantêm o estado histórico de cada etapa.

## 1. Análise do enunciado

Lida integralmente a ficha 10 - Ficha Prática 03.pdf e consultados os prompts do arquivo 09 - Anexos.zip. O enunciado exige conciliar diferenças antes de implementar, escolher stack/persistência, completar contratos e planear incrementos. O âmbito integral inclui autenticação/autorização, responsáveis múltiplos, comentários, pesquisa de projetos e eliminação de projetos.

## 2. Autorização de mudança de âmbito

Pedido do utilizador: “ok vamos então alterar o que já foi feito até agora para estar compatível com o âmbito colaborativo pedido”. A decisão COL-01 substitui o âmbito individual anterior, mantendo as funcionalidades previamente aprovadas que não conflituam.

### Respostas específicas do utilizador

| Questão | Resposta | Decisão |
|---|---|---|
| Proprietário gere projeto/colaboradores; proprietário e colaboradores gerem tarefas, responsáveis e comentários. | Sim, adotar esta política. | COL-02 |
| Eliminar projeto e dependentes. | Eliminar tudo, com confirmação explícita. | COL-03 |
| Tarefas independentes. | Manter, privadas do utilizador que as cria. | COL-04 |
| Sessão no servidor e cookie, deixando biblioteca para a stack. | Sim, sessão no servidor e cookie. | COL-05 |
| Atribuições que se tornam inelegíveis por transferência/remoção de acesso. | Remover as atribuições inelegíveis na mesma operação. | COL-06 |

Foi explicado o significado de sessão/cookie e das opções remover atribuições vs bloquear a operação antes das duas últimas respostas. Nenhuma escolha de stack foi feita por implicação.

## 3. Preservação do histórico

Copiados 14 artefactos anteriores para docs/history/mvp-individual-v1, sem apagar o conteúdo histórico dos logs 1/2. Os documentos ativos passam a indicar COL-01 e as decisões que substituem a versão individual.

## 4. Alterações documentais

- Requisitos F01–F19 e acesso por recurso, tarefas pessoais privadas, cascata, elegibilidade e sessão.
- Backlog ampliado para 22 histórias, mantendo US-01–US-14 adaptadas e acrescentando US-15–US-22; critérios são especificação, não testes aprovados.
- Arquitetura mantém monólito modular com identidade/acesso, projetos e tarefas; comentários/atribuições pertencem a Tarefas.
- Modelo com UTILIZADOR, PROJETO, TAREFA, COLABORACAO_PROJETO, ATRIBUICAO_TAREFA e COMENTARIO, com criador/proprietário/autor confiáveis e relações de acesso.
- Contrato OpenAPI colaborativo com sessão/CSRF, perfil/logout, projetos, colaboradores, tarefas, elegíveis, responsáveis, comentários e painel. Naming/DTOs e detalhes de proteção são propostas técnicas a rever, não código implementado.
- Wireframes de tarefa colaborativa e login, com utilizador, responsáveis, comentários e ações do proprietário. Nova disposição é proposta, não aprovação UI-01 retroativa.
- Criados scope-reconciliation.md, implementation-plan.md e verification.md; revistos constraints, AGENTS, README e architecture-review.

## 5. Limites e próximos passos

Ainda por escolher stack, persistência, tipos físicos, migrations/seed, biblioteca e duração da sessão, detalhes de cookie/CSRF, limites de texto, pesquisa de projetos/acentos e âmbito final das contagens. O contrato propõe explicitar a inclusão de tarefas pessoais por parâmetro do painel; não se atribui aprovação implícita dessa convenção ao utilizador.

Não foram instaladas dependências da aplicação, criados frontend/backend, executadas migrations ou feitos commits/push nesta atividade. A ficha 3 não está concluída; a preparação documental permite rever a base e escolher tecnologias antes da implementação incremental.

## 6. Validações documentais executadas

- OpenAPI 3.1 validado por openapi-spec-validator 0.9.0: 24 operações, operationIds únicos, dois pontos públicos (CSRF pré-login e login); os restantes declaram sessão exigida.
- Todos os métodos de alteração declaram cabeçalho CSRF. Isto não comprova a implementação da proteção.
- Doze exemplos validados contra os respetivos schemas. Verificações positivas/negativas de nome/título/conteúdo, enumerações, datas e campos opcionais passaram nos schemas.
- Pedidos com proprietarioId, criadorId ou autorId indevidos são rejeitados pelos DTOs de escrita; os DTOs públicos não contêm hashes.
- 22 histórias e 88 critérios únicos; 19 funcionalidades mapeadas no backlog e cobertura conceptual documentada.
- Fonte Mermaid do documento corresponde ao .mmd. ERD de seis entidades renderizado em SVG/PNG com código 0 e imagem inspecionada.
- Wireframes colaborativo e login inspecionados: legíveis, sem cortes ou sobreposições relevantes.
- Links locais dos documentos ativos e logs verificados. A fotografia histórica mantém os caminhos originais e as respetivas limitações indicadas em LEITURA.md.
- git diff --check sem erros de whitespace; aviso de normalização LF/CRLF no log histórico não é erro de conteúdo.

**Limitação:** não existem testes executados da aplicação, transações, verificação de hashes ou autorização real. A base está atualizada para colaboração e pronta para revisão técnica e seleção de stack. Não é conclusão da ficha 03.

## 7. Escolha de stack e fecho do Exercício 1 — TEC-01

**Data:** 18/11/2026. Após recomendar a stack e rever todos os materiais do curso (14 PDFs, 709 páginas extraídas, 28 ficheiros dos três anexos ZIP e AI.txt), o utilizador autorizou: **“ok vamos fechar o exercicio 1 da ficha 03 então com essa sugestão”**.

### Decisão aprovada

Java 21/Spring Boot/Maven, Spring Security com sessão/cookie, PostgreSQL/Spring Data JPA/Flyway, React/TypeScript/Vite/CSS simples. Testes JUnit/Spring Boot Test/MockMvc, Testcontainers/PostgreSQL, Playwright/TypeScript e ESLint. Docker/Compose e GitHub Actions.

Estratégia: desenvolvimento com Vite e backend locais; entrega com React compilado servido pelo Spring Boot e PostgreSQL em segundo serviço Compose com persistência. Não introduzir FastAPI/Pytest apenas para reproduzir exemplos do curso. AI.txt é um percurso separado, não requisito de incluir IA na Matriz.

### Artefactos atualizados

architecture.md regista TEC-01 e justificação; constraints.md distingue decisões resolvidas de detalhes pendentes; implementation-plan.md especifica caminhos/componentes, dependências, critérios e verificações de cada incremento. README/AGENTS e referências de estado atualizados. Não foi criada estrutura de código, instalado backend/frontend, iniciada BD ou executado build da aplicação nesta atividade.

### Checklist do Exercício 1

| Pedido | Evidência/estado |
|---|---|
| Rever artefactos e conciliar âmbito | COL-01–06 e scope-reconciliation.md. Concluído documentalmente. |
| Confirmar linguagem/frameworks/base de dados | TEC-01 em architecture.md. Aprovado. |
| Consolidar regras de acesso | requirements.md e COL-02/04/06. Documentado. |
| Completar contrato para os fluxos | OpenAPI colaborativo com 24 operações, validado. Convenções e detalhes pendentes identificados para revisão antes do incremento dependente. |
| Criar plano com sequência/componentes/aceitação/verificação | implementation-plan.md concretizado com estrutura prevista e gates por incremento. |

**Estado:** Exercício 1 concluído no âmbito de planeamento. Não significa que todas as decisões de pormenor estejam resolvidas: versões, tipos físicos, limites de texto, detalhes de sessão, pesquisa e contagens constam dos gates do plano. Não iniciar o comportamento dependente por suposição nem confundir o plano com implementação concluída.

**Próximo passo:** Exercício 2 — preparar estrutura e persistência, fixar versões, esquema/migrations e seed, executar/validar arranque e registar comandos reais. Commit/push e implementação não realizados neste fecho documental.

## Rastreabilidade Git e issues — 18/11/2026

A pedido do utilizador, foram criadas retroativamente duas issues para organizar o trabalho documental já realizado:

- [Issue #1](https://github.com/TiagoLeiteDev/Matriz/issues/1): consolidação histórica das fichas 01/02. Referencia o commit original ca85963, já publicado, e o commit dbfb2e0 com a fotografia documental individual em docs/history/mvp-individual-v1.
- [Issue #2](https://github.com/TiagoLeiteDev/Matriz/issues/2): documentação ativa do MVP colaborativo e planeamento do Exercício 1 da ficha 03. O commit deste registo referencia esta issue.

Não foi reescrito o histórico existente. A criação destas issues é posterior ao trabalho documental; não demonstra planeamento prévio. Os novos commits ficam locais, pendentes de push. A implementação da aplicação e os seus testes continuam por executar.

## Exercício 2 — estrutura, configuração e persistência

**Data:** 18/11/2026. **Ferramenta:** Codex/ChatGPT.

### Pedido e análise

O utilizador forneceu o prompt Senior Software Engineer/DevOps Engineer para preparar um projeto executável com estrutura, persistência, configuração externalizada, migrations, seed seguro, dois utilizadores/projetos, validação de arranque e documentação. Foram consultados AGENTS, README, requisitos, histórias, restrições, modelo, arquitetura, OpenAPI, reconciliação e plano.

A implementação mantém TEC-01 e as seis entidades aprovadas, incluindo COLABORACAO_PROJETO além da lista resumida no prompt. Não acrescenta regras funcionais, limites de texto ou APIs de negócio. UUIDs, tipos PostgreSQL, versões exatas e health são escolhas técnicas deste exercício; não se atribui uma nova aprovação humana específica a estes detalhes.

### Trabalho realizado

- Backend Java 21/Spring Boot 3.5.16, Maven Wrapper 3.9.16, módulos identity/projects/tasks e bootstrap.
- Seis mapeamentos JPA e migration V1 com UUID/text/boolean/date/timestamptz, PKs, FKs, CHECKs, unicidade e cascatas. Hibernate validate; Flyway clean desativado.
- .env.example fictício, configuração por ambiente, helper PowerShell que não executa conteúdo do .env, Compose e Dockerfile multi-stage com imagens fixadas por digest e utilizador não root.
- Frontend React/TypeScript/Vite, lockfile, ESLint e página técnica com diagnóstico real, sem mocks de negócio.
- Seed explícito, transacional, serializado por lock: duas contas BCrypt, dois projetos com acessos distintos, tarefa partilhada/privada/pessoal, responsáveis e comentário. Repetição conserva edições/remoções e não repõe palavras-passe.
- Spring Security bloqueia operações de negócio; apenas página/assets/health disponíveis. Não há bypass temporário nem utilizador gerado exposto.
- Nove testes com PostgreSQL descartável, documentação setup/README e atualização das referências/estados em modelo, arquitetura, restrições, plano, requisitos e AGENTS.

### Evidências e revisão humana

Build Java e 9 testes passaram; frontend lint/build passou; Wrapper verificado da raiz. Arranque local aplicou V1 sem seed automático (0 contas). Seed explícito gerou contagens 2/2/3/1/2/1. Build Docker passou; seed na imagem final terminou com código 0 sem duplicar. App/BD healthy, página e assets HTTP 200. Reinício de app/BD conservou as contagens e health UP. Evidências detalhadas em verification.md e dependências/variáveis/relações/riscos em setup.md.

Na revisão final foi corrigido um risco de colisão de ID de projeto no seed, com rollback e teste de regressão, sem alterar dados existentes. Durante a verificação, foi simplificado o build Docker para evitar um download geral de dependências; foi resolvido um aviso de Fast Refresh e fixado ESLint suportado. Não se declara revisão/aprovação humana posterior da implementação: esta versão fica disponível para análise do utilizador.

### Estado e próximo passo

Exercício 2 concluído quanto à base executável/persistência/configuração/migrations/seed. Login, autorização funcional, CRUD e UI de negócio ficam nos próximos incrementos. Duração de sessão, pormenores cookie/CSRF, pesquisa e contagens continuam pendentes antes dos respetivos fluxos. Não foram criadas issues novas, feitos commits ou push nesta atividade. Os commits das issues #1/#2 pertencem à etapa anterior, cujo push o utilizador já confirmou.

### Prompt integral fornecido

<details>
<summary>Ver pedido do Exercício 2</summary>

# ROLE

Atua como Senior Software Engineer e DevOps Engineer, especializado em preparação de projetos, persistência de dados, gestão de configurações, migrações de bases de dados e definição de planos de implementação para MVPs.

# GOAL

Preparar a base técnica da aplicação Matriz, garantindo que a estrutura do projeto, persistência de dados, configurações de ambiente, migrações e dados de demonstração estão corretamente definidos e documentados, de acordo com a arquitetura aprovada.

# CONTEXT

Consulta os seguintes documentos:

- docs/requirements.md
- docs/user\-stories.md
- docs/constraints.md
- docs/database.md
- docs/architecture.md
- openapi.yaml
- AGENTS.md
- README.md

A arquitetura da solução já foi definida e aprovada. O objetivo é preparar a infraestrutura mínima necessária para suportar o desenvolvimento do MVP, assegurando que o projeto pode ser configurado, executado e reproduzido por qualquer membro da equipa.

# CONSTRAINTS

- Considera apenas o âmbito do MVP.
- Segue rigorosamente a arquitetura aprovada.
- Não introduzas novas tecnologias sem justificação.
- Não alteres requisitos funcionais.
- Não utilizes credenciais reais.
- Todas as configurações sensíveis devem ser externalizadas através de variáveis de ambiente.
- Deve existir um ficheiro de exemplo (.env.example ou equivalente) com valores fictícios.
- O processo de inicialização deve ser reproduzível.
- Os dados de demonstração não devem ser recriados nem apagar dados existentes a cada arranque.
- Mantém as decisões compatíveis com ambientes de desenvolvimento e produção.
- Não assumes comportamentos não documentados.

# TASKS

1. Analisar a arquitetura aprovada e identificar a estrutura adequada para o projeto.
2. Definir a organização de diretórios e módulos da aplicação.
3. Identificar e listar as dependências necessárias.
4. Definir a estratégia de configuração:
    - Variáveis de ambiente
    - Gestão de segredos
    - Configuração da base de dados
5. Definir a configuração da persistência de dados.
6. Criar o esquema inicial da base de dados ou equivalente reproduzível para:
    - Utilizadores
    - Projetos
    - Tarefas
    - Comentários
    - Atribuições
7. Definir as migrations necessárias para criar o esquema.
8. Especificar um mecanismo de inicialização (seed) com dados de demonstração reproduzíveis.
9. Garantir que os dados de demonstração incluem:
    - Pelo menos dois utilizadores
    - Pelo menos dois projetos
    - Cenários de acesso distintos
10. Validar que a aplicação consegue:
    - Arrancar corretamente
    - Estabelecer ligação à base de dados
    - Executar migrations
    - Carregar dados de demonstração
11. Definir os comandos necessários para documentação no README.md.
12. Rever criticamente a proposta e identificar riscos ou lacunas de implementação.

# OUTPUT

## Estrutura do Projeto

Apresentar a árvore de diretórios proposta, incluindo:

- Aplicação
- Configuração
- Persistência
- Migrações
- Seeds
- Testes
- Documentação

## Dependências

Listar:

- Dependências obrigatórias
- Dependências de desenvolvimento
- Justificação de cada dependência

## Configuração

### Variáveis de Ambiente

Apresentar:

- Nome
- Descrição
- Exemplo de valor

### Exemplo de Ficheiro

Gerar o conteúdo de:

- .env.example

utilizando apenas valores fictícios.

## Persistência

### Modelo de Dados

Descrever:

- Utilizadores
- Projetos
- Tarefas
- Comentários
- Atribuições

### Relações

Identificar todas as relações entre entidades.

## Migrações

Definir:

- Estratégia de migrations
- Ordem de execução
- Dependências entre migrations

## Dados de Demonstração

Descrever:

- Utilizadores criados
- Projetos criados
- Relações estabelecidas
- Cenários de teste suportados

Garantir que os dados podem ser carregados de forma segura sem eliminar dados existentes.

## Comandos README

Gerar os comandos necessários para:

- Instalar dependências
- Configurar ambiente
- Executar migrations
- Carregar dados de demonstração
- Iniciar a aplicação
- Confirmar a ligação à base de dados

## Plano de Implementação Revisto

Apresentar:

- Etapas de implementação
- Dependências entre etapas
- Riscos identificados
- Mitigações sugeridas

# VALIDATION

Antes de concluir:

- Verifica que a estrutura respeita a arquitetura aprovada.
- Confirma que todas as entidades definidas no modelo de dados estão representadas.
- Verifica que o esquema suporta as User Stories do MVP.
- Confirma que a configuração não expõe credenciais reais.
- Verifica que existe um processo reproduzível de migrations.
- Confirma que os dados de demonstração permitem validar diferentes níveis de acesso.
- Verifica que os dados de demonstração não são recriados automaticamente em cada arranque.
- Confirma que todos os comandos necessários estão documentados para inclusão no README.md.
- Identifica qualquer informação em falta que impeça uma implementação segura.

## Critério de Aceitação

O resultado será considerado válido apenas se permitir obter um projeto executável, com persistência configurada, esquema criado, dados de demonstração reproduzíveis e um plano de implementação tecnicamente consistente com a arquitetura do MVP.

</details>

## Registo Git do Exercício 2 — 18/11/2026

Após rever o incremento, o utilizador autorizou criar a issue, fazer commit/push e fechar as issues com critérios cumpridos: “faz isso então”.

Criada a [issue #3](https://github.com/TiagoLeiteDev/Matriz/issues/3) para a base técnica e persistência. A criação é posterior à implementação e anterior ao commit. Este incremento fica num commit com `Refs #3`, incluindo código, migrations, configuração fictícia, testes e documentação. .env, dependências, builds e relatórios temporários ficam excluídos.

Antes deste commit, foi confirmado diretamente no remoto que main apontava para e01a32e, contendo também dbfb2e0 e ca85963. As issues #1/#2 já têm os commits publicados. O resultado do push deste incremento e o fecho das issues serão registados no GitHub após confirmação, sem antecipar sucesso neste registo.

## Exercício 3 — autenticação e autorização

**Data:** 18/11/2026. **Ferramenta:** Codex/ChatGPT. **Issue:** [#4](https://github.com/TiagoLeiteDev/Matriz/issues/4), aberta após análise/decisão e antes da implementação do incremento.

### Pedido, análise e controlo humano

O utilizador forneceu o prompt de Senior Backend Engineer/Security Engineer para definir e implementar acesso seguro, identidade no backend, sessão, permissões, interface, testes e documentação. Revistos requisitos, histórias, restrições, modelo, arquitetura, contrato, README, AGENTS e código do Exercício 2. Foi preparado docs/security.md com plano, matriz, endpoints e casos de teste antes da implementação.

A política temporal estava explicitamente pendente. O utilizador rejeitou a proposta inicial de 30 minutos (resposta “Quero definir outros prazos ou comportamento”) e escolheu: **“60 minutos de inatividade, sem «lembrar-me»; reinício e logout exigem novo login”**. Registada como SEG-01 e implementada sem persistência de sessões. Não se atribui aprovação à proposta rejeitada.

Foi explicitada uma ambiguidade do prompt: responsáveis não podem ser sempre inferidos como o próprio ator, porque COL-02 permite selecionar vários elegíveis. Mantém-se o responsável selecionado e verifica-se elegibilidade; quem executa a operação vem exclusivamente da sessão. Proprietário/criador/autor serão inferidos nos respetivos casos de uso de escrita, ainda por implementar nos exercícios seguintes.

### Implementação

- Spring Security/DaoAuthenticationProvider/BCrypt e consulta parametrizada de contas; perfil DTO id/nome/email. Credenciais são apagadas do principal autenticado e não entram em respostas/logs.
- Sessão em memória, timeout=60m, sem lembrar-me/URL rewriting/persistência. Identificador rodado no login e contexto guardado explicitamente; logout invalida sessão e expira cookie.
- CSRF por sessão com token mascarado, inclusive login/logout; token anterior limpo após login. Origin explícito validado em escritas; sem CORS permissivo. Cookie HttpOnly/SameSite=Lax; perfil prod exige origens e ativa Secure.
- CurrentIdentity, ProjectAccess/TaskAccess e quatro consultas mínimas reais de projeto/tarefa/comentários/responsáveis. Leituras autorizadas na mesma transação REPEATABLE_READ. Sem alterações à V1 ou entidades novas. Guardas de proprietário, arquivo, elegibilidade e destino para reutilização futura.
- Interface React real: formulário, perfil/reload, logout, mensagens de erro e regresso ao login no 401. Sem tokens em localStorage/sessionStorage nem repetição automática de escritas.
- OpenAPI 0.3.0: servidor /api, oito operações implementadas e dezasseis planeadas, IDs UUID coerentes com a persistência e exemplos corrigidos. A alteração de exemplos/formato é alinhamento técnico, não novo requisito funcional.
- README, security, constraints, architecture, database, requirements, plano, verification e AGENTS atualizados. Dependência Playwright 1.65.1 fixada em e2e com lockfile; sem segredos novos.

### Provas e revisão

**21 testes Java passaram** (9 de infraestrutura + 12 de segurança), sem falhas/erros/ignorados. Incluem Tomcat real com política efetiva=60 minutos, rotação/credenciais, CSRF, dois utilizadores, revogação de participação, privacidade e guardas. Lint/build frontend passaram. Build Docker e app/BD healthy.

No Chrome headless: **5 testes normais passaram** e um de timeout ficou ignorado por desenho; o mesmo teste de expiração foi executado separadamente contra container temporário com prazo=1m e **passou após 65 segundos sem pedidos**. A instância principal manteve 60 minutos. Reinício real: login prévio=200 e perfil com sessão anterior=401. Perfil prod local emitiu cookie Secure/HttpOnly/SameSite=Lax; não equivale a implantação HTTPS pública.

OpenAPI validado: 24 operações, 12 exemplos válidos com formatos e 7 respostas JSON reais conformes com os schemas; logout=204. Resultados detalhados e limites em verification.md. Não se afirma implementação de CRUD, criação/autoria de escritas, limpeza COL-06 ou MVP completo.

### Estado e próximo passo

Exercício 3 concluído no âmbito de acesso e base de autorização, com dependências de escrita explicitamente identificadas. Próximo: Exercício 4, gestão de projetos com primeiro percurso completo. A issue #4 regista o incremento; commit/push deste trabalho não foram efetuados nesta etapa de implementação.

### Prompt integral fornecido

<details>
<summary>Ver pedido do Exercício 3</summary>

# ROLE

Atua como Senior Backend Engineer e Security Engineer, especializado em autenticação, autorização, gestão de identidades, segurança de APIs REST e controlo de acessos em aplicações web.

# GOAL

Definir e implementar o mecanismo de autenticação e autorização da aplicação Matriz, garantindo que a identidade do utilizador é obtida exclusivamente através do processo de autenticação e que todas as regras de acesso são aplicadas no Backend de forma segura e consistente.

# CONTEXT

Consulta os seguintes documentos:

- docs/requirements.md
- docs/user\-stories.md
- docs/constraints.md
- docs/database.md
- docs/architecture.md
- openapi.yaml
- README.md
- AGENTS.md

A infraestrutura da aplicação, a persistência de dados e a arquitetura já foram definidas nos exercícios anteriores. O objetivo agora é implementar e validar o acesso à aplicação, assegurando autenticação, autorização e proteção dos recursos do MVP.

# CONSTRAINTS

- Considera apenas o âmbito do MVP.
- Segue o mecanismo de autenticação previamente aprovado na arquitetura.
- Não inventes regras de negócio ou permissões não documentadas.
- Todas as verificações de autorização devem ser realizadas no Backend.
- Nunca confiar em dados de identidade enviados pelo cliente para determinar proprietário, autor ou permissões.
- Se existirem palavras\-passe locais, armazenar apenas hashes seguros utilizando bibliotecas adequadas.
- Utilizar apenas contas e dados de demonstração fictícios.
- Aplicar as regras de acesso tanto através da interface como por acesso direto à API.
- Não expor credenciais, tokens ou segredos reais.
- Manter toda a configuração de segurança compatível com ambientes de desenvolvimento e produção.

# TASKS

## Autenticação

1. Implementar o mecanismo de início de sessão.
2. Implementar a consulta do perfil do utilizador autenticado.
3. Implementar o fim de sessão.
4. Definir o ciclo de vida da sessão ou token:
    - Criação
    - Utilização
    - Renovação (se aplicável)
    - Expiração
    - Revogação ou logout
5. Documentar o ciclo de autenticação no contrato da API e no README.

## Gestão de Credenciais

1. Verificar se existem palavras\-passe locais.
2. Garantir o armazenamento exclusivo de hashes seguros.
3. Identificar a biblioteca ou mecanismo de hashing utilizado.
4. Validar que nenhuma palavra\-passe é armazenada em texto simples.

## Identidade do Utilizador

1. Obter a identidade do utilizador exclusivamente através da autenticação.
2. Inferir automaticamente no Backend:
    - Proprietário dos projetos
    - Autor dos comentários
    - Responsável pelas atribuições
3. Impedir que estes valores sejam aceites como prova de identidade enviada pelo cliente.

## Autorização

1. Identificar as regras de acesso definidas para:
    - Projetos
    - Tarefas
    - Comentários
    - Atribuições
2. Implementar mecanismos de autorização para cada operação protegida.
3. Garantir que os mesmos controlos são aplicados:
    - Na interface da aplicação
    - Na API REST
    - Em pedidos diretos à API

## Validação

1. Definir cenários de teste para:
    - Credenciais válidas
    - Credenciais inválidas
    - Pedidos sem autenticação
    - Acesso por utilizadores distintos
    - Recursos autorizados
    - Recursos não autorizados

## Interface

1. Verificar o comportamento da interface quando:
    - O utilizador não está autenticado
    - A sessão expira
    - O token deixa de ser válido
2. Garantir que o utilizador é encaminhado para o início de sessão quando necessário.
3. Garantir que são apresentadas mensagens de erro compreensíveis.

# OUTPUT

## Estratégia de Autenticação

Descrever:

- Mecanismo utilizado
- Fluxo de autenticação
- Fluxo de logout
- Gestão de sessões ou tokens
- Justificação da solução

## Gestão de Credenciais

Apresentar:

- Estratégia de hashing
- Biblioteca utilizada
- Medidas de proteção adotadas

## Fluxo de Identidade

Explicar:

- Como a identidade é obtida
- Como é propagada no Backend
- Como é utilizada para determinar:
    - Proprietários
    - Autores
    - Responsáveis

## Matriz de Autorização

Tabela contendo:

- Recurso
- Operação
- Quem pode executar
- Regra aplicada
- Local da validação

## Endpoints Protegidos

Para cada endpoint:

- Requer autenticação?
- Requer autorização?
- Validações aplicadas

## Casos de Teste

Listar cenários para:

- Sucesso
- Falha de autenticação
- Falha de autorização
- Expiração de sessão
- Utilizadores distintos

## Atualizações ao README

Gerar os conteúdos necessários para documentar:

- Configuração da autenticação
- Processo de login
- Processo de logout
- Gestão de tokens ou sessões
- Execução dos testes de segurança

## Riscos e Recomendações

Identificar:

- Riscos de segurança
- Limitações do MVP
- Melhorias futuras recomendadas

# VALIDATION

Antes de concluir:

- Verifica que a identidade do utilizador nunca depende de dados enviados pelo cliente.
- Confirma que proprietários, autores e responsáveis são inferidos a partir da autenticação.
- Verifica que todas as operações protegidas possuem controlo de autorização.
- Confirma que a proteção funciona para acessos via interface e via API.
- Verifica que palavras\-passe locais são armazenadas apenas sob a forma de hash.
- Confirma que não existem credenciais ou segredos expostos.
- Verifica que os cenários de teste cobrem autenticação e autorização.
- Confirma que a interface trata corretamente sessões inválidas ou expiradas.
- Identifica qualquer requisito sem implementação ou validação correspondente.

## Critério de Aceitação

O resultado será considerado válido apenas se permitir identificar de forma segura os utilizadores, aplicar corretamente as regras de acesso do MVP, proteger todos os recursos relevantes da aplicação e disponibilizar documentação clara sobre autenticação, autorização e gestão de sessão.

</details>

## Exercício 4 — gestão de projetos integrada

**Data:** 18/11/2026. **Ferramenta:** Codex/ChatGPT. **Issue:** [#5](https://github.com/TiagoLeiteDev/Matriz/issues/5), criada antes da implementação. Depende do incremento local da issue #4, ainda não publicado.

### Pedido e decisões humanas

O utilizador forneceu o prompt de Senior Full-Stack Engineer para implementar criação/listagem/consulta/edição/eliminação de projetos, autorização, pesquisa, validação segundo o contrato e primeiro percurso vertical com persistência após reinício. Revistos os documentos pedidos, código de acesso, contratos e dados. Análise/plano registados em docs/projects.md antes das alterações significativas.

A pesquisa de projetos estava pendente. O utilizador rejeitou a primeira proposta de distinguir acentos (“Quero outra correspondência”) e escolheu **“Por parte do nome, ignorando maiúsculas e acentos: «APRESENTACAO» encontra «Preparar apresentação»”**. Registada PESQ-01. Não se atribuiu aprovação à proposta rejeitada.

O contrato não fixa máximos de nome/descrição nem paginação. Aplicado nome obrigatório/não só espaços e descrição opcional, sem copiar 3–100/1000 dos exemplos. Mantidas permissões COL-02/03: só proprietário gere projeto; colaborador consulta; eliminação com confirmação e cascata atómica, sem apagar contas.

### Implementação e interação com IA

- ProjectInput com DTOs Create/Patch, JSON estrito e distinção omitido/null; validação sem máximos arbitrários.
- ProjectCommands transacional: UUID/proprietário inferidos; lock/autorização antes de editar/eliminar; cascata da V1 reutilizada.
- Listagem agregada só de projetos autorizados/arquivo; pesquisa NFD/sem marcas/maiúsculas normalizadas no backend, texto literal e sem extensão nova de BD.
- Interface ProjectsWorkspace/ProjectForm/DeleteProjectDialog: navegação por hash, lista/pesquisa/arquivo, detalhe, criação/edição, confirmação/cancelamento, mensagens e preservação do formulário em falha; CSRF e 401 integrados.
- OpenAPI 0.4.0 com 12 operações implementadas, pesquisa PESQ-01 e status codes coerentes; sem alteração de entidades, schema V1 ou stack.
- Testes backend e browser, incluindo falha real de cascata e reinício de app; README, projetos, requisitos, restrições, arquitetura, modelo, plano, histórias e evidências atualizados.

### Provas e revisão crítica

**33 testes Java passaram**: 9 infraestrutura + 12 segurança + 12 projetos, zero falhas/erros/ignorados. Lint/build frontend passaram. No Chrome, **10 testes normais passaram**; expiração/reinício ficam ignorados no comando normal. O percurso de reinício foi executado separadamente e **1 passou**: criar pela UI, reload, reiniciar backend, novo login e confirmar mesmo projeto/descrição.

Cancelar eliminação enviou 0 DELETE; confirmar enviou 1, com ausência posterior. Trigger de falha apenas na BD descartável comprovou rollback de toda a cascata e erro genérico. A mensagem de falha de comunicação foi traduzida no cliente HTTP para português; o teste verifica essa mensagem e a ausência de reenvio. Falha de comunicação injetada no teste conservou o formulário e não anunciou sucesso/reenvio automático. API real validada contra schemas: 5 respostas conformes e DELETE 204. Contagens originais da demonstração conservadas após limpeza apenas dos registos criados pelo teste.

A primeira execução de browser falhou por seletor role=status ambíguo (sucesso/carregamento simultâneos); corrigido para mensagem exata e repetição passou. Também ajustado ref do dialog e coordenação de navegação/hash para não deixar carregamento pendente sem pedido. Resultados e limitações em verification.md.

### Estado e próximos passos

Exercício 4 concluído para gestão de projetos e primeiro percurso vertical. Restam tarefas/colaboração de escrita e restante ficha. Issues #4/#5 e alterações dos exercícios 3/4 continuam locais quanto a código, sem commits/push novos nesta atividade. Não foi reescrito histórico, apagado volume ou publicado o serviço na Internet.

### Prompt integral fornecido

<details>
<summary>Ver pedido do Exercício 4</summary>

# ROLE

Atua como Senior Full\-Stack Engineer, especializado em desenvolvimento de aplicações web, APIs REST, persistência de dados, autenticação/autorização e integração completa entre Frontend, Backend e Base de Dados.

# GOAL

Implementar a gestão de projetos da aplicação Matriz, disponibilizando o primeiro percurso funcional completo entre interface, API e persistência de dados, garantindo conformidade com o contrato OpenAPI, as regras de autorização e os requisitos do MVP.

# CONTEXT

Consulta os seguintes documentos:

- docs/requirements.md
- docs/user\-stories.md
- docs/constraints.md
- docs/database.md
- docs/architecture.md
- openapi.yaml
- README.md
- AGENTS.md

A autenticação, autorização, persistência e contratos de API já foram definidos nos exercícios anteriores.

O objetivo deste exercício é implementar o primeiro fluxo vertical completo da aplicação, desde a interface até à base de dados, utilizando a gestão de projetos como funcionalidade principal.

# CONSTRAINTS

- Considera apenas o âmbito do MVP.
- Respeita rigorosamente o contrato OpenAPI aprovado.
- Não introduzas novos requisitos ou funcionalidades.
- Todas as operações devem respeitar os códigos HTTP definidos.
- As validações devem seguir exatamente as regras documentadas no OpenAPI.
- A associação entre projeto e utilizador deve ser inferida no Backend.
- Nunca aceitar o proprietário do projeto como dado de confiança enviado pelo cliente.
- Aplicar todas as regras de autorização também em pedidos diretos à API.
- Evitar registos órfãos ao eliminar projetos.
- Utilizar persistência real.
- Garantir que os dados persistem após reinício do Backend.
- A experiência da interface deve ser coerente com os fluxos definidos nas User Stories.

# TASKS

## Gestão de Projetos

1. Implementar a criação de projetos.
2. Implementar a listagem de projetos.
3. Implementar a consulta de um projeto específico.
4. Implementar a edição de projetos.
5. Implementar a eliminação de projetos.
6. Validar todas as operações contra o contrato OpenAPI.

## Validações

1. Aplicar as regras de validação definidas para:
    - Nome do projeto
    - Descrição do projeto
2. Garantir o cumprimento dos limites definidos no OpenAPI.

Exemplo:

- Nome: entre 3 e 100 caracteres.
- Descrição: até 1000 caracteres.

Utilizar sempre os valores efetivamente definidos no contrato aprovado.

## Associação ao Utilizador

1. Associar automaticamente cada projeto ao utilizador autenticado.
2. Impedir a criação ou alteração do proprietário através da API.
3. Garantir que o proprietário é obtido exclusivamente a partir da autenticação.

## Autorização

1. Limitar a listagem aos projetos acessíveis ao utilizador autenticado.
2. Restringir:
- Consulta
- Edição
- Eliminação

a projetos para os quais o utilizador possua autorização.

1. Verificar estas regras tanto:
- Na interface
- Como na API

## Pesquisa e Paginação

1. Implementar pesquisa por nome.
2. Implementar paginação caso exista no contrato OpenAPI.
3. Garantir consistência entre resposta da API e comportamento da interface.

## Eliminação

1. Solicitar confirmação explícita antes da eliminação.
2. Aplicar a regra documentada para entidades dependentes.
3. Garantir que não são criados dados órfãos.

## Integração Completa

1. Validar o fluxo completo:
- Iniciar sessão.
- Criar um projeto.
- Visualizar o projeto na listagem.
- Consultar o projeto individualmente.
- Recarregar a página.
- Reiniciar o Backend.
- Confirmar que os dados continuam disponíveis.

# OUTPUT

## Análise de Implementação

Descrever:

- Requisitos suportados
- User Stories cobertas
- Fluxos implementados

## Operações Implementadas

Para cada operação:

- Método HTTP
- Endpoint
- Permissões necessárias
- Validações aplicadas
- Status codes suportados

## Validações

Apresentar:

- Campos validados
- Regras aplicadas
- Mensagens de erro previstas

## Regras de Autorização

Tabela contendo:

- Operação
- Recurso
- Regra aplicada
- Resultado esperado

## Fluxo Completo

Documentar o percurso:

1. Login
2. Criação de projeto
3. Listagem
4. Consulta
5. Atualização
6. Eliminação
7. Persistência após reinício

## Persistência

Descrever:

- Como os dados são armazenados
- Como é garantida a sua persistência
- Como são tratados os registos dependentes

## Atualizações Necessárias ao README

Gerar os conteúdos a incluir relativamente a:

- Configuração
- Arranque da aplicação
- Fluxo de autenticação
- Gestão de projetos
- Testes manuais do fluxo principal

## Problemas e Riscos

Identificar:

- Limitações conhecidas
- Riscos técnicos
- Melhorias futuras

# VALIDATION

Antes de concluir:

- Verifica que todas as operações CRUD estão implementadas.
- Confirma que os endpoints respeitam o contrato OpenAPI.
- Verifica que as validações coincidem com as definições aprovadas.
- Confirma que o proprietário do projeto é inferido a partir da autenticação.
- Verifica que um utilizador não consegue aceder a projetos não autorizados.
- Confirma que a pesquisa por nome funciona conforme definido.
- Verifica a paginação, caso exista no contrato.
- Confirma que a eliminação exige confirmação na interface.
- Verifica que não existem dados órfãos após eliminação.
- Confirma que os dados permanecem disponíveis após reinício do Backend.
- Verifica que o fluxo completo do MVP pode ser executado sem intervenção manual na base de dados.

## Critério de Aceitação

O resultado será considerado válido apenas se permitir que um utilizador autenticado consiga criar, listar, consultar, editar e eliminar projetos de forma segura, utilizando uma interface integrada com a API e com persistência real dos dados, mantendo total conformidade com o contrato OpenAPI e as regras de autorização definidas para o MVP.

</details>

## Registo Git dos Exercícios 3/4 — 18/11/2026

Após recomendar dois commits por incremento, o utilizador autorizou: **“faz isso então”**, incluindo push e fecho das issues após confirmação.

- Issue #4: commit 0c1351e, autenticação/autorização, consultas protegidas, interface de acesso e documentação.
- Issue #5: commit deste registo, gestão de projetos, pesquisa PESQ-01, formulários, cascata e respetiva evidência.

Como os ficheiros partilhados continham alterações de ambos os exercícios, foi preparada e validada uma versão autónoma do exercício 3 numa pasta temporária ignorada pelo Git. Passaram 21 testes backend e lint/build; OpenAPI e links locais válidos. O conteúdo final da pasta de trabalho foi preservado e comparado por hash antes de registar a evolução do exercício 4. Não foi reescrito o histórico anterior.

.env, node_modules, target, relatórios e cópias temporárias ficam excluídos. As confirmações de push e fecho das issues são registadas no GitHub após a publicação dos dois commits. Não foi publicada a aplicação na Internet nem alterada a base de dados por esta operação Git.


## Exercício 5 — Gestão de tarefas — 18/11/2026

**Ferramenta:** ChatGPT/Codex. **Issue:** [#6](https://github.com/TiagoLeiteDev/Matriz/issues/6), aberta antes da implementação.

### Pedido integral recebido

<details>
<summary>Prompt do utilizador</summary>

# ROLE

Atua como Senior Full\-Stack Engineer, especializado em desenvolvimento de aplicações web, APIs REST, modelação de domínios de negócio, persistência de dados, experiência de utilizador e integração entre Frontend, Backend e Base de Dados.

# GOAL

Implementar a gestão de tarefas da aplicação Matriz, permitindo criar, listar, consultar, editar e eliminar tarefas associadas a projetos autorizados, garantindo conformidade com o contrato OpenAPI, validação de dados, atualização dinâmica da interface e persistência real.

# CONTEXT

Consulta os seguintes documentos:

- docs/requirements.md
- docs/user\-stories.md
- docs/constraints.md
- docs/database.md
- docs/architecture.md
- openapi.yaml
- README.md
- docs/ui/
- AGENTS.md

A autenticação, autorização e a gestão de projetos já foram implementadas nos exercícios anteriores.

O objetivo deste exercício é disponibilizar a gestão operacional do trabalho dentro de cada projeto através de tarefas persistidas na base de dados, acessíveis através da API e totalmente integradas com a interface da aplicação.

# CONSTRAINTS

- Considera apenas o âmbito do MVP.
- Respeita rigorosamente o contrato OpenAPI aprovado.
- Não introduzas novas funcionalidades sem suporte documental.
- Todas as operações devem respeitar os pedidos, respostas e códigos HTTP definidos.
- As tarefas devem estar sempre associadas a um projeto existente.
- O acesso às tarefas deve respeitar as regras de autorização definidas para o respetivo projeto.
- Todas as validações devem ser realizadas no Backend.
- A interface deve apresentar mensagens de erro claras e compreensíveis.
- Rejeitar valores constituídos apenas por espaços em branco.
- Implementar filtros apenas para os campos definidos no contrato.
- Implementar paginação apenas se estiver prevista no OpenAPI.
- Após qualquer operação, a interface deve refletir imediatamente o estado atualizado dos dados, sem exigir recarregamento manual da página.
- Os valores técnicos da API devem ser preservados:
    - Estado: TODO, IN\_PROGRESS, DONE
    - Prioridade: LOW, MEDIUM, HIGH
- A interface pode apresentar traduções destes valores para português.

# TASKS

## Gestão de Tarefas

1. Implementar a criação de tarefas associadas a um projeto autorizado.
2. Implementar a listagem de tarefas.
3. Implementar a consulta de uma tarefa específica.
4. Implementar a edição de tarefas.
5. Implementar a eliminação de tarefas.
6. Garantir que todas as operações respeitam o contrato OpenAPI.

## Associação a Projetos

1. Garantir que cada tarefa está associada a um projeto válido.
2. Impedir a criação de tarefas para projetos inexistentes.
3. Impedir o acesso a tarefas pertencentes a projetos não autorizados.
4. Garantir que a autorização é validada em todas as operações.

## Validações

1. Aplicar as regras de validação definidas para os campos da tarefa.

Campos de referência:

### Título

- Obrigatório.
- Entre 3 e 150 caracteres.

### Descrição

- Até 2000 caracteres.

### Estado

- TODO
- IN\_PROGRESS
- DONE

Valor inicial:

- TODO

### Prioridade

- LOW
- MEDIUM
- HIGH

Valor inicial:

- MEDIUM
1. Rejeitar valores compostos apenas por espaços em branco.
2. Garantir consistência entre validações do Backend e contrato OpenAPI.

## Estados e Prioridades

1. Implementar alteração de estado.
2. Implementar alteração de prioridade.
3. Garantir que apenas os valores permitidos são aceites.
4. Garantir persistência correta das alterações.

## Pesquisa, Filtros e Paginação

1. Implementar filtragem por estado.
2. Implementar filtragem por prioridade.
3. Permitir combinação dos filtros caso esteja prevista no contrato.
4. Implementar paginação, se definida no OpenAPI.
5. Garantir consistência entre API e interface.

## Atualização Dinâmica da Interface

1. Atualizar automaticamente a listagem após:
- Criação
- Edição
- Alteração de estado
- Alteração de prioridade
- Eliminação
1. Atualizar contagens, indicadores ou métricas apresentadas na interface.
2. Garantir que o utilizador não necessita de recarregar a página para visualizar as alterações.

## Tratamento de Erros

1. Apresentar mensagens adequadas para:
- Dados inválidos
- Projeto inexistente
- Tarefa inexistente
- Falha de autorização
- Falha de autenticação
- Erros inesperados
1. Garantir alinhamento entre erros da API e mensagens da interface.

# OUTPUT

## Análise de Implementação

Descrever:

- Funcionalidades implementadas
- User Stories suportadas
- Fluxos de negócio cobertos

## Operações Implementadas

Para cada operação:

- Método HTTP
- Endpoint
- Permissões exigidas
- Validações aplicadas
- Status codes suportados

## Modelo de Tarefa

Apresentar:

- Campos
- Tipos de dados
- Restrições
- Valores por defeito

## Regras de Validação

Tabela contendo:

- Campo
- Regra
- Mensagem de erro esperada

## Filtros e Paginação

Documentar:

- Filtros suportados
- Parâmetros utilizados
- Comportamento esperado
- Paginação implementada

## Regras de Autorização

Tabela contendo:

- Operação
- Recurso
- Regra aplicada
- Comportamento esperado

## Comportamento da Interface

Descrever:

- Criação de tarefas
- Edição de tarefas
- Alteração de estados
- Alteração de prioridades
- Atualizações automáticas
- Tratamento de erros

## Persistência

Explicar:

- Como as tarefas são armazenadas
- Relação com projetos
- Garantias de integridade dos dados

## Atualizações ao README

Gerar os conteúdos necessários para documentar:

- Gestão de tarefas
- Filtros
- Atualizações de estado
- Atualizações de prioridade
- Testes manuais
- Fluxos de validação

## Problemas e Riscos

Identificar:

- Limitações conhecidas
- Riscos técnicos
- Melhorias futuras

# VALIDATION

Antes de concluir:

- Verifica que todas as operações CRUD de tarefas estão implementadas.
- Confirma que todas as tarefas pertencem a projetos válidos.
- Verifica que apenas utilizadores autorizados podem aceder às tarefas.
- Confirma que as validações respeitam o contrato OpenAPI.
- Verifica que valores compostos apenas por espaços são rejeitados.
- Confirma que os estados aceites são apenas TODO, IN\_PROGRESS e DONE.
- Confirma que as prioridades aceites são apenas LOW, MEDIUM e HIGH.
- Verifica que os valores por defeito são corretamente aplicados.
- Confirma que os filtros por estado e prioridade funcionam corretamente.
- Verifica a paginação, caso exista no contrato.
- Confirma que a interface apresenta mensagens de erro adequadas.
- Verifica que a listagem e as contagens são atualizadas após cada operação.
- Confirma que não é necessário recarregar a página para observar alterações.
- Verifica que a persistência dos dados é mantida após reinício do Backend.

## Critério de Aceitação

O resultado será considerado válido apenas se permitir que um utilizador autenticado crie, consulte, liste, filtre, atualize e elimine tarefas pertencentes a projetos autorizados, utilizando uma interface integrada com a API e persistência real, mantendo conformidade com o contrato OpenAPI, as regras de autorização e as validações definidas para o MVP.



</details>

### Análise crítica e controlo humano

O prompt exigia projeto obrigatório e alterava estados/prioridades, contrariando COL-04 e o contrato anterior. A implementação dependente aguardou confirmação. Não foram importados os limites de texto exemplificativos.

1. Manter tarefas pessoais além das de projetos? **Resposta:** “Manter tarefas pessoais e tarefas de projetos, conforme o contrato atual”.
2. Manter contrato ou alterar enumerações? **Resposta:** “Alterar para TODO/IN_PROGRESS/DONE e LOW/MEDIUM/HIGH, com MEDIUM por defeito”. TAR-01.
3. Pesquisa ignora maiúsculas/acentos como projetos? **Resposta:** “Sim, ignorar maiúsculas e acentos também nas tarefas”. PESQ-02.
4. Converter Em Revisão→IN_PROGRESS e Crítica→HIGH, conservar prioridades vazias existentes, MEDIUM apenas por omissão numa nova tarefa e null explícito = Sem prioridade? **Resposta:** “Sim, aplicar este mapeamento e conservar os valores vazios existentes”.

### Trabalho realizado

- DTOs/validações estritos, criação/listagem/edição/eliminação, privacidade e filtros combinados; identidade inferida da sessão.
- V2 incremental aprovada, sem editar V1, sem eliminar tarefas ou repor seed. Mapeamento/relações testados antes da atualização local.
- Transações e locks coordenados com projetos; arquivo bloqueia escritas; transferência limpa atribuições inelegíveis atomicamente, conservando criador/comentários.
- Desassociação por colaborador que perde acesso devolve JSON literal null; UI explica e retira a tarefa da lista.
- Navegação de tarefas, formulário, filtros, detalhe, confirmação/cancelamento, erros e progresso atualizado sem reload.
- OpenAPI 0.5.0, 16/24 operações implementadas; requisitos, histórias, modelo, arquitetura, plano, README e instruções alinhados com TAR-01/PESQ-02.

### Verificação e resultado

50 testes backend sem falhas; migração cobre 20 combinações antigas e uma tarefa pessoal. Lint/build passaram. 14 testes normais de browser passaram, incluindo regressão de autenticação/projetos, CRUD pessoal, pesquisa sem acentos, progresso dinâmico, null após perda de acesso e conservação do formulário em erro. Reinício/contrato com evidência detalhada em verification.md.

Escrita de comentários/responsáveis, colaboradores, painel, Kanban e calendário permanecem pendentes. No fecho da implementação, as alterações estavam locais; publicação autorizada posteriormente no registo seguinte.


## Registo Git do Exercício 5 — 18/11/2026

Após o ponto de situação, o utilizador autorizou “faz isso”: criar commit com Refs #6, fazer push e fechar a issue após confirmar a publicação. O incremento inclui gestão de tarefas, V2/TAR-01/PESQ-02, interface, testes, contrato e documentação associados.

Verificações funcionais já executadas: 50 testes backend, lint/build, 14 cenários normais de browser e 1 separado de reinício. Esta operação Git não os apresenta como nova execução. Foram revistos os ficheiros alterados e excluídos ficheiros de ambiente, caches, relatórios e temporários. A confirmação de SHA remoto/push e fecho é registada na issue #6 após publicação; não é reescrito histórico nem alterada a base de dados por esta operação.


## Exercício 6 — Responsáveis e comentários: análise inicial — 18/11/2026

**Ferramenta:** ChatGPT/Codex. **Estado:** plano submetido; implementação aguarda validação explícita pedida na Fase 1 do prompt.

<details>
<summary>Pedido integral do utilizador</summary>

# ROLE

Atua como Senior Full\-Stack Engineer, especializado em desenvolvimento de aplicações web, APIs REST, modelação de dados, segurança, persistência e implementação incremental orientada a MVPs.

# GOAL

Implementar a gestão de responsáveis e comentários da aplicação Matriz, permitindo atribuir utilizadores a tarefas e registar comentários persistentes, garantindo conformidade com o contrato OpenAPI, regras de acesso, validações de negócio e persistência dos dados.

# CONTEXT

Consulta os seguintes documentos:

- docs/requirements.md
- docs/user\-stories.md
- docs/constraints.md
- docs/database.md
- docs/architecture.md
- openapi.yaml
- README.md
- docs/ui/
- AGENTS.md

A gestão de projetos, tarefas, autenticação e autorização já foi implementada nos exercícios anteriores.

O objetivo deste exercício é concluir a gestão operacional das tarefas, adicionando responsáveis e comentários funcionais e persistentes.

O trabalho deverá seguir uma abordagem incremental:

1. Análise e plano.
2. Validação do plano.
3. Implementação de um incremento.
4. Execução das verificações relevantes.
5. Revisão do diff produzido.
6. Avanço para o incremento seguinte.

# CONSTRAINTS

- Considera apenas o âmbito do MVP.
- Respeita integralmente o contrato OpenAPI aprovado.
- Não introduzas novas funcionalidades não suportadas pelos requisitos.
- Todas as regras de autorização devem ser aplicadas no Backend.
- Não aceitar identidade, autor ou responsável enviados pelo cliente como fonte de verdade.
- O autor dos comentários deve ser inferido a partir do utilizador autenticado.
- A data dos comentários deve ser gerada pelo Backend.
- Validar a existência do utilizador antes de criar uma atribuição.
- Validar a elegibilidade do utilizador segundo as regras de acesso definidas.
- Impedir atribuições duplicadas entre a mesma tarefa e o mesmo utilizador.
- Garantir integridade referencial entre tarefas, responsáveis e comentários.
- Rejeitar conteúdos compostos apenas por espaços.
- Garantir persistência após reinício da aplicação.
- Executar verificações após cada incremento antes de avançar para o seguinte.
- Rever sempre o diff antes de continuar.

# TASKS

## Fase 1 – Análise e Planeamento

1. Analisar os requisitos relacionados com:
    - Responsáveis
    - Comentários
    - Regras de acesso
    - Persistência
2. Identificar as alterações necessárias em:
    - API
    - Backend
    - Base de dados
    - Frontend
3. Definir um plano incremental de implementação.
4. Identificar riscos, dependências e validações necessárias.
5. Submeter o plano para validação antes da implementação.

## Fase 2 – Responsáveis

### Associação de Responsáveis

1. Implementar a atribuição de utilizadores a tarefas.
2. Permitir associar múltiplos responsáveis à mesma tarefa.
3. Permitir remover cada atribuição individualmente.

### Elegibilidade

1. Validar que o utilizador existe.
2. Validar que o utilizador possui acesso ao projeto da tarefa.
3. Aplicar as regras de autorização definidas para o projeto.

### Prevenção de Duplicados

1. Impedir a criação de múltiplas atribuições para:
- A mesma tarefa
- O mesmo utilizador
1. Garantir proteção através de:
- Validação de negócio
- Persistência de dados

### Interface

1. Disponibilizar seleção dos utilizadores elegíveis.
2. Atualizar a lista de responsáveis após cada operação.

## Fase 3 – Comentários

### Criação

1. Implementar a criação de comentários sobre tarefas.
2. Associar automaticamente o comentário ao utilizador autenticado.
3. Registar automaticamente a data de criação.

### Validação

1. Aplicar as regras definidas para o campo conteúdo:
- Obrigatório.
- Entre 1 e 2000 caracteres.
- Não aceitar apenas espaços.
1. Apresentar erros compreensíveis na interface.

### Consulta

1. Implementar a listagem de comentários de uma tarefa.
2. Apresentar para cada comentário:
- Autor
- Data
- Conteúdo
1. Garantir ordenação consistente conforme definido no contrato.

## Fase 4 – Persistência

1. Garantir persistência de:
- Responsáveis
- Comentários
1. Validar que os dados permanecem acessíveis após:
- Reabrir a tarefa.
- Recarregar a interface.
- Reiniciar o Backend.

## Fase 5 – Verificação Incremental

1. Após cada incremento:
- Executar verificações relevantes.
- Validar regras funcionais.
- Rever o diff gerado.
- Registar observações.
1. Apenas avançar para o incremento seguinte após validação do incremento atual.

# OUTPUT

## Análise Inicial

Apresentar:

- Requisitos identificados
- User Stories suportadas
- Dependências encontradas
- Impacto na arquitetura

## Plano de Implementação

Dividir em incrementos contendo:

- Objetivo
- Alterações previstas
- Critérios de conclusão
- Verificações necessárias

## Alterações de Dados

Descrever:

- Novas entidades ou relações
- Restrições
- Índices
- Regras de integridade

## Gestão de Responsáveis

Para cada operação:

- Endpoint
- Método HTTP
- Permissões necessárias
- Validações aplicadas
- Status codes suportados

## Gestão de Comentários

Para cada operação:

- Endpoint
- Método HTTP
- Permissões necessárias
- Validações aplicadas
- Status codes suportados

## Regras de Negócio

Listar:

- Elegibilidade dos utilizadores
- Regras de atribuição
- Regras de autoria
- Regras de integridade

## Comportamento da Interface

Descrever:

### Responsáveis

- Seleção
- Associação
- Remoção
- Atualização da interface

### Comentários

- Criação
- Visualização
- Mensagens de erro

## Verificações

Para cada incremento:

- Testes executados
- Resultado esperado
- Resultado obtido

## Revisão do Diff

Após cada incremento apresentar:

- Alterações realizadas
- Impacto funcional
- Riscos identificados
- Próximos passos

## Atualizações ao README

Gerar os conteúdos necessários para documentar:

- Gestão de responsáveis
- Gestão de comentários
- Validações
- Testes manuais
- Fluxos principais

## Problemas e Riscos

Identificar:

- Limitações conhecidas
- Riscos técnicos
- Melhorias futuras

# VALIDATION

Antes de concluir:

- Verifica que uma tarefa pode possuir vários responsáveis.
- Confirma que uma atribuição pode ser removida individualmente.
- Verifica que não podem existir atribuições duplicadas.
- Confirma que apenas utilizadores elegíveis podem ser atribuídos.
- Verifica que todas as regras de autorização são aplicadas no Backend.
- Confirma que o autor do comentário é obtido através da autenticação.
- Verifica que a data do comentário é gerada pelo Backend.
- Confirma que comentários compostos apenas por espaços são rejeitados.
- Verifica que o conteúdo respeita o limite de 1 a 2000 caracteres.
- Confirma que comentários e atribuições persistem após reabrir a tarefa.
- Verifica que os dados permanecem disponíveis após reinício do Backend.
- Confirma que cada incremento foi analisado, validado, testado e revisto antes do seguinte.
- Identifica quaisquer requisitos sem cobertura funcional ou técnica.

## Critério de Aceitação

O resultado será considerado válido apenas se permitir gerir tarefas completas com estado, prioridade, responsáveis e comentários persistentes, garantindo validação adequada, controlo de acessos, persistência real e integração consistente entre interface, API e base de dados, seguindo uma abordagem incremental com revisão contínua das alterações.



</details>

### Análise e proposta

Foram consultados os documentos de contexto e os pontos de implementação relevantes. O esquema já suporta os recursos e as leituras autorizadas existem. Proposto plano em [task-collaboration.md](../task-collaboration.md): responsáveis/API, responsáveis/UI, comentários/API, comentários/UI/fecho, com testes e revisão do diff entre incrementos.

A diferença identificada é o máximo de 2000 caracteres pedido agora, ausente do contrato anterior. Proposta COM-01 para schema específico de comentários, sem limitar outros textos ou truncar dados existentes. A ordem existente criadoEm/id ascendente será explicitada no contrato. Mantêm-se pessoais, arquivo, elegibilidade e identidade inferida da sessão; gestão de colaboradores não incluída automaticamente.

Não foram alterados código, migrations, contrato nem dados da aplicação. Não foram executados novos testes funcionais nesta fase documental; não declarar o exercício concluído.


### Aprovação e incremento 1 — API de responsáveis

O utilizador respondeu **“sim”** ao plano, incluindo COM-01. Issue [#7](https://github.com/TiagoLeiteDev/Matriz/issues/7) aberta antes da implementação. Acrescentados GET de elegíveis e POST/DELETE de atribuições, JSON estrito, transações/locks partilhados com tarefas e validação de existência/elegibilidade. Duplicado protegido por PK/ON CONFLICT e 409; remoção inexistente 404. Sem novas entidades/migrations.

**Verificação executada:** AssignmentTest, 8 testes PostgreSQL descartável, 0 falhas/erros/ignorados; inclui pedidos concorrentes com resultados 201/409 e uma só atribuição, privacidade, arquivo, CSRF e revogação. OpenAPI válido com 19 operações implementadas. Diff revisto: novas rotas específicas, sem permitir genericamente a API; locks mantêm ordem projeto/tarefa. Incremento 1 validado antes de avançar para a UI.


### Incremento 2 — Interface de responsáveis

Lint/build passaram. Dois testes de Chrome passaram: colaborador associa dois/remove um/reload; pessoal só mostra criador e erro de rede conserva seleção sem falso sucesso/reenvio. A primeira execução falhou porque os seletores omitiam “Demo” dos nomes do seed; seletores corrigidos, repetição 2/2 passou. Não se alterou o seed para fazer passar testes.

Diff revisto antes do incremento 3: componente no detalhe com lista/seleção, loading/vazio/erro, leitura de arquivadas; identidade/CSRF no cliente existente e regras definitivas no backend. Sem gestão de colaboradores ou novas entidades.


### Incremento 3 — API de comentários

COM-01 implementada: 1–2000 pontos de código Unicode, não só espaços; JSON estrito, autor e instante do backend. Transação/locks coordenados com tarefa/projeto. Comentários históricos não truncados; nenhuma migration. Ordem existente criadoEm/id ASC explicitada no contrato.

**Verificação:** 28 testes passaram (8 CommentTest, 8 AssignmentTest e 12 SecurityTest), zero falhas/erros/ignorados; limites Unicode, autoria/data, campos forjados, arquivo, privacidade, ordem/desempate, leitura histórica >2000 e falha real de BD sem exposição de detalhes. Schema COM-01 validado também com JSON Schema. Diff revisto antes da UI: rotas específicas, sem abrir gestão de colaboradores, sem alteração V1/V2; teste de rota não implementada passa a usar colaboradores. Uma invocação Maven teve erro de sintaxe PowerShell na opção com vírgulas, antes de executar; repetida com argumento devidamente entre aspas, com sucesso.


### Incremento 4 — Interface de comentários e fecho local

TaskComments integrado no detalhe: autor/data/conteúdo, ordem mais antigos primeiro, texto seguro, contador Unicode, 1–2000/não só espaços, rascunho preservado em falha, bloqueio de pedidos simultâneos e nenhuma repetição automática. Arquivadas em leitura; pessoais mantêm privacidade. README, requisitos/histórias/COM-01, modelo/arquitetura, instruções, plano, contrato e evidências atualizados.

**Resultados finais:** 66 testes backend sem falhas/erros/ignorados; lint/build e Docker passaram; 18 testes normais Chrome passaram (4 especiais ignorados). Teste separado de reinício: 1 passou, com sessão anterior 401 e novo login a recuperar dois responsáveis e comentário com autoria/data. A primeira tentativa do teste ficou à espera do login sem pedir atualização à UI após navegar para o mesmo fragmento; o percurso foi corrigido para Atualizar perfil, como nos testes anteriores, e passou. Não se alterou a política de sessão para contornar a falha.

OpenAPI 0.6.0 válido, 20/24 operações implementadas; cinco exemplos e sete respostas reais de colaboração validados com schemas, DELETE atribuição 204; fixtures próprias removidas. V1/V2 preservadas, sem novas migrations. Após cleanup conservam-se 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições. Diff final, links locais e exclusão de ficheiros gerados/ambiente verificados.

Cada incremento foi testado e revisto antes do seguinte. Gestão de colaboradores (US-17-01/02), painel, Kanban/calendário, CI e entrega noutro computador continuam pendentes; elegíveis US-17-03/04, responsáveis US-18 e comentários US-19 implementados. Nenhum commit/push deste exercício ainda efetuado; issue #7 mantém-se aberta até registo/publicação posterior.


## Registo Git do Exercício 6 — 18/11/2026

O utilizador autorizou: “avança com o commit e push do exercicio 6”. Preparado commit com Refs #7 para responsáveis/elegíveis, comentários COM-01, interface, testes, contrato e documentação associados. A confirmação de SHA remoto/publicação e fecho da issue será registada no GitHub após push verificado.

Evidência funcional existente: 66 testes backend, lint/build, 18 testes normais Chrome e 1 separado de reinício passaram; não foram apresentados como nova execução nesta operação Git. Diff e ficheiros revistos, sem incluir ambiente, gerados, caches ou temporários. Sem reescrever histórico, alterar migrations ou dados da aplicação.
