# Ficha Prática 01 — Registo de interações com IA

- **Data do registo:** 29/10/2026
- **Ferramenta utilizada:** ChatGPT

## Exercício 1 — Prompt Engineering

### Parte A — Prompt simples

**Registo da interação:** [Conversa no ChatGPT](https://chatgpt.com/c/6ae3a002-0ee8-83eb-80fd-a8533be2d11a)

#### 1. Pedido enviado à IA

> Quero criar uma aplicação de gestão de projetos e tarefas chamada Matriz. Ajuda-me a desenvolver a aplicação.

#### 2. Resposta da IA

A transcrição seguinte reproduz o conteúdo textual fornecido pelo utilizador, com organização em Markdown para facilitar a leitura. As imagens e os elementos interativos da conversa não estão incorporados; mantêm-se os títulos e as opções presentes no texto recebido.

---

Vamos desenvolver a Matriz, uma aplicação web de gestão de projetos e tarefas, desde a definição das funcionalidades até à implementação do frontend, backend e base de dados.

A minha sugestão é construirmos uma aplicação real, com uma arquitetura bem organizada, que possas utilizar e também apresentar no teu portefólio de desenvolvimento de software.

**1. Conceito da aplicação Matriz**

Matriz  
Organiza. Prioriza. Concretiza.

Uma plataforma onde utilizadores podem criar projetos, organizar tarefas, definir prioridades, acompanhar prazos e visualizar o progresso do trabalho.

**2. Funcionalidades principais**

Para a primeira versão (MVP), proponho:

| Módulo | Funcionalidades |
|---|---|
| Autenticação | Registo, login e gestão de utilizadores |
| Dashboard | Resumo dos projetos, tarefas pendentes e prazos |
| Projetos | Criar, editar, arquivar e eliminar projetos |
| Tarefas | Criar, editar, atribuir e concluir tarefas |
| Prioridades | Baixa, média, alta e urgente |
| Estados | Por fazer, em progresso, em revisão e concluída |
| Kanban | Organizar tarefas em colunas |
| Calendário | Visualizar tarefas por data de vencimento |
| Equipas | Associar membros a projetos |

**3. Como poderá ser a interface**

Project Management Dashboard by Zalak Raja Opi on Dribbble  
Kanban Board White by Maria on Dribbble  
Browse thousands of Project Details Dashboard images for design inspiration | Dribbble

Estas são referências visuais para definirmos a identidade da Matriz. Podemos criar um design próprio, moderno e responsivo, com uma navegação lateral e um dashboard central.

**4. Tecnologias recomendadas**

Tendo em conta a tua experiência com Java, Spring Boot, React, TypeScript, testes automatizados e Docker no Switch-Dev, aproveitaria esse conhecimento para construir a aplicação, acrescentando algumas tecnologias novas.

React JS Logo | Js logo, React js, Creative project

Frontend — React + TypeScript

Interface com Vite, React Router e Mantine para os componentes visuais.

Why Spring Boot Throws UnsatisfiedDependencyException (And How Senior Devs Solve It) | by Gaddam.Naveen | Medium

Backend — Java + Spring Boot

API REST, lógica de negócio, autenticação e validações.

Homepage | DAO Systems Latino América

Base de dados — PostgreSQL

Persistência de utilizadores, projetos, tarefas e equipas.

Docker, logo, logos, Logos and Brands icon, png | PNGWing

Infraestrutura — Docker + GitHub Actions

Ambientes reproduzíveis, testes e integração contínua.

**5. Arquitetura inicial**

Frontend React + TypeScript  
API REST Spring Boot  
Application Services  
Domain Model  
Repositories  
PostgreSQL

Podemos aplicar princípios de DDD, SOLID e Clean Architecture, mas sem introduzir complexidade desnecessária nesta fase.

**6. Plano de desenvolvimento**

Fase 1 — Planeamento

Definir requisitos, casos de uso, entidades, relações e estrutura do projeto.

Fase 2 — Backend

Criar o projeto Spring Boot, o modelo de domínio, os repositórios e os endpoints REST.

Fase 3 — Frontend

Desenvolver o layout, o dashboard, as páginas de projetos e a gestão de tarefas.

Fase 4 — Integração e testes

Ligar frontend e backend, implementar autenticação e criar testes automatizados.

Fase 5 — Deploy

Publicar a aplicação online e preparar documentação técnica para o GitHub.

**7. Primeira decisão: que tipo de Matriz queremos construir?**

Antes de começarmos a escrever código, importa definir a dimensão do produto.

Qual será o objetivo principal da aplicação?

Gestão pessoal

Cada utilizador gere os seus próprios projetos e tarefas.

Gestão colaborativa (recomendado)

Projetos partilhados, membros, tarefas atribuídas e acompanhamento de equipas.

Gestão avançada

Funcionalidades semelhantes ao Jira, Trello ou Asana, com relatórios, automações e permissões.

Como preferes desenvolver?

Passo a passo, compreendendo cada decisão e implementação

Criar primeiro uma versão funcional e depois evoluí-la

A minha proposta é começarmos por uma aplicação colaborativa, mas com um MVP suficientemente pequeno para conseguirmos implementar, testar e publicar. Assim, a Matriz poderá crescer progressivamente até se tornar um projeto de portefólio bastante completo.

---

#### 3. Análise crítica da resposta

A análise incide sobre o pedido e a resposta transcritos acima. Neste pedido, os únicos elementos explicitamente indicados são o nome Matriz, a gestão de projetos e tarefas e a intenção de receber ajuda no desenvolvimento. O contexto do curso fornece informação adicional, mas não foi incluído no prompt simples. Não é possível determinar, apenas pela transcrição, se a ferramenta tinha acesso a memória ou a conversas anteriores.

##### 3.1. Que funcionalidades foram sugeridas?

A IA propôs nove módulos para o MVP:

| Área | Funcionalidades sugeridas | Aspetos ainda por definir |
|---|---|---|
| Autenticação e utilizadores | Registo, login e gestão de utilizadores. | Quem pode registar-se, quem gere utilizadores e quais as permissões. |
| Dashboard | Resumo dos projetos, tarefas pendentes e prazos. | Indicadores necessários, visibilidade dos dados e cálculo do progresso. |
| Projetos | Criação, edição, arquivo e eliminação. | Quem executa cada ação e o efeito do arquivo ou eliminação sobre as tarefas. |
| Tarefas | Criação, edição, atribuição e conclusão. | Campos obrigatórios, regras de atribuição e permissões de alteração. |
| Prioridades | Baixa, média, alta e urgente. | Necessidade destes quatro níveis e significado de cada um. |
| Estados | Por fazer, em progresso, em revisão e concluída. | Transições permitidas, responsáveis pelas mudanças e possibilidade de reabertura. |
| Kanban | Organização das tarefas em colunas. | Relação entre colunas e estados e necessidade de movimentação direta de tarefas. |
| Calendário | Visualização das tarefas por data de vencimento. | Obrigatoriedade dos prazos e tratamento de tarefas sem data. |
| Equipas | Associação de membros a projetos. | Como se constituem as equipas, quem adiciona membros e que acesso recebem. |

A resposta também apresenta relatórios, automações e permissões como possibilidades de uma opção de gestão avançada. Estas possibilidades não são descritas como parte do MVP proposto. Ainda assim, as regras básicas de acesso precisam de ser esclarecidas se a aplicação for colaborativa.

**Avaliação:** a proposta oferece ideias úteis, mas inclui um conjunto alargado de funcionalidades sem estabelecer prioridades, justificar cada inclusão ou demonstrar que todas são necessárias para a primeira versão. Não apresenta critérios de aceitação nem distingue claramente o essencial do que pode ser adiado. A lista é uma proposta para discussão, não um conjunto de requisitos aprovado.

##### 3.2. Que decisões foram assumidas pela IA sem confirmação?

É importante distinguir pressupostos usados como base da resposta de propostas explicitamente apresentadas como sugestões. A IA usa expressões como “proponho” e “podemos”, pelo que não seria rigoroso afirmar que todas as escolhas foram tratadas como decisões finais. Contudo, avança bastante na solução antes de esclarecer o problema.

| Pressuposto ou escolha antecipada | Evidência na resposta | Porque precisava de confirmação |
|---|---|---|
| A aplicação será web. | A abertura descreve uma “aplicação web”. | O pedido não especifica plataforma web, móvel ou desktop. |
| O trabalho abrangerá frontend, backend e base de dados. | A resposta anuncia a implementação dos três componentes. | Não foi confirmado o tipo de ajuda pretendida nem a fase do projeto. |
| O projeto terá uma finalidade de portefólio. | A resposta sugere uma aplicação para apresentar no portefólio. | O pedido não refere esse objetivo; ele pode influenciar o âmbito e as prioridades. |
| A experiência do utilizador inclui determinadas tecnologias e o Switch-Dev. | A recomendação tecnológica é justificada com essa experiência. | A informação não aparece no pedido transcrito. Pode resultar de contexto anterior, mas não fica comprovada nesta interação. |
| O MVP incluirá nove módulos. | A tabela é apresentada como proposta para a primeira versão. | Faltam necessidades validadas, prioridades, prazo e capacidade de desenvolvimento. |
| Existirão quatro prioridades e quatro estados específicos. | São definidos níveis e um fluxo com etapa de revisão. | Estas regras dependem da forma como os utilizadores trabalham. |
| Haverá arquivo e eliminação de projetos. | Ambas as operações constam do módulo de projetos. | As consequências sobre dados, histórico e tarefas não foram discutidas. |
| A interface poderá ter navegação lateral e dashboard central. | A resposta recomenda esse desenho. | Os utilizadores, os fluxos prioritários e os dispositivos ainda não estão definidos. |
| A aplicação deverá ser publicada online e documentada no GitHub. | O plano inclui uma fase de deploy e documentação para o GitHub. | Não foram confirmados alojamento, custos, exposição pública ou requisitos de entrega. |
| A gestão colaborativa será o caminho preferido. | A opção é marcada como recomendada e retomada na conclusão. | A IA pergunta pelo objetivo, mas já orienta a proposta antes de receber a resposta. |

**Avaliação:** a principal fragilidade é a sequência do raciocínio: primeiro são apresentadas funcionalidades, tecnologias e arquitetura; só depois surgem perguntas sobre o objetivo e a dimensão do produto. As perguntas finais são úteis, mas deveriam preceder e orientar essas propostas.

##### 3.3. A IA propôs uma arquitetura ou tecnologias?

**Sim.** A resposta recomenda uma stack concreta:

| Componente | Tecnologias propostas | Finalidade indicada |
|---|---|---|
| Frontend | React, TypeScript, Vite, React Router e Mantine. | Interface, navegação e componentes visuais. |
| Backend | Java e Spring Boot. | API REST, lógica de negócio, autenticação e validações. |
| Persistência | PostgreSQL. | Armazenamento de utilizadores, projetos, tarefas e equipas. |
| Infraestrutura e integração contínua | Docker e GitHub Actions. | Ambientes reproduzíveis, testes e integração contínua. |

Propõe também uma organização com frontend, API REST, serviços de aplicação, modelo de domínio, repositórios e base de dados. Esta descrição sugere separação de responsabilidades e uma organização por camadas, mas não permite concluir que foi definido formalmente um monólito, um monólito modular ou uma arquitetura de microserviços.

A IA menciona DDD, SOLID e Clean Architecture como abordagens possíveis. A simples referência a estes conceitos não demonstra que a arquitetura os aplica, nem explica quais os elementos necessários para o MVP.

**Avaliação:** as recomendações podem vir a ser adequadas, mas a resposta não compara alternativas nem as fundamenta em requisitos confirmados, restrições de alojamento, orçamento, dimensão da equipa ou necessidades de manutenção. Para esta fase da ficha 1, a escolha tecnológica é prematura: o exercício pretende analisar e clarificar requisitos, deixando a arquitetura e as tecnologias para a etapa seguinte. O prompt simples, porém, não comunicava essa restrição à IA.

##### 3.4. Que informações deveriam ter sido clarificadas primeiro?

Antes de propor o MVP e a solução técnica, seria necessário esclarecer as questões seguintes.

**Objetivo e contexto de utilização**

- Qual é o problema concreto que a aplicação deve resolver?
- Quem a vai utilizar e em que contexto: pessoal, académico ou empresarial?
- O objetivo imediato é produzir uma especificação, um protótipo ou uma aplicação funcional?
- Como será avaliado o sucesso da primeira versão?

**Utilizadores, colaboração e acesso**

- A aplicação será pessoal ou colaborativa?
- Que tipos de utilizadores existem e que responsabilidades têm?
- Quem pode criar projetos, adicionar membros, atribuir tarefas e consultar informação?
- O registo será livre, por convite ou gerido por um administrador?
- Os projetos serão privados, partilhados com uma equipa ou acessíveis a todos os utilizadores?

**Âmbito e prioridades do MVP**

- Quais são as funcionalidades indispensáveis para resolver o problema inicial?
- Dashboard, Kanban e calendário são todos necessários na primeira versão?
- Que funcionalidades podem ser adiadas?
- Qual é a prioridade e a justificação de cada funcionalidade?

**Regras de negócio**

- Que dados são obrigatórios num projeto e numa tarefa?
- Uma tarefa pertence a um único projeto?
- Uma tarefa pode ter um responsável ou vários?
- Quais são os estados e as transições permitidas?
- São necessárias prioridades e datas de vencimento? Com que regras?
- O que acontece às tarefas quando um projeto é arquivado ou eliminado?
- É necessário manter histórico de alterações ou permitir recuperar dados eliminados?

**Restrições e requisitos não funcionais**

- Qual é o prazo, o orçamento e a dimensão da equipa de desenvolvimento?
- Existem tecnologias obrigatórias ou limitações do ambiente do curso?
- A aplicação deve funcionar em que dispositivos e navegadores?
- Que requisitos de acessibilidade, desempenho e disponibilidade são necessários?
- Que dados pessoais serão tratados e que regras de segurança e privacidade se aplicam?
- Existem requisitos de alojamento, backups ou integração com outros sistemas?

**Forma de trabalho e validação**

- Pretende-se orientação passo a passo ou execução de tarefas delimitadas?
- Que decisões precisam de validação humana antes de avançar?
- Quais os critérios objetivos de aceitação das funcionalidades?
- Como serão registados os pressupostos e as questões sem resposta?

**Avaliação:** as respostas a estas perguntas condicionam o MVP, o modelo de dados, as permissões e a arquitetura. Não é necessário resolver todos os detalhes de uma só vez, mas as questões com maior impacto no âmbito devem ser esclarecidas antes de consolidar a solução. Quando não houver resposta do cliente, os pressupostos devem ficar identificados e sujeitos a validação.

#### 4. Conclusão da Parte A

O prompt simples gerou uma resposta abrangente, organizada e útil para explorar possibilidades. A IA apresentou funcionalidades, referências visuais, tecnologias, uma arquitetura inicial e um plano de desenvolvimento. Também colocou perguntas sobre o tipo de produto e a forma de trabalhar, e não chegou a implementar código.

Contudo, a resposta passou rapidamente da intenção geral para uma solução concreta. Não clarificou primeiro as necessidades, não justificou a prioridade das funcionalidades e não definiu critérios de aceitação. Algumas informações, como a experiência tecnológica do utilizador, não podem ser verificadas a partir do pedido transcrito.

O resultado mostra que um pedido aberto deixa espaço para a IA preencher lacunas com propostas e pressupostos. Na Parte B, o prompt deverá explicitar o papel de analista de requisitos, fornecer o contexto do exercício, impedir a proposta de tecnologias e a implementação de código nesta fase e exigir a separação entre factos conhecidos, pressupostos e perguntas de clarificação.

**Estado:** Parte A documentada e analisada. As funcionalidades e tecnologias transcritas são propostas da resposta original; este registo não as aprova como requisitos ou decisões do projeto.
