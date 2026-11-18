# Matriz — Restrições, pressupostos e decisões pendentes

**Data:** 18/11/2026. Consolidação da base aprovada; novas propostas são identificadas explicitamente.

## Restrições confirmadas

| ID | Restrição |
|---|---|
| RC-01 | Entrega final em 19/11/2026 às 17h, hora de Lisboa. Este prazo substitui a referência inicial genérica a 24 horas. |
| RC-02 | Prazo inclui implementação, verificações, documentação, configuração reproduzível, GitHub e preparação da apresentação. |
| RC-03 | Entregar aplicação funcional com o âmbito de [requirements.md](requirements.md); não apenas protótipo. |
| RC-04 | Uso individual sem registo ou login; colaboração e funcionalidades adiadas permanecem fora do MVP. |
| RC-05 | Lista, Kanban e calendário são obrigatórios. Prioridades de implementação não autorizam exclusões. |
| RC-06 | Funcionar noutro computador sem exigir sincronização dos mesmos dados. |
| RC-07 | A ficha 1 produz especificação; a ficha 2 produz arquitetura. As escolhas técnicas não foram aprovadas nesta fase. |

## Pressupostos e distinção de estados

- A utilização num computador de confiança é uma condição de utilização aprovada, não uma garantia técnica de proteção dos dados.
- A execução noutro computador não significa que os dados tenham de ser transferidos, sincronizados ou acedidos remotamente.
- As regras originalmente identificadas como PV-01–PV-08 foram aprovadas globalmente com o backlog; deixaram de ser pressupostos pendentes.
- As sugestões tecnológicas da Parte A não foram aprovadas e não determinam a stack.
- A mudança do enunciado colaborativo para MVP individual foi aprovada pelo utilizador. Não existe evidência de aceitação pelo formador.
- Não há novos pressupostos adotados por esta consolidação. Questões não resolvidas mantêm-se abaixo como pendências.

## Segurança e privacidade

### Confirmado

- Não incluir palavras-passe, tokens, chaves de API, ficheiros `.env` reais nem dados pessoais desnecessários no repositório.
- Usar configuração de exemplo sem valores sensíveis quando aplicável.
- Garantir que o formador tem acesso ao repositório; se privado, conceder as permissões necessárias.
- Sem autenticação, quem aceder à aplicação no mesmo ambiente poderá consultar os dados. Não prometer contas privadas ou isolamento entre utilizadores.
- A eliminação de tarefas requer confirmação e aviso de que é definitiva.

### Por definir

Proteção dos dados guardados, política de cópias de segurança e recuperação, ambiente de execução e eventual exposição em rede. A ausência de sincronização obrigatória não resolve estas escolhas. Não está aprovado alojamento público.

## Decisões técnicas pendentes

| ID | Questão | Fase e impacto |
|---|---|---|
| DT-01 | Linguagem e framework ainda pendentes; organização definida. | ARQ-01 aprovada: monólito modular com Projetos e Tarefas. Escolha de tecnologias ainda por validar. |
| DT-02 | Mecanismo de persistência. | Arquitetura; cumprir conservação dos dados sem impor tecnologia antecipadamente. |
| DT-03 | Forma de instalação e arranque noutro computador. | Arquitetura/entrega; permitir execução reproduzível. |
| DT-04 | Modelo conceptual aprovado; contrato REST simplificado documentado. | DADOS-01 em [database.md](database.md); [OpenAPI](../openapi.yaml) com duas operações e validação automática concluída. Contrato para revisão; modelo físico ainda pendente. Renderização ER concluída. |
| DT-05 | Ambiente de execução e distribuição. | Entrega; distinguir execução local de eventual alojamento. |

## Detalhes funcionais e de qualidade ainda pendentes

| ID | Ponto | Estado |
|---|---|---|
| PD-01 | Limites de caracteres dos campos. | Não definidos; não introduzir limites arbitrários como requisitos aprovados. |
| PD-02 | Tratamento de falhas de gravação. | Resolvido em REV-02: erro explícito, sem anúncio de sucesso, conservação dos dados confirmados e nova tentativa após corrigir a causa. Recuperação automática não é exigida. |
| PD-03 | Metas de desempenho, disponibilidade e acessibilidade. | Não foram aprovadas métricas nem ambiente-alvo detalhado. |
| PD-04 | Correspondência de acentos na pesquisa. | Só está aprovado ignorar maiúsculas/minúsculas. |
| PD-05 | Ordenação inicial, navegação do calendário e gestos de interface. | A definir no desenho; não são funcionalidades obrigatórias adicionais. |
| PD-06 | Aceitação pelo formador do âmbito individual. | Sem evidência de confirmação externa. |

Estas pendências não reabrem decisões aprovadas. Devem ser resolvidas na fase adequada e identificadas como propostas quando alterem o comportamento esperado.

## Entrega final e apresentação

Fonte: `17 - Apresentações Finais.pdf`, na pasta de materiais do curso.

- Submeter URL do repositório GitHub da versão final no espaço disponibilizado pelo formador.
- Incluir código-fonte, configuração e dependências, README com funcionalidades implementadas, tecnologias e instruções de instalação/execução.
- Documentar pré-requisitos e comandos e incluir `.env.example` sem segredos quando aplicável.
- Repositório acessível ao formador; ZIP e capturas não substituem o URL.
- Preparar aplicação aberta e dados de teste; evitar instalações durante a apresentação.
- Apresentação até cinco minutos com 1–2 fluxos principais, objetivo, funcionalidades, tecnologias, decisões, metodologia, utilização/validação da IA e desafio/solução/aprendizagem.
- Ensaiar a demonstração. Diapositivos são opcionais.

## Referências

- [Requisitos](requirements.md)
- [User Stories e critérios](user-stories.md)
- [Decisões e evidências](ai-logs/ficha-01.md)
