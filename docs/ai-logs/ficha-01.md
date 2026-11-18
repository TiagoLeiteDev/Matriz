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

### Parte B — Prompt estruturado

#### 1. Pedido enviado à IA

O pedido utilizado segue a estrutura Goal → Context → Constraints → Task → Validation.

```markdown
## 1. Goal (Objetivo)

Atua como um analista de requisitos de software. O teu objetivo é ajudar-me a definir e compreender os requisitos de uma aplicação de gestão de projetos e tarefas chamada **Matriz**, antes de iniciar o seu desenvolvimento.

## 2. Context (Contexto)

Pretendo desenvolver uma aplicação chamada Matriz, destinada à gestão de projetos e tarefas.

Nesta fase, ainda não estão definidos os requisitos funcionais e não funcionais, os utilizadores-alvo, as funcionalidades específicas ou o âmbito da aplicação.

Preciso de identificar estas informações para compreender melhor o problema e estabelecer uma base sólida para o desenvolvimento.

## 3. Constraints (Restrições)

- Não proponhas tecnologias, frameworks ou arquiteturas de software.
- Não escrevas nem implementes código.
- Não assumas funcionalidades ou requisitos que ainda não foram confirmados.
- Distingue claramente os factos conhecidos dos pressupostos ou hipóteses.
- Não tomes decisões sobre o funcionamento da aplicação sem a minha confirmação.
- Concentra-te exclusivamente na análise e levantamento de requisitos.

## 4. Task (Tarefa)

Com base na descrição apresentada:

1. Analisa o problema e identifica o objetivo principal da aplicação.
2. Identifica os factos conhecidos e separa-os dos pressupostos.
3. Identifica as informações em falta para definir os requisitos.
4. Formula perguntas de clarificação sobre os utilizadores, as necessidades, as funcionalidades pretendidas e as restrições do projeto.
5. Organiza as perguntas por ordem de prioridade, começando pelas mais importantes para compreender o problema.
6. Aguarda pelas minhas respostas antes de avançar para a definição dos requisitos.

## 5. Validation (Validação)

A tua resposta deverá:

- Apresentar um resumo do que já é conhecido sobre a aplicação.
- Separar claramente os factos confirmados dos pressupostos.
- Identificar as principais informações em falta.
- Apresentar perguntas claras e objetivas para esclarecer os requisitos.
- Não incluir propostas de tecnologias, arquiteturas ou código.
- Permitir validar o entendimento do problema antes de avançar para a próxima fase.
```

#### 2. Resposta da IA

A resposta seguinte foi fornecida pelo utilizador. A formatação da tabela foi reconstituída a partir dos rótulos e itens copiados. Os elementos de navegação do questionário são registados como texto, pois o componente interativo não está incorporado neste ficheiro.

---

**Análise e levantamento de requisitos — Matriz**

Fase 1 — Compreensão do problema

Nesta primeira fase, vou assumir o papel de **analista de requisitos de software**, com o objetivo de compreender o problema que a aplicação Matriz pretende resolver, identificar as necessidades dos seus futuros utilizadores e recolher informação para definir o âmbito do projeto.

Não serão tomadas decisões sobre funcionalidades, tecnologias ou implementação até que as necessidades sejam esclarecidas e validadas.

**1. Análise inicial do problema**

A **Matriz** é uma aplicação que se pretende desenvolver para a gestão de projetos e tarefas.

O seu objetivo geral é apoiar a gestão de projetos e das tarefas associadas. No entanto, ainda não é possível determinar exatamente que problemas pretende resolver, quais os processos que deverá suportar ou que benefícios específicos deverá proporcionar.

Por exemplo, ainda não sabemos se o principal problema está relacionado com a organização individual do trabalho, a coordenação de equipas, o acompanhamento de prazos ou outra necessidade.

Estas possibilidades são apenas hipóteses a explorar.

**2. Factos conhecidos e pressupostos**

| Factos confirmados | Pressupostos por validar |
|---|---|
| A aplicação terá o nome Matriz. | Poderá ser utilizada por equipas. |
| Destina-se à gestão de projetos e tarefas. | Poderá permitir atribuir tarefas a utilizadores. |
| Encontra-se na fase inicial de análise. | Poderá permitir acompanhar prazos e progresso. |
| Os requisitos ainda não foram definidos. | Poderá ter diferentes tipos de utilizadores. |
| Pretende-se esclarecer o âmbito antes do desenvolvimento. | Poderá ser utilizada em contexto profissional ou pessoal. |

**Importante:** Nenhum dos pressupostos apresentados constitui um requisito aprovado.

