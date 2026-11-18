# Matriz — Plano e evidências de verificação

**Data:** 18/11/2026. **Estado:** base técnica do Exercício 2 implementada e verificada abaixo. Cenários funcionais V01–V10 ainda não executados de ponta a ponta; resultados de infraestrutura não os substituem.

| Caso | Resultado esperado | Resultado observado | Evidência |
|---|---|---|---|
| V01 Acesso | Credenciais válidas permitem sessão, inválidas são rejeitadas; perfil e logout funcionam. | Não executado. | Pendente. |
| V02 Projetos | Criar/pesquisar/editar/eliminar respeita propriedade e cascata. | Não executado. | Pendente. |
| V03 Persistência | Tarefa surge sem recarregar e mantém-se após reinício. | Não executado. | Pendente. |
| V04 Validação | Vazio/só espaços e valores inválidos rejeitados no backend. | Não executado. | Pendente. |
| V05 Estados/filtros | Alterações atualizam tarefa, filtros, progresso e contagens. | Não executado. | Pendente. |
| V06 Responsáveis | Múltiplos elegíveis, sem duplicação; remoção e limpeza atómicas. | Não executado. | Pendente. |
| V07 Comentários | Conteúdo, autor e data confiáveis persistem e são autorizados. | Não executado. | Pendente. |
| V08 Autorização | Segundo utilizador não acede a dados alheios por IDs manipulados; tarefas pessoais privadas. | Não executado. | Pendente. |
| V09 Eliminação/erro | Cascata sem órfãos, cancelar sem pedido, erro sem falso sucesso/perda do formulário. | Não executado. | Pendente. |
| V10 Sessão/CSRF | Cookie, logout, invalidação e proteção de operações de escrita. | Não executado. | Pendente. |

## Testes automatizados mínimos a implementar

1. Regra de validação (nome/título obrigatório e não só espaços).
2. Autorização com dois utilizadores e recurso não acessível.
3. Criação e consulta posterior à persistência real.

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
