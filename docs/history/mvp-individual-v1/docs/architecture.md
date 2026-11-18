# Matriz — Arquitetura inicial do MVP

**Data:** 18/11/2026. **Decisão ARQ-01:** monólito modular.
**Estado:** aprovada pelo utilizador em 18/11/2026, mediante resposta “sim aprovo” à proposta ARQ-01. A aprovação abrange o monólito modular e as responsabilidades descritas; não constitui implementação nem seleção de tecnologias.

## Contexto e critérios

A Matriz é uma aplicação de organização individual de projetos e tarefas, sem login ou colaboração no MVP. O âmbito aprovado inclui tarefas independentes ou associadas, lista/Kanban/calendário, pesquisa e filtros, progresso, arquivo/restauro e conservação dos dados entre utilizações.

A entrega final é em 19/11/2026 às 17h, hora de Lisboa, incluindo execução reproduzível, verificações, documentação e demonstração. Não há necessidade confirmada de escalar componentes independentemente ou distribuir o trabalho por equipas autónomas. Equipa reduzida e baixo custo operacional são premissas de desenho fornecidas para esta análise, não medições da utilização futura.

Critérios: complexidade de implementação, velocidade de desenvolvimento, manutenção, escalabilidade futura e custos operacionais. A comparação é qualitativa e relativa a este MVP; não constitui orçamento ou garantia de desempenho.

## Comparação das alternativas

Nesta comparação, monólito significa uma aplicação única organizada principalmente por camadas técnicas; monólito modular mantém a unidade de entrega, acrescentando fronteiras explícitas por responsabilidade de negócio. Um monólito pode ser bem organizado; o acoplamento não é uma consequência obrigatória do estilo.

| Critério | Monólito | Monólito modular | Microservices |
|---|---|---|---|
| Implementação | Baixa complexidade inicial; comunicação interna direta. | Baixa a moderada: exige definir poucos módulos e controlar dependências. | Elevada: comunicação em rede, vários serviços e coordenação de falhas e dados. |
| Velocidade | Arranque rápido; alterações podem atravessar várias camadas à medida que cresce. | Arranque rápido com pequeno esforço de organização; alterações ficam mais localizadas. | Menor adequação ao prazo: configuração, integração e entrega de vários serviços. |
| Manutenção | Simples no início; exige disciplina para evitar dependências dispersas. | Responsabilidades e interfaces explícitas facilitam localizar regras e testar alterações. | Mais componentes e contratos a manter; complexidade operacional desproporcionada neste contexto. |
| Escalabilidade futura | Escala-se a aplicação como um todo; separação posterior depende das fronteiras existentes. | Também escala como um todo; módulos claros facilitam reorganização ou eventual extração justificada. | Permite escalar serviços individualmente, mediante desenho e operação adicionais. |
| Custos operacionais | Tendencialmente baixos: uma unidade de aplicação para operar. | Tendencialmente baixos: uma unidade de aplicação, sem serviços distribuídos por módulo. | Tendencialmente superiores em esforço e infraestrutura para este MVP; sem valores monetários estimados. |

## Decisão arquitetural

**Foi selecionado e aprovado um monólito modular para o MVP da Matriz.**

A lógica da aplicação será organizada em poucos módulos dentro da mesma aplicação e entregue como uma unidade. Os módulos comunicam por chamadas internas com contratos explícitos, não por serviços de rede independentes. A persistência é comum à aplicação, respeitando a responsabilidade de cada módulo sobre os seus dados.

A interface é um componente da solução e não constitui um microservice. O eventual uso de uma API HTTP entre interface e aplicação não transforma os módulos internos em serviços distribuídos.

### Justificação

- **Complexidade e velocidade:** conserva uma unidade de entrega e evita descoberta de serviços, brokers e coordenação distribuída, mantendo o trabalho compatível com uma equipa reduzida e prazo curto.
- **Manutenção:** separa projetos e tarefas, com regras identificáveis para arquivo, estados e associação. Lista, Kanban e calendário reutilizam a mesma lógica.
- **Evolução:** permite acrescentar requisitos aprovados dentro de fronteiras claras. Uma futura extração de serviços exigirá necessidade demonstrada e nova decisão; não é automática nem necessária para o MVP.
- **Operação:** reduz o número de componentes que precisam de arranque, configuração e diagnóstico. O custo concreto dependerá da stack e da distribuição ainda por escolher.

Não se seleciona o monólito organizado apenas por camadas porque há regras transversais suficientes para beneficiar de fronteiras explícitas de negócio. Não se selecionam microservices porque não há requisito aprovado que compense os seus custos adicionais no prazo disponível.

## Componentes e responsabilidades