**3. Principais informações em falta**

Para compreender o problema, precisamos de esclarecer cinco áreas fundamentais.

**Problema e objetivo**

Que dificuldade concreta motiva a criação da Matriz e que resultados se pretende alcançar?

**Utilizadores-alvo**

Quem utilizará a aplicação, em que contexto e com que responsabilidades?

**Necessidades e funcionalidades**

Que atividades deverão ser suportadas e quais são indispensáveis?

**Requisitos não funcionais**

Que expectativas existem relativamente a segurança, facilidade de utilização, desempenho e disponibilidade?

**Âmbito e restrições**

Que limites existem em termos de tempo, recursos e dimensão da primeira versão?

**4. Perguntas de clarificação por prioridade**

As perguntas estão organizadas em três níveis. A prioridade mais elevada corresponde às informações necessárias para compreender o problema antes de discutir funcionalidades específicas.

Questionário de levantamento

Etapa 1 de 3

**Prioridade 1 — Problema e utilizadores**

Essencial para compreender a finalidade da aplicação.

1. Qual é o principal problema que pretendes resolver com a Matriz?

Por exemplo, dificuldades em organizar tarefas, acompanhar projetos ou coordenar trabalho. Estas são apenas possibilidades.

> organização de tarefas,

2. Quem serão os principais utilizadores da aplicação?

Utilizadores individuais, equipas, gestores de projeto, organizações ou outro público?

3. Em que contexto será utilizada?

Uso pessoal, profissional, académico ou uma combinação destes?

4. Como é que os futuros utilizadores gerem atualmente os seus projetos e tarefas?

Que dificuldades encontram nos métodos que já utilizam?

Anterior · Seguinte

As respostas são mantidas ao navegar entre etapas. Podes deixar perguntas por responder.

**Questionário de levantamento — Etapa 2 de 3**

**Prioridade 2 — Necessidades e funcionalidades**

Para identificar o comportamento esperado da aplicação.

5. Que atividades concretas deverão conseguir realizar os utilizadores?

Descreve as necessidades sem ser necessário definir já como serão implementadas.

6. O que entendes por projeto e por tarefa no contexto da Matriz?

Que relação esperas que exista entre estes dois conceitos?

7. A aplicação deverá suportar trabalho individual, colaborativo ou ambos?

Caso exista colaboração, que necessidades específicas deverão ser satisfeitas?

8. Quais seriam as capacidades indispensáveis numa primeira versão?

O que teria de existir para considerares a aplicação minimamente útil?

**Questionário de levantamento — Etapa 3 de 3**

**Prioridade 3 — Qualidade, âmbito e restrições**

Para identificar limites e critérios de sucesso.

9. Existem requisitos especiais de segurança, privacidade, desempenho ou acessibilidade?

Se ainda não estiverem definidos, podem permanecer em aberto.

10. Existem limitações de prazo, orçamento ou recursos disponíveis?

Importa perceber o que poderá condicionar o âmbito.

11. Pretendes disponibilizar a Matriz a outras pessoas ou utilizá-la apenas num contexto restrito?

Esta informação ajuda a compreender a dimensão esperada.

12. Como saberemos que a primeira versão da Matriz cumpre o seu objetivo?

Que resultados observáveis demonstrariam o sucesso da aplicação?

**5. Validação do entendimento atual**

Com a informação disponível, a descrição preliminar do projeto é:

> A Matriz será uma aplicação destinada à gestão de projetos e tarefas. O seu público-alvo, as necessidades concretas que deverá satisfazer, as funcionalidades, os requisitos de qualidade e o âmbito da primeira versão serão determinados através do levantamento e validação de requisitos.

**Estado da análise: por validar**

Esta descrição identifica a intenção geral do projeto, mas ainda não é suficiente para estabelecer requisitos funcionais ou não funcionais verificáveis.

**6. Próxima fase**

Após receber as tuas respostas, irei analisar a informação recolhida, identificar eventuais ambiguidades e apresentar um resumo estruturado para validação.

**Só depois da tua confirmação avançaremos para a definição dos requisitos funcionais, não funcionais e do âmbito da Matriz.**

Podemos começar apenas pelas quatro perguntas da **Prioridade 1**, se preferires um levantamento mais gradual.

---

**Nota sobre a evidência:** o texto copiado contém a expressão “organização de tarefas,” junto à primeira pergunta. Pela sua posição, parece ser uma entrada no questionário, mas a transcrição não permite confirmar a sua autoria nem se foi submetida. Mantém-se no registo e não é considerada, por si só, um requisito validado. As etapas 2 e 3 foram fornecidas posteriormente e integradas neste registo em 17/11/2026. Estão agora documentadas as doze perguntas das três etapas, mas ainda não as respostas ao questionário.

