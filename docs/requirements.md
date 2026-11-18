# Matriz — Requisitos do MVP colaborativo

**Versão:** 2.0, 18/11/2026. **Estado:** mudança para âmbito colaborativo autorizada pelo utilizador; detalhes técnicos pendentes explicitados em constraints.md. Base técnica implementada no Exercício 2; acesso e consultas protegidas implementados no Exercício 3, com limites em security.md. Gestão de projetos integrada no Exercício 4; Gestão de tarefas integrada no Exercício 5; Responsáveis e comentários integrados no Exercício 6; gestão de colaboradores e restante MVP ainda por concluir.

## Visão e objetivos

A Matriz permite organizar projetos partilhados, gerir tarefas e acompanhar o trabalho de utilizadores autenticados em contextos pessoais e profissionais. Mantém tarefas pessoais independentes, privadas de quem as cria.

Objetivos: organizar iniciativas e ações, colaborar com acesso controlado, atribuir responsáveis, comentar tarefas, consultar lista/Kanban/calendário, acompanhar progresso e conservar dados após recarregar ou reiniciar o backend.

## Utilizadores e acesso

Não há administrador global obrigatório. Proprietário e colaborador são relações de um utilizador com cada projeto, não perfis globais. Uma pessoa pode ser proprietária num projeto e colaboradora noutro.

- Qualquer utilizador autenticado pode criar projeto; o backend define o proprietário pela identidade autenticada.
- Proprietário gere dados, arquivo/restauro, eliminação e acesso de colaboradores ao seu projeto.
- Proprietário e colaboradores consultam o projeto e gerem as suas tarefas, incluindo criação, edição, mudança de estado, atribuições, comentários e eliminação de tarefas, respeitando arquivo.
- Ser responsável por uma tarefa não concede acesso a um projeto por si só: o utilizador tem de ser elegível antes da atribuição.
- Sem relação de acesso, o utilizador não pode consultar nem alterar o projeto ou os recursos dependentes, mesmo por pedido direto à API.
- Uma tarefa independente só é acessível ao criador autenticado. O criador é definido pelo backend e imutável.
- Atribuições de tarefa independente só podem apontar para o seu criador, por ser o único utilizador com acesso.
- Registo público e recuperação de palavra-passe não são exigidos; preparar contas fictícias por seed reproduzível, sem apagar dados em cada arranque.

## Funcionalidades e rastreabilidade

| ID | Requisito | Prioridade | User Stories |
|---|---|---|---|
| F01 | Criar, consultar e editar projetos autorizados. | P1 | US-01, US-02 |
| F02 | Criar, consultar e editar tarefas em projetos acessíveis ou tarefas pessoais independentes. | P1 | US-03, US-04 |
| F03 | Alterar estados e reabrir tarefas autorizadas. | P1 | US-05 |
| F04 | Conservar dados após recarregar a interface e reiniciar o backend. | P1 | US-11 |
| F05 | Consultar tarefas autorizadas em lista, Kanban e calendário. | P1 | US-06, US-07, US-08 |
| F06 | Prazo e prioridade opcionais nas tarefas. | P1 | US-03, US-04 |
| F07 | Percentagem de conclusão por projeto acessível. | P1 | US-09 |
| F08 | Associar, transferir e desassociar tarefas, verificando acesso de origem/destino. | P1 | US-10 |
| F09 | Arquivar e restaurar projetos pelo proprietário, conservando dependentes. | P2 | US-12 |
| F10 | Eliminar tarefas autorizadas com confirmação. | P1 | US-13 |
| F11 | Pesquisar tarefas pelo título e filtrar projeto, estado e prioridade. | P1 | US-14 |
| F12 | Iniciar sessão, consultar perfil autenticado e terminar sessão. | P1 | US-15 |
| F13 | Autorizar no backend todos os pedidos e impedir acessos cruzados indevidos. | P1 | US-16 |
| F14 | Proprietário gerir colaboradores e listar utilizadores elegíveis. | P1 | US-17 |
| F15 | Associar vários responsáveis elegíveis e remover atribuições sem duplicação. | P1 | US-18 |
| F16 | Adicionar/listar comentários, com autor e data definidos no backend. | P1 | US-19 |
| F17 | Proprietário eliminar projeto e dependentes mediante confirmação explícita. | P1 | US-20 |
| F18 | Pesquisar projetos acessíveis pelo nome. | P1 | US-21 |
| F19 | Painel com utilizador autenticado, projetos acessíveis e contagens por estado. | P1 | US-22 |

