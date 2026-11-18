# Matriz — Acesso à aplicação (Exercício 3)

Este documento regista o incremento de acesso. A evolução posterior com CRUD/pesquisa de projetos do Exercício 4 encontra-se em [projects.md](projects.md); os limites abaixo sobre escritas futuras são históricos do fecho do Exercício 3, exceto onde ainda aplicáveis a tarefas/colaboração.

## Análise e plano para revisão

**Estado:** acesso e consultas protegidas do Exercício 3 implementados e verificados; 21 testes Java e 6 cenários de browser passaram (5 normais + 1 de expiração isolada), além de reinício/cookie prod. Resultados e limites em verification.md. A política temporal foi confirmada pelo utilizador em SEG-01. COL-02–06, TEC-01, requisitos e contrato existentes prevalecem.

1. Implementar autenticação local com Spring Security/BCrypt e consulta da conta na BD. Expor apenas DTO de perfil (id, nome, email); nunca palavra-passe/hash.
2. Implementar GET /api/sessao/csrf, POST/GET/DELETE /api/sessao. Guardar o contexto no servidor, rodar o identificador ao autenticar e invalidar sessão/cookie no logout. CSRF também no login e logout; devolver erros JSON coerentes.
3. Centralizar a identidade autenticada e as políticas por recurso. Proprietário ou colaborador pode consultar projeto; apenas proprietário o pode gerir. Tarefa pessoal pertence ao criador; tarefa de projeto depende da participação atual. Comentários/atribuições herdam o acesso à tarefa. Atribuição não concede acesso.
4. Disponibilizar consultas mínimas de recursos existentes para verificar autorização através da API real, sem implementar o CRUD dos exercícios seguintes. Não abrir escritas sem o caso de uso correspondente. A política terá verificações para proprietário, arquivo, destino de transferência e elegibilidade, a reutilizar nos incrementos dependentes.
5. Criar ecrã de login, consulta do perfil e logout. Cliente guarda CSRF apenas em memória, envia cookies da mesma origem e encaminha 401 para login com mensagem compreensível. Não repetir automaticamente escritas após falha/expiração.
6. Testar com PostgreSQL/Testcontainers e duas identidades reais: credenciais, sessão, fixação, logout, CSRF, privacidade, acesso atual, papéis por projeto, arquivo e pedidos diretos. Testar o fluxo na interface e atualizar OpenAPI/README/evidências/log. Progresso e resultados concretos estão registados em verification.md.

## Decisão humana SEG-01

O utilizador rejeitou a proposta inicial de 30 minutos e escolheu explicitamente: **“60 minutos de inatividade, sem «lembrar-me»; reinício e logout exigem novo login”**. Aplicada em server.servlet.session.timeout=60m, persistent=false e cookie sem Max-Age. Logout revoga imediatamente a sessão. Não há renovação por token ou lembrar-me; cada pedido com sessão válida conta como atividade para o servidor. Não há polling automático que prolongue a sessão sem interação. Fechar o browser não constitui revogação no servidor; alguns browsers restauram cookies de sessão, por isso o logout é a forma explícita de terminar o acesso.

## Convenções técnicas implementadas

- Cookie matriz_session: HttpOnly, SameSite=Lax, sem Max-Age persistente; Secure obrigatório no perfil HTTPS de produção. Desenvolvimento apenas local, sem CORS permissivo.
- Sessões em memória, sem JWT nem tabela funcional adicional. Não escalar réplicas sem rever esta escolha.
- Token CSRF mantido no servidor, obtido por GET /api/sessao/csrf e enviado no cabeçalho X-CSRF-Token; renovar após login e logout.
- Credenciais inválidas produzem mensagem genérica; consultas de perfil não devolvem campos de persistência.
- Configuração por ambiente; sem segredos novos ou contas reais. Não alterar a migration V1.

## Identidade e responsáveis

O ator de todas as operações vem da autenticação. Proprietário, criador e autor são derivados desse ator nos casos de uso que criam recursos. O responsável atribuído continua a ser uma escolha entre os utilizadores elegíveis, como aprovado em COL-02; o seu ID não prova a identidade de quem faz o pedido. Inferir sempre o próprio utilizador como responsável contrariaria a atribuição múltipla aprovada.

## Matriz de autorização e responsabilidades

