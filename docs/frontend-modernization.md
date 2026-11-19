# Modernização do frontend da Matriz

**Data:** 19/11/2026. **Estado:** modernização aprovada visualmente pelo utilizador; código/testes no commit `32b019d`. Integração e publicação autorizadas na [issue #21](https://github.com/TiagoLeiteDev/Matriz/issues/21), onde ficam os SHAs publicados e o resultado da CI. Pedido: modernizar o frontend e navegar entre projetos, tarefas e painel após o login.

## Estrutura e decisões

A aplicação já era uma SPA React. A alteração extrai a estrutura comum para `frontend/src/components/AppShell.tsx` e moderniza a apresentação com React/TypeScript/CSS, sem dependências ou requisitos de negócio novos.

- Menu lateral Painel/Projetos/Tarefas com `aria-current="page"`; no móvel, navegação em linha no topo.
- Cabeçalho com contexto da página, utilizador da sessão, atualização de perfil e logout.
- Área central para os componentes existentes; rotas por fragmentos preservadas, incluindo detalhe de projeto/tarefa, reload e histórico.
- Login em duas colunas no desktop e formulário compacto no móvel.
- Cartões com contagens reais e progresso derivado dos valores da API.
- Filtros em grelha, cartões com rótulos de estado, Lista/Kanban/Calendário e metadados agrupados.
- Paleta clara com verde, estilos comuns para formulários, mensagens e diálogos. Sem fontes externas ou serviços novos.
- Foco visível, ligação para saltar para o conteúdo, rótulos existentes e preferência por movimento reduzido; não constitui auditoria integral de acessibilidade.

Autenticação, expiração, CSRF, autorização, privacidade, arquivo, validações e contratos mantêm a implementação existente. Backend, modelo e migrations não foram alterados.

## Como consultar

Servidor de desenvolvimento em [http://127.0.0.1:5173/](http://127.0.0.1:5173/), com o proxy Vite existente para o backend da porta 8080. Para voltar a iniciar, com esse backend disponível:

```powershell
npm.cmd --prefix frontend run dev -- --port 5173 --strictPort
```

A porta 5173 apresenta a árvore de trabalho atual. A instância isolada 18087 e a tag publicada `v0.1.0-rc.1` conservam a interface da candidata. Os dados de apresentação preparados em 18087 pertencem à base dessa instância, não à da porta 8080. A evolução está em «Não publicado» no Changelog: não foi criada uma nova versão/tag; para identificar as alterações usar o SHA, pois os manifests conservam 0.1.0-rc.1.

## Verificação executada

| Verificação | Resultado real |
|---|---|
| `npm.cmd --prefix frontend run lint` | Aprovada |
| `npm.cmd --prefix frontend run build` | Aprovada: TypeScript e Vite |
| Novos testes `frontend-shell.spec.ts` | 2 aprovados: 1365 px e 360 px |
| Suite completa Playwright contra 5173 → backend 8080 | 32 aprovados, 4 especiais ignorados, 47,1 s |
| Build Docker multi-stage e health da instância isolada | Aprovados, projeto `matriz-ui-review`, portas 18088/55438 e volume próprio |
| Suite completa contra a imagem Docker isolada | 32 aprovados, 4 especiais ignorados, 49,7 s |
| Atualização da app principal 8080 | Mesma imagem validada; apenas container app recriado, sem seed ou alteração do volume |
| Verificação SPA na app principal | 2 aprovados (desktop/360 px), 5,1 s; health healthy |
| Conferência de contagens após atualização | Principal e apresentação iguais às baselines |
| Inspeção visual | Login, painel, tarefas desktop e tarefas a 360 px abertos e inspecionados |
| `git diff --check` | Sem erros |

Os testes novos, com comentários AAA, verificam sessão, seleção ativa, histórico, reload, logout, ausência de overflow horizontal e navegação sem substituição do documento. As suites existentes verificam CRUD, permissões, filtros, progresso, colaboração e erros; criam e limpam apenas fixtures próprias.

Uma primeira execução foi interrompida por falhas de ligação ao Vite dentro da sandbox. Um teste isolado confirmou `ERR_CONNECTION_TIMED_OUT` antes de carregar a aplicação. Após reiniciar o mesmo servidor fora da sandbox, os dois testes novos e a suite completa passaram. A execução interrompida não é contada como aprovada.

Não foram repetidos localmente testes backend, reinícios ou expiração real: a alteração é de apresentação e navegação. O Dockerfile faz package com testes ignorados; o build da imagem não é prova de testes backend aprovados. A CI do SHA publicado executa backend, frontend, E2E e reinícios; consultar o resultado efetivo na issue #21. Não foi feito novo teste local noutro sistema nem auditoria integral de usabilidade. A apreciação visual positiva do utilizador foi registada separadamente dos resultados automáticos.

## Capturas

- [Login](ui/frontend-moderno-login.png)
- [Painel](ui/frontend-moderno-painel.png)
- [Projetos](ui/frontend-moderno-projetos.png)
- [Tarefas](ui/frontend-moderno-tarefas.png)
- [Móvel](ui/frontend-moderno-movel.png)

O wireframe colaborativo anterior permanece como evidência do desenho. Estas capturas representam a implementação modernizada; não constituem auditoria integral do wireframe.

## Distribuição Docker e conservação

Fontes funcionais do commit `32b019d` construídas com `docker compose build app seed`, no projeto isolado `matriz-ui-review`. Seed apenas nessa base nova; migrations V1/V2 aplicadas sem alterações. A suite completa passou contra os ficheiros React compilados servidos pelo Spring Boot, sem Vite.

Depois de aprovada a imagem, foi criada a referência local `matriz-app:before-ui-32b019d` para a imagem anterior e a nova imagem foi selecionada como `matriz-app:latest`. Executado `docker compose up -d --no-deps --no-build app --wait` no projeto principal. Nova interface em [http://localhost:8080/](http://localhost:8080/). O reinício invalida sessões em memória, pelo que é necessário voltar a iniciar sessão.

Conferência SQL apenas de contagens, antes/depois: principal com 2 utilizadores, 2 projetos, 3 tarefas, 1 colaboração, 2 atribuições e 1 comentário; apresentação com 2 utilizadores, 5 projetos, 5 tarefas, 4 colaborações, 3 atribuições e 3 comentários. Contagens iguais; não equivale a comparação campo a campo. Container e dados da porta 18087 não foram recriados. A tag candidata continua a apontar para `9c2f1af`.

## Pendências

O utilizador apreciou positivamente a interface e autorizou issue/commits/push, documentação e distribuição Docker. Integração e CI acompanhadas na issue #21. Tag candidata intacta; alteração preexistente no Dockerfile e documentos de apresentação preservados, fora dos commits desta modernização. Não se apresenta esta aprovação visual como execução humana exaustiva dos critérios funcionais.
