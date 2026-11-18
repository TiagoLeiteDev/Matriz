# Matriz — User Stories do MVP colaborativo

**Versão:** 2.0, 18/11/2026. Âmbito colaborativo autorizado por COL-01. As histórias existentes foram adaptadas ao acesso autenticado, mantendo identificadores para rastreabilidade. Novas histórias correspondem à ficha 03. Detalhes pendentes estão em constraints.md.

## Precondições e regras transversais

- Em todos os cenários de sucesso, existe sessão válida e autorização sobre o recurso. Sem sessão, negar; sem permissão, impedir acesso/leitura/escrita sem revelar dados alheios.
- O proprietário gere projeto e colaboradores, arquivo/restauro e eliminação do projeto. Proprietário e colaboradores gerem tarefas, atribuições e comentários.
- Tarefas independentes são privadas do criador, definido pelo backend. Participação num projeto não dá acesso às tarefas pessoais de outros utilizadores.
- Arquivo impede alterações a tarefas/dependentes e exclui-os das vistas principais; restauro mantém filtros e prazo.
- Estados: TODO (inicial), IN_PROGRESS, DONE. Prioridades: LOW, MEDIUM, HIGH ou null; omitida na criação = MEDIUM, null explícito = Sem prioridade (TAR-01). Prazos opcionais, incluindo passados.
- Textos obrigatórios não podem ser só espaços. Progresso arredondado a uma casa decimal; sem tarefas 0% e Sem tarefas.
- Campos omitidos, pesquisa/filtros e Sem projeto/Sem prioridade conservam as regras aprovadas. As consultas operam apenas no conjunto autorizado.
- Conservar dados confirmados nas falhas, preservar entradas do formulário, não anunciar sucesso e impedir submissão repetida em curso.
- Identidade, proprietário, criador, autor e data de comentário são inferidos no backend, não confiados ao cliente.

## Referências de regras preservadas

As referências PV/DEC nos critérios existentes mantêm rastreabilidade ao histórico das fichas 1/2. Onde existe mudança, COL-01–06 e a política de acesso atual prevalecem.

| Regra | Conteúdo atual |
|---|---|
| PV-01 | Estado inicial TODO quando não escolhido outro. |
| PV-02 | Nome/título apenas com espaços é inválido. |
| PV-03 | Progresso com uma casa decimal. |
| PV-04 | Pesquisa por parte do título sem distinguir maiúsculas; vazio não restringe. |
| PV-05 | Interseção de pesquisa/filtros nas três vistas, dentro do conjunto autorizado. |
| PV-06 | Prazo passado permitido, sem notificações. |
| PV-07 | Consulta separada de arquivo; bloquear alterações de tarefas/dependentes e novas associações; permissões atuais prevalecem. |
| PV-08 | Filtro Sem projeto, limitado às tarefas pessoais do utilizador autenticado. |

## Backlog e critérios



### US-01 — Criar um projeto

Como utilizador autenticado, quero criar um projeto, para organizar tarefas relacionadas com uma iniciativa.

**Prioridade:** P1 — Base da organização por projeto.
**Origem:** F01; DEC-07–09.
**Dependências:** Nenhuma.

**CA-US-01-01**

- **Given:** estou a criar um projeto.
- **When:** introduzo nome não vazio e guardo, com ou sem descrição.
- **Then:** o projeto fica disponível com os dados introduzidos.

**CA-US-01-02**

- **Given:** estou a criar um projeto.
- **When:** tento guardar sem nome ou apenas com espaços.
- **Then:** a criação é impedida e é indicado o campo obrigatório.

**CA-US-01-03 — Projeto inicialmente não arquivado**

- **Given:** estou a criar um projeto com nome válido.
- **When:** guardo o projeto e a gravação é confirmada.
- **Then:** o projeto é criado com arquivado=false e fica elegível para as consultas de projetos não arquivados.

Critério acrescentado na revisão do Architecture Pack (P03) para verificar uma decisão já aprovada na preparação da API.

### US-02 — Consultar e editar projetos

Como utilizador autenticado, quero consultar e editar os meus projetos, para manter a informação atualizada.

**Prioridade:** P1 — Consulta e manutenção do projeto.
**Origem:** F01; DEC-08–09.
**Dependências:** US-01.