Prioridades indicam ordem de implementação, não exclusão de requisitos. Alterações de prioridade refletem as dependências do âmbito integral da ficha 03.

## Dados e regras de negócio

### Projetos

Nome obrigatório e não apenas espaços; descrição opcional. Criados com arquivado=false e proprietário inferido da autenticação. Podem existir sem tarefas. Não têm estados próprios. Só o proprietário altera dados/arquivo/colaboradores ou elimina o projeto.

Eliminação: confirmação clara de que serão removidos projeto, tarefas, comentários e atribuições; remover também as associações de colaboradores. A operação é atómica e não deixa dados órfãos; não elimina contas de utilizadores. Cancelar não executa a operação.

### Tarefas

Título obrigatório e não apenas espaços; descrição, projeto, prazo e prioridade opcionais. TAR-01: estado inicial TODO, salvo escolha válida. Estados técnicos: TODO, IN_PROGRESS, DONE; apresentação Por fazer, Em curso, Concluída. Transições livres, incluindo reabertura. Prioridades técnicas: LOW, MEDIUM, HIGH ou ausência; apresentação Baixa, Média, Alta. Em novas tarefas, prioridade omitida assume MEDIUM; null explícito significa Sem prioridade. Prioridades vazias preexistentes mantêm-se vazias. Prazos passados são permitidos.

O criador é inferido no backend. Sem projeto, apenas o criador pode consultar ou alterar a tarefa. Numa tarefa de projeto, o acesso decorre da participação atual no projeto, não apenas da autoria histórica.

Transferir exige autorização sobre tarefa e projeto de destino, que tem de estar não arquivado. Desassociar torna a tarefa privada do criador original; o utilizador que executa a ação pode perder o acesso se não for esse criador. Identificador, criador e dados mantêm-se. As atribuições que se tornem inelegíveis são removidas na mesma operação atómica, conforme COL-06.

### Responsáveis e comentários

Uma tarefa pode ter zero ou vários responsáveis. Cada par tarefa/utilizador é único. Elegíveis são utilizadores com acesso atual ao projeto (proprietário e colaboradores) ou apenas o criador numa tarefa independente. A API fornece a lista elegível, sem usar atribuições para conceder acesso.

Comentário: conteúdo obrigatório, não apenas espaços; autor e instante de criação definidos pelo backend, não aceites do cliente como identidade. Listagem apenas por utilizadores com acesso atual à tarefa. Sem edição/eliminação de comentários como funcionalidade autónoma; eliminação de tarefa/projeto remove os comentários dependentes.

### Arquivo

Arquivo conserva dados, estados, colaboradores, comentários e atribuições. Exclui projeto/tarefas das vistas principais. Consulta separada só de projetos arquivados acessíveis; restauro apenas pelo proprietário. Impede novas tarefas/associações e alterações às tarefas, incluindo edição, estado, eliminação, transferência/desassociação, comentários e atribuições. A consulta autorizada de dados existentes pode manter-se em modo de leitura. O restauro repõe visibilidade conforme filtros e prazo.

### Consultas, progresso e painel

PESQ-02: pesquisa de tarefas por parte literal do título sem distinguir maiúsculas/minúsculas nem acentos; filtros por projeto, estado e prioridade combinados por interseção nas três vistas. Opções Sem projeto e Sem prioridade; pesquisa vazia não restringe. Todas as consultas começam pelo conjunto autorizado, nunca pelos dados globais.

