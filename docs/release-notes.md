# Matriz 0.1.0-rc.1 — notas da versão candidata

**19/11/2026 · estado: candidata em validação; tag final ainda não criada nesta etapa.**

Inclui MVP colaborativo, testes e preparação de entrega. Arquitetura mantida: React compilado servido pelo Spring Boot e PostgreSQL separado. Configuração fictícia, migrations V1/V2 e seed explícito. Mudança de versão não altera o contrato funcional da API (OpenAPI 0.7.0) ou regras de acesso.

## Instalação

Seguir README.md a partir de clone limpo: .env.example → configuração local, docker compose build app seed, BD saudável, seed explícito e app saudável. Java/Node necessários apenas para desenvolvimento/testes no host; a opção Docker fornece build/runtime. JAR local backend/target/matriz.jar.

Para instância separada definir COMPOSE_PROJECT_NAME e portas próprias no mesmo terminal. Nunca apagar volume ou repetir seed para repor edições. Não executar testes de reinício contra a app principal por engano.

## Validação e identificação

Os comandos/resultados reais ficam em verification.md; o pipeline em .github/workflows/ci.yml. A versão será identificada por commit/tag v0.1.0-rc.1 depois da verificação final. Não interpretar esta nota como publicação de release de produção ou aplicação na Internet.

## Limitações conhecidas

Sessões em memória exigem novo login após reinício. HTTP local não é infraestrutura pública HTTPS; perfil prod não instala TLS. Sem promessa de desempenho para grandes volumes, acessibilidade universal ou suporte de todos os browsers. Expiração real é teste separado; estado exato das execuções consta do relatório.