**CA-US-02-01**

- **Given:** existe um projeto não arquivado.
- **When:** consulto e abro o projeto.
- **Then:** vejo o nome e a descrição.

**CA-US-02-02**

- **Given:** existe um projeto com tarefas.
- **When:** altero nome ou descrição e guardo com nome válido.
- **Then:** os dados são atualizados e as tarefas mantêm a associação.

**CA-US-02-03**

- **Given:** existe um projeto do qual sou proprietário.
- **When:** tento guardar nome vazio ou apenas com espaços.
- **Then:** a alteração é rejeitada e o nome anterior mantém-se.

### US-03 — Criar tarefas

Como utilizador autenticado, quero criar tarefas com ou sem projeto, para registar ações que preciso de realizar.

**Prioridade:** P1 — Registo do trabalho.
**Origem:** F02; DEC-07–10.
**Dependências:** US-01 apenas para associação.

**CA-US-03-01**

- **Given:** estou a criar uma tarefa.
- **When:** guardo título não vazio e estado válido sem selecionar projeto.
- **Then:** a tarefa fica independente.

**CA-US-03-02**

- **Given:** existe um projeto não arquivado.
- **When:** crio uma tarefa com título e estado válidos associada ao projeto.
- **Then:** a tarefa pertence ao projeto e entra no cálculo de progresso.

**CA-US-03-03**

- **Given:** estou a criar uma tarefa.
- **When:** guardo sem título ou apenas com espaços.
- **Then:** a criação é impedida e é indicado o campo obrigatório.

**CA-US-03-04**

- **Given:** introduzi título e estado válidos.
- **When:** guardo sem descrição, prazo ou prioridade.
- **Then:** a tarefa é criada sem exigir esses campos.

**CA-US-03-05**

- **Given:** introduzi um título válido sem escolher estado.
- **When:** guardo a tarefa.
- **Then:** o estado inicial é TODO, conforme PV-01.

### US-04 — Consultar e editar tarefas

Como utilizador autenticado, quero consultar e editar os dados das tarefas, para manter o planeamento atualizado.

**Prioridade:** P1 — Manutenção do trabalho.
**Origem:** F02, F06; DEC-08, DEC-10.
**Dependências:** US-03.

**CA-US-04-01**

- **Given:** existe uma tarefa.
- **When:** consulto e altero título ou descrição e guardo título válido.
- **Then:** as consultas seguintes apresentam os dados atualizados.

**CA-US-04-02**

- **Given:** existe uma tarefa.
- **When:** defino prazo e prioridade LOW, MEDIUM ou HIGH e guardo.
- **Then:** os valores são guardados e a tarefa aparece no calendário na data do prazo.

**CA-US-04-03**

- **Given:** existe uma tarefa com prazo e prioridade.
- **When:** retiro esses valores e guardo.
- **Then:** deixa de ter entrada no calendário, mantendo-se na lista e no Kanban.

**CA-US-04-04**

- **Given:** existe uma tarefa guardada.
- **When:** tento guardar título vazio ou apenas com espaços.
- **Then:** a alteração é rejeitada e o título anterior mantém-se.

**CA-US-04-05**

- **Given:** existe uma tarefa.
- **When:** guardo um prazo anterior ao dia atual.
- **Then:** o prazo é aceite, sem criar notificações, conforme PV-06.

### US-05 — Alterar estados e reabrir tarefas

Como utilizador autenticado, quero alterar o estado das tarefas, para acompanhar o trabalho e retomar tarefas concluídas.

**Prioridade:** P1 — Acompanhamento da execução.
**Origem:** F03; DEC-03.
**Dependências:** US-03.

**CA-US-05-01**

- **Given:** uma tarefa está num dos estados aprovados.
- **When:** escolho qualquer outro entre TODO, IN_PROGRESS e DONE e guardo.
- **Then:** a lista mostra o novo estado e o Kanban a coluna correspondente; verificar todas as transições.

**CA-US-05-02**

- **Given:** um projeto tem duas tarefas concluídas e progresso 100%.
- **When:** altero uma para IN_PROGRESS.
- **Then:** a tarefa é reaberta e o progresso passa a 50%.

