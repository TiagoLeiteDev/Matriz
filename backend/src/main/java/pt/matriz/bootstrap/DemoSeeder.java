package pt.matriz.bootstrap;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "matriz.demo.seed-only", havingValue = "true")
public class DemoSeeder implements ApplicationRunner {
    public static final UUID ANA = id(1), BRUNO = id(2), PROJETO_ANA = id(11), PROJETO_BRUNO = id(12);
    public static final UUID TAREFA_PARTILHADA = id(21), TAREFA_PRIVADA = id(22), TAREFA_PESSOAL = id(23);
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final String password;

    public DemoSeeder(JdbcTemplate jdbc, PasswordEncoder encoder,
                      @Value("${matriz.demo.password}") String password) {
        this.jdbc = jdbc;
        this.encoder = encoder;
        this.password = password;
    }

    public static UUID id(int suffix) {
        return UUID.fromString("00000000-0000-4000-8000-" + String.format("%012d", suffix));
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (password.isBlank()) {
            throw new IllegalArgumentException("DEMO_PASSWORD é obrigatória ao executar o seed explícito");
        }
        // Serialize explicit seed runs; transaction rollback prevents partially loaded demos.
        jdbc.execute("SELECT pg_advisory_xact_lock(781204019)");
        Integer existing = jdbc.queryForObject("SELECT count(*) FROM utilizador WHERE id IN (?, ?)", Integer.class, ANA, BRUNO);
        if (existing != null && existing == 2) {
            Integer matching = jdbc.queryForObject("""
                SELECT count(*) FROM utilizador WHERE (id = ? AND email = 'ana@example.test')
                OR (id = ? AND email = 'bruno@example.test')
                """, Integer.class, ANA, BRUNO);
            if (matching == null || matching != 2) {
                throw new IllegalStateException("IDs de demonstração já ocupados por outras contas");
            }
            return; // Already initialized: preserve edits, removals, passwords and memberships.
        }
        if (existing != null && existing != 0) {
            throw new IllegalStateException("Seed parcial ou colisão de IDs; rever os dados sem os apagar");
        }
        user(ANA, "Ana Demo", "ana@example.test");
        user(BRUNO, "Bruno Demo", "bruno@example.test");
        project(PROJETO_ANA, "Projeto partilhado da Ana", ANA);
        project(PROJETO_BRUNO, "Projeto privado do Bruno", BRUNO);
        jdbc.update("INSERT INTO colaboracao_projeto VALUES (?, ?)", PROJETO_ANA, BRUNO);
        task(TAREFA_PARTILHADA, "Preparar apresentação", PROJETO_ANA, ANA);
        task(TAREFA_PRIVADA, "Rever trabalho privado", PROJETO_BRUNO, BRUNO);
        task(TAREFA_PESSOAL, "Organizar notas pessoais", null, ANA);
        jdbc.update("INSERT INTO atribuicao_tarefa VALUES (?, ?)", TAREFA_PARTILHADA, ANA);
        jdbc.update("INSERT INTO atribuicao_tarefa VALUES (?, ?)", TAREFA_PARTILHADA, BRUNO);
        jdbc.update("""
            INSERT INTO comentario (id, conteudo, tarefa_id, autor_id, criado_em)
            VALUES (?, ?, ?, ?, TIMESTAMPTZ '2026-11-18 12:00:00+00')
            """, id(31), "Dados fictícios para demonstração de colaboração.", TAREFA_PARTILHADA, BRUNO);
    }

    private void user(UUID id, String name, String email) {
        // Only insert. Never reset a password or overwrite an existing user's data.
        jdbc.update("INSERT INTO utilizador VALUES (?, ?, ?, ?)",
            id, name, email, encoder.encode(password));
    }

    private void project(UUID id, String name, UUID owner) {
        jdbc.update("INSERT INTO projeto (id, nome, proprietario_id) VALUES (?, ?, ?)",
            id, name, owner);
    }

    private void task(UUID id, String title, UUID project, UUID creator) {
        jdbc.update("INSERT INTO tarefa (id, titulo, projeto_id, criador_id) VALUES (?, ?, ?, ?)",
            id, title, project, creator);
    }
}
