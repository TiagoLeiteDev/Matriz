# Matriz — Frontend e integração (Ficha 03, Exercício 7)

**Data:** 18/11/2026. Fonte: 10 - Ficha Prática 03.pdf, página 5. Implementação por incrementos concluída e verificada localmente; issue [#8](https://github.com/TiagoLeiteDev/Matriz/issues/8), registo Git autorizado; confirmação de publicação na issue #8.

## Análise e decisões

Login, projetos, tarefas, filtros, responsáveis/comentários já funcionam. Faltam painel, gestão de colaboradores e vistas Kanban/calendário do MVP aprovado. Reutilizar componentes, cliente /api e proxy Vite; manter formulários/erros/CSRF e detalhe autorizados.

PAINEL-01 (resposta expressa): pessoais + projetos ativos acessíveis, excluir arquivados; contagens independentes dos filtros da lista. GET /painel mantém o parâmetro explícito incluirIndependentes; UI envia true. O backend suporta também false conforme contrato, e informa o conjunto em incluiIndependentes.

COL-07 (resposta expressa): proprietário pode adicionar/remover colaboradores mesmo arquivado; COL-06 remove atribuições inelegíveis atomicamente e preserva autoria/contas. Isto não autoriza escritas de tarefas arquivadas. Adicionar por email existente exato como login; proprietário não é colaboração redundante, duplicado=409; ausência de conta/colaboração=404. Consultas pelo conjunto autorizado.

## Plano e critérios

1. Colaboradores/API e UI: GET/POST/DELETE já previstos no contrato; lock do projeto e autorização do proprietário; remover participação e atribuições no mesmo commit; seleção por email. Testes PostgreSQL de papéis, duplicados, arquivo, privacidade, CSRF, limpeza/rollback e revogação. Rever diff antes do painel.
2. Painel/API e UI: snapshot consistente com utilizador/projetos/contagens, sem expor dados alheios; vista inicial com navegação para projeto/tarefas. Testar 0, três estados, pessoais, arquivo e dois atores; total independente da lista. Rever diff.
3. Tarefas: alternar Lista/Kanban/Calendário sobre o mesmo resultado autorizado/filtrado. Kanban com três estados TAR-01, cartões abrem detalhe. Calendário mensal, mês local atual, segunda-feira primeiro, anterior/seguinte/hoje; prazo como data sem conversão UTC, sem prazo apenas na lista/Kanban. Escolhas de apresentação simples para PD-05, sem nova regra de negócio. Testar filtros, mudança de estado/prazo e reabertura do detalhe.
4. Layout/coerência: navegação Painel/Projetos/Tarefas, layout adaptável, teclado/foco, loading/vazio/erro/sucesso e formulários preservados. Contagens/progresso atualizados após resposta confirmada; painel consulta estado atual ao regressar, sem polling que renove sessão. Browser desktop/móvel e regressão completa, schemas, diff, README/log/verification.

## Ficheiros e limites

Backend projects/collaborators e novo componente de consulta de painel, SecurityConfiguration, testes; frontend App, painel, projetos/colaboradores, vistas de tarefas, estilos; OpenAPI/README/AGENTS/docs/e2e. Sem novas tabelas/migrations ou alteração de stack. Testes não apagam seed/volume; testes destrutivos/rollback só em bases descartáveis.

F01–F19/US-01–22 devem estar acessíveis nos ecrãs relevantes ao fechar este incremento. CI/entrega reproduzível e auditoria final da ficha têm exercícios próprios. Não introduzir registo público, notificações, estados novos ou funcionalidades fora do MVP. Resultados serão registados por incremento após execução real; commit/push tratados após implementação.


## Resultado e rastreabilidade

- Colaboradores: API/UI no detalhe; proprietário exclusivo, email existente exato, duplicados/proprietário 409, inexistente 404; gestão em arquivo conforme COL-07, limpeza COL-06 atómica e rollback testado. US-17 completa.
- Painel: perfil/projetos/contagens consistentes e autorizados, incluindo pessoais na UI; projetos/tarefas arquivados excluídos e filtros da lista não alteram o total. US-22 e F19.
- Vistas: Lista/Kanban/calendário usam o mesmo conjunto/filtros e abrem o detalhe existente; estados/prazos atualizados após gravação. US-07/08/14 e F05/F11. Datas sem prazo ficam nas vistas adequadas.
- Interface: navegação e ecrãs reutilizam os fluxos existentes de projetos/tarefas/autenticação/responsáveis/comentários; mensagens/formulários e proteção de sessão/CSRF preservadas. Layout a 360 px com agenda móvel legível. Wireframe conceptual continua referência; capturas da implementação em ui/ e verificação visual em verification.md.

Resultados executados: 75 testes backend, lint/build e Docker passaram; 21 cenários normais Chrome passaram (4 especiais ignorados), três cenários afetados pela melhoria móvel repetidos com sucesso, um teste separado de reinício de projetos passou. OpenAPI 0.7.0 válido com 24 operações implementadas e respostas reais conformes. Baseline conservada: dois utilizadores, dois projetos, três tarefas, um comentário e duas atribuições. Sem novas entidades/migrations. Detalhe dos erros corrigidos, comandos e limitações em verification.md.

Revisão final: nenhuma identidade imposta pelo cliente, proprietário controla colaboradores, remoção transacional conserva autoria/contas; consultas não expõem dados alheios. Vistas não criam cópias de tarefas, calendário usa data textual e paginação não é inventada. Parâmetro ausente do painel agora devolve 400; aviso Fast Refresh resolvido; captura móvel inspecionada levou a ajuste de legibilidade e nova execução dos testes afetados. Diff revisto por incremento e no fecho.

Não existem dúvidas funcionais críticas pendentes deste incremento. Continuam pendentes a verificação consolidada do Exercício 8, CI/entrega reproduzível/apresentação e ensaios de acessibilidade/browser/desempenho mais amplos. Registo Git autorizado posteriormente; commit e confirmação de publicação na issue #8.