| Componente | Responsabilidades | Limites |
|---|---|---|
| Interface de utilização | Formulários de projetos/tarefas; lista, Kanban e calendário; pesquisa/filtros; confirmação de eliminação e apresentação de erros. | Não mantém três cópias independentes das tarefas nem é a única responsável pelas regras de negócio. |
| Entrada e coordenação dos casos de uso | Receber pedidos, validar estrutura das entradas, coordenar chamadas aos módulos e devolver resultados/erros consistentes. Pode ser exposta por API HTTP, a detalhar no contrato OpenAPI. | Não concentra regras de negócio em controladores ou manipuladores de interface. Não escolhe ainda framework ou endpoints. |
| Módulo Projetos | Criar, consultar e editar projetos; gerir arquivo/restauro; validar nomes; fornecer consulta do estado de arquivo. Responsável pelos dados dos projetos. | Não altera diretamente dados internos de tarefas. Não inclui estados próprios de projeto, eliminação de projetos ou equipas. |
| Módulo Tarefas | Criar, consultar, editar e eliminar tarefas; estados e reabertura; prioridades/prazos; associações opcionais; pesquisa e filtros. Responsável pelos dados das tarefas. | Verifica projeto de origem e destino quando aplicável, por contrato de Projetos. Impede alterações a tarefas de projetos arquivados. |
| Consultas e cálculo de progresso | Combinar consultas dos módulos e calcular concluídas/total, arredondamento e caso sem tarefas; fornecer dados coerentes às visualizações. | É um componente interno de consulta, não um novo serviço ou módulo com cópia própria dos dados. Não guarda percentagem como segunda fonte de verdade. |
| Persistência e tratamento de gravação | Implementar acesso aos dados, integridade e operações atómicas quando necessário; confirmar sucesso só depois de gravar; conservar dados confirmados perante falhas. | Tecnologia e modelo físico pendentes. Os módulos usam interfaces de acesso, sem manipular diretamente a persistência de outros módulos. |
| Configuração e arranque | Preparar execução reproduzível, dependências e configuração documentadas para o mesmo ambiente e outro computador. | Sem autenticação ou publicação pública implícitas; não acrescenta sincronização. |

Projetos e Tarefas são os dois módulos de negócio propostos. Os restantes componentes são responsabilidades internas ou transversais; não exigem projetos separados, frameworks próprios ou uma biblioteca por camada.

## Fronteiras, dados e consistência

- A tarefa tem associação opcional a projeto. Não se introduzem entidades Utilizador, Equipa, Comentário ou Atribuição múltipla sem um requisito aprovado.
- Tarefas consulta o estado de arquivo através do contrato de Projetos antes de editar, concluir/reabrir, eliminar, transferir ou desassociar uma tarefa e antes de a associar a um projeto de destino.
- Projetos pode ser arquivado sem reescrever os estados das tarefas. As consultas principais excluem projetos arquivados e as suas tarefas; a consulta de arquivo permite localizar/restaurar projetos.
- O coordenador de consultas usa contratos internos de leitura de Projetos e Tarefas; Projetos não precisa de depender de Tarefas para calcular progresso. Evitar dependência circular entre módulos.
- O progresso é derivado das tarefas confirmadas de cada projeto. Alterar estado, eliminar ou transferir uma tarefa muda o resultado das consultas seguintes, sem contador duplicado para manter sincronizado.
- Lista, Kanban e calendário usam a mesma origem de dados e a mesma regra de interseção de pesquisa/filtros. O calendário acrescenta a condição de existir prazo no período consultado.
- Não declarar sucesso antes da confirmação da gravação. Em falha, apresentar erro e manter os últimos dados confirmados; operações que afetem várias alterações persistidas devem ser atómicas.
- O desenho da persistência deve impedir associações a projetos inexistentes e respeitar o arquivo. Os mecanismos concretos serão definidos no modelo de dados e implementação.

## Fluxos de referência

1. **Criar tarefa:** interface → entrada/caso de uso → Tarefas → consulta de Projetos se houver associação → validação → gravação confirmada → atualização das consultas.
2. **Arquivar projeto:** interface → caso de uso de Projetos → gravação do arquivo → consultas principais deixam de incluir projeto/tarefas. Os dados das tarefas conservam-se.
3. **Consultar progresso:** interface → consulta coordenada → leitura das tarefas do projeto → cálculo segundo DEC-05/PV-03 → resultado; zero tarefas produz 0% e Sem tarefas.

## Limitações, riscos e evolução

- Há uma unidade de entrega: uma alteração implica voltar a entregar a aplicação; não existe escalabilidade independente por módulo.
- A modularidade depende de disciplina e revisão: organizar pastas, por si só, não impede acoplamento.
- Regras de arquivo e associação precisam de testes para não serem contornadas por um ponto de entrada diferente.
- O MVP sem login destina-se a ambiente de confiança e não garante isolamento de dados. Não está aprovada exposição pública em rede.
- A seleção de tecnologias deve manter a estrutura pequena; não acrescentar mensageria, CQRS, serviços autónomos ou camadas repetitivas sem necessidade concreta.

## Decisões ainda pendentes

| Tema | Próxima atividade |
|---|---|
| Linguagem, framework e tecnologias de interface | Comparar uma stack pequena e compatível com execução reproduzível e prazo; submeter a escolha para validação. |
| Persistência, identificadores e modelo físico | Modelo conceptual e ER aprovados em [database.md](database.md); persistência, identificadores físicos e tecnologia continuam por selecionar. |
| Contrato de entrada HTTP | Exercício 3: [contrato simplificado](../openapi.yaml) com POST /projetos e POST /tarefas, validado automaticamente; proposta para revisão, sem implementação. |
| Protótipo | Exercício 4: [wireframe de tarefas](ui/wireframe-tarefas.md) produzido, inspecionado e aprovado pelo utilizador. |
| Arranque e distribuição | Definir e verificar comandos e pré-requisitos; atualizar README após implementação. |

## Verificação documental e fontes

A proposta mantém F01–F11, tarefas independentes, arquivo restritivo, três vistas, progresso e persistência. Não introduz funcionalidades de colaboração nem escolhe tecnologias. A verificação é documental: não foram compilados componentes ou executados testes de aplicação.

- [Requisitos aprovados](requirements.md)
- [User Stories e critérios](user-stories.md)
- [Restrições e pendências](constraints.md)
- [Instruções para agentes](../AGENTS.md)
- [Registo da ficha 2](ai-logs/ficha-02.md)
