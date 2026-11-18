# Matriz — Gestão de projetos (Ficha 03, Exercício 4)

## Análise e plano

**Estado:** CRUD e interface integrados; validações e evidências em verification.md. Depende do acesso do Exercício 3, registado em 0c1351e e associado à issue #4. Este incremento referencia a issue #5. Issue #5 aberta antes da implementação.

Requisitos: F01 (gestão de projetos), F04 (persistência), F07 (progresso), F09 (arquivo/restauro), F13 (autorização), F17 (eliminação) e F18 (pesquisa). Histórias: US-01/02/09/11/12/16/20/21, com os limites dos casos de uso ainda não implementados de tarefas/colaboração.

1. Criar DTOs de entrada com validação estrita segundo o OpenAPI; manter PATCH com distinção entre campo omitido e null.
2. Implementar comandos transacionais de criar/editar/eliminar. O proprietário vem de CurrentIdentity; alterações obtêm lock do projeto antes de autorizar e gravar. Cascatas da V1 fazem parte da mesma transação, sem eliminar utilizadores.
3. Implementar listagem autorizada/arquivo/pesquisa e reutilizar o DTO de consulta/progresso existente. Sem paginação ou limites de texto arbitrários.
4. Integrar formulário/lista/detalhe/edição na interface, com estados de carregamento/vazio/sucesso/erro, CSRF, mensagens de sessão inválida, prevenção de submissões repetidas e confirmação de eliminação.
5. Testar validação, permissões por identidade, PATCH, arquivo, cascata/rollback, browser e o percurso completo após reiniciar backend/reautenticar. Atualizar contrato/README/log/evidências.

## Decisão humana PESQ-01

O utilizador rejeitou a proposta de distinguir acentos e confirmou: **“Por parte do nome, ignorando maiúsculas e acentos: «APRESENTACAO» encontra «Preparar apresentação»”**. Pesquisa vazia não restringe o conjunto autorizado no estado de arquivo selecionado.

Implementação: filtrar acesso e arquivo em SQL; comparar nomes por normalização Unicode no backend sobre esse conjunto, sem extensões novas de BD. Esta opção é adequada à listagem integral do MVP sem paginação; não é uma promessa de desempenho para grandes volumes. Pesquisa trata %, _ e aspas como texto literal, não SQL ou wildcards.

## Validações e contrato

- Nome: string obrigatória na criação; pelo menos um carácter não branco. Sem mínimo de 3 ou máximo de 100 no contrato atual.
- Descrição: string ou null, opcional, sem máximo definido. Vazio é permitido.
- PATCH: pelo menos um dos campos nome, descricao, arquivado. Omitido conserva; null limpa apenas descricao. Nome null/branco e arquivado null/não booleano são inválidos.
- Propriedades desconhecidas, incluindo id/proprietario/proprietarioId, são rejeitadas. O proprietário é imutável e inferido no backend.
- Listagem: q opcional; arquivado booleano, por defeito false; resposta é array, sem paginação/ordem garantida.
- IDs UUID opacos fornecidos pelo servidor; formato inválido 400, recurso inexistente 404.

## Operações e autorização

| Método / endpoint | Regra | Sucesso / erros principais |
|---|---|---|
| POST /api/projetos | Autenticado + CSRF; proprietário da sessão, arquivado=false | 201 + Location/Projeto; 400/401/403/422/500. |
| GET /api/projetos | Autenticado; só propriedade/colaboração atual, arquivo e pesquisa | 200 + array; 400/401/403/500. |
| GET /api/projetos/{id} | Proprietário ou colaborador atual; leitura de arquivo permitida | 200 + Projeto; 400/401/403/404/500. |
| PATCH /api/projetos/{id} | Apenas proprietário + CSRF, incluindo arquivo/restauro | 200 + Projeto; 400/401/403/404/422/500. |
| DELETE /api/projetos/{id} | Apenas proprietário + CSRF; confirmação prévia na UI | 204; 400/401/403/404/500. |

Proprietário gere o projeto; colaborador vê o detalhe e progresso, mas não tem controlos de gestão. Pedidos diretos de gestão por colaborador também são negados. Uma recusa não deve alterar dados nem anunciar sucesso.

## Persistência e concorrência

