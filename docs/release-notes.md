# Matriz 0.1.0-rc.1 — notas da versão candidata

**19/11/2026 · candidata tecnicamente validada · identificação Git: v0.1.0-rc.1.** Confirmação do SHA, tag e publicação na [issue #20](https://github.com/TiagoLeiteDev/Matriz/issues/20).

Inclui MVP colaborativo, testes e preparação de entrega. Arquitetura mantida: React compilado servido pelo Spring Boot e PostgreSQL separado. Configuração fictícia, migrations V1/V2 e seed explícito. Mudança de versão não altera o contrato funcional da API (OpenAPI 0.7.0) ou regras de acesso.

## Instalação

Seguir README.md a partir de clone limpo: .env.example → configuração local, docker compose build app seed, BD saudável, seed explícito e app saudável. Java/Node necessários apenas para desenvolvimento/testes no host; a opção Docker fornece build/runtime. JAR local backend/target/matriz.jar.

Para instância separada definir COMPOSE_PROJECT_NAME e portas próprias no mesmo terminal. Nunca apagar volume ou repetir seed para repor edições. Não executar testes de reinício contra a app principal por engano.

## Validação e identificação

Os comandos/resultados reais ficam em verification.md; o pipeline em .github/workflows/ci.yml. A tag v0.1.0-rc.1 identifica o fecho; obter o SHA com `git rev-list -n 1 v0.1.0-rc.1`. A execução do fecho é confirmada na issue #20, evitando inventar um SHA antes de criar o commit. Não interpretar esta nota como publicação de release de produção ou aplicação na Internet.

## Limitações conhecidas

Sessões em memória exigem novo login após reinício. HTTP local não é infraestrutura pública HTTPS; perfil prod não instala TLS. Sem promessa de desempenho para grandes volumes, acessibilidade universal ou suporte de todos os browsers. Expiração real é teste separado; estado exato das execuções consta do relatório.


## Evidências de validação

- [CI main aprovada](https://github.com/TiagoLeiteDev/Matriz/actions/runs/44529912559): 95 backend, 30 normais, 3 reinícios; 4 especiais ignorados na etapa normal, três deles executados na etapa separada.
- [Falha deliberada](https://github.com/TiagoLeiteDev/Matriz/actions/runs/44525427870): um teste falhou e construção de imagens bloqueada; [recuperação aprovada](https://github.com/TiagoLeiteDev/Matriz/actions/runs/44530492017). Branch de demonstração preserva histórico, teste falhado ausente do HEAD.
- Clone remoto limpo da candidata com Chromium: 30 normais/4 ignorados, três reinícios separados e uma expiração real separada; timeout temporário de um minuto apenas nesse ensaio, restaurado depois.
- Persistência comprovada após recriar containers, com arquivo/tarefa/comentário/atribuição e eliminação; baseline conservado. Utilizador do container sem privilégios; V1/V2 aplicadas.
- Dois testes E2E foram corrigidos durante a validação: conclusão do logout e seletor do comentário persistido; nenhuma mudança de regra de negócio para obter sucesso.

Changelog e manifesto em CHANGELOG.md e docs/delivery-manifest.txt. Fonte das conclusões/comandos/limitações: verification.md e ai-logs/ficha-05.md. Não inclui publicação de produção, registry, verificação manual do formador, auditoria de carga/acessibilidade ou segurança universal.