| Recurso / operação | Quem pode executar | Regra / local de validação |
|---|---|---|
| Sessão / preparar CSRF e login | Visitante com CSRF válido no login | Filtros Spring Security, validação do pedido e AuthenticationManager. |
| Sessão / perfil e logout | Identidade autenticada | Contexto da sessão no backend; logout exige CSRF e invalidação no servidor. |
| Projeto / criar | Utilizador autenticado | Caso de uso define proprietário pelo ator; criação no exercício 4. |
| Projeto / consultar | Proprietário ou colaborador atual | Política de Projetos consulta as relações atuais na BD em cada pedido. |
| Projeto / editar, arquivar/restaurar, eliminar, gerir colaboradores | Apenas proprietário | Política de Projetos; escritas só abertas com os casos de uso seguintes. |
| Tarefa de projeto / consultar | Proprietário ou colaborador atual do projeto | Política de Tarefas consulta projeto e política de Projetos; autoria histórica não concede acesso. |
| Tarefa de projeto / gerir | Proprietário ou colaborador atual, projeto não arquivado | Política de Tarefas; caso de uso transacional verifica arquivo e acesso. |
| Tarefa pessoal / consultar ou gerir | Apenas criador | Identidade da sessão comparada com criador persistido. |
| Tarefa / transferir | Ator com acesso à origem e destino; ambos operáveis | Caso de uso verifica acesso/arquivo e limpa atribuições inelegíveis atomicamente; exercício de tarefas. |
| Comentário / consultar | Quem tem acesso atual à tarefa | Mesma política da tarefa; não usar autoria do comentário como autorização independente. |
| Comentário / adicionar | Quem pode gerir a tarefa | Autor/data no backend; acesso e arquivo verificados no caso de uso futuro. |
| Atribuição / consultar | Quem tem acesso atual à tarefa | Política da tarefa antes de ler associações. |
| Atribuição / adicionar ou remover | Quem pode gerir a tarefa | Verificar ator e elegibilidade do utilizador selecionado; PK impede repetição; caso de uso futuro. |

