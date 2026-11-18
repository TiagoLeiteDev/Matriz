package pt.matriz.tasks;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Locale;
import java.text.Normalizer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.matriz.identity.UserProfile;
import pt.matriz.identity.CurrentIdentity;

@Service
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class TaskQueries {
    private final TaskAccess access;
    private final JdbcTemplate jdbc;
    private final CurrentIdentity identity;
    public TaskQueries(TaskAccess access, JdbcTemplate jdbc, CurrentIdentity identity) { this.access = access; this.jdbc = jdbc; this.identity = identity; }
    private static String fold(String text) { return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT); }
    public List<TaskAccess.Task> list(TaskFilter filter) {
        UUID actor = identity.id();
        StringBuilder sql = new StringBuilder("""
            SELECT t.* FROM tarefa t LEFT JOIN projeto p ON p.id = t.projeto_id
            WHERE ((t.projeto_id IS NULL AND t.criador_id = ?) OR
                (p.arquivado = false AND (p.proprietario_id = ? OR EXISTS (
                    SELECT 1 FROM colaboracao_projeto c WHERE c.projeto_id = p.id AND c.utilizador_id = ?))))
            """);
        List<Object> values = new ArrayList<>(List.of(actor, actor, actor));
        if (filter.projetoId() != null) { sql.append(" AND t.projeto_id = ?"); values.add(filter.projetoId()); }
        if (filter.semProjeto()) sql.append(" AND t.projeto_id IS NULL");
        if (filter.estado() != null) { sql.append(" AND t.estado = ?"); values.add(filter.estado()); }
        if (filter.prioridade() != null) { sql.append(" AND t.prioridade = ?"); values.add(filter.prioridade()); }
        if (filter.semPrioridade()) sql.append(" AND t.prioridade IS NULL");
        if (filter.prazoDe() != null) { sql.append(" AND t.prazo >= ?"); values.add(filter.prazoDe()); }
        if (filter.prazoAte() != null) { sql.append(" AND t.prazo <= ?"); values.add(filter.prazoAte()); }
        String query = fold(filter.q() == null ? "" : filter.q());
        return jdbc.query(sql.toString(), TaskAccess::row, values.toArray()).stream()
            .filter(task -> fold(task.titulo()).contains(query)).toList();
    }
    public TaskAccess.Task get(UUID id) { return access.requireView(id); }
    public record CommentView(UUID id, String conteudo, UserProfile autor, Instant criadoEm) {}
    public record AssignmentView(UserProfile utilizador) {}
    public List<UserProfile> eligible(UUID taskId) {
        var task = access.requireView(taskId);
        return jdbc.query("""
            SELECT u.id, u.nome, u.email FROM utilizador u WHERE
            (?::uuid IS NULL AND u.id = ?) OR EXISTS (
                SELECT 1 FROM projeto p WHERE p.id = ? AND (p.proprietario_id = u.id OR EXISTS (
                    SELECT 1 FROM colaboracao_projeto c WHERE c.projeto_id = p.id AND c.utilizador_id = u.id)))
            ORDER BY u.id
            """, (rs, n) -> new UserProfile(rs.getObject("id", UUID.class), rs.getString("nome"), rs.getString("email")),
            task.projetoId(), task.criadorId(), task.projetoId());
    }
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
