# Matriz — Requisitos

**Data:** 18/11/2026. **Estado:** consolidação das decisões e do backlog aprovados pelo utilizador; implementação ainda não iniciada.

## Visão e problema

A Matriz apoia a organização pessoal de projetos e tarefas em contextos pessoais e profissionais. O problema identificado é a ausência de um método organizado para registar ações, agrupá-las em iniciativas e acompanhar a execução.

O MVP adapta o enunciado inicial destinado a equipas para utilização individual. A colaboração fica para uma evolução futura. Esta adaptação foi aprovada pelo utilizador; não existe confirmação de aprovação pelo formador.

## Objetivos

- Organizar iniciativas em projetos e ações concretas em tarefas.
- Permitir tarefas com ou sem projeto e reorganizar as associações.
- Consultar o trabalho em lista, Kanban e calendário.
- Acompanhar execução por estados e progresso calculado.
- Retomar o trabalho após reabrir a aplicação sem perder os dados guardados.

## Utilizador

Um único tipo: utilizador individual que gere os seus próprios dados num computador de confiança. Não existem contas, login, perfis de administrador ou equipas no MVP. A utilização por outra pessoa noutro computador não exige dados partilhados.

## Âmbito funcional do MVP

P1 e P2 indicam a sequência recomendada de implementação. Ambas integram a entrega aprovada.

| ID | Requisito | Prioridade | Justificação | User Stories |
|---|---|---|---|---|
| F01 | Criar, consultar e editar projetos. | P1 | Organizar iniciativas e manter a informação atualizada. | US-01, US-02 |
| F02 | Criar, consultar e editar tarefas associadas ou independentes. | P1 | Registar ações concretas. | US-03, US-04 |
| F03 | Alterar estados e reabrir tarefas concluídas. | P1 | Acompanhar execução. | US-05 |
| F04 | Conservar os dados guardados após fechar e reabrir no mesmo ambiente. | P1 | Permitir utilização continuada. | US-11 |
| F05 | Consultar tarefas em lista, Kanban e calendário. | P1 | Oferecer as três visualizações aprovadas. | US-06, US-07, US-08 |
| F06 | Definir e remover prazo e prioridade opcionais nas tarefas. | P1 | Apoiar planeamento e calendário. | US-03, US-04 |
| F07 | Mostrar a percentagem de conclusão por projeto. | P1 | Tornar o progresso observável. | US-09 |
| F08 | Associar, transferir e desassociar tarefas de projetos. | P1 | Reorganizar sem recriar tarefas. | US-10 |
| F09 | Arquivar e restaurar projetos, conservando tarefas. | P2 | Retirar trabalho das vistas principais sem o perder. | US-12 |
| F10 | Eliminar tarefas mediante confirmação. | P2 | Remover registos desnecessários. | US-13 |
| F11 | Pesquisar pelo título e filtrar por projeto, estado e prioridade. | P2 | Encontrar trabalho relevante. | US-14 |

## Dados e regras de negócio

### Projetos

- Nome obrigatório; um nome vazio ou composto apenas por espaços impede a gravação.
- Descrição opcional.
- Podem existir projetos sem tarefas.
- Não existem estados próprios de projeto no MVP; o progresso é calculado pelas tarefas.
- É possível arquivar e restaurar, mas não eliminar projetos. Novos projetos são criados não arquivados, conforme confirmação na preparação do contrato API-01.

### Tarefas

- Título obrigatório, não vazio nem composto apenas por espaços.
- Descrição, projeto, prazo e prioridade opcionais.
- Prioridades: Baixa, Média, Alta e Crítica; sem prioridade é permitido.
- Estados: Por Fazer, Em Curso, Em Revisão e Concluída.
- Estado inicial Por Fazer quando não for escolhido outro.
- Transições livres entre os quatro estados, incluindo reabertura.
- É permitida associação posterior a um projeto, mudança de projeto e passagem a independente, conservando os restantes dados.
- Eliminação definitiva apenas após confirmação e aviso. Cancelar conserva a tarefa.

### Visualizações e prazos