Pesquisa de projetos pelo nome sem devolver projetos sem acesso; PESQ-01 confirma correspondência por parte do nome sem distinguir maiúsculas ou acentos; texto literal, vazio não restringe. Sem paginação definida. O calendário usa prazo previsto; tarefas sem prazo permanecem na lista/Kanban. Arrastar cartões não é obrigatório.

Progresso = tarefas DONE/total das tarefas do projeto × 100, arredondado a uma casa decimal; sem tarefas, 0% e Sem tarefas. Tarefas independentes não entram no progresso de projetos. Alterações e eliminações atualizam resultados confirmados.

Painel mostra utilizador, projetos acessíveis e contagens por estado do conjunto autorizado relevante, não apenas da página visível. Filtros e contagens não podem revelar dados sem acesso. A inclusão de tarefas independentes/arquivadas nas contagens do painel deve ser explícita no contrato antes da implementação.

### Interface, falhas e sessão

Formulários com validação, carregamento, vazio, sucesso e erro. Preservar entradas quando uma submissão falha; evitar submissões repetidas durante pedido em curso. Atualizar listagens, filtros e contagens com resposta confirmada da API, sem recarregar a página nem duplicar registos. Remover mocks das funcionalidades concluídas.

Sem sucesso antes da gravação; em falha conservar dados confirmados e permitir nova tentativa após corrigir a causa. Sessão inválida encaminha para início de sessão e apresenta mensagem compreensível. Sessão no servidor e cookie aprovados em COL-05; Spring Security e SEG-01 concretizam sessão em memória com 60 minutos de inatividade, sem lembrar-me; reinício e logout exigem novo login. Ver security.md para cookie/CSRF.

## Qualidade, verificação e entrega

- Autorizar no backend todos os recursos, incluindo pedidos diretos com identificadores alterados.
- Se houver palavras-passe locais, guardar apenas hashes com biblioteca adequada; nunca devolver hashes nos DTOs.
- Preparar pelo menos dois utilizadores fictícios e projetos que permitam testar acessos distintos.
- Inicialização reproduzível sem apagar dados em cada arranque.
- Executar pelo menos três testes automatizados: validação, autorização e criação seguida de consulta à persistência; acrescentar regressão quando houver bug.
- Registar esperado/observado/evidências e comandos em verification.md, distinguindo testes de verificações manuais e não executadas.
- Configuração e arranque reproduzíveis noutro computador; segredos reais fora do repositório.
- Entrega 19/11/2026 às 17h, hora de Lisboa, incluindo código, testes, dependências, esquema/migrations, seed, configuração de exemplo e documentação coerente.

## Fora do âmbito

Organizações/equipas com gestão própria, convites, registo público, recuperação de palavra-passe, notificações, subtarefas, modelos, recorrência, estimativas e estados próprios de projeto. Colaboradores, autenticação, responsáveis, comentários e eliminação de projetos deixam de estar excluídos.

## Fontes e estado

[Reconciliação](scope-reconciliation.md), [User Stories](user-stories.md), [restrições](constraints.md), [arquitetura](architecture.md) e [registo da ficha 3](ai-logs/ficha-03.md). A autorização de alterar o âmbito não prova implementação, aprovação de detalhes pendentes ou cumprimento de testes.


## COM-01 — Comentários (Exercício 6)

Plano aprovado pelo utilizador: novos comentários têm conteúdo obrigatório de 1 a 2000 pontos de código Unicode, não apenas espaços. Autor e instante gerados pelo backend; só conteudo aceite no pedido. Sem truncar dados históricos nem limitar títulos/descrições. Ordem de consulta criadoEm/id ascendente; leitura autorizada de arquivadas, escrita bloqueada. Ver task-collaboration.md e OpenAPI 0.6.0.