#### 3. Análise crítica da resposta da Parte B

##### 3.1. Que funcionalidades foram sugeridas?

A resposta não apresenta uma lista de funcionalidades para o MVP. Menciona utilização por equipas, atribuição de tarefas, acompanhamento de prazos e progresso e diferentes tipos de utilizadores, mas identifica estas possibilidades como hipóteses por validar. Esta distinção respeita a restrição de não assumir funcionalidades não confirmadas.

Os exemplos nas perguntas ajudam a compreender o que está a ser perguntado, embora possam orientar as respostas para as possibilidades apresentadas. Seria útil acrescentar que o utilizador pode indicar necessidades diferentes dos exemplos e pedir uma descrição livre do problema antes de sugerir opções.

##### 3.2. Que decisões foram assumidas sem confirmação?

Não foram identificadas decisões substantivas sobre o funcionamento do produto apresentadas como aprovadas. A IA reconhece que ainda faltam informações para definir o público-alvo, as funcionalidades e o âmbito. Os factos que apresenta correspondem ao contexto do prompt estruturado.

A IA escolheu organizar o levantamento em três níveis e apresentar um questionário gradual. Trata-se de uma decisão sobre a forma da interação, não sobre os requisitos do produto. A abordagem é compatível com a prioridade pedida: as doze perguntas progridem da compreensão do problema para as necessidades funcionais e, por fim, para a qualidade, os limites e os critérios de sucesso.

Os itens designados “pressupostos por validar” são, mais precisamente, hipóteses de necessidades: não foram efetivamente adotados para orientar uma solução. A própria resposta esclarece que não constituem requisitos aprovados.

##### 3.3. A IA propôs uma arquitetura ou tecnologias?

Não. A resposta não escolhe linguagens, frameworks, base de dados, plataforma de execução ou estilo arquitetural. Também não apresenta código. Cumpre, assim, as restrições técnicas indicadas no prompt.

##### 3.4. Que informações procura clarificar e o que fica em falta?

As quatro perguntas da primeira etapa procuram esclarecer o problema principal, os utilizadores-alvo, o contexto de utilização e os métodos atualmente usados. São perguntas adequadas para iniciar o levantamento, porque ajudam a perceber a necessidade antes de discutir a solução.

A segunda etapa pergunta pelas atividades concretas, pelo significado de projeto e tarefa, pela colaboração e pelas capacidades indispensáveis do MVP. A terceira aborda segurança, privacidade, desempenho, acessibilidade, prazo, orçamento, recursos, dimensão da disponibilização e resultados observáveis de sucesso. Existe, assim, cobertura explícita das necessidades funcionais e das restrições, sem antecipar soluções técnicas.

Para completar o levantamento, será necessário responder às doze perguntas e aprofundar as respostas, sobretudo quanto a regras de negócio, permissões e critérios mensuráveis de qualidade e sucesso. Por exemplo, perguntar se existem requisitos de desempenho ainda não define um limite verificável de tempo de resposta. A priorização funcional também precisa de ser concretizada após identificar as capacidades indispensáveis. Estes detalhes devem resultar da clarificação com o utilizador, sem converter hipóteses em requisitos automaticamente.

##### 3.5. Verificação dos critérios do prompt

| Critério solicitado | Evidência observada | Avaliação |
|---|---|---|
| Resumir o que é conhecido. | Identifica o nome, a finalidade geral e a fase inicial de análise. | Cumprido. |
| Distinguir factos de pressupostos. | Apresenta categorias separadas e assinala que as hipóteses não estão aprovadas. | Cumprido no conteúdo fornecido. |
| Identificar informação em falta. | Enumera cinco áreas: problema, utilizadores, funcionalidades, qualidade e restrições. | Cumprido. |
| Formular perguntas claras e prioritárias. | Apresenta doze perguntas em três prioridades: problema e utilizadores; necessidades e funcionalidades; qualidade, âmbito e restrições. | Cumprido. |
| Perguntar sobre funcionalidades e restrições. | As perguntas 5 a 8 exploram atividades, conceitos, colaboração e MVP; as perguntas 9 a 12 exploram qualidade, recursos, disponibilização e sucesso. | Cumprido ao nível do levantamento inicial; respostas e aprofundamento pendentes. |
| Evitar tecnologias, arquitetura e código. | Não apresenta propostas técnicas nem implementação. | Cumprido. |
| Aguardar respostas e validação. | Mantém a análise por validar e condiciona a definição de requisitos à confirmação. | Cumprido na resposta transcrita. |

