# Matriz — Code Review e Security Review

**Ficha 04, exercício 5 · 19/11/2026 · branch main · base revista 3b03d15.**
**Estado atual:** correções aprovadas e executadas; ver secção final. Diagnóstico e resultados iniciais preservados como histórico. Alterações ainda locais, sem commit/push.

## Resumo e âmbito

**0 vulnerabilidades confirmadas**, **1 fragilidade confirmada de testes (Q01, baixa)**, **1 risco/ambiguidade por validar (R01)** e **1 sugestão de manutenção (M01)**. Não foi demonstrado IDOR/Broken Access Control nos cenários executados. Isto não constitui certificação de segurança integral.

Git status inicialmente limpo; git diff sem alterações locais. Histórico revisto: 6b586cb plano, f2dc375 unitários, eaefeee AAA, 6cd862b integração e 3b03d15 E2E, face à base 212a4f6. Alterações recentes são testes/documentação/AGENTS; produção não alterada neste intervalo. Code Review limitado a TaskInputTest, diferenças de TaskCrudTest e tasks.spec.ts, hooks/asserções e organização AAA. Segurança: extensão necessária aos componentes já existentes de autenticação/autorização que suportam estes percursos, sem auditoria geral/refatoração.

Fontes: AGENTS.md; requirements.md (F02/F12/F13), user-stories.md (US-15/16, CA-US-16-01/03/04), constraints.md (COL-02/04/06/07, SEG-01), architecture.md, test-plan.md (T08–T11), verification.md, OpenAPI 0.7.0, README. Componentes inspecionados: SecurityConfiguration, SessionController, OriginGuard, AccountRepository, ProjectAccess/ProjectCommands, TaskAccess/TaskCommands/TaskQueries, AssignmentCommands, ApiExceptionHandler/ApiProblem, client.ts, TaskForm e TaskComments. Não se afirma revisão exaustiva de todos os ficheiros da aplicação.

## Observações de Code Review

Os testes de integração usam POST/GET reais via MockMvc, sem transação de teste que esconda a persistência. DTO completo, Location, identidade inferida, defaults e leitura SQL cobertos. O cenário negativo verifica 422 e snapshots de seis tabelas. Unitários chamam TaskInput sem serviços. E2E confirma UI e API, reload e cleanup próprio; não se confundiu status 201 com prova suficiente de persistência. Sem alterações de regras de negócio identificadas no diff.

### Q01 — limpeza interrompida na primeira falha

