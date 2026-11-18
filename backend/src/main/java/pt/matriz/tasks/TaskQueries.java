package pt.matriz.tasks;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.matriz.identity.UserProfile;

@Service
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class TaskQueries {
    private final TaskAccess access;
    private final JdbcTemplate jdbc;
    public TaskQueries(TaskAccess access, JdbcTemplate jdbc) { this.access = access; this.jdbc = jdbc; }
    public TaskAccess.Task get(UUID id) { return access.requireView(id); }
    public record CommentView(UUID id, String conteudo, UserProfile autor, Instant criadoEm) {}
    public record AssignmentView(UserProfile utilizador) {}
    public List<CommentView> comments(UUID task) {
        access.requireView(task);
        return jdbc.query("""
            SELECT c.id, c.conteudo, c.criado_em, u.id AS autor_id, u.nome, u.email
            FROM comentario c JOIN utilizador u ON u.id = c.autor_id WHERE c.tarefa_id = ?
            ORDER BY c.criado_em, c.id
            """, (rs, n) -> new CommentView(rs.getObject("id", UUID.class), rs.getString("conteudo"),
                new UserProfile(rs.getObject("autor_id", UUID.class), rs.getString("nome"), rs.getString("email")),
                rs.getTimestamp("criado_em").toInstant()), task);
    }
    public List<AssignmentView> assignments(UUID task) {
        access.requireView(task);
        return jdbc.query("""
            SELECT u.id, u.nome, u.email FROM atribuicao_tarefa a
            JOIN utilizador u ON u.id = a.utilizador_id WHERE a.tarefa_id = ? ORDER BY u.id
            """, (rs, n) -> new AssignmentView(new UserProfile(rs.getObject("id", UUID.class),
                rs.getString("nome"), rs.getString("email"))), task);
    }
}