- Lista, Kanban e calendário representam os mesmos dados, refletindo alterações guardadas.
- O Kanban agrupa tarefas pelo estado; arrastar cartões não é requisito obrigatório.
- O calendário representa a tarefa na data de conclusão prevista, não na data em que foi efetivamente concluída.
- Tarefas sem prazo não têm entrada no calendário e continuam acessíveis na lista e no Kanban.
- Prazos passados são permitidos; não geram notificações no MVP.
- Não estão definidos ordem inicial da lista nem formato mensal/semanal/diário do calendário.

### Progresso

- Progresso = tarefas concluídas do projeto ÷ total de tarefas do projeto × 100.
- Apresentação arredondada a uma casa decimal.
- Projeto sem tarefas: 0% e indicação “Sem tarefas”.
- Apenas tarefas do próprio projeto entram no cálculo; tarefas independentes não entram.
- Conclusão, reabertura, eliminação e mudança de associação atualizam o cálculo dos projetos afetados.
- Não existe ponderação por esforço, prioridade ou progresso parcial.

### Arquivo e restauro

- Projeto arquivado e suas tarefas ficam fora das vistas principais, mantendo dados e associações.
- Existe consulta separada de projetos arquivados para os localizar e restaurar.
- Durante o arquivo são impedidas novas associações e edição, mudança de estado, eliminação, transferência e desassociação das tarefas desse projeto.
- O restauro repõe a visibilidade, respeitando filtros, estados e prazos; tarefas sem prazo continuam fora do calendário.

### Pesquisa e filtros

- Pesquisa por parte do título, sem distinguir maiúsculas de minúsculas.
- Pesquisa vazia não restringe resultados.
- Filtros por projeto, estado e prioridade; projeto inclui “Sem projeto” e prioridade inclui “Sem prioridade”.
- Pesquisa e filtros combinam-se por interseção e aplicam-se às três visualizações, respeitando as regras próprias de cada vista.
- Zero correspondências não altera os dados.

### Falhas de gravação

Em falha de criação, edição ou eliminação, apresentar mensagem explícita, não anunciar sucesso e conservar os últimos dados confirmados. Permitir nova tentativa após corrigir o problema; não é exigida recuperação automática. Regras aprovadas em REV-02 no Exercício 6.

## Requisitos não funcionais e de entrega confirmados

| ID | Requisito | Verificação prevista |
|---|---|---|
| RNF-01 | Conservar dados após reabertura no mesmo ambiente. | Executar CA-US-11-01 e CA-US-11-02. |
| RNF-02 | Executar noutro computador, sem sincronização obrigatória. | Seguir as instruções finais de instalação e arranque num segundo ambiente. |
| RNF-03 | Configuração reproduzível e documentada. | README final com pré-requisitos, dependências e comandos; configuração de exemplo quando aplicável. |
| RNF-04 | Não publicar segredos nem dados pessoais desnecessários. | Rever ficheiros e histórico a entregar antes da publicação final. |

Não foram aprovados valores mensuráveis de desempenho, disponibilidade ou requisitos específicos de acessibilidade. São pontos pendentes, não metas já cumpridas. O MVP sem login não fornece isolamento entre pessoas que acedam ao mesmo ambiente.

## Fora do âmbito

Colaboração e equipas; autenticação; eliminação de projetos; estados próprios dos projetos; objetivos, âmbito, datas e prioridades próprios de projeto; subtarefas; comentários; notificações; modelos; recorrência; estimativas. A sincronização entre computadores não é exigida.

## Cenário de sucesso aprovado

> Consigo criar um projeto e tarefas associadas e independentes, consultar as tarefas nas três visualizações, alterar os seus estados, observar o progresso correto do projeto e voltar a abrir a aplicação sem perder os dados.

A especificação verificável encontra-se em [user-stories.md](user-stories.md), com 14 histórias, 55 critérios e 13 casos excecionais. A aprovação da especificação não significa que a aplicação já passou nesses critérios.

## Fontes e rastreabilidade

- [Registo de interações e decisões](ai-logs/ficha-01.md): DEC-01–DEC-18 e aprovação dos exercícios 2 e 3.
- [Backlog aprovado](user-stories.md): PV-01–PV-08 aprovadas, US-01–US-14 e critérios CA-US.
- [Restrições e pendências](constraints.md).