#### 4. Comparação entre o prompt simples e o prompt estruturado

| Aspeto | Parte A — Prompt simples | Parte B — Prompt estruturado |
|---|---|---|
| Objetivo transmitido | Pedido geral de ajuda para desenvolver a aplicação. | Análise e levantamento de requisitos antes do desenvolvimento. |
| Papel da IA | Não é especificado; a resposta abrange produto, design e desenvolvimento. | Analista de requisitos explicitamente definido. |
| Funcionalidades | Propõe nove módulos para o MVP antes de esclarecer as necessidades. | Apresenta possibilidades como hipóteses, sem fixar o MVP. |
| Pressupostos | Assume uma aplicação web e usa informação sobre experiência tecnológica não comprovada no pedido. | Separa os factos do prompt das hipóteses por validar. |
| Tecnologias e arquitetura | Recomenda uma stack concreta e uma organização inicial da solução. | Respeita a instrução de não propor tecnologias ou arquitetura. |
| Perguntas de clarificação | Surgem depois das propostas de funcionalidades e solução. | Orientam a interação desde o início e têm prioridade explícita. |
| Controlo humano | Solicita escolhas finais após uma proposta extensa. | Aguarda respostas e confirmação antes de definir requisitos. |
| Adequação à fase do exercício | Útil para explorar ideias, mas antecipa decisões das fases seguintes. | Mais alinhada com a compreensão e delimitação do problema. |
| Limitações observadas | Âmbito alargado sem prioridades justificadas ou critérios de aceitação. | As três etapas estão documentadas; faltam respostas, aprofundamento das regras e validação. |

A comparação mostra uma mudança concreta no resultado: ao explicitar o papel, o contexto, as restrições, a tarefa e os critérios de validação, o segundo pedido conduziu a uma resposta centrada em esclarecer necessidades. O primeiro deixou maior liberdade para a IA propor uma solução completa.

Esta observação refere-se às duas interações registadas. Não demonstra que qualquer prompt estruturado produzirá sempre uma resposta correta, nem permite atribuir toda a diferença exclusivamente à estrutura, pois não foram controladas outras condições da conversa. Continua a ser necessária revisão humana.

#### 5. Conclusão do Exercício 1

O prompt estruturado foi mais adequado ao objetivo desta fase: compreender o problema antes de definir funcionalidades ou escolhas técnicas. A resposta distinguiu factos de hipóteses, identificou informação em falta e começou por perguntas sobre o problema e os utilizadores. Cumpriu as restrições de não propor tecnologias, arquitetura ou código.

As três etapas do questionário estão agora transcritas e cobrem os principais temas solicitados no prompt. A evidência continua a exigir cuidado: o fragmento “organização de tarefas,” não deve ser tratado automaticamente como uma resposta validada. O levantamento ainda precisa de respostas e de esclarecimentos para produzir requisitos verificáveis, incluindo regras de negócio e critérios mensuráveis de qualidade e sucesso.

As vantagens observadas no pedido estruturado foram maior controlo do âmbito, menor antecipação de decisões, explicitação das incertezas e um ponto de validação humana antes de avançar. A aprendizagem principal é que a qualidade do pedido ajuda a orientar a resposta, mas a análise crítica continua a ser responsabilidade do utilizador.

**Estado:** pedidos, respostas e análise comparativa do Exercício 1 registados. A revisão humana desta análise e o levantamento de requisitos do Exercício 2 constituem os passos seguintes; este registo não aprova funcionalidades, tecnologias ou regras de negócio.


## Exercício 2 — Análise de requisitos com IA

### Decisões validadas — 18/11/2026

**Origem:** confirmação explícita do utilizador nesta conversa: “vamos avançar com a decisão do MVP individual. Aceito a sugestão do cenário de sucesso.”

#### DEC-01 — Utilização individual no MVP

A primeira versão da Matriz será destinada à organização individual de projetos e tarefas, em contextos pessoais e profissionais. A colaboração entre equipas fica fora do âmbito desta versão.

Esta decisão adapta o âmbito colaborativo do enunciado original. É uma decisão confirmada pelo utilizador; não constitui evidência de aprovação pelo formador.

#### DEC-02 — Cenário de sucesso do MVP

O utilizador aprovou o seguinte cenário:

> Consigo criar um projeto e tarefas associadas e independentes, consultar as tarefas nas três visualizações, alterar os seus estados, observar o progresso correto do projeto e voltar a abrir a aplicação sem perder os dados.

