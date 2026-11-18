# Matriz

Aplicação de organização pessoal de projetos e tarefas, desenvolvida no curso AI-Powered Software Engineering.

## Problema e objetivos

A Matriz pretende substituir uma gestão desorganizada do trabalho por um fluxo que permita registar tarefas, agrupá-las em projetos, definir prazos e prioridades e acompanhar a execução. Destina-se a utilização individual, em contextos pessoais e profissionais, com colaboração reservada a uma evolução futura.

## Estado atual

Especificação funcional e backlog aprovados pelo utilizador. Documentação do Exercício 4 consolidada em 18/11/2026. A aplicação ainda não está implementada; tecnologias, arquitetura, instalação e comandos de execução serão definidos nas fases seguintes.

## Funcionalidades previstas para o MVP

- Criar, consultar e editar projetos e tarefas.
- Manter tarefas independentes ou associadas a projetos e reorganizar associações.
- Estados Por Fazer, Em Curso, Em Revisão e Concluída, com reabertura.
- Descrição, prazo e prioridade opcionais nas tarefas.
- Lista, Kanban e calendário por prazo.
- Progresso por percentagem de tarefas concluídas do projeto.
- Arquivar/restaurar projetos e eliminar tarefas com confirmação.
- Pesquisa pelo título e filtros por projeto, estado e prioridade.
- Conservar os dados entre utilizações e permitir execução noutro computador, sem sincronização obrigatória.

O MVP não inclui login, equipas, eliminação de projetos, estados próprios dos projetos, subtarefas, comentários, notificações, modelos, recorrência ou estimativas. É destinado a um computador de confiança; não oferece isolamento de dados entre pessoas que acedam ao mesmo ambiente.

## Documentação

| Documento | Conteúdo |
|---|---|
| [Requisitos](docs/requirements.md) | Visão, utilizador, âmbito, funcionalidades e regras aprovadas. |
| [User Stories](docs/user-stories.md) | 14 histórias, 54 critérios de aceitação, 13 casos excecionais e rastreabilidade. |
| [Restrições](docs/constraints.md) | Prazo, segurança, limites, pressupostos e decisões pendentes. |
| [Registo da ficha 1](docs/ai-logs/ficha-01.md) | Interações com IA, análises e aprovações. |

As instruções persistentes para agentes estão em [AGENTS.md](AGENTS.md), criado no Exercício 5. O Architecture Pack será produzido na ficha 2.

## Cenário de sucesso

Criar um projeto, registar tarefas associadas e independentes, consultar as três visualizações, alterar estados, observar o progresso e reabrir a aplicação sem perder os dados guardados.

## Instalação e execução

Ainda não existem comandos de instalação ou execução da aplicação. Esta secção será atualizada após a definição técnica e implementação, com pré-requisitos, dependências, configuração e comandos verificados. Os critérios de aceitação documentados ainda não constituem resultados de testes executados.

## Entrega e demonstração

Entrega final: **19/11/2026 às 17h, hora de Lisboa**. Inclui aplicação funcional, verificações, configuração reproduzível, documentação e URL do repositório GitHub acessível ao formador.

Preparar apresentação até cinco minutos com 1–2 fluxos, dados de teste, tecnologias e decisões, metodologia, utilização e validação da IA e um desafio com solução e aprendizagem. Não incluir segredos nem dados pessoais desnecessários no repositório.