### US-06 — Consultar tarefas em lista

Como utilizador autenticado, quero consultar as tarefas em lista, para ter uma visão conjunta do trabalho.

**Prioridade:** P1 — Consulta geral.
**Origem:** F05; DEC-02, DEC-04.
**Dependências:** US-03.

**CA-US-06-01**

- **Given:** existem tarefas independentes e de projetos não arquivados.
- **When:** abro a lista sem restrições.
- **Then:** consigo identificar e consultar essas tarefas, incluindo as sem prazo.

**CA-US-06-02**

- **Given:** uma tarefa está na lista.
- **When:** guardo alteração de título ou estado e volto à lista.
- **Then:** vejo os valores atualizados.

### US-07 — Consultar tarefas em Kanban

Como utilizador autenticado, quero consultar tarefas agrupadas por estado num Kanban, para perceber a distribuição do trabalho.

**Prioridade:** P1 — Visualização obrigatória por estado.
**Origem:** F05; DEC-02–04.
**Dependências:** US-03, US-05.

**CA-US-07-01**

- **Given:** existem tarefas elegíveis em diferentes estados.
- **When:** abro o Kanban sem restrições.
- **Then:** cada tarefa aparece apenas na coluna do seu estado.

**CA-US-07-02**

- **Given:** existe tarefa sem prazo fora de projeto arquivado.
- **When:** consulto o Kanban.
- **Then:** a tarefa aparece na coluna do seu estado.

### US-08 — Consultar tarefas em calendário

Como utilizador autenticado, quero consultar tarefas pelos prazos num calendário, para planear o trabalho ao longo do tempo.

**Prioridade:** P1 — Visualização obrigatória por prazo.
**Origem:** F05, F06; DEC-04, DEC-10.
**Dependências:** US-03.

**CA-US-08-01**

- **Given:** existe tarefa com prazo fora de projeto arquivado.
- **When:** consulto o período correspondente.
- **Then:** a tarefa aparece na data de conclusão prevista.

**CA-US-08-02**

- **Given:** existe tarefa sem prazo, independente ou de projeto não arquivado, com filtros compatíveis.
- **When:** consulto o calendário.
- **Then:** não tem entrada, mas permanece na lista e no Kanban.

**CA-US-08-03**

- **Given:** uma tarefa tem prazo definido.
- **When:** altero e guardo outra data.
- **Then:** a consulta seguinte mostra a nova data e não a anterior.

### US-09 — Consultar progresso dos projetos

Como utilizador autenticado, quero consultar a percentagem de conclusão de cada projeto, para acompanhar o progresso do trabalho associado.

**Prioridade:** P1 — Acompanhamento do resultado.
**Origem:** F07; DEC-05.
**Dependências:** US-01, US-03, US-05; CA-US-09-04 depende também de US-13.

**CA-US-09-01**

- **Given:** um projeto tem quatro tarefas, uma concluída.
- **When:** consulto o progresso.
- **Then:** vejo 25%.

**CA-US-09-02**

- **Given:** um projeto não tem tarefas.
- **When:** consulto o progresso.
- **Then:** vejo 0% e Sem tarefas.

**CA-US-09-03**

- **Given:** um projeto tem duas tarefas, uma concluída, e há uma tarefa independente.
- **When:** concluo a tarefa independente.
- **Then:** o projeto mantém 50%.

**CA-US-09-04**

- **Given:** um projeto tem duas tarefas, uma concluída.
- **When:** confirmo eliminar a não concluída.
- **Then:** o projeto fica com uma tarefa concluída e mostra 100%.

**CA-US-09-05**

- **Given:** um projeto tem três tarefas, uma concluída.
- **When:** consulto o progresso.
- **Then:** vejo 33,3%, segundo PV-03.

### US-10 — Reorganizar a associação das tarefas autorizadas

Como utilizador autenticado, quero associar, transferir ou desassociar tarefas de projetos, para reorganizar o trabalho sem recriar tarefas.

**Prioridade:** P1 — Reorganização do trabalho.
**Origem:** F08; DEC-06.
**Dependências:** US-01, US-03.

**CA-US-10-01**

- **Given:** existe tarefa independente e projeto não arquivado.
- **When:** associo e guardo.
- **Then:** a tarefa conserva os restantes dados e entra no progresso do projeto.