O frontend apresenta os controlos adequados, mas a recusa é sempre do backend. Uma operação ainda não implementada não fica acessível por ter sessão válida. Não abrir genericamente todos os caminhos /api/** sem controlo do recurso.

## Endpoints disponibilizados neste incremento

| Endpoint | Autenticação / autorização | Validações |
|---|---|---|
| GET /api/sessao/csrf | Público; pode criar sessão anónima | Emitir token associado à sessão, sem cache e sem o guardar em logs. |
| POST /api/sessao | Público com CSRF | Email/palavra-passe conforme DTO; rejeitar campos extra de identidade; credenciais inválidas 401 genérico; rotação da sessão e do CSRF após sucesso. |
| GET /api/sessao | Sessão válida | Perfil da identidade autenticada; sem ID de utilizador vindo do pedido e sem hash. |
| DELETE /api/sessao | Sessão válida e CSRF | Revogar sessão, limpar contexto e expirar cookie; 204 após sucesso. |
| GET /api/projetos/{projetoId} | Sessão + acesso atual ao projeto | Validar ID, existência e participação; DTO aprovado com progresso derivado. |
| GET /api/tarefas/{tarefaId} | Sessão + acesso atual à tarefa | Distinguir tarefa pessoal e de projeto; DTO sem identidade imposta pelo cliente. |
| GET /api/tarefas/{tarefaId}/comentarios | Sessão + acesso atual à tarefa | Autorizar antes de devolver qualquer conteúdo/autor/data. |
| GET /api/tarefas/{tarefaId}/responsaveis | Sessão + acesso atual à tarefa | Autorizar antes de listar associações; não expor hashes. |

As quatro consultas de recursos são uma parcela mínima de operações já existentes no contrato, para demonstrar a proteção por recurso com pedidos reais. Listagens gerais, criação, edição, eliminação, comentários e atribuições de escrita permanecem nos exercícios próprios. Sem alterações ao âmbito funcional ou novas entidades.

Erros previstos: 400 para JSON/ID malformado, 401 para ausência/invalidade de autenticação, 403 para CSRF/permissões, 404 para recurso inexistente, 422 para campos inválidos e 500 genérico sem detalhes internos. Documentar precedência: um pedido de escrita sem CSRF pode ser recusado pelo filtro com 403 antes de chegar à autenticação/controlador.

## Organização dos ficheiros

- backend/.../identity: consulta de credenciais, principal, identidade atual, configuração e controlador de sessão.
- backend/.../projects e tasks: políticas por recurso e consultas mínimas com DTOs, sem expor entidades JPA.
- backend/.../shared: erros JSON consistentes e conversão de validações.
- backend/src/main/resources/application.yml e perfil de produção: cookie e configuração temporal após decisão humana; sem tocar na migration V1.
- frontend/src/api e features/auth: cliente com cookies/CSRF em memória, formulário de login, perfil/logout e tratamento de 401.
- backend/src/test e verificações de browser: testes reais com contas fictícias e PostgreSQL descartável.
- OpenAPI, README, constraints, plano, verification e log da ficha 03: ciclo de sessão, matriz, evidências e limites reais.

## Casos de teste e evidência

1. Login válido devolve só id/nome/email, roda identificador de sessão e permite consultar perfil.
2. Palavra-passe errada e email inexistente devolvem a mesma mensagem genérica; nenhum perfil é autenticado.
3. Campos adicionais como proprietarioId/autorId não alteram a identidade; corpo de credenciais com campos não permitidos é rejeitado.
4. Sem cookie/sessão válida, perfil e consultas protegidas devolvem 401.
5. Login/logout sem CSRF ou com token de outra sessão é recusado; CSRF anterior ao login deixa de servir para escritas.
6. Logout invalida acesso com a sessão anterior e expira cookie; consultar perfil depois exige novo login.
7. Expiração/invalidação no servidor conduz a 401 e a interface regressa ao login com mensagem compreensível. Teste de timeout real a definir após decisão temporal.
8. Ana consulta o projeto partilhado; Bruno consulta-o como colaborador; Ana não consulta o projeto privado do Bruno.
9. Bruno não consulta tarefa pessoal da Ana, incluindo comentários e atribuições por pedido direto.
10. Remoção da participação na BD revoga imediatamente novas leituras do projeto/tarefas, sem exigir novo login; autoria histórica ou atribuição isolada não concedem acesso.
11. Política recusa gestão de projeto por colaborador e escrita em tarefa arquivada. Casos de uso de escrita só serão concluídos nos exercícios seguintes.
12. A mesma conta pode ser proprietária num projeto e colaboradora noutro, sem papel global inventado.
13. Hash BCrypt do seed valida a palavra-passe fictícia; não existe palavra-passe em claro na coluna nem hash no DTO, logs ou resposta.
14. Cookies HttpOnly/SameSite/Secure e persistência do contexto são verificados num servidor HTTP real, além de MockMvc.
15. Interface: carregamento, credenciais inválidas, sucesso, logout e sessão expirada; não guardar palavra-passe após sucesso nem tokens em armazenamento persistente.

## Riscos e recomendações

- Sessões em memória exigem nova autenticação após reinício e não suportam várias réplicas sem coordenação. Documentar essa limitação, sem introduzir infraestrutura nova.
- O cenário de timeout deve ser testado sem esperar 60 minutos: usar configuração curta apenas num ambiente isolado de teste, nunca alterar a política da demonstração silenciosamente.
- SameSite não substitui CSRF; manter o filtro e renovar o token após autenticação/logout.
- TLS e cookie Secure devem ser coerentes no ambiente de produção. Não permitir uma configuração que torne credenciais acessíveis por HTTP público.
- Não implementar autorizações apenas no controlador ou frontend: as consultas/casos de uso devem aplicar as políticas e consultar participação atual.
- Não declarar implementada a atribuição/criação/autoria de escritas que ainda não existem. Registar essas dependências nos resultados da revisão.
- Proteção contra tentativas automatizadas, monitorização e operação pública exigem revisão própria antes de exposição externa; a aplicação continua com execução local autorizada.

## Rastreabilidade e limites

US-15/16 e política transversal das US-17–20. Implementar autenticação não conclui o CRUD, limpeza atómica COL-06, painel ou interface integral. A autorização de novas escritas tem de ser aplicada e testada quando essas operações forem disponibilizadas.

Referências técnicas: [sessão e persistência do contexto — Spring Security 6.5](https://docs.spring.io/spring-security/reference/6.5/servlet/authentication/session-management.html), [CSRF — Spring Security 6.5](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html).

## Implementação e limites concretos

- SessionController usa AuthenticationManager/DaoAuthenticationProvider/BCrypt, invoca a estratégia de rotação e guarda explicitamente o SecurityContext na HttpSession. ProviderManager apaga as credenciais, incluindo o hash do principal autenticado. O perfil público é UserProfile.
- CSRF usa HttpSessionCsrfTokenRepository e o tratamento mascarado padrão do Spring. O endpoint devolve o token mascarado; CsrfAuthenticationStrategy limpa o token anterior no login. OriginGuard recusa origens fora da lista explícita nas escritas. Pedidos CLI sem Origin continuam a exigir CSRF. Não há CORS permissivo.
- ProjectAccess e TaskAccess obtêm o ator de CurrentIdentity. Nunca recebem um ator escolhido pelo cliente. ProjectQueries/TaskQueries fazem autorização e leitura na mesma transação read-only REPEATABLE_READ, evitando devolver dados de outro estado após uma transferência concorrente. Casos de uso de escrita futuros exigem coordenação transacional própria e a limpeza COL-06.
- As consultas usam SQL parametrizado e DTOs, não entidades JPA. A escolha técnica de leitura JDBC reutiliza a mesma ligação/transação PostgreSQL; JPA continua a validar os seis mapeamentos. Sem nova tecnologia ou alteração de esquema.
- GETs de recursos permitem leitura autorizada de arquivo; políticas requireManage/requireActive impedem escritas nesse estado. Métodos de política para proprietário/elegibilidade/transferência foram testados, mas não equivalem a endpoints de escrita implementados.
- JSON rejeita propriedades desconhecidas. O login não aplica normalização de email/palavra-passe: correspondência exata na BD, conforme a ausência de decisão aprovada sobre normalização. Valida email não vazio e formato, palavra-passe não vazia, sem trim da palavra-passe. Credenciais erradas/inexistentes têm a mesma resposta genérica.
- Cookie HttpOnly/SameSite=Lax/Path=/; tracking só por cookie, sem URL rewriting. Sem persistência de sessão, sem JWT, sem segredo de aplicação novo. Spring devolve cache-control sem armazenamento. Perfil prod exige APP_ALLOWED_ORIGINS explícito e cookie Secure; HTTPS deve ser configurado no limite da infraestrutura, sem confiar arbitrariamente em cabeçalhos forwarded.
- ApiExceptionHandler e handlers dos filtros devolvem application/problem+json; sem stack trace, hashes ou valores de credenciais. Sessão inválida em GET devolve 401; CSRF ausente/inválido em escrita pode produzir 403 antes do controlo de sessão, conforme a cadeia de filtros documentada.
- O cliente React guarda a palavra-passe só no estado do formulário e limpa-a no sucesso; CSRF apenas em memória durante o pedido. Não usa localStorage/sessionStorage. Trata 401 no arranque e na consulta/logout; cada novo fluxo de negócio deve reutilizar o tratamento de sessão e nunca repetir escritas automaticamente.

O exercício 3 não conclui os exercícios 4–6: criação de projetos com proprietário, criação de tarefas com criador, comentários com autor/data e seleção/persistência de responsáveis continuam por implementar nos respetivos casos de uso. As regras/guardas já existem; não é atribuída evidência de escrita real inexistente. Não existe registo público, recuperação de palavra-passe, rate limiting ou exposição externa implementada.


## Atualização — Exercício 5

CRUD/listagem de tarefas implementados com CurrentIdentity, TaskAccess e CSRF; 16 de 24 operações disponíveis. Transferência/desassociação aplicam COL-06 transacionalmente. Arquivo bloqueia todas as escritas. Comentários/responsáveis mantêm apenas leitura; gestão de colaboradores e painel continuam bloqueados. Ver tasks.md e verification.md.


## Atualização — Exercício 6

20 de 24 operações implementadas: elegíveis, criação/remoção de responsáveis e criação de comentários acrescentados. Escritas verificam acesso/arquivo com locks projeto/tarefa dentro da transação; CSRF obrigatório. utilizadorId é uma seleção validada, nunca identidade do ator. Duplicado não concede acesso; apenas criador elegível em pessoais. Autor/instante de comentário exclusivamente do backend; conteúdo COM-01 (1–2000 pontos de código Unicode, não só espaços). Gestão de colaboradores continua bloqueada. Ver task-collaboration.md e verification.md.


## Atualização — Exercício 7

Colaboradores (US-17), painel (US-22), Kanban (US-07) e calendário (US-08) integrados na interface/API. PAINEL-01 inclui pessoais + projetos ativos acessíveis na UI, exclui arquivo, não depende dos filtros da lista. COL-07 permite ao proprietário gerir colaboradores mesmo arquivado; remoção limpa atribuições atomicamente e conserva autoria/contas. Tarefas arquivadas continuam protegidas contra escrita. Modelo/stack/V1/V2 preservados; OpenAPI 0.7.0 com 24 operações implementadas. As referências anteriores a estas funcionalidades pendentes ficam superadas por este incremento. Ver [integração](frontend-integration.md) e verification.md para resultados/limites; entrega final/CI não declaradas concluídas.