As três visualizações são lista, Kanban e calendário, conforme as respostas anteriores do utilizador. O cenário confirma a necessidade de conservar os dados entre utilizações; não exige sincronização entre computadores nem determina uma tecnologia de armazenamento.

Este cenário orientará os critérios de aceitação. Ainda não especifica as regras detalhadas de cálculo do progresso, datas do calendário, transições de estados ou campos obrigatórios.

### Regras funcionais validadas — 18/11/2026

**Origem:** resposta explícita do utilizador “Sim aceito” à tabela das cinco regras propostas nesta conversa.

| ID | Regra aprovada | Comportamento esperado |
|---|---|---|
| DEC-03 | Estados das tarefas | Por Fazer, Em Curso, Em Revisão e Concluída. São permitidas transições entre quaisquer destes estados, incluindo reabrir uma tarefa concluída. |
| DEC-04 | Calendário e prazos | O prazo da tarefa é opcional. As tarefas com prazo são apresentadas no calendário na respetiva data de conclusão prevista. As tarefas sem prazo permanecem acessíveis na lista e no Kanban, sem entrada no calendário. |
| DEC-05 | Progresso do projeto | Percentagem = tarefas concluídas do projeto ÷ total de tarefas do projeto × 100. Um projeto sem tarefas apresenta 0% e a indicação “Sem tarefas”. Tarefas independentes não entram no cálculo de um projeto. |
| DEC-06 | Associação de tarefas | É permitido associar uma tarefa independente a um projeto, mudar uma tarefa de projeto e retirar a associação para a tornar independente. |
| DEC-07 | Campos obrigatórios | O nome do projeto e o título da tarefa são obrigatórios. Esta decisão não aprova automaticamente a inclusão de outros campos no MVP. |

Estas regras concretizam o cenário de sucesso aprovado em DEC-02 e servirão de referência para as User Stories e os critérios de aceitação. As decisões sobre estados dos projetos são distintas dos estados das tarefas definidos em DEC-03.

### Fecho do âmbito e da entrega — 18/11/2026

**Origem:** o utilizador aceitou todas as propostas da tabela de fecho do MVP e confirmou: “tem que ser entregue amanha as 17h, com tudo o que é necessário para a app funcionar na apresentação e pedido no doc das apresentações finais”.

#### Decisões adicionais aprovadas

| ID | Decisão aprovada |
|---|---|
| DEC-08 | Permitir criar, consultar e editar projetos e tarefas. |
| DEC-09 | Projetos têm nome obrigatório e descrição opcional. Objetivos, âmbito, datas e prioridades próprias do projeto ficam adiados. |
| DEC-10 | Tarefas têm título obrigatório, descrição opcional, projeto opcional, estado, prazo opcional e prioridade opcional: Baixa, Média, Alta ou Crítica. |
| DEC-11 | O MVP não terá registo nem login. Destina-se a utilização individual num computador de confiança; quem tiver acesso à aplicação nesse ambiente poderá consultar os dados. |
| DEC-12 | Permitir arquivar e restaurar projetos. O projeto e as suas tarefas são conservados e ficam fora das vistas principais enquanto o projeto estiver arquivado. |
| DEC-13 | Permitir eliminar tarefas mediante confirmação e aviso de que a operação é definitiva. |
| DEC-14 | Adiar a eliminação de projetos; usar o arquivo para os retirar das vistas principais. |
| DEC-15 | Incluir pesquisa pelo título da tarefa e filtros por projeto, estado e prioridade. |
| DEC-16 | Adiar os cinco estados próprios dos projetos; acompanhar o progresso através das tarefas e da percentagem de conclusão. |
| DEC-17 | Adiar equipas, subtarefas, comentários, notificações, modelos, recorrência e estimativas de esforço. |
| DEC-18 | Entregar a versão final em 19/11/2026 às 17h, hora de Lisboa, incluindo implementação, verificações, documentação, preparação da demonstração e entrega exigida pelo documento de apresentações finais. Este prazo absoluto substitui a referência genérica anterior a 24 horas. |

#### Funcionalidades incluídas, justificação e prioridade

Todas as funcionalidades abaixo estão aprovadas para o MVP. As prioridades indicam uma ordem de implementação recomendada pela análise; não autorizam retirar funcionalidades aprovadas sem nova decisão.