**CA-US-10-02**

- **Given:** A tem uma tarefa concluída e uma não concluída e B não tem tarefas.
- **When:** transfiro a concluída de A para B.
- **Then:** A mostra 0%, B mostra 100% e os restantes dados da tarefa conservam-se.

**CA-US-10-03**

- **Given:** uma tarefa pertence a um projeto.
- **When:** retiro a associação e guardo.
- **Then:** fica independente e privada do criador original, conserva os dados e deixa de contar no projeto anterior. Se não sou o criador, deixo de ter acesso por esta operação.

**CA-US-10-04 — Remover atribuições inelegíveis**

- **Given:** uma tarefa tem responsáveis que não têm acesso ao projeto de destino.
- **When:** uma transferência autorizada é confirmada.
- **Then:** as atribuições agora inelegíveis são removidas na mesma operação; as elegíveis mantêm-se, os comentários/autoria conservam-se e nenhuma alteração parcial é confirmada.

### US-11 — Retomar o trabalho sem perder dados

Como utilizador autenticado, quero encontrar os dados guardados quando reabro a aplicação, para continuar o trabalho anterior.

**Prioridade:** P1 — Cenário de sucesso e utilização continuada.
**Origem:** F04; DEC-02.
**Dependências:** Operações de gravação; CA-US-11-02 depende de US-12 e US-13.

**CA-US-11-01**

- **Given:** guardei projetos, tarefas, estados, prioridades, prazos e associações.
- **When:** recarrego a interface ou reinicio o backend e volto a autenticar-me quando necessário.
- **Then:** os dados continuam disponíveis com os mesmos valores.

**CA-US-11-02**

- **Given:** arquivei um projeto e confirmei eliminar uma tarefa.
- **When:** reinicio o backend e volto a autenticar-me quando necessário.
- **Then:** o projeto continua arquivado com as suas tarefas e a tarefa eliminada não reaparece.

**CA-US-11-03 — Falha de criação**

- **Given:** existem dados confirmados e uma nova tarefa está preparada para gravação.
- **When:** a gravação da nova tarefa falha.
- **Then:** é apresentada uma mensagem de erro, não é anunciado sucesso nem criada uma tarefa confirmada; os dados anteriores mantêm-se. Após corrigir a causa, posso voltar a tentar guardar.

**CA-US-11-04 — Falha de edição**

- **Given:** uma tarefa tem um título confirmado.
- **When:** tento alterar o título e a gravação falha.
- **Then:** é apresentado erro sem anúncio de sucesso; o título confirmado mantém-se. Após corrigir a causa, posso tentar novamente.

**CA-US-11-05 — Falha de eliminação**

- **Given:** existe uma tarefa e confirmei a sua eliminação.
- **When:** a gravação da eliminação falha.
- **Then:** é apresentado erro sem anúncio de sucesso e a tarefa mantém-se nos dados confirmados, sem alterar o progresso. Após corrigir a causa, posso tentar novamente.

### US-12 — Arquivar e restaurar projetos

Como utilizador autenticado, quero arquivar e restaurar projetos, para retirar trabalho das vistas principais sem perder informação.

**Prioridade:** P2 — Organização de trabalho antigo.
**Origem:** F09; DEC-12, DEC-14.
**Dependências:** US-01; US-03 quando existam tarefas.

**CA-US-12-01**

- **Given:** existe projeto ativo com tarefas.
- **When:** arquivo o projeto.
- **Then:** projeto e tarefas saem das vistas principais e conservam dados e associações.

**CA-US-12-02**

- **Given:** existe projeto arquivado com tarefas.
- **When:** restauro o projeto.
- **Then:** voltam a ser elegíveis para as vistas principais com os dados conservados, respeitando os filtros e o período do calendário.

**CA-US-12-03**

- **Given:** um projeto arquivado tem tarefa com prazo e outra sem prazo.
- **When:** restauro o projeto.
- **Then:** ambas regressam à lista e ao Kanban com filtros compatíveis; apenas a com prazo aparece no calendário, no período correspondente.

**CA-US-12-04**

- **Given:** existe projeto arquivado.
- **When:** consulto os arquivados.
- **Then:** consigo localizar e restaurar o projeto.

