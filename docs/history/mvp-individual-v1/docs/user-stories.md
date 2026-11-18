# Matriz — User Stories e critérios de aceitação

**Estado:** aprovado pelo utilizador em 18/11/2026, na sequência da proposta do Exercício 3.
**Origem:** decisões DEC-01–DEC-18 e funcionalidades F01–F11 em `ai-logs/ficha-01.md`.

## Âmbito e prioridades

Utilização individual, sem login, com projetos e tarefas independentes ou associadas; lista, Kanban, calendário, progresso e conservação dos dados. Inclui edição, prazos e prioridades opcionais, associação de tarefas, arquivo/restauro, eliminação de tarefas, pesquisa e filtros.

P1 é o núcleo a implementar primeiro; P2 é complemento aprovado para a mesma entrega. Nenhuma prioridade autoriza retirar funcionalidades aprovadas.

Excluídos: colaboração, eliminação de projetos, estados próprios de projetos, objetivos/âmbito/datas/prioridades próprios dos projetos, subtarefas, comentários, notificações, modelos, recorrência e estimativas.

## Regras adicionais aprovadas com o backlog

As propostas PV-01–PV-08 da resposta do Exercício 3 foram incluídas na aprovação global do utilizador. Mantêm-se os identificadores para rastreabilidade, agora com estado aprovado.

| ID | Regra |
|---|---|
| PV-01 | Estado inicial Por Fazer quando não for escolhido outro estado. |
| PV-02 | Nome ou título composto apenas por espaços é tratado como vazio e impede a gravação. |
| PV-03 | Progresso apresentado com arredondamento a uma casa decimal. |
| PV-04 | Pesquisa por parte do título, sem distinguir maiúsculas/minúsculas; pesquisa vazia não restringe resultados. |
| PV-05 | Pesquisa e filtros combinados por interseção e aplicados a lista, Kanban e calendário. |
| PV-06 | São permitidos prazos passados, sem acrescentar notificações. |
| PV-07 | Consulta separada de projetos arquivados para restauro; durante o arquivo, impedir novas associações e alterações às suas tarefas. |
| PV-08 | Filtro por projeto inclui a opção Sem projeto. |

Os cenários referem operações guardadas com sucesso. Os limites de caracteres continuam por definir. O tratamento de falhas foi aprovado na revisão do Exercício 6: apresentar erro, não anunciar sucesso e conservar os últimos dados confirmados, permitindo nova tentativa após corrigir o problema.

## Regras transversais aprovadas na revisão do Exercício 6

- Critérios de alteração de tarefas aplicam-se a tarefas independentes ou de projetos não arquivados. Durante o arquivo, ficam impedidas edição, mudança de estado, eliminação, transferência e desassociação das tarefas, bem como novas associações ao projeto.
- As vistas principais excluem tarefas de projetos arquivados. Referências a lista/Kanban/calendário pressupõem filtros compatíveis; o calendário pressupõe também o período que contém o prazo.
- O restauro conserva os dados e repõe a elegibilidade para as vistas, respeitando os filtros e o período do calendário.
- Em falha de gravação, mostrar mensagem explícita, não anunciar sucesso e conservar os últimos dados confirmados. Permitir nova tentativa após corrigir o problema, sem exigir recuperação automática.
- O filtro de prioridade inclui Sem prioridade, combinável por interseção com a pesquisa e os restantes filtros.

## Backlog e critérios

### US-01 — Criar um projeto

Como utilizador individual, quero criar um projeto, para organizar tarefas relacionadas com uma iniciativa.

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

Como utilizador individual, quero consultar e editar os meus projetos, para manter a informação atualizada.

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

- **Given:** existe um projeto guardado.
- **When:** tento guardar nome vazio ou apenas com espaços.
- **Then:** a alteração é rejeitada e o nome anterior mantém-se.

### US-03 — Criar tarefas

Como utilizador individual, quero criar tarefas com ou sem projeto, para registar ações que preciso de realizar.

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
- **Then:** o estado inicial é Por Fazer, conforme PV-01.

### US-04 — Consultar e editar tarefas

Como utilizador individual, quero consultar e editar os dados das tarefas, para manter o planeamento atualizado.

**Prioridade:** P1 — Manutenção do trabalho.
**Origem:** F02, F06; DEC-08, DEC-10.
**Dependências:** US-03.

**CA-US-04-01**

- **Given:** existe uma tarefa.
- **When:** consulto e altero título ou descrição e guardo título válido.
- **Then:** as consultas seguintes apresentam os dados atualizados.

**CA-US-04-02**

- **Given:** existe uma tarefa.
- **When:** defino prazo e prioridade Baixa, Média, Alta ou Crítica e guardo.
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

Como utilizador individual, quero alterar o estado das tarefas, para acompanhar o trabalho e retomar tarefas concluídas.

**Prioridade:** P1 — Acompanhamento da execução.
**Origem:** F03; DEC-03.
**Dependências:** US-03.

**CA-US-05-01**

- **Given:** uma tarefa está num dos estados aprovados.
- **When:** escolho qualquer outro entre Por Fazer, Em Curso, Em Revisão e Concluída e guardo.
- **Then:** a lista mostra o novo estado e o Kanban a coluna correspondente; verificar todas as transições.

**CA-US-05-02**

- **Given:** um projeto tem duas tarefas concluídas e progresso 100%.
- **When:** altero uma para Em Curso.
- **Then:** a tarefa é reaberta e o progresso passa a 50%.

### US-06 — Consultar tarefas em lista

Como utilizador individual, quero consultar as tarefas em lista, para ter uma visão conjunta do trabalho.

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