| ID | Funcionalidade | Justificação | Prioridade proposta |
|---|---|---|---|
| F01 | Criar, consultar e editar projetos. | Organizar iniciativas e manter a informação atualizada. | P1 |
| F02 | Criar, consultar e editar tarefas, associadas ou independentes. | Registar e organizar ações concretas. | P1 |
| F03 | Alterar estados e reabrir tarefas. | Acompanhar a execução do trabalho. | P1 |
| F04 | Conservar os dados depois de fechar e reabrir a aplicação. | Permitir utilização continuada e cumprir o cenário de sucesso. | P1 |
| F05 | Consultar tarefas em lista, Kanban e calendário. | Cumprir as três formas de visualização exigidas pelo utilizador. | P1 |
| F06 | Definir prazo opcional e prioridade opcional nas tarefas. | Apoiar o planeamento e a ordenação do trabalho; o prazo alimenta o calendário. | P1 |
| F07 | Acompanhar a percentagem de conclusão dos projetos. | Tornar visível o progresso com base nas tarefas concluídas. | P1 |
| F08 | Associar, transferir e desassociar tarefas de projetos. | Permitir reorganizar trabalho sem recriar tarefas. | P1 |
| F09 | Arquivar e restaurar projetos e conservar as suas tarefas. | Retirar projetos das vistas principais sem perder dados. | P2 |
| F10 | Eliminar tarefas com confirmação. | Remover registos desnecessários com uma decisão explícita. | P2 |
| F11 | Pesquisar tarefas pelo título e filtrar por projeto, estado e prioridade. | Encontrar trabalho relevante nas consultas. | P2 |

P1: núcleo a implementar primeiro. P2: complemento aprovado a implementar depois de estabilizar o núcleo. Não há funcionalidades P3 incluídas; as funcionalidades futuras estão identificadas em DEC-09, DEC-14, DEC-16 e DEC-17.

#### Utilizador e fluxo principal

O MVP tem um único tipo de utilizador: a pessoa que gere os seus próprios projetos e tarefas. Não existem perfis de equipa ou permissões distintas nesta versão.

Fluxo principal: criar um projeto, criar tarefas associadas ou independentes, definir os dados necessários, consultar lista/Kanban/calendário, atualizar estados e observar o progresso. Os dados permanecem disponíveis após reabrir a aplicação. Arquivo, restauro, pesquisa, filtros e eliminação de tarefas completam a gestão quotidiana.

#### Critérios de entrega e apresentação

Fonte: `17 - Apresentações Finais.pdf`, consultado durante a análise anterior nesta conversa.

- Aplicação funcional, aberta e pronta a demonstrar, com dados de teste adequados.
- Demonstração de 1 a 2 fluxos principais dentro de uma apresentação com duração máxima de 5 minutos.
- Explicação breve do objetivo, funcionalidades principais, tecnologias e decisões técnicas, metodologia, utilização de IA e validação dos resultados.
- Exemplo concreto de um desafio, da solução aplicada e da aprendizagem obtida.
- Repositório GitHub correspondente à versão final, com código-fonte, estrutura, configuração e dependências necessárias.
- README com descrição, funcionalidades implementadas, tecnologias, pré-requisitos e instruções de instalação e execução.
- Configuração reproduzível e, quando aplicável, `.env.example` sem valores sensíveis.
- Acesso garantido ao formador e submissão do URL no espaço indicado. ZIP e capturas não substituem o URL.
- Ausência de palavras-passe, tokens, chaves de API, ficheiros `.env` reais e dados pessoais desnecessários no repositório.
- Demonstração ensaiada; evitar instalações e configurações durante a apresentação. Diapositivos não são obrigatórios.

#### Estado e próximo passo

O âmbito funcional acima está validado pelo utilizador. Não existe ainda evidência de aprovação pelo formador da adaptação para utilização individual.

O próximo passo é o Exercício 3: elaborar User Stories e critérios de aceitação rastreáveis a F01–F11 e DEC-01–DEC-18, incluindo casos excecionais. Depois, consolidar os documentos do Specification Pack e avançar para o Architecture Pack antes da implementação.

Detalhes como estados iniciais, limites de texto, arredondamento da percentagem e comportamento da pesquisa devem ser explicitados na especificação como propostas quando não decorrerem das decisões aprovadas. Não são motivo para reabrir as decisões já confirmadas.

## Exercício 3 — User Stories e critérios de aceitação

**Validação em 18/11/2026:** o utilizador respondeu “aprovado” à proposta completa de backlog, critérios, casos excecionais e rastreabilidade. A aprovação abrange as propostas PV-01–PV-08 apresentadas nessa resposta, agora regras aprovadas.

