# Matriz — Reconciliação com a Ficha Prática 03

**Data:** 18/11/2026. **Decisão COL-01:** o utilizador autorizou alterar os artefactos para cumprir o âmbito colaborativo integral da ficha 03. Sem implementação nesta etapa.

## Decisões confirmadas pelo utilizador

- Colaboração, autenticação e autorização substituem o MVP exclusivamente individual e sem login.
- O proprietário gere o projeto e os colaboradores; proprietário e colaboradores podem gerir tarefas, atribuir responsáveis elegíveis e comentar.
- Eliminar projeto elimina também tarefas, comentários e atribuições, mediante confirmação explícita. As associações de colaboradores desse projeto deixam de existir.
- Tarefas independentes mantêm-se privadas do utilizador que as cria.
- Mantêm-se estados, prioridades, prazos opcionais, lista/Kanban/calendário, arquivo/restauro, progresso e conservação de dados anteriormente aprovados.

## Reconciliação de decisões anteriores

| Base anterior | Estado atual |
|---|---|
| Uso individual sem login; DEC-01 e DEC-11 | Substituído: utilizadores autenticados e autorização no backend. |
| Comentários e colaboração fora do MVP; parte de DEC-17 | Substituído: comentários, colaboradores e responsáveis múltiplos incluídos. |
| Projetos não elimináveis; DEC-14 | Substituído: eliminação autorizada pelo proprietário, em cascata e confirmada. |
| Tarefas independentes visíveis no ambiente individual | Mantidas com privacidade por criador. |
| Dois módulos de negócio sem identidade | Monólito modular mantido; responsabilidade de identidade/acesso acrescentada. |
| Duas entidades PROJETO/TAREFA | Acrescentar UTILIZADOR, COLABORACAO_PROJETO, ATRIBUICAO_TAREFA e COMENTARIO. |
| Duas operações REST da ficha 2 | Preservadas no histórico; contrato colaborativo precisa também de acesso e operações dos restantes fluxos. |
| Um wireframe sem utilizador | Rever cabeçalho e tarefa com responsáveis/comentários; acrescentar variantes de acesso. |

Os estados/prioridades e limites numéricos apresentados como exemplo na ficha 03 não substituem os valores aprovados do projeto. Limites de texto continuam por fechar. Kanban, calendário e arquivo não são removidos para reduzir o âmbito.

## Histórico

A versão anterior foi preservada em [mvp-individual-v1](history/mvp-individual-v1/LEITURA.md). Os registos antigos de ai-logs permanecem como evidência cronológica, não como fonte de regras atuais contraditórias.

## Entrega

Mantém-se 19/11/2026 às 17h, hora de Lisboa, com todos os entregáveis e verificações. A autorização de alargar o âmbito não altera o prazo nem constitui evidência de viabilidade de calendário.

## Pontos em definição

Sessão no servidor/cookie e remoção atómica de atribuições inelegíveis foram aprovadas em COL-05/COL-06. Stack e tecnologias de persistência foram aprovadas em TEC-01; versões, tipos físicos, detalhes temporais/configuração de sessão, cookie/CSRF e âmbito das contagens continuam explícitos em constraints.md.

## Atualização aprovada — Exercício 5 (TAR-01/PESQ-02)

Os estados técnicos ativos passam a TODO/IN_PROGRESS/DONE (rótulos Por fazer/Em curso/Concluída), com inicial TODO; prioridades LOW/MEDIUM/HIGH (Baixa/Média/Alta) ou null. Em novas tarefas prioridade omitida = MEDIUM; null explícito = Sem prioridade. As referências anteriores aos quatro estados/prioridades ficam superadas por esta decisão humana, sem alterar cardinalidades nem remover tarefas pessoais COL-04.

A V2 incremental converte Por Fazer→TODO, Em Curso/Em Revisão→IN_PROGRESS, Concluída→DONE, Baixa→LOW, Média→MEDIUM, Alta/Crítica→HIGH; conserva prioridades null preexistentes, IDs, criador, comentários e atribuições. PESQ-02 confirma pesquisa literal parcial de tarefas ignorando maiúsculas e acentos.

Lista/CRUD/filtros e transferências de tarefas implementados com autorização, CSRF e transações; COL-06 remove atribuições inelegíveis na mesma operação. Progresso calcula tarefas DONE/total. Escrita de comentários/responsáveis, gestão de colaboradores, painel, Kanban e calendário continuam pendentes. Ver [tarefas](tasks.md), OpenAPI 0.5.0 e verification.md para o estado observado.