- **Categoria/estado:** confiabilidade dos testes, **confirmado por inspeção e reprodução sintética do fluxo**, não vulnerabilidade da aplicação.
- **Localização:** [e2e/tests/tasks.spec.ts:16](../e2e/tests/tasks.spec.ts#L16), loops nas linhas 20–27; hooks sequenciais semelhantes em outras specs devem ser revistos apenas se a correção for aprovada.
- **Evidência:** cada await/expect pode lançar; não existe try/catch por recurso antes de continuar o loop. Uma falha ao remover/verificar a primeira tarefa impede tentar as restantes tarefas e projetos. Reproduzido com Node e listas fictícias: primeira operação lançou erro simulado, apenas task1 foi visitada; task2/project1 não foram tentados. Não foi induzida falha destrutiva na BD.
- **Impacto:** fixtures podem ficar no ambiente de teste e prejudicar repetibilidade/diagnóstico após falha; não demonstra perda/exposição de dados de utilizadores.
- **Severidade:** **Baixa**. **Prioridade:** Média; cenário exige falha prévia de limpeza.
- **Proposta limitada:** recolher falhas por ID, continuar a tentar limpar todos os IDs próprios e apresentar erros no fim; conservar o erro original e não limpar dados fora da fixture. Alterar apenas hook de tasks.spec.ts inicialmente.
- **Regressão recomendada:** primeiro DELETE/GET de limpeza falha; verificar que IDs seguintes são tentados e que a suite conserva a falha. Também verificar caminho de sucesso/404 permitido.
- **Decisão:** proposta pendente de revisão humana; hook existente não corrigido automaticamente. Novo teste desta revisão usa cleanup allSettled para tentar todas as fixtures próprias.

### R01 — equivalência de espaços Unicode entre UI e backend

- **Categoria/estado:** validação/UX, **potencial, por validar**; não vulnerabilidade confirmada nem requisito novo.
- **Localização:** [TaskForm.tsx:24](../frontend/src/features/tasks/TaskForm.tsx#L24), [TaskInput.java:16](../backend/src/main/java/pt/matriz/tasks/TaskInput.java#L16) e linha 37; schema Texto do OpenAPI.
- **Evidência:** UI usa String.trim; backend usa Pattern.UNICODE_CHARACTER_CLASS para encontrar conteúdo não branco. Mecanismos diferentes não provam correspondência para todos os caracteres. O plano já sinaliza esta questão; não há decisão específica sobre U+0085/U+FEFF.
- **Impacto potencial:** um título limítrofe pode ser aceite numa camada e rejeitado noutra; não demonstrado bypass de autorização. **Severidade potencial Baixa**, prioridade Baixa, probabilidade não medida.
- **Proposta:** reproduzir casos concretos e validar com humano o significado de "apenas espaços" antes de uniformizar ou alterar contrato/produção.
- **Regressão recomendada:** matriz acordada de caracteres e títulos mistos nos unitários/API/UI. **Não executada nesta revisão** e sem requisito inventado.

### M01 — comentários AAA e leitura de cenários compostos

- **Categoria/estado:** sugestão de manutenção, não defeito funcional; severidade/prioridade Baixa.
- **Localização:** TaskCrudTest.patchPreservesOmittedFieldsAndClearsOnlyOptionalOnes e cenários compostos em mvp-verification.spec.ts.
- Alguns blocos combinam preparação/ação/verificação em linhas compactas; comentários AAA não substituem legibilidade. Na próxima alteração relevante, separar instruções/nomes por intenção, mantendo os ciclos e dependências. Não sugerir refatoração geral apenas por estilo.
- Verificação futura: comparação de operações/asserções e testes relevantes; nenhuma alteração aplicada nesta revisão.

## Segurança — mecanismo e evidências estáticas

| Área | Evidência no código | Conclusão limitada |
|---|---|---|
| Autenticação | SecurityConfiguration:32/60/85; SessionController | BCrypt por biblioteca, sessão no servidor, CSRF, rotação após login e denyAll para rotas não abertas. Login não substitui autorização. |
| Projetos | ProjectAccess:29/35; ProjectCommands.lockOwner | Consulta exige proprietário/colaborador atual; alterações de projeto exigem proprietário revalidado após lock. |
| Tarefas | TaskAccess.requireView/requireManage; TaskCommands:25/34 | Pessoal só criador; projeto exige acesso atual; escrita exige projeto ativo e revalida acesso/associação sob lock. |
| Dependentes/listas | TaskQueries e AssignmentCommands | Consultas/dependentes herdam acesso; SQL parametrizado para valores; elegibilidade não atribui acesso por si só. |
| Identidade | CurrentIdentity/SessionController e TaskCommands.create | Ator deriva da sessão; não de criador/proprietário enviados no corpo. |
| Erros e UI | ApiExceptionHandler/ApiProblem; client.ts/TaskComments | Erros genéricos sem SQL/credenciais; comentários renderizados como texto; nenhum dangerouslySetInnerHTML/eval/storage de sessão identificado na pesquisa feita. Não é prova universal contra XSS. |

Diferença 403/404 é do contrato aprovado; não classificada automaticamente como IDOR. SQL com concatenação em loops de testes usa nomes de tabelas internos fixos, não entrada do utilizador; não foi classificado como SQL injection. HTTP local/cookie não Secure na demonstração não foi tratado como vulnerabilidade confirmada de produção: exposição pública/HTTPS não foram executados. Configuração real de outro ambiente permanece fora do ensaio.

## Verificação dinâmica com duas contas fictícias

Instância local autorizada **matriz-ficha04-e2e**, app 18083, BD 55434, volume próprio conservado do exercício anterior. Ana e Bruno em contextos Playwright independentes, sem partilha de cookies. Nenhuma credencial/token é registado no relatório.

1. Cenário existente `mvp-verification.spec.ts`, IDs alheios: recursos próprios criados por Ana, Bruno sem acesso; sete leituras e dez escritas diretas negadas com 403; tarefa conservada, exclusão das listagens; colaboração concedida permite leitura, revogação volta a negar e limpa atribuição sem apagar autoria. Executado duas vezes: **2 passaram, 4,2 s**.
2. Novo [security-review.spec.ts](../e2e/tests/security-review.spec.ts), teste complementar de revisão: cada conta cria projeto/tarefa privados; nas duas direções a outra conta tenta GET/PATCH/DELETE de projeto/tarefa, com sessão e CSRF válidos. **12 tentativas negadas por execução** com 403, application/problem+json, type/status/codigo corretos e apenas campos de erro. Snapshots autorizados do projeto, colaboradores, tarefa, comentários, responsáveis e elegíveis mantêm-se iguais após cada tentativa. Listas excluem IDs alheios. Executado duas vezes: **2 passaram, 4,7 s**.

São **dois cenários distintos repetidos duas vezes**, não quatro cenários independentes. Novo teste organizado em ciclos AAA, fixtures novas e cleanup em finally/allSettled; confirma GET 404 dos projetos removidos e termina contextos. Testes API Playwright não abrem um browser nem substituem os testes UI do exercício 4.

Comandos efetivamente executados, PowerShell na raiz:

```powershell
. ./scripts/Load-LocalEnv.ps1
$env:APP_PORT = '18083'
$env:DB_PORT = '55434'
$env:APP_ALLOWED_ORIGINS = 'http://127.0.0.1:18083'
docker compose -p matriz-ficha04-e2e up -d db app --wait
$env:PLAYWRIGHT_CHANNEL = 'chrome'
$env:MATRIZ_BASE_URL = 'http://127.0.0.1:18083'
npm.cmd --prefix e2e test -- tests/mvp-verification.spec.ts --grep 'IDs alheios' --repeat-each=2
npm.cmd --prefix e2e test -- tests/security-review.spec.ts --repeat-each=2
npm.cmd --prefix frontend run lint
```

SQL de conferência final confirmou **2 utilizadores, 2 projetos, 3 tarefas, 1 comentário e 2 atribuições**. Containers/rede isolados removidos com `docker compose -p matriz-ficha04-e2e down`, sem -v; volume conservado. Principal não foi reiniciado, sem seed repetido ou alterações de dados principais.

## Análise estática e segredos

- **ESLint configurado** em frontend/eslint.config.js; `npm.cmd --prefix frontend run lint` terminou **exit 0, sem avisos/erros reportados**. Analisa frontend, não Java nem testes e2e fora dessa pasta; não equivale a scanner completo de vulnerabilidades. Semgrep/SonarQube não configurados; não instalados.
- Inspeção por padrões nos ficheiros versionados: cabeçalhos de chaves privadas, tokens GitHub clássicos/fine-grained e access keys AWS. **Zero ficheiros sinalizados** pelos padrões pesquisados. Não é pesquisa exaustiva de segredos, auditoria do histórico Git, análise de dependências ou certificação de ausência de credenciais. .env não foi impresso ou versionado; contas e palavras-passe de testes são fictícias e configuração fica fora dos relatórios.
- Aviso NO_COLOR/FORCE_COLOR observado no Node, sem relação demonstrada com vulnerabilidades. Nenhum aviso ESLint para classificar como falso positivo.
- Reprodução sintética Q01 usa apenas Node/assert e listas fictícias; confirma interrupção do loop, não uma falha real de DELETE na aplicação.

## Entregáveis, decisões e pendências

Existem no repositório unitários TaskInputTest, integração TaskCrudTest e E2E tasks.spec.ts; resultados reais anteriores preservados em verification.md. README já contém execução geral e recebeu comandos focados da Ficha 04. test-plan.md atualizado com a verificação complementar. O relatório mantém o SHA base 3b03d15; não inventa commit final para alterações locais.

Decisões da revisão: rejeitar inferência de que 403/404 diferentes, concatenação de nomes internos de tabelas ou ausência de cookie Secure no HTTP local sejam, por si, IDOR/SQL injection/falha confirmada de produção. Não confundir falta de reprodução com prova de ausência de vulnerabilidades. Não aplicar Q01/R01/M01 automaticamente. A validação humana deste relatório/propostas **ainda não ocorreu**.

Pendências: decidir Q01 e reproduzir/clarificar R01 se priorizado. Não houve correção de produção e, portanto, não há regressão de uma correção a declarar aprovada. Novo teste cobre o controlo de acesso observado, não corrige a aplicação. Não foram executadas nesta revisão a suite backend integral, unitários, todos os E2E, reinícios/expiração, carga, outros browsers, pentest externo ou auditoria completa de dependências/concurrency. A ausência de vulnerabilidade demonstrada aplica-se apenas às verificações descritas. Exercício 5 tem revisão/testes/relatório preparados, com decisões humanas pendentes explícitas.


## Correções aprovadas — 19/11/2026

**Decisão humana:** "aceito as correções propostas". R01 clarificada em resposta específica: "Conservar a definição Unicode do backend e alinhar a interface". O diagnóstico inicial acima é histórico; os estados atuais são os desta secção. Sem novo requisito de comprimento ou normalização de texto.

| Ponto | Alteração aplicada | Estado atual |
|---|---|---|
| Q01 | e2e/helpers/cleanup.ts recolhe falhas e tenta todos os passos sequencialmente; afterEach de tasks.spec.ts tenta autenticação, tarefas e projetos, anexa apenas nomes de fixtures quando falha e só lança AggregateError se não existir falha original | Corrigido no hook revisto; outros hooks não foram generalizados nesta correção limitada |
| R01 | TaskForm.tsx usa conteúdo fora de Unicode White_Space, em vez de trim. Backend inalterado. Schema Texto do OpenAPI explicitado com o conjunto Unicode, evitando semântica de \S dependente do motor | Clarificado e alinhado para o título de tarefas; não se afirma revisão de todos os formulários |
| M01 | Separadas autenticação/criação, instruções e asserções em TaskCrudTest.patchPreservesOmittedFieldsAndClearsOnlyOptionalOnes; comentários AAA corrigidos | Melhoria localizada aplicada, sem refatoração geral; comparação de tokens confirmou operações/asserções preservadas |

### Regressões e verificações reais

- **cleanup.spec.ts:** três testes sem serviços: primeira limpeza falha e as seguintes continuam; caminho sem falhas; falha original do teste não é substituída. Usa o mesmo helper do hook real. Não provoca falhas destrutivas na aplicação. Execução isolada inicial: 3 passaram, 782 ms.
- **TaskInputTest:** cinco variantes Unicode inválidas e três válidas acrescentadas à cobertura, tanto create como patch. U+0085/U+00A0/U+2003 apenas espaços são rejeitados; FEFF não é White_Space na definição existente e continua aceite. Conteúdo misto preservado sem trim. Total classe: 19 testes.
- **tasks.spec.ts:** novo cenário UI rejeita quatro títulos só de espaços Unicode sem POST e cria dois títulos válidos (FEFF e conteúdo misto), confirmando API e conservação do texto. Hooks reais limpam os IDs. Restantes cenários do ficheiro preservados.
- `./backend/mvnw.cmd -f backend/pom.xml '-Dtest=TaskInputTest,TaskCrudTest' test` → **BUILD SUCCESS, 36 testes, 0 falhas/erros/ignorados**, 41,633 s (19 unitários + 17 integração PostgreSQL Testcontainers).
- `npm.cmd --prefix frontend run lint` e `npm.cmd --prefix frontend run build` → passaram. Imagem da app isolada reconstruída com sucesso, serviços healthy.
- Ambiente de execução como no exercício 4: APP_PORT=18083, DB_PORT=55434, APP_ALLOWED_ORIGINS=http://127.0.0.1:18083, MATRIZ_BASE_URL igual, PLAYWRIGHT_CHANNEL=chrome. `docker compose -p matriz-ficha04-e2e build app` e `up -d db app --wait`; seed não repetido, volume existente conservado.
- `npm.cmd --prefix e2e test -- tests/cleanup.spec.ts tests/tasks.spec.ts tests/security-review.spec.ts` → **9 passaram, 1 ignorado**, 13,2 s. Ignorado apenas o reinício opt-in; não aprovado por esta execução. Inclui a regressão de segurança com duas identidades.
- OpenAPI 3.1 validado com OpenAPIV31SpecValidator e sete exemplos do schema Texto com Draft202012Validator. Primeira tentativa de importar yaml falhou no runtime base; reutilizado tmp/openapi-validation já existente, sem instalar dependências. Validação final passou.
- Comparação Node do padrão explícito do schema com a regex UI nos 1 114 112 pontos de código possíveis: equivalentes para caracteres isolados. Isto não constitui prova exaustiva de equivalência entre versões Unicode de Java/JS; o backend foi exercitado pelos exemplos unitários e pela UI/API real.
- SQL final na instância isolada: 2 utilizadores, 2 projetos, 3 tarefas, 1 comentário, 2 atribuições. Containers/rede removidos com down sem -v; volume conservado. App principal não reconstruída/reiniciada neste pedido.

Não foram alteradas as regras/implementação do backend ou migrations. A clarificação do schema Texto também descreve os nomes de projeto que já usam a mesma regra no backend; o formulário de projetos não foi alterado e equivalência Unicode de todos os formulários não foi certificada. Não houve novas execuções da suite backend completa, todos os E2E ou especiais. Q01/R01 não são agora decisões pendentes neste âmbito; novas extensões continuam fora dele. Alterações locais sem commit/push. Relatórios anteriores preservados, sem transformar resultados históricos em novas execuções.


## Fecho Git da Ficha 04 — 19/11/2026

O utilizador autorizou os commits finais e o push para concluir a Ficha 04. Issues #11–15 acompanham plano/unitários/AAA/integração/E2E; #16 acompanha revisão e correções aprovadas do exercício 5, em dois commits. Os checkpoints anteriores sem commit/push são históricos. Os SHAs e a confirmação efetiva do remoto ficam nas issues após o push. Não foram repetidas suites apenas para o registo Git; conservam-se os resultados reais descritos acima. Exercícios obrigatórios concluídos; CI opcional na Ficha 04 fica para a Ficha 05. Uma alteração externa de uma linha vazia em Dockerfile foi preservada fora dos commits desta ficha.
