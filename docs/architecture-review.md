# Matriz — Revisão e rastreabilidade da base colaborativa

**Versão:** 2.0, 18/11/2026. Esta revisão substitui a visão ativa do MVP individual; relatório anterior preservado em history/mvp-individual-v1. Escopo autorizado; contrato e layout revistos são propostas técnicas para revisão, não implementação.

## Decisões e consistência

COL-01–06 em constraints.md autorizam colaboração integral, política proprietário/colaboradores, cascata, tarefas privadas, sessão/cookie e limpeza atómica de atribuições. ARQ-01 permanece monólito modular. PROJETO e TAREFA mantêm identidade própria; novas entidades suportam identidade, colaboração, responsáveis e comentários.

## Matriz de cobertura

| História | Responsabilidade/dados | Contrato proposto | Interface |
|---|---|---|---|
| US-01/02 | Projetos/PROJETO e proprietário | POST/GET/PATCH projetos | Novo projeto, detalhe/gestão; parte descrita. |
| US-03/04 | Tarefas/TAREFA e criador | POST/GET/PATCH tarefas | Formulário/painel. |
| US-05 | Tarefas/estado | PATCH tarefa | Campo de estado. |
| US-06/07/08 | Consultas/estado/prazo | GET tarefas com filtros | Lista desenhada; Kanban/calendário como destinos. |
| US-09 | Consulta derivada | Progresso em DTO Projeto | Indicador do projeto. |
| US-10 | Associação e acesso | PATCH tarefa, limpeza de atribuições | Campo projeto e aviso de perda de acesso. |
| US-11 | Persistência/sessão | Contratos não comprovam durabilidade | Estado de gravação; verificar em execução. |
| US-12 | Projetos/arquivo | PATCH projeto e GET com filtro | Navegação arquivo e ações do proprietário. |
| US-13 | Tarefas/dependentes | DELETE tarefa | Confirmação descrita. |
| US-14 | Consultas autorizadas | GET tarefas | Pesquisa/filtros. |
| US-15 | Identidade/sessão | Sessão, perfil, logout e CSRF | Login desenhado e terminar sessão. |
| US-16 | Política transversal | Autenticação e autorização em todos os recursos | Não confiar em ocultar controlos; testar backend. |
| US-17 | Projetos/COLABORACAO_PROJETO | Colaboradores e elegíveis | Gestão do proprietário e seleção. |
| US-18 | Tarefas/ATRIBUICAO_TAREFA | Responsáveis e elegíveis | Seleção/listagem/remoção. |
| US-19 | Tarefas/COMENTARIO | Comentários GET/POST | Lista/formulário com autor/data. |
| US-20 | Projetos e cascata | DELETE projeto | Confirmação explícita descrita. |
| US-21 | Consultas/nome | GET projetos com q | Pesquisa de projetos como destino descrito. |
| US-22 | Consultas e identidade | GET painel | Painel como destino descrito; âmbito das contagens proposto. |

## Pendências de implementação e revisão

- Stack e tecnologias de persistência/migrations escolhidas em TEC-01; tipos físicos, esquema/seed, versões, comandos e política temporal/configuração de sessão ainda por concretizar e verificar.
- Contrato contém convenções técnicas propostas (nomes de rotas/cookie, cabeçalho CSRF, DTOs/status e parâmetro de contagem), a rever antes de implementar.
- Limites de texto, detalhe da pesquisa/acentos, ordem inicial, navegação do calendário e composição das contagens permanecem explícitos.
- Wireframes só representam login e tarefa colaborativa; destinos adicionais estão descritos, não desenhados.
- Modelação e OpenAPI não comprovam hash seguro, autorização, transações ou persistência real; executar testes previstos em verification.md.
- Retirar colaboração/transferir deve remover atribuições inelegíveis atomicamente; autoria de comentários e contas preservadas.
- Arquivo e eliminação têm semânticas diferentes; eliminar projeto inclui dependentes, arquivar não apaga.

## Histórico da revisão individual

P01 relação não identificadora, P02 exemplo de validação e P03 default de arquivo foram corrigidos na base anterior. A versão colaborativa conserva cardinalidade opcional da tarefa e projeto inicialmente não arquivado; segurança sem login deixou de ser uma regra atual. Ver history para preservar evidência sem a confundir com a especificação ativa.

[Requisitos](requirements.md) · [User Stories](user-stories.md) · [Modelo](database.md) · [API](../openapi.yaml) · [UI](ui/wireframe-tarefas.md) · [Plano](implementation-plan.md) · [Verificação](verification.md) · [Reconciliação](scope-reconciliation.md)

## Atualização aprovada — Exercício 5 (TAR-01/PESQ-02)

Os estados técnicos ativos passam a TODO/IN_PROGRESS/DONE (rótulos Por fazer/Em curso/Concluída), com inicial TODO; prioridades LOW/MEDIUM/HIGH (Baixa/Média/Alta) ou null. Em novas tarefas prioridade omitida = MEDIUM; null explícito = Sem prioridade. As referências anteriores aos quatro estados/prioridades ficam superadas por esta decisão humana, sem alterar cardinalidades nem remover tarefas pessoais COL-04.

A V2 incremental converte Por Fazer→TODO, Em Curso/Em Revisão→IN_PROGRESS, Concluída→DONE, Baixa→LOW, Média→MEDIUM, Alta/Crítica→HIGH; conserva prioridades null preexistentes, IDs, criador, comentários e atribuições. PESQ-02 confirma pesquisa literal parcial de tarefas ignorando maiúsculas e acentos.

Lista/CRUD/filtros e transferências de tarefas implementados com autorização, CSRF e transações; COL-06 remove atribuições inelegíveis na mesma operação. Progresso calcula tarefas DONE/total. Escrita de comentários/responsáveis, gestão de colaboradores, painel, Kanban e calendário continuam pendentes. Ver [tarefas](tasks.md), OpenAPI 0.5.0 e verification.md para o estado observado.


## Atualização — Exercício 6

Responsáveis/elegíveis e comentários integrados na API e no detalhe da tarefa, mantendo o modelo de seis entidades e V1/V2. COM-01: novos comentários de 1–2000 pontos de código Unicode, não só espaços; autor/data do backend; ordem criadoEm/id ASC. Responsáveis múltiplos com par único, remoção individual e elegibilidade revalidada no backend. Arquivo bloqueia escritas; pessoais privadas. Contratos/limites em [task-collaboration.md](task-collaboration.md). As menções anteriores a escrita de comentários/responsáveis pendente ficam superadas por este incremento; gestão de colaboradores, painel e Kanban/calendário continuam pendentes. Ver verification.md para evidências, sem confundir plano com teste executado.


## Atualização — Exercício 7

Colaboradores (US-17), painel (US-22), Kanban (US-07) e calendário (US-08) integrados na interface/API. PAINEL-01 inclui pessoais + projetos ativos acessíveis na UI, exclui arquivo, não depende dos filtros da lista. COL-07 permite ao proprietário gerir colaboradores mesmo arquivado; remoção limpa atribuições atomicamente e conserva autoria/contas. Tarefas arquivadas continuam protegidas contra escrita. Modelo/stack/V1/V2 preservados; OpenAPI 0.7.0 com 24 operações implementadas. As referências anteriores a estas funcionalidades pendentes ficam superadas por este incremento. Ver [integração](frontend-integration.md) e verification.md para resultados/limites; entrega final/CI não declaradas concluídas.