**CA-US-12-05**

- **Given:** um projeto está arquivado.
- **When:** tento associar nova tarefa ou alterar uma tarefa desse projeto.
- **Then:** a operação é impedida enquanto estiver arquivado, conforme PV-07.

**CA-US-12-06 — Proteção das tarefas arquivadas**

- **Given:** um projeto arquivado tem uma tarefa com dados e associação guardados.
- **When:** tento editar, mudar o estado, eliminar, transferir ou desassociar essa tarefa.
- **Then:** cada tentativa é impedida e os dados, a associação e o progresso mantêm-se.

### US-13 — Eliminar tarefas

Como utilizador autenticado, quero eliminar tarefas que deixaram de ser necessárias, para manter o trabalho organizado.

**Prioridade:** P1 — Gestão de tarefas exigida na ficha 03.
**Origem:** F10; DEC-13.
**Dependências:** US-03.

**CA-US-13-01**

- **Given:** existe uma tarefa.
- **When:** peço para eliminar.
- **Then:** é pedida confirmação e avisado que a eliminação é definitiva antes de remover dados.

**CA-US-13-02**

- **Given:** está pendente a confirmação.
- **When:** cancelo.
- **Then:** a tarefa e os dados mantêm-se sem alterar progresso.

**CA-US-13-03**

- **Given:** está pendente a confirmação.
- **When:** confirmo.
- **Then:** a tarefa é removida das visualizações e o progresso do projeto é recalculado sem ela.

### US-14 — Pesquisar e filtrar tarefas

Como utilizador autenticado, quero pesquisar tarefas pelo título e filtrá-las, para encontrar o trabalho relevante.

**Prioridade:** P1 — Filtros de tarefas exigidos na ficha 03.
**Origem:** F11; DEC-15.
**Dependências:** US-03 e visualizações correspondentes.

**CA-US-14-01**

- **Given:** existe apenas uma tarefa intitulada Preparar apresentação.
- **When:** pesquiso esse título completo.
- **Then:** essa tarefa consta dos resultados.

**CA-US-14-02**

- **Given:** existem tarefas com diferentes projetos, estados e prioridades.
- **When:** aplico isoladamente cada filtro.
- **Then:** apenas vejo tarefas com o valor selecionado.

**CA-US-14-03**

- **Given:** não existe título correspondente à pesquisa.
- **When:** pesquiso.
- **Then:** vejo zero correspondências e os dados não são alterados.

**CA-US-14-04**

- **Given:** existe tarefa Preparar apresentação.
- **When:** pesquiso APRESENTAÇÃO.
- **Then:** a tarefa corresponde por parte do título sem distinção de maiúsculas, conforme PV-04.

**CA-US-14-05**

- **Given:** A tem título Preparar apresentação, projeto Curso, estado Em Curso, prioridade Alta e prazo 19/11/2026; B tem os mesmos valores mas prioridade Baixa; C tem os mesmos valores de A mas projeto Pessoal. Os projetos não estão arquivados.
- **When:** pesquiso apresentação e filtro por Curso, Em Curso e Alta.
- **Then:** apenas A é apresentada na lista e no Kanban e, ao consultar 19/11/2026, no calendário. B e C não satisfazem a interseção aprovada em PV-05.

**CA-US-14-06**

- **Given:** existem tarefas independentes e associadas.
- **When:** seleciono Sem projeto.
- **Then:** apenas vejo as independentes, conforme PV-08.

**CA-US-14-07**

- **Given:** existe uma pesquisa preenchida.
- **When:** limpo a pesquisa.
- **Then:** o título deixa de restringir os resultados, mantendo os filtros selecionados.

**CA-US-14-08 — Sem prioridade**

- **Given:** A é uma tarefa independente sem prioridade e B é independente com prioridade Alta; ambas têm título Preparar apresentação e estado Por Fazer.
- **When:** pesquiso apresentação e filtro por Sem projeto, Por Fazer e Sem prioridade.
- **Then:** apenas A corresponde; aplicam-se também as regras de visibilidade e prazo de cada vista.

### US-15 — Aceder e terminar sessão