Como utilizador individual, quero consultar tarefas agrupadas por estado num Kanban, para perceber a distribuição do trabalho.

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

Como utilizador individual, quero consultar tarefas pelos prazos num calendário, para planear o trabalho ao longo do tempo.

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

Como utilizador individual, quero consultar a percentagem de conclusão de cada projeto, para acompanhar o progresso do trabalho associado.

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

### US-10 — Reorganizar a associação das tarefas

Como utilizador individual, quero associar, transferir ou desassociar tarefas de projetos, para reorganizar o trabalho sem recriar tarefas.

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
- **Then:** fica independente, conserva os dados e deixa de contar no projeto anterior.

### US-11 — Retomar o trabalho sem perder dados

Como utilizador individual, quero encontrar os dados guardados quando reabro a aplicação, para continuar o trabalho anterior.

**Prioridade:** P1 — Cenário de sucesso e utilização continuada.
**Origem:** F04; DEC-02.
**Dependências:** Operações de gravação; CA-US-11-02 depende de US-12 e US-13.

**CA-US-11-01**

- **Given:** guardei projetos, tarefas, estados, prioridades, prazos e associações.
- **When:** fecho e reabro no mesmo ambiente.
- **Then:** os dados continuam disponíveis com os mesmos valores.

**CA-US-11-02**

- **Given:** arquivei um projeto e confirmei eliminar uma tarefa.
- **When:** fecho e reabro.
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

Como utilizador individual, quero arquivar e restaurar projetos, para retirar trabalho das vistas principais sem perder informação.

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

Como utilizador individual, quero eliminar tarefas que deixaram de ser necessárias, para manter o trabalho organizado.

**Prioridade:** P2 — Remoção de registos desnecessários.
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

Como utilizador individual, quero pesquisar tarefas pelo título e filtrá-las, para encontrar o trabalho relevante.

**Prioridade:** P2 — Localização de trabalho.
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

## Casos excecionais

| ID | Situação | Critério |
|---|---|---|
| CE-01 | Projeto sem nome ou só espaços: rejeitar. | CA-US-01-02 |
| CE-02 | Tarefa sem título ou só espaços: rejeitar. | CA-US-03-03 |
| CE-03 | Projeto sem tarefas: 0% e Sem tarefas. | CA-US-09-02 |
| CE-04 | Tarefa sem prazo: fora do calendário, presente em lista/Kanban. | CA-US-08-02 |
| CE-05 | Reabrir tarefa: recalcular progresso. | CA-US-05-02 |
| CE-06 | Transferir tarefa concluída: recalcular ambos os projetos. | CA-US-10-02 |
| CE-07 | Cancelar eliminação: conservar dados. | CA-US-13-02 |
| CE-08 | Arquivar/restaurar: conservar dados e repor visibilidade. | CA-US-12-01/02/03 |
| CE-09 | Pesquisa sem correspondência: zero resultados sem alterar dados. | CA-US-14-03 |
| CE-10 | Eliminar única tarefa não concluída: projeto sem tarefas. | CA-US-13-03 e CA-US-09-02 |
| CE-11 | Falha de criação, edição ou eliminação: erro e conservação dos dados confirmados. | CA-US-11-03/04/05 |
| CE-12 | Tentativa de alterar ou eliminar tarefa arquivada: impedir. | CA-US-12-06 |
| CE-13 | Tarefa sem prioridade: incluir apenas no filtro correspondente, com interseção. | CA-US-14-08 |

## Matriz de cobertura

| Funcionalidade | Histórias | Critérios |
|---|---|---|
| F01 | US-01, US-02 | CA-US-01-01/02/03; CA-US-02-01/02/03 |
| F02 | US-03, US-04 | CA-US-03-01/02/03/04/05; CA-US-04-01/04 |
| F03 | US-05 | CA-US-05-01/02 |
| F04 | US-11 | CA-US-11-01/02/03/04/05 |
| F05 | US-06, US-07, US-08 | CA-US-06-01/02; CA-US-07-01/02; CA-US-08-01/02/03 |
| F06 | US-03, US-04 | CA-US-03-04; CA-US-04-02/03/05 |
| F07 | US-09 | CA-US-09-01/02/03/04/05 |
| F08 | US-10 | CA-US-10-01/02/03 |
| F09 | US-12 | CA-US-12-01/02/03/04/05/06 |
| F10 | US-13 | CA-US-13-01/02/03 |
| F11 | US-14 | CA-US-14-01/02/03/04/05/06/07/08 |

## Cenário de sucesso e revisão

Criar projeto → criar tarefas associadas e independentes → consultar lista/Kanban/calendário → alterar estados → consultar progresso → fechar e reabrir sem perder dados.

Cobertura: US-01 → US-03 → US-06/07/08 → US-05 → US-09 → US-11.

Todas as funcionalidades aprovadas têm cobertura. Os efeitos entre histórias são intencionais: edição de prazo altera calendário; estados, transferência e eliminação alteram progresso. Não se exige arrastar cartões, ordem inicial da lista ou um formato específico do calendário.

## Entrega separada do backlog funcional

Prazo: 19/11/2026 às 17h, hora de Lisboa. Verificar execução noutro computador, configuração reproduzível, README, dependências, repositório GitHub acessível ao formador, submissão do URL, ausência de segredos e demonstração preparada de 1–2 fluxos em até cinco minutos. Preparar explicação de tecnologias, metodologia, utilização e validação da IA e um desafio com solução e aprendizagem. Estas obrigações não acrescentam funcionalidades ao produto.