O documento [user-stories.md](../user-stories.md) consolida as 14 histórias US-01–US-14, critérios Given/When/Then, 10 casos excecionais, matriz F01–F11 e cenário de sucesso. Os critérios anteriormente condicionados pelas PV foram explicitados segundo as regras aprovadas.

**Próximo passo:** Exercício 4 — consolidar requirements.md, constraints.md e README.md, reutilizando as decisões e o backlog aprovados.

## Exercício 4 — Context Engineering e documentação

**Data:** 18/11/2026. A pedido do utilizador, foram consolidados [requirements.md](../requirements.md), [constraints.md](../constraints.md) e [README.md](../../README.md), reutilizando o [backlog aprovado](../user-stories.md).

A documentação separa âmbito aprovado, restrições, regras adicionais PV já aprovadas e questões pendentes. Não seleciona tecnologias nem apresenta a aplicação como implementada. As escolhas de arquitetura e instruções de execução ficam para as fases seguintes.

**Verificação:** consistência das funcionalidades F01–F11, exclusões, estados, arquivo, calendário, progresso e prazo; referências locais verificadas. A configuração de execução e os testes da aplicação ainda não existem nesta fase.

**Próximo passo:** Exercício 5 — criar AGENTS.md com instruções persistentes para os agentes.

## Exercício 5 — Instruções persistentes para Coding Agents

**Data:** 18/11/2026. O utilizador aprovou o conteúdo proposto e solicitou a criação do ficheiro.

Foi criado [AGENTS.md](../../AGENTS.md) na raiz do projeto, com contexto, documentação de referência, processo de trabalho, controlo de âmbito, aprovação de ações destrutivas ou sensíveis, proteção de informação, validação e comunicação dos resultados. As instruções são independentes das tecnologias ainda por escolher. O README foi atualizado para apontar para o ficheiro criado.

**Verificação:** presença das secções aprovadas e existência dos documentos de referência. Não se aplica execução de testes da aplicação a esta alteração documental.

**Próximo passo:** Exercício 6 — revisão crítica e Human-in-the-Loop do Specification Pack.

## Exercício 6 — Revisão crítica e Human-in-the-Loop

**Data:** 18/11/2026. **Estado:** revisão realizada pela IA; recomendações pendentes de decisão humana.

### Âmbito e método

Foram lidos integralmente README.md, AGENTS.md, docs/requirements.md, docs/user-stories.md e docs/constraints.md. A revisão comparou âmbito, exclusões, regras, critérios de aceitação, pressupostos e segurança, sem executar a aplicação ou alterar os requisitos aprovados.

### Resultados positivos

- O âmbito individual sem login, as três visualizações, tarefas independentes, progresso, arquivo e exclusões são consistentes entre os documentos.
- Não foram identificadas funcionalidades adicionais sem justificação no âmbito atual. As tecnologias sugeridas no primeiro prompt continuam sem aprovação e não foram adotadas na especificação.
- PV-01–PV-08 têm aprovação global explícita do utilizador; não foram indevidamente mantidas como propostas pendentes.
- README distingue funcionalidades previstas de implementação existente. Nenhum documento afirma que os testes da aplicação passaram.
- As limitações de privacidade sem login e a ausência de autorização de alojamento público estão documentadas.
- AGENTS.md cobre os pontos do Exercício 5 e remete para a documentação relevante.
- Verificação estrutural: 14 histórias, 49 critérios com identificadores únicos e nenhuma referência Markdown local em falta nos cinco documentos.

### Recomendações para decisão humana

