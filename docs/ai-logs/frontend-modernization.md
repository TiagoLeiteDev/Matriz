# Modernização do frontend — registo da interação

**Data:** 19/11/2026. **Ferramenta:** ChatGPT/Codex.

**Pedido:** «voltando agora a aplicação, podemos refaturar o frontend de forma a ser uma coisa mais moderna? uma single page aplication onde depois do loggin possamos navegar de projetos, tarefas, etc?»

**Análise:** já existe SPA React com rotas por fragmentos e componentes separados. A apresentação básica concentra perfil/navegação acima do conteúdo e formulários verticais. Consultados AGENTS, fontes React/CSS, requisitos, histórias, restrições, arquitetura/modelo, API, integração/wireframe e verificações pertinentes.

**Proposta comunicada e executada:** extrair AppShell, menu lateral, conta no cabeçalho, secção ativa, modernizar login/painel/projetos/tarefas e adaptar ao móvel. Paleta clara com verde, sem dependências novas ou mudanças nas regras. Mantidas rotas, componentes de negócio e permissões.

**Alterações:** App/AppShell, LoginForm, Dashboard, ProjectsWorkspace, TasksWorkspace, TaskViews e CSS comum. Metadados agrupados, filtros com wrappers de campo, barras sobre progresso existente e rótulos de estado. Novo `e2e/tests/frontend-shell.spec.ts`, em AAA, verifica navegação no mesmo documento, histórico, sessão, logout e desktop/móvel.

**Resultados reais:** lint/build aprovados; 2 novos testes aprovados isoladamente; suite completa com 32 aprovados e 4 especiais ignorados em 47,1 s. Primeira execução interrompida por ligação indisponível ao Vite dentro da sandbox; teste isolado confirmou timeout. Servidor reiniciado fora da sandbox, seguido de execução aprovada, sem alteração de código para resolver a limitação do ambiente.

**Inspeção:** cinco capturas geradas com browser temporário e logout no final; login, painel e tarefas desktop/móvel abertos para inspeção visual. Sem guardar credenciais/cookies. Evidências e limites em [frontend-modernization.md](../frontend-modernization.md).

**Entrega local:** porta 5173, proxy para backend 8080. Candidata isolada 18087, dados de apresentação e tag v0.1.0-rc.1 preservados. Não repetidos testes backend, especiais ou CI; sem commit/push. Apreciação visual humana e integração/publicação ficam pendentes.

## Integração autorizada — continuação de 19/11/2026

**Validação humana:** o utilizador respondeu «ok parece me muito melhor» e perguntou pelas atualizações necessárias. Foi apresentada a lista: README/arquitetura/referências visuais, Changelog Não publicado, issue/commits/push/CI e reconstrução/verificação Docker. Resposta: «muito bem, faz isso».

**Rastreabilidade:** criada a [issue #21](https://github.com/TiagoLeiteDev/Matriz/issues/21). Primeiro commit `32b019d`, apenas frontend e testes; segundo commit para documentação/evidências, ambos com Refs #21. Dockerfile preexistente e documentos/capturas da apresentação mantidos fora destes commits. README, AGENTS, arquitetura, integração, wireframe, Changelog, relatório e verificações atualizados. Inventário/notas da candidata e tag conservados como evidência dessa versão; não foi criada nova versão/tag.

**Docker:** build multi-stage aprovado, projeto `matriz-ui-review`, portas 18088/55438, volume próprio, V1/V2 e seed explícito só na base nova. Suite contra a imagem servida pelo Spring: 32 aprovados e quatro especiais ignorados em 49,7 s. O build usa testes backend ignorados, como já previsto no Dockerfile; aprovação de build não é execução de testes backend.

**Atualização principal:** referência à imagem anterior conservada; nova imagem aplicada apenas ao container app na porta 8080, sem seed ou recriação da BD/volume. Dois testes SPA desktop/móvel aprovados, 5,1 s. Health principal/apresentação healthy. Contagens das duas bases iguais antes/depois, sem alegar comparação de todos os campos. Dados e container da apresentação 18087 preservados. Sessões principais exigem novo login após reinício, conforme SEG-01.

**Publicação e CI:** autorizadas nesta continuação; SHAs, execução e conclusão efetivas ficam registados na issue #21. Não inferir aprovação da CI a partir dos testes locais. A apreciação visual do utilizador não equivale a auditoria integral de acessibilidade ou aceitação funcional humana de todos os critérios.
