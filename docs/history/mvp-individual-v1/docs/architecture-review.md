# Matriz — Revisão do Architecture Pack

**Data:** 18/11/2026. **Método:** leitura cruzada, validação OpenAPI, referências locais e inspeção visual. Não é validação de uma aplicação implementada.

## Decisão humana e âmbito da aplicação

A revisão identificou 3 problemas confirmados, 5 riscos e 3 sugestões. Foi recomendado começar por P01, P02, P03 e pelo registo da matriz de cobertura. O utilizador respondeu **“ok aceito a sugestão”**. Foram autorizadas e aplicadas essas três correções e a documentação da matriz (S01). Não se considera que esta resposta escolhe tecnologias, distribuição ou soluções de recuperação/idempotência.

## Observações e estado

| ID | Classe | Evidência/impacto | Prioridade | Recomendação e estado |
|---|---|---|---|---|
| P01 | Problema confirmado | database.md representava relação identificadora apesar de identidade independente da tarefa. | Média | Aplicado: relação não identificadora o|..o{; cardinalidades/atributos mantidos; fonte e imagens regeneradas. |
| P02 | Problema confirmado | POST /projetos reutilizava exemplo 422 sobre titulo, que não pertence ao pedido. | Baixa | Aplicado: exemplo com nome para projetos; exemplo com titulo conservado em tarefas; schema de erro partilhado. |
| P03 | Omissão confirmada | Valor inicial arquivado=false aprovado, mas sem critério explícito em US-01. | Média | Aplicado: CA-US-01-03; rastreabilidade e contagens atualizadas. |
| R01 | Risco de cobertura | OpenAPI contém apenas duas criações, conforme âmbito do exercício. Não descreve o fluxo completo. | Alta antes da integração | Manter o contrato simplificado da ficha; planear os contratos restantes na implementação, sem introduzir novas funcionalidades. Contratos ainda não definidos. |
| R02 | Risco de execução | constraints.md DT-01/02/03/05 mantém stack, persistência e arranque pendentes. | Alta | Selecionar e validar separadamente; testar execução noutro ambiente. Pendente. |
| R03 | Risco de segurança | MVP sem login e exposição pública não aprovada. | Alta se houver exposição | Definir ambiente e limites de acesso antes de distribuir. Pendente. |
| R04 | Risco de consistência | Arquivo, associação e gravação têm de respeitar architecture.md e regras de integridade. | Alta | Definir atomicidade ou mecanismo equivalente e testar caminhos de alteração; solução técnica pendente. |
| R05 | Risco de duplicação | Timeout pode ocorrer depois de um POST gravado. | Média | Definir comportamento de resultado desconhecido; não assumir repetição automática segura. Pendente. |
| S01 | Melhoria | Documentos têm rastreabilidade local, mas faltava visão global por história. | Média | Aplicado: matriz abaixo, distinguindo dados, API e interface. |
| S02 | Melhoria | Modelo usa projeto_id e DTO usa projetoId. | Baixa | Explicitar mapeamento e identificadores opacos; sem mudar o modelo. Sugestão ainda não aplicada nesta correção. |
| S03 | Melhoria | Variantes de criação, confirmação, cancelamento e erro estão descritas, não desenhadas. | Média | Detalhar interações na fase adequada; sem exigir alta fidelidade. Pendente. |

A correção P01 alinha a notação Mermaid com a identidade da tarefa: -- significa relação identificadora e .. não identificadora. Referência técnica: https://mermaid.js.org/syntax/entityRelationshipDiagram.html#identification

## Matriz consolidada de cobertura

Suporte conceptual não significa endpoint implementado, e acesso de navegação não significa destino prototipado.

| User Story | Arquitetura e modelo | OpenAPI atual | Wireframe |
|---|---|---|---|
| US-01 Criar projeto | Projetos; PROJETO, nome e descricao; inicialmente não arquivado. | POST /projetos. | Acesso Novo projeto; formulário descrito. |
| US-02 Consultar/editar projetos | Módulo Projetos e atributos existentes. | Não documentado. | Destino de navegação. |
| US-03 Criar tarefas | Tarefas; TAREFA e projeto_id opcional. | POST /tarefas. | Nova tarefa e variante de formulário descrita. |
| US-04 Consultar/editar tarefas | Módulo Tarefas. | Não documentado. | Painel representado. |
| US-05 Estados/reabertura | TAREFA.estado e regras de transição. | Estado na criação; alteração não documentada. | Campo de estado representado. |
| US-06 Lista | Consultas dos dados confirmados. | Consulta não documentada. | Lista representada. |
| US-07 Kanban | Consultas agrupadas por estado. | Consulta não documentada. | Alternador e destino descritos. |
| US-08 Calendário | Consultas por prazo opcional. | Consulta não documentada. | Alternador e regras descritos. |
| US-09 Progresso | Consulta derivada das tarefas, sem atributo duplicado. | Não documentado. | Indicador representado. |
| US-10 Associação | projeto_id opcional e validação de arquivo. | Associação na criação; mudanças não documentadas. | Campo de projeto representado. |
| US-11 Persistência | Componente de persistência; tecnologia pendente. | Comportamento não comprovável pelo contrato. | Gravação/reabertura descritas, não demonstradas. |
| US-12 Arquivo/restauro | PROJETO.arquivado e regras de visibilidade/alteração. | Não documentado. | Acessos e destinos descritos. |
| US-13 Eliminar tarefas | Módulo Tarefas. | Não documentado. | Botão e confirmação descritos. |
| US-14 Pesquisa/filtros | Consultas por título, projeto, estado, prioridade. | Não documentado. | Controlos representados. |

## Ambiguidades ainda abertas

Limites de texto, pesquisa com/sem acentos, ordenação inicial, formato/navegação do calendário, ambiente de execução, persistência e distribuição. Estão registadas nos documentos de restrições e desenho; não foram resolvidas implicitamente nesta revisão. O valor inicial de arquivo e os identificadores JSON já estavam aprovados.

## Resultado e limites

O monólito modular, duas entidades, tarefas independentes, progresso derivado e regras de arquivo estão alinhados. Não foram identificados componentes sem justificação, dados sem uso ou endpoints fora do âmbito. Não é necessário incluir Utilizador/Equipa ou aumentar o modelo.

O pack cumpre a natureza simplificada da ficha: duas operações e um ecrã. Ainda não contém contratos para todo o MVP nem decisões de execução suficientes para implementar sem trabalho adicional. Não se declara a aplicação concluída nem pronta a apresentar.

## Referências

- [Requisitos](requirements.md)
- [User Stories](user-stories.md)
- [Restrições](constraints.md)
- [Arquitetura](architecture.md)
- [Modelo de dados](database.md)
- [OpenAPI](../openapi.yaml)
- [Wireframe](ui/wireframe-tarefas.md)
- [Registo da ficha 2](ai-logs/ficha-02.md)

## Verificação das correções aplicadas

OpenAPI 3.1 validado sem erros, com duas operações e 11 exemplos válidos. Confirmados 55 critérios únicos, 14 histórias e 13 casos excecionais; referências locais sem faltas. ERD regenerado em SVG/PNG e PNG inspecionado, sem alterar cardinalidades. git diff --check sem erros. Não foram executados testes da aplicação.
