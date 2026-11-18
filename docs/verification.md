# Matriz — Plano e evidências de verificação

**Data:** 18/11/2026. **Estado:** base técnica, acesso e gestão de projetos (Exercícios 2–4) verificados. Tarefas, colaboração de escrita e restantes fluxos do MVP ainda não concluídos. As secções por exercício preservam a sequência de evidências.

| Caso | Resultado esperado | Resultado observado | Evidência |
|---|---|---|---|
| V01 Acesso | Credenciais válidas permitem sessão, inválidas são rejeitadas; perfil e logout funcionam. | Passou no Exercício 3, API e Chrome. | SecurityTest e e2e/tests/auth.spec.ts. |
| V02 Projetos | Criar/pesquisar/editar/eliminar respeita propriedade e cascata. | Passou no Exercício 4: API/UI, arquivo/restauro, pesquisa e cascata/rollback. | ProjectCrudTest, projects.spec.ts e OpenAPI. |
| V03 Persistência | Tarefa surge sem recarregar e mantém-se após reinício. | Projetos passaram em reload/reinício; fluxo de tarefas ainda não executado. | projects.spec.ts @restart; tarefas pendentes. |
| V04 Validação | Vazio/só espaços e valores inválidos rejeitados no backend. | Passou para projetos no Exercício 4; DTOs de tarefas ainda pendentes. | ProjectCrudTest e formulário no Chrome. |
| V05 Estados/filtros | Alterações atualizam tarefa, filtros, progresso e contagens. | Não executado. | Pendente. |
| V06 Responsáveis | Múltiplos elegíveis, sem duplicação; remoção e limpeza atómicas. | Não executado. | Pendente. |
| V07 Comentários | Conteúdo, autor e data confiáveis persistem e são autorizados. | Não executado. | Pendente. |
| V08 Autorização | Segundo utilizador não acede a dados alheios por IDs manipulados; tarefas pessoais privadas. | Passou nas consultas e nas escritas de projetos; restantes escritas futuras ainda pendentes. | SecurityTest e Playwright; limites em security.md. |
| V09 Eliminação/erro | Cascata sem órfãos, cancelar sem pedido, erro sem falso sucesso/perda do formulário. | Passou para projetos, incluindo rollback por falha de BD; gestão autónoma de tarefas pendente. | ProjectCrudTest e projects.spec.ts. |
| V10 Sessão/CSRF | Cookie, logout, invalidação e proteção de operações de escrita. | Passou em login/logout, expiração/reinício e flags do cookie; escritas de negócio ainda pendentes. | SecurityTest, Playwright, test:restart e perfil prod local. |

## Testes automatizados mínimos da entrega — estado atual

1. Regra de validação: nome obrigatório/não só espaços passou nos projetos (Exercício 4); título de tarefa ainda pendente.
2. Autorização: dois utilizadores e recursos não acessíveis verificados em leitura e CRUD de projetos.
3. Criação/consulta posterior: projeto com PostgreSQL real, nova sessão, reload e reinício verificados no Exercício 4.

Registar comandos, ambiente, resultados e limitações reais. Não substituir testes de aplicação por validação de OpenAPI. Acrescentar regressão para bugs reproduzidos e corrigidos.

## Exemplos de Human-in-the-Loop já observados

| Objetivo da IA | Decisão do utilizador/programador | Verificação |
|---|---|---|
| Definir âmbito inicial | Aprovou uso individual; depois autorizou reconciliação colaborativa em COL-01. | Matriz de diferenças e atualização documental; não equivale a teste de execução. |
| Modelar associação Projeto/Tarefa | Aprovou tarefas independentes e correção da relação não identificadora. | Revisão semântica e renderização Mermaid. |
| Preparar API | Proposta inicial sem autenticação foi superada pelo novo âmbito; utilizador aprovou sessão/cookie. | Revisão do contrato e validação automática; autorização real ainda por testar. |

## Verificações documentais