Como utilizador autenticado, quero iniciar sessão, consultar o meu perfil e terminar sessão, para utilizar a aplicação com a minha identidade.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F12; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-15-01**

- **Given:** existe uma conta fictícia válida.
- **When:** inicio sessão com credenciais válidas.
- **Then:** obtenho sessão válida e o perfil sem palavra-passe ou hash.

**CA-US-15-02**

- **Given:** não existe sessão válida.
- **When:** tento aceder a operação protegida ou uso credenciais inválidas.
- **Then:** o backend nega o acesso e a interface apresenta mensagem de acesso necessária ou credenciais inválidas.

**CA-US-15-03**

- **Given:** tenho sessão válida.
- **When:** termino sessão.
- **Then:** a sessão deixa de autorizar novos pedidos e a interface regressa ao início de sessão.

**CA-US-15-04**

- **Given:** a sessão deixou de ser válida.
- **When:** a interface recebe recusa de autenticação.
- **Then:** apresenta mensagem compreensível e encaminha para início de sessão.

### US-16 — Impedir acesso indevido

Como utilizador autenticado, quero ter os meus recursos protegidos no backend, para evitar acesso por utilizadores sem permissão.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F13; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-16-01**

- **Given:** A e B têm projetos privados distintos.
- **When:** B altera identificadores em pedidos diretos para os recursos de A.
- **Then:** não obtém projeto, tarefas, comentários ou atribuições de A nem consegue alterá-los.

**CA-US-16-02**

- **Given:** sou colaborador de um projeto.
- **When:** tento editar/eliminar o projeto ou gerir colaboradores.
- **Then:** a operação é negada; continuo autorizado a gerir tarefas conforme a política.

**CA-US-16-03**

- **Given:** A tem tarefa independente.
- **When:** B tenta consultar, alterar ou atribuir responsáveis nessa tarefa.
- **Then:** o acesso é negado e nenhum dado é alterado.

**CA-US-16-04**

- **Given:** um cliente envia proprietário/criador/autor de outra pessoa como identidade.
- **When:** o pedido é processado.
- **Then:** esses valores não conferem identidade ou permissão e não substituem a identidade autenticada.

### US-17 — Gerir colaboradores e elegibilidade

Como utilizador autenticado, quero gerir colaboradores de um projeto meu, para partilhar o trabalho com pessoas autorizadas.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F14; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-17-01**

- **Given:** sou proprietário e existe outro utilizador.
- **When:** adiciono-o como colaborador.
- **Then:** passa a ter acesso ao projeto sem duplicar a mesma colaboração.

**CA-US-17-02**

- **Given:** sou proprietário de projeto com colaborador.
- **When:** retiro a colaboração.
- **Then:** o utilizador perde o acesso por essa relação e as atribuições que se tornam inelegíveis são removidas na mesma operação atómica, sem apagar comentários ou conta.

**CA-US-17-03**

- **Given:** tenho acesso a uma tarefa de projeto.
- **When:** consulto utilizadores elegíveis.
- **Then:** recebo proprietário e colaboradores atuais sem contas inelegíveis nem hashes.

**CA-US-17-04**

- **Given:** sou criador de tarefa independente.
- **When:** consulto elegíveis.
- **Then:** só o meu utilizador é elegível.

### US-18 — Gerir responsáveis de tarefas

Como utilizador autenticado, quero associar vários responsáveis elegíveis e remover atribuições, para repartir o trabalho entre colaboradores.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F15; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-18-01**

- **Given:** tenho acesso a tarefa não arquivada e dois utilizadores elegíveis.
- **When:** associo ambos.
- **Then:** as duas atribuições ficam disponíveis após reabrir a tarefa e reiniciar o backend.

**CA-US-18-02**

- **Given:** já existe atribuição para um par tarefa/utilizador.
- **When:** tento repetir a atribuição.
- **Then:** não é criado registo duplicado.

**CA-US-18-03**

- **Given:** um utilizador não existe ou não é elegível.
- **When:** tento atribuí-lo.
- **Then:** o backend rejeita a atribuição e não concede acesso por esse pedido.

**CA-US-18-04**

- **Given:** existe atribuição e tenho acesso à tarefa não arquivada.
- **When:** removo a atribuição.
- **Then:** apenas essa associação é removida, sem eliminar conta ou tarefa.