PostgreSQL e volume existente; sem nova migration ou alteração à V1. Nome/descrição/arquivo e proprietário permanecem nas tabelas existentes. Comandos transacionais só devolvem sucesso após commit. Edição/eliminação bloqueiam a linha do projeto, verificam o proprietário atual e mantêm a transação até terminar. As escritas futuras de tarefas/colaboração devem coordenar-se com os mesmos recursos e as regras COL-06.

Eliminar projeto remove tarefas, comentários, atribuições e colaborações por FK/cascata, conservando contas, outros projetos e tarefas pessoais. Uma falha faz rollback de todo o conjunto. O seed não recria dados eliminados em arranques ou repetições.

## Fluxo a validar

Login → criar projeto → vê-lo na lista → abrir detalhe → editar → recarregar → reiniciar backend → novo login → voltar a consultar os valores → pedir eliminação → cancelar sem pedido → confirmar eliminação → confirmar ausência e conservação dos restantes dados.

## Riscos e limites

Sem paginação: a pesquisa normalizada na aplicação deverá ser revista se o volume crescer. Sem controlo de versões do registo no contrato; edições concorrentes serializam-se por lock, sem deteção de intenção de edição antiga. Falha de rede após envio não prova rollback: preservar formulário/erro e não repetir escritas automaticamente. Interface e API não substituem os testes transacionais dos futuros casos de uso de tarefas. CRUD de tarefas, gestão de colaboradores e painel integral continuam fora deste incremento.

Referências técnicas consultadas: [locks PostgreSQL 17](https://www.postgresql.org/docs/17/explicit-locking.html), [transações Spring](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).

## Implementação e mensagens de erro

ProjectInput valida JSON estrito e devolve DTOs Create/Patch. Campos desconhecidos resultam em 400. Falhas de validação resultam em 422/VALIDACAO com mensagem do campo, por exemplo: `nome: Indica um nome que não seja vazio ou apenas espaços.`, `descricao: É necessário texto.`, `arquivado: Indica true ou false.`, ou `pedido: Indica pelo menos um campo para alterar.`. Descrição vazia/null são permitidas; nome e booleano null não são. Não há coerção de números/objetos para texto nestes DTOs.

ProjectCommands usa CurrentIdentity e ProjectAccess, transações e lock antes de editar/eliminar. O retorno ao controlador só acontece após commit. ProjectQueries usa snapshot consistente e consulta agregada para todos os projetos autorizados; a pesquisa normalizada não concede acesso e não aplica wildcards. Não há N+1 por projeto na listagem.

A interface ProjectsWorkspace oferece lista, pesquisa explícita/limpeza, arquivo e detalhe; ProjectForm cria/edita com validação e preservação de entradas; DeleteProjectDialog pede confirmação modal e descreve toda a cascata. Sem confirmação não há pedido DELETE. Ref de operação em curso e botões desativados impedem submissões repetidas. A navegação por fragmento #projetos/{id} permite recarregar/partilhar a localização sem transformar o ID em autorização. O backend volta a verificar o acesso.

Apenas o proprietário vê controlos de edição/arquivo/restauro/eliminação; um colaborador vê o projeto em leitura. Mensagens de sessão inválida regressam ao login, sem tentar novamente uma escrita. Falhas de comunicação mostram uma mensagem em português, mantêm o formulário e não anunciam sucesso; quando o resultado do servidor é desconhecido, não há reenvio automático.

## Plano de testes e evidência

- Backend: criação com proprietário inferido/Location, persistência em nova sessão, nomes de 1 carácter e texto acima dos máximos dos exemplos, inválidos/identidade forjada, PATCH/omissão/null, acesso e revogação, pesquisa/case/acentos/wildcards literais, arquivo/restauro, CSRF, cascata/seed não destrutivo, rollback por falha real de BD e IDs inválidos/inexistentes.
- Browser: criar/listar/consultar/editar/recarregar/eliminar, cancelamento sem DELETE, pesquisa sem acentos, arquivo/restauro, colaborador em leitura, privacidade, conservação de formulário em falha injetada e validação de espaços/1 carácter.
- Percurso de reinício separado: cria projeto pelo browser, recarrega, reinicia app sem apagar BD, volta ao login, autentica e confirma o mesmo projeto/descrição. Fixtures criadas pelo teste são limpas apenas pelos IDs registados, sem apagar dados preexistentes.
- Resultados efetivos, incluindo a distinção entre os testes normais/isolados e falhas corrigidas, em verification.md. Não equivale a conclusão de tarefas, colaboradores de escrita ou painel integral.