A execução e resultados da validação de OpenAPI, referências e imagens ficam registados no log da ficha 3 após serem efetivamente executados. O registo seguinte corresponde à fase documental anterior à implementação. Os comandos reais da base técnica foram acrescentados no Exercício 2 ao README.

### Resultado da revisão documental — 18/11/2026

OpenAPI 3.1 validado: 24 operações e 12 exemplos válidos; schemas rejeitam campos de identidade indevidos e entradas obrigatórias inválidas. 22 histórias/88 critérios únicos; fontes ER sincronizadas e imagens ER/login/tarefas inspecionadas. Referências locais válidas e git diff --check sem erros. Ferramentas de validação isoladas em tmp, sem dependências da aplicação adicionadas. Todos os cenários de execução V01–V10 continuam Não executado.

## Exercício 2 — evidências de execução, 18/11/2026

Ambiente: Windows 11/PowerShell, JDK Temurin 21.0.9, Maven 3.9.16 (incluindo Wrapper), Node 24.16.0, Docker Desktop Engine 29.1.3/Linux. A imagem final utiliza Java 21.0.13 e PostgreSQL 17.9. As versões transitivas são fixadas pelo parent/lockfile; imagens identificadas por digest no Dockerfile/Compose.

| Verificação | Comando / evidência | Observado |
|---|---|---|
| Compilação e testes Java | `./backend/mvnw.cmd -B -ntp -f backend/pom.xml verify` | BUILD SUCCESS; 9 testes, 0 falhas, 0 erros, 0 ignorados. PostgreSQL real descartável via Testcontainers. |
| Wrapper | `./backend/mvnw.cmd -f backend/pom.xml validate` e `mvnw.cmd --version` | BUILD SUCCESS; Maven 3.9.16/Java 21; comando da raiz funcional. |
| Dependências frontend | `npm.cmd install`, seguido de `npm ci` no build Docker | Lockfile gerado e instalação Linux reproduzida; auditoria da instalação: 0 vulnerabilidades reportadas nessa execução. Não constitui auditoria integral de segurança. |
| Frontend | `npm.cmd run lint` e `npm.cmd run build` em frontend | Sem erros/avisos na versão final; TypeScript/Vite compilam a página técnica. |
| Compose | `docker compose config --quiet` | Configuração válida, sem imprimir valores resolvidos. |
| Arranque nativo | `. ./scripts/Load-LocalEnv.ps1` e `java -jar backend/target/matriz-0.1.0-SNAPSHOT.jar` | Conexão JDBC, V1 aplicada numa BD vazia, Hibernate valida seis entidades; health UP. |
| Ausência de seed no arranque | Consulta contagem de utilizador após primeiro arranque normal | 0 utilizadores; histórico Flyway version=1/success=true. |
| Seed no host | Mesmo JAR com `--matriz.demo.seed-only=true` | Termina com código 0; 2 utilizadores, 2 projetos, 3 tarefas, 1 colaboração, 2 atribuições, 1 comentário. |
| Build integrado | `docker compose build app seed` | Imagens construídas; React compilado copiado para static e servido pelo Spring Boot. target/node_modules do host excluídos do contexto. Testes são saltados nesta etapa de empacotamento, tendo sido executados separadamente. |
| Seed repetido na imagem | `docker compose --profile tools run --rm seed` | Termina com código 0; mantém as mesmas contagens, sem duplicar fixtures. |
| App integrada | `docker compose up -d app --wait` | App e BD healthy, execução da app como utilizador não root. |
| HTTP integrado | GET / e dois assets (/assets/*.js e *.css) | HTTP 200 para página, JavaScript e CSS. Verificação HTTP, não teste visual/E2E do browser. |
| Persistência no reinício | `docker compose restart db app`; `docker compose up -d db app --wait` | Antes/depois: 2/2/3/1/2/1 nas seis tabelas; health UP. O container da BD também foi recriado ao fixar o digest, conservando o volume e os dados. |
| Exclusão Git | `git check-ignore .env backend/target frontend/node_modules frontend/dist` | Todos ignorados. Configuração versionável só com valores fictícios. |

### Testes de InfrastructureTest

1. Migration versionada aplicada uma vez, reexecução sem migrations pendentes e sem seed automático.
2. Seis entidades, fixtures com acessos distintos, tarefa pessoal e hash BCrypt verificável.
3. Seed repetido conserva título editado, comentário/colaboração removidos e hash original.
4. Colisão de email na inicialização faz rollback e conserva o registo anterior.
5. CHECKs de espaços/enumerações, FK e unicidade de atribuição rejeitam dados inválidos.
6. Cascata de projeto elimina só os dependentes e conserva contas, outro projeto e tarefa pessoal.
7. Gravação/consulta SQL conserva data passada e campos opcionais.
8. MockMvc: health UP sem detalhes; pedidos GET/POST de negócio bloqueados. Não comprova login ou autorização por recurso.

9. Regressão de colisão de ID de projeto: rollback conserva proprietário/dados e não concede participação nem deixa contas fictícias parciais.

Relatório gerado: backend/target/surefire-reports/pt.matriz.InfrastructureTest.txt (não versionado); resumo manual de smoke: tmp/verification/foundation-smoke.json (não versionado). Os resultados observados estão preservados nesta tabela.

### Revisão crítica e limites

- Na revisão crítica foi removida a tolerância a colisões de IDs na primeira criação de fixtures; a transação passa a falhar em vez de reutilizar um projeto existente. Teste de regressão acrescentado; versão final com nove testes passou.
- O build preliminar com dependency:go-offline foi interrompido por demorar no descarregamento de dependências desnecessárias. O Dockerfile final resolve apenas o build necessário, com cache BuildKit; a construção final passou.
- O aviso de Fast Refresh inicial foi resolvido separando App.tsx de main.tsx. ESLint foi atualizado para uma versão suportada compatível com os plugins; a versão final passou sem avisos.
- JUnit/Mockito em Java 21 emitiu aviso sobre carregamento dinâmico de agente; não provocou falhas. Rever configuração de agente numa futura atualização de JDK/testes.
- Não houve teste de clonagem noutro computador; houve build Linux em containers sem artefactos de build do host. Não declarar estes resultados como equivalentes.
- Sem login, autorização por recurso, CRUD, limpeza COL-06, regras de arquivo, UI funcional, E2E ou pipeline CI implementados. V01–V10 mantêm o estado não executado de ponta a ponta.
- As restrições SQL não substituem validação dos DTOs nem políticas de acesso. Os três testes funcionais mínimos da entrega continuam por concluir nos fluxos reais, apesar dos nove testes de infraestrutura aprovados.

## Exercício 3 — acesso e segurança, 18/11/2026

**Política humana SEG-01:** 60 minutos de inatividade, sem lembrar-me; reinício do backend e logout exigem novo login. A proposta inicial de 30 minutos foi rejeitada. A configuração efetiva de 60 minutos foi verificada no ServletContext de um Tomcat real; não foi substituída pela configuração curta de teste.

| Verificação | Comando / evidência | Resultado observado |
|---|---|---|
| Backend completo | `./backend/mvnw.cmd -B -ntp -f backend/pom.xml verify` | BUILD SUCCESS; **21 testes**, 0 falhas, 0 erros, 0 ignorados: 9 de infraestrutura e 12 de segurança. PostgreSQL real descartável. |
| Identidade e credenciais | SecurityTest | Login válido roda ID da sessão; principal autenticado fica sem password/credenciais. Perfil apenas id/nome/email. Email inexistente e palavra-passe errada: mesma resposta 401. |
| CSRF/origem | SecurityTest e Playwright | Login/logout exigem CSRF; token de outra sessão ou anterior ao login é recusado; origem hostil é recusada mesmo com CSRF correto. |
| Logout e ausência de sessão | SecurityTest/Chrome | Sessão invalidada, cookie expirado, perfil 401 após logout. Cookies ausentes/desconhecidos não autorizam consultas. |
| Acesso aos recursos | SecurityTest/Chrome | Proprietário/colaborador leem projeto partilhado; Ana não lê projeto privado do Bruno; Bruno não lê tarefa pessoal da Ana/comentários/responsáveis. |
| Permissões atuais | SecurityTest | Remover participação revoga novas leituras sem novo login; criador histórico/atribuição isolada não concedem acesso. Guardas de proprietário, arquivo, destino e elegibilidade testadas. |
| Identidade forjada e validação | SecurityTest | Campo extra proprietarioId no login rejeitado; utilizadorId em query não troca o perfil; email/formato/UUID inválidos recusados; recurso inexistente 404. |
| Política efetiva | SecurityTest com servidor Servlet real | Timeout=60 minutos, tracking apenas COOKIE, nome matriz_session e HttpOnly verificados. |
| Frontend | `npm.cmd --prefix frontend run lint` e `npm.cmd --prefix frontend run build` | Sem erros/avisos; build concluído. Interface de login/perfil/logout, sem mocks ou armazenamento persistente de tokens. |
| Browser normal | `npm.cmd --prefix e2e test`, Chrome headless | **5 passaram; 1 ignorado por desenho** (timeout curto). Login, reload, logout, erro, duas identidades, privacidade, CSRF/origem e mensagem após revogação. |
| Expiração real | `npm.cmd --prefix e2e run test:expiry` com MATRIZ_SHORT_SESSION_TEST=1 e baseURL 18081 | **1 passou**: instância temporária com timeout=1m, 65 segundos sem pedidos; Atualizar perfil recebe 401 e mostra login com mensagem. Não foram esperados 60 minutos na instância principal. |
| Reinício do backend | `npm.cmd --prefix e2e run test:restart` | Login antes=200; perfil com a sessão anterior depois do reinício=401. BD/volume mantidos. |
| Cookie real no browser | Playwright | HttpOnly=true, SameSite=Lax, cookie de sessão sem expiração persistente; ID muda ao autenticar. Sem localStorage/sessionStorage. |
| Cookie do perfil prod | GET /api/sessao/csrf em container temporário com perfil prod | Secure=true, HttpOnly=true, SameSite=Lax. Verificação de cabeçalho local; não houve implantação/TLS público. |
| Build e execução | `docker compose build app seed`; `docker compose up -d app --wait` | App/BD healthy; frontend e backend integrados. V1 não alterada. |
| Contrato | OpenAPI 3.1 + JSON Schema com formatos | Especificação válida; 24 operações, 8 implementadas e 16 planeadas. Servidor /api, 12 exemplos válidos e 7 respostas JSON reais validadas contra os schemas; logout 204. |

### Testes de segurança (12)

1. Timeout real do Servlet, cookie nome/HttpOnly e tracking apenas por cookie.
2. Login, rotação da sessão, remoção de credenciais/hash do principal e perfil seguro.
3. Credenciais erradas e conta inexistente com a mesma mensagem genérica.
4. CSRF obrigatório no login e não transferível entre sessões.
5. Token CSRF pré-login inválido após autenticação; logout revoga sessão e expira cookie.
6. Sem sessão ou cookie desconhecido: todas as consultas protegidas recusadas.
7. Dois utilizadores, projetos com acessos distintos, tarefa pessoal e dependentes privados.
8. Revogação imediata de participação, mesmo com autoria histórica/atribuição existente.
9. Políticas de proprietário/colaborador, arquivo, elegibilidade pessoal e destino de transferência.
10. Identidade extra rejeitada, perfil não alterável por query, JSON/campos/UUIDs e recurso inexistente.
11. Origem hostil recusada com CSRF válido.
12. Escritas futuras permanecem bloqueadas mesmo para utilizador autenticado com CSRF.

### Revisão crítica e limites do exercício

- Os oito endpoints implementados estão enumerados em security.md e no contrato. Não há criação/edição/eliminação funcional de projetos/tarefas, colaboração de escrita, limpeza COL-06, painel ou vistas completas.
- Inferência de proprietário/criador/autor é uma obrigação dos casos de uso de criação futuros. CurrentIdentity já fornece a identidade confiável; não se afirma teste de uma escrita ainda inexistente. Responsável selecionado não é confundido com ator autenticado.
- Guardas por recurso foram testadas e aplicadas às leituras; cada nova escrita exige aplicação transacional e respetivos testes, incluindo concorrência/limpeza de atribuições.
- O teste real de expiração usa configuração curta isolada; o teste de servidor real verifica a política de 60 minutos. O teste de restart demonstra a perda da autenticação, sem apagar dados.
- Cookies Secure foram verificados no perfil prod, mas HTTPS/reverse proxy/certificados e implantação pública não foram executados. Sem rate limiting, monitorização ou gestão global de contas no MVP atual.
- A interface trata 401 nos fluxos existentes; os próximos ecrãs devem reutilizar essa política. Não há reenvio automático de escritas, polling de sessão ou lembrar-me.
- Traces/vídeo de rede desativados. Relatórios e screenshots de falha ficam ignorados pelo Git. Apenas contas e dados fictícios utilizados.

## Exercício 4 — gestão de projetos, 18/11/2026

**PESQ-01:** o utilizador rejeitou pesquisa que distingue acentos e confirmou correspondência por parte do nome, ignorando maiúsculas e acentos. Sem paginação e sem copiar máximos dos exemplos para o contrato.

| Verificação | Evidência | Resultado observado |
|---|---|---|
| Backend completo | `./backend/mvnw.cmd -B -ntp -f backend/pom.xml verify` | BUILD SUCCESS: **33 testes**, 0 falhas/erros/ignorados (9 infraestrutura + 12 segurança + 12 projetos). |
| Criação/persistência | ProjectCrudTest | 201/Location/DTO; proprietário da sessão; arquivado=false; consulta noutra sessão conserva dados; nome de 1 carácter e textos acima de 100/1000 aceites. |
| Validação/identidade | ProjectCrudTest | Nome vazio/branco/null/tipo errado e descrição inválida 422; JSON/campos extra de identidade 400; sem dados alterados. |
| PATCH | ProjectCrudTest | Omitidos conservam; null limpa descrição; vazio de descrição aceite; booleanos/null/nome inválidos recusados; proprietário imutável. |
| Listagem/pesquisa | ProjectCrudTest/Chrome | Só acessíveis, sem duplicação de proprietário/colaborador; revogação imediata. APRESENTACAO encontra apresentação; %, _ e aspas são literais. Arquivo true/false estrito; parâmetros inválidos 400. |
| Permissões/CSRF | ProjectCrudTest | Colaborador/não membro não editam/eliminam; todas as escritas exigem CSRF; ausência de sessão recusada. |
| Arquivo/restauro | ProjectCrudTest/Chrome | Preserva tarefas/dependentes; alterna listas ativa/arquivo; metadados conservados. |
| Cascata | ProjectCrudTest | Remove só projeto/tarefas/comentários/atribuições/colaborações relacionados; conserva contas, outro projeto e tarefa pessoal. Seed explícito não faz reaparecer projeto eliminado. |
| Falha real de cascata | Trigger de falha apenas no PostgreSQL descartável | DELETE devolve 500 genérico; todas as seis tabelas conservam contagens originais, sem eliminação parcial ou detalhes SQL no erro. Trigger/função de teste removidos. |
| Frontend | lint + TypeScript/Vite build | Passaram sem avisos na versão final. Ajustado cleanup do ref do dialog e navegação para evitar estado de carregamento sem novo pedido ao mudar hash. |
| Browser normal | `npm.cmd --prefix e2e test`, Chrome headless | **10 passaram; 2 ignorados por desenho** (expiração curta e reinício separados). Inclui 5 de acesso e 5 de projetos. |
| Eliminação UI | projects.spec.ts | Dialog explica cascata; Cancelar envia 0 DELETE, confirmar envia 1; 204 e ausência posterior. |
| Falha de comunicação | projects.spec.ts com abort de POST no teste | Mensagem de comunicação em português; formulário conserva nome/descrição, não anuncia sucesso nem reenvia; corrigida a causa, guardar passa e cria apenas o registo esperado. A app não contém mocks. |
| Percurso vertical com reinício | MATRIZ_RESTART_TEST=1 + `npm.cmd --prefix e2e run test:projects-restart` | **1 passou**: login/criação/reload/reinício real de app/novo login/mesmo projeto e descrição. Sem intervenção SQL manual para o fluxo. |
| Contrato | OpenAPI 3.1 e JSON Schema | Especificação válida: 24 operações, 12 implementadas. Cinco respostas reais de projetos/erro validadas contra schemas; DELETE 204. |
| Conservação de demonstração | Contagens SQL após cleanup dos testes | 2 utilizadores, 2 projetos, 3 tarefas e 1 comentário originais mantidos. Cleanup só dos IDs criados pelos testes. |
| Build integrado | Docker Compose build/up | App e PostgreSQL healthy, React servido pelo Spring Boot, sem alterar V1 ou apagar volume. |

### ProjectCrudTest — 12 casos

1. Criação infere proprietário, Location e consulta persistida com nova sessão.
2. Não impõe máximos pedagógicos nem trim de texto válido.
3. Corpos inválidos/campos proibidos sem alteração de dados.
4. PATCH/omissão/null, tipos estritos e proprietário imutável.
5. Listagem autorizada, sem duplicação e revogação de colaboração.
6. Pesquisa parcial sem acentos/maiúsculas, wildcards/aspas literais e vazio.
7. Arquivo/restauro e filtro booleano estrito, conservando dependentes.
8. Colaborador e utilizador alheio não editam/eliminam.
9. CSRF em todas as escritas e proteção de pedidos não autenticados.
10. Cascata sem órfãos, sem apagar contas/outros projetos/tarefa pessoal e sem restauro pelo seed.
11. Falha real de cascata faz rollback integral e não expõe erro de BD.
12. IDs malformados/inexistentes não alteram dados.

### Revisão, correções e limites

O primeiro comando normal de browser teve 1 falha num seletor de teste demasiado amplo: havia dois role=status (sucesso e carregamento) após DELETE. Corrigido o seletor para a mensagem exata; repetição passou 10/10 cenários normais. Não foi necessário alterar a API ou eliminar dados para ultrapassar essa falha.

O teste de reinício é explícito/separado por alterar o estado de app durante a execução. O de expiração curta foi executado no Exercício 3 e não foi repetido neste incremento, cuja política de sessão não mudou. Não somar testes ignorados como passados. Relatórios JUnit/Playwright são gerados fora do Git; resumo de resultados preservado aqui.

Permanecem limitações documentadas: sem paginação/garantias para grandes volumes; pesquisa normalizada no conjunto autorizado em memória; sem versão/ETag para rejeitar edições baseadas em dados antigos; só operações de projeto concluídas. Gestão de tarefas/colaboração de escrita/limpeza COL-06, vistas, painel e CI/entrega integral continuam pendentes. Rastreabilidade dos commits e publicação dos exercícios 3/4 registada nas issues #4/#5 e no checkpoint do log.

## Verificação da separação Git — 18/11/2026

A pedido do utilizador, o acesso e os projetos foram separados em dois commits. A versão intermédia de acesso foi materializada numa pasta isolada a partir da base do Exercício 2, sem alterar os ficheiros finais: 21 testes backend passaram, lint/build do frontend passaram e OpenAPI com oito operações implementadas foi validado. Os links locais dessa versão também foram verificados. O commit 0c1351e referencia #4.

As fontes finais foram comparadas por SHA-256 com a cópia anterior à separação e conservaram o conteúdo verificado no Exercício 4 (33 testes backend e cenários de browser documentados). O commit seguinte contém apenas a evolução de projetos/documentação, com Refs #5. Nenhum teste foi apresentado como execução nova da versão final nesta operação de Git; usou-se a evidência existente e a igualdade dos ficheiros de código. Credenciais/padrões e ficheiros gerados foram revistos antes dos commits. A confirmação de publicação é registada nas issues após o push.
