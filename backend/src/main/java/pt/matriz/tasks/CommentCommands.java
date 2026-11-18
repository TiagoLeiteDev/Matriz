package pt.matriz.tasks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.matriz.identity.CurrentIdentity;
import pt.matriz.identity.UserProfile;

@Service
public class CommentCommands {
    private final TaskCommands commands;
    private final JdbcTemplate jdbc;
    private final CurrentIdentity identity;
    public CommentCommands(TaskCommands commands, JdbcTemplate jdbc, CurrentIdentity identity) {
        this.commands = commands; this.jdbc = jdbc; this.identity = identity;
    }
    @Transactional
    public TaskQueries.CommentView add(UUID task, String content) {
        commands.lockForManage(task);
        UUID actor = identity.id(), id = UUID.randomUUID();
        Instant created = Instant.now().truncatedTo(ChronoUnit.MICROS);
        jdbc.update("INSERT INTO comentario(id, conteudo, tarefa_id, autor_id, criado_em) VALUES (?, ?, ?, ?, ?)",
            id, content, task, actor, java.sql.Timestamp.from(created));
        UserProfile author = jdbc.queryForObject("SELECT id, nome, email FROM utilizador WHERE id = ?",
            (rs, n) -> new UserProfile(rs.getObject("id", UUID.class), rs.getString("nome"), rs.getString("email")), actor);
        return new TaskQueries.CommentView(id, content, author, created);
    }
}