### US-19 — Adicionar e consultar comentários

Como utilizador autenticado, quero comentar e consultar comentários de tarefas autorizadas, para partilhar informação sobre o trabalho.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F16; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-19-01**

- **Given:** tenho acesso a tarefa não arquivada.
- **When:** envio conteúdo não vazio.
- **Then:** o comentário fica guardado com o meu utilizador e instante definidos no backend.

**CA-US-19-02**

- **Given:** tenho acesso à tarefa.
- **When:** reabro a tarefa após reiniciar o backend.
- **Then:** vejo conteúdo, autor e data dos comentários guardados.

**CA-US-19-03**

- **Given:** estou a comentar.
- **When:** envio conteúdo vazio ou só espaços.
- **Then:** o comentário é rejeitado e o formulário conserva os dados introduzidos.

**CA-US-19-04**

- **Given:** o projeto está arquivado ou não tenho acesso.
- **When:** tento adicionar comentário.
- **Then:** a operação é impedida.

### US-20 — Eliminar projeto e dependentes

Como utilizador autenticado, quero eliminar um projeto meu com confirmação explícita, para remover definitivamente os dados desse projeto.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F17; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-20-01**

- **Given:** sou proprietário de projeto com tarefas, comentários e atribuições.
- **When:** peço para eliminar.
- **Then:** a interface avisa que projeto e dependentes serão removidos e pede confirmação.

**CA-US-20-02**

- **Given:** está aberta a confirmação.
- **When:** cancelo.
- **Then:** não há pedido de eliminação nem alteração de dados.

**CA-US-20-03**

- **Given:** sou proprietário e confirmo eliminação.
- **When:** a operação é concluída.
- **Then:** projeto, tarefas, comentários, atribuições e colaborações do projeto são removidos sem apagar contas de utilizador ou outros projetos.

**CA-US-20-04**

- **Given:** a gravação da eliminação falha.
- **When:** a operação termina com erro.
- **Then:** nenhuma eliminação parcial fica confirmada e a interface não anuncia sucesso.

### US-21 — Pesquisar projetos acessíveis

Como utilizador autenticado, quero pesquisar projetos pelo nome, para encontrar os projetos em que trabalho.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F18; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-21-01**

- **Given:** existem projetos meus, partilhados comigo e sem acesso.
- **When:** pesquiso pelo nome.
- **Then:** só recebo correspondências entre os projetos acessíveis e conforme estado de arquivo da consulta.

**CA-US-21-02**

- **Given:** não há correspondência acessível.
- **When:** pesquiso.
- **Then:** vejo ausência de resultados sem dados de projetos alheios.

**CA-US-21-03**

- **Given:** a pesquisa de projetos está preenchida.
- **When:** limpo o campo.
- **Then:** deixa de restringir por nome e mantêm-se as regras de acesso.

**CA-US-21-04**

- **Given:** um projeto era partilhado comigo e o proprietário retirou a colaboração.
- **When:** volto a pesquisar pelo nome desse projeto.
- **Then:** esse projeto deixa de aparecer nos meus resultados.

### US-22 — Consultar painel inicial

Como utilizador autenticado, quero ver os meus projetos acessíveis e contagens por estado, para acompanhar o trabalho e navegar para o detalhe.

**Prioridade:** P1 — Âmbito integral da ficha 03.
**Origem:** F19; COL-01.
**Dependências:** autenticação e política de acesso; consultar implementation-plan.md.

**CA-US-22-01**

- **Given:** tenho sessão válida.
- **When:** abro o painel.
- **Then:** vejo o utilizador autenticado, projetos acessíveis e contagens identificadas por estado.

**CA-US-22-02**

- **Given:** há mais tarefas do que as linhas apresentadas.
- **When:** consulto contagens.
- **Then:** refletem o conjunto autorizado relevante e não apenas a página visível.

**CA-US-22-03**

- **Given:** crio, edito ou elimino tarefa com sucesso.
- **When:** regresso ao painel ou atualizo o conjunto apresentado.
- **Then:** listagens e contagens refletem dados confirmados sem exigir recarregar a página.

**CA-US-22-04**