| ID | Tipo e problema | Documentos afetados | Correção proposta | Decisão humana |
|---|---|---|---|---|
| REV-01 | Inconsistência de precondições: critérios de edição, estados, associação e eliminação dizem apenas que existe uma tarefa; poderiam aplicar-se a tarefas arquivadas, contrariando PV-07. CA-US-08-02 também não exclui arquivo ao afirmar presença na lista/Kanban. Restauro menciona visibilidade sem explicitar filtros. | user-stories.md: US-04, US-05, US-06–08, US-10, US-12, US-13; requirements.md: arquivo e visualizações. | Explicitar regra transversal: critérios de alteração e vistas principais aplicam-se a tarefas independentes ou de projetos não arquivados; arquivo impede edição, mudança de estado, eliminação, transferência e desassociação das suas tarefas e novas associações ao projeto. No restauro, apresentação sujeita aos filtros e ao período do calendário. Acrescentar verificações negativas de tentativa de alterar/eliminar tarefa arquivada. | Pendente: aceitar, alterar ou rejeitar. |
| REV-02 | Lacuna conhecida de tratamento de falhas: os critérios só abrangem gravações bem-sucedidas e não especificam o resultado observável de uma falha. Não é contraditório, mas deixa um caso relevante sem teste. | constraints.md: PD-02; user-stories.md: US-11 e operações de gravação; requirements.md. | Em falha, apresentar mensagem explícita, não indicar sucesso e conservar os últimos dados confirmados. Permitir nova tentativa após corrigir o problema; não exigir mecanismo novo de recuperação automática. Acrescentar critérios para falha de criação, edição e eliminação. | Pendente: aceitar, alterar ou rejeitar. |
| REV-03 | Cobertura incompleta dos filtros: prioridade é opcional, mas só Sem projeto está definido como filtro de ausência. CA-US-14-05 não fornece dados concretos para verificar a interseção. | requirements.md: pesquisa e filtros; user-stories.md: US-14. | Incluir opção Sem prioridade e concretizar um cenário com tarefas de atributos diferentes em que apenas uma cumpre título + projeto + estado + prioridade. A opção Sem prioridade é uma proposta nova e depende de aprovação; a concretização do teste não altera a regra de interseção aprovada. | Pendente: aceitar, alterar ou rejeitar, podendo separar as duas propostas. |
| REV-04 | Dependência de implementação não registada: US-09 verifica eliminação mas não refere US-13; US-11 verifica arquivo/eliminação mas só indica operações de gravação. | user-stories.md: dependências de US-09 e US-11. | Acrescentar dependências por cenário: CA-US-09-04 depende de US-13; CA-US-11-02 depende de US-12 e US-13. Não mudar a prioridade das histórias. | Pendente: aceitar, alterar ou rejeitar. |

### Pendências já reconhecidas e encaminhamento

Não são contradições novas: limites de caracteres, métricas de qualidade, comportamento de acentos, desenho de interface, persistência e ambiente de execução continuam explicitamente pendentes. Devem ser resolvidos na arquitetura/prototipagem ou antes de implementar e testar o comportamento respetivo. Não se propõem valores arbitrários nesta revisão.

A adaptação do enunciado para uso individual continua aprovada pelo utilizador. A revisão não pede nova aprovação dessa decisão nem afirma aceitação pelo formador.

### Versionamento

A consulta apenas de leitura `git status --short` mostrou README.md e este registo modificados e AGENTS.md, requirements.md, constraints.md e user-stories.md ainda não rastreados. Logo, a versão atual do Specification Pack ainda não está integralmente registada num commit. Não foi executado git add, commit ou push nesta revisão.

### Conclusão e intervenção humana

A base está coerente quanto ao âmbito. A principal correção é tornar explícita a precedência do arquivo nos critérios genéricos; as restantes recomendações completam validação e rastreabilidade. Os documentos de requisitos não foram corrigidos automaticamente. O utilizador deverá indicar quais as recomendações REV-01–REV-04 que aceita, altera ou rejeita; a decisão e as alterações resultantes serão registadas aqui.

### Decisão humana e aplicação das correções — Exercício 6

**Data:** 18/11/2026. O utilizador respondeu “aceito” às quatro recomendações REV-01–REV-04. Todas foram aceites e aplicadas. O estado pendente na tabela anterior representa o momento da proposta e é substituído por este registo de decisão.

- REV-01: regra transversal de arquivo explícita, restauro sujeito a filtros/período e novo critério CA-US-12-06.
- REV-02: tratamento de falhas aprovado e documentado; novos critérios CA-US-11-03/04/05. PD-02 passou a resolvido.
- REV-03: Sem prioridade aprovado e incluído, CA-US-14-05 concretizado e CA-US-14-08 acrescentado.
- REV-04: dependências de US-09 e US-11 completadas por cenário, sem mudar prioridades.

requirements.md, user-stories.md, constraints.md e README.md atualizados de forma consistente. O backlog mantém 14 histórias e passa a 54 critérios e 13 casos excecionais. Não foram executados testes da aplicação; trata-se de revisão documental. O registo num commit Git continua por realizar.

**Estado:** revisão do Exercício 6 concluída com decisão humana e correções aplicadas. Segue-se a ficha 2, Architecture Pack.

## Mudança posterior de âmbito — COL-01, 18/11/2026

O utilizador autorizou alinhar o projeto com o colaborativo integral da ficha 03. As decisões históricas individuais foram substituídas onde indicado em [scope-reconciliation.md](../scope-reconciliation.md); consultar o [registo da ficha 3](ficha-03.md) e os documentos ativos. As aprovações anteriores permanecem como evidência da evolução, não como impedimento à mudança agora autorizada.