- **Given:** outro utilizador tem dados sem acesso para mim.
- **When:** consulto projetos, contagens e navegação.
- **Then:** esses dados não contribuem para os resultados apresentados.

## Casos excecionais

| ID | Situação | Referências |
|---|---|---|
| CE-01 | Nome/título obrigatório ausente ou só espaços. | CA-US-01-02; CA-US-03-03 |
| CE-02 | Projeto sem tarefas: 0% e Sem tarefas. | CA-US-09-02 |
| CE-03 | Tarefa sem prazo: fora do calendário. | CA-US-08-02 |
| CE-04 | Reabertura e transferência recalculam progresso. | CA-US-05-02; CA-US-10-02 |
| CE-05 | Cancelar eliminação não altera dados. | CA-US-13-02; CA-US-20-02 |
| CE-06 | Arquivo impede alteração de tarefas/dependentes. | CA-US-12-05/06; CA-US-19-04 |
| CE-07 | Falha de gravação conserva dados confirmados. | CA-US-11-03/04/05; CA-US-20-04 |
| CE-08 | Pesquisa sem resultados e valores opcionais. | CA-US-14-03/08; CA-US-21-02 |
| CE-09 | Credenciais inválidas ou sessão expirada. | CA-US-15-02/04 |
| CE-10 | Alterar identificadores não permite acesso alheio. | CA-US-16-01/03 |
| CE-11 | Atribuição duplicada ou inelegível. | CA-US-18-02/03 |
| CE-12 | Comentário vazio ou apenas espaços. | CA-US-19-03 |
| CE-13 | Eliminar projeto não elimina contas/outros projetos. | CA-US-20-03 |

## Matriz de cobertura

| Funcionalidade | Histórias |
|---|---|
| F01 | US-01, US-02 |
| F02 | US-03, US-04 |
| F03 | US-05 |
| F04 | US-11 |
| F05 | US-06, US-07, US-08 |
| F06 | US-03, US-04 |
| F07 | US-09 |
| F08 | US-10 |
| F09 | US-12 |
| F10 | US-13 |
| F11 | US-14 |
| F12 | US-15 |
| F13 | US-16 |
| F14 | US-17 |
| F15 | US-18 |
| F16 | US-19 |
| F17 | US-20 |
| F18 | US-21 |
| F19 | US-22 |

## Estado dos cenários

São especificações, não resultados de testes. Spring Security e a política SEG-01 (60 minutos de inatividade, sem lembrar-me, reinício/logout exigem login) foram concretizados no Exercício 3. Âmbito exato das contagens continua por fechar antes do painel. PESQ-01 de projetos foi confirmada e implementada no Exercício 4. Resultados reais de acesso/leitura em verification.md; CRUD e escritas ainda pendentes. Mantêm-se limites de texto e detalhes de pesquisa por decidir. A base individual anterior foi preservada em history/mvp-individual-v1; não constitui especificação ativa.

## Atualização aprovada — Exercício 5 (TAR-01/PESQ-02)

Os estados técnicos ativos passam a TODO/IN_PROGRESS/DONE (rótulos Por fazer/Em curso/Concluída), com inicial TODO; prioridades LOW/MEDIUM/HIGH (Baixa/Média/Alta) ou null. Em novas tarefas prioridade omitida = MEDIUM; null explícito = Sem prioridade. As referências anteriores aos quatro estados/prioridades ficam superadas por esta decisão humana, sem alterar cardinalidades nem remover tarefas pessoais COL-04.

A V2 incremental converte Por Fazer→TODO, Em Curso/Em Revisão→IN_PROGRESS, Concluída→DONE, Baixa→LOW, Média→MEDIUM, Alta/Crítica→HIGH; conserva prioridades null preexistentes, IDs, criador, comentários e atribuições. PESQ-02 confirma pesquisa literal parcial de tarefas ignorando maiúsculas e acentos.

Lista/CRUD/filtros e transferências de tarefas implementados com autorização, CSRF e transações; COL-06 remove atribuições inelegíveis na mesma operação. Progresso calcula tarefas DONE/total. Escrita de comentários/responsáveis, gestão de colaboradores, painel, Kanban e calendário continuam pendentes. Ver [tarefas](tasks.md), OpenAPI 0.5.0 e verification.md para o estado observado.
