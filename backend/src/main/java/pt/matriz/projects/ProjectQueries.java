package pt.matriz.projects;

import java.util.UUID;
import java.util.List;
import java.util.Locale;
import java.text.Normalizer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.matriz.identity.AccountRepository;
import pt.matriz.identity.UserProfile;
import pt.matriz.identity.CurrentIdentity;

@Service
public class ProjectQueries {
    private final ProjectAccess access;
    private final JdbcTemplate jdbc;
    private final AccountRepository accounts;
    private final CurrentIdentity identity;
    public ProjectQueries(ProjectAccess access, JdbcTemplate jdbc, AccountRepository accounts, CurrentIdentity identity) {
        this.access = access; this.jdbc = jdbc; this.accounts = accounts; this.identity = identity;
    }
    public record Progress(long totalTarefas, long tarefasConcluidas, double percentagem) {}
    public record ProjectView(UUID id, String nome, String descricao, boolean arquivado, UserProfile proprietario, Progress progresso) {}
    private static String fold(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<ProjectView> list(String query, boolean archived) {
        UUID actor = identity.id();
        String search = fold(query == null ? "" : query);
        return jdbc.query("""
            SELECT p.id, p.nome, p.descricao, p.arquivado, u.id AS owner_id, u.nome AS owner_name,
                u.email AS owner_email, count(t.id) AS total,
                count(t.id) FILTER (WHERE t.estado = 'DONE') AS done
            FROM projeto p JOIN utilizador u ON u.id = p.proprietario_id
            LEFT JOIN tarefa t ON t.projeto_id = p.id
            WHERE p.arquivado = ? AND (p.proprietario_id = ? OR EXISTS (
                SELECT 1 FROM colaboracao_projeto c WHERE c.projeto_id = p.id AND c.utilizador_id = ?))
            GROUP BY p.id, u.id
            """, (rs, n) -> {
                long total = rs.getLong("total"), done = rs.getLong("done");
                return new ProjectView(rs.getObject("id", UUID.class), rs.getString("nome"), rs.getString("descricao"),
                    rs.getBoolean("arquivado"), new UserProfile(rs.getObject("owner_id", UUID.class),
                        rs.getString("owner_name"), rs.getString("owner_email")),
                    new Progress(total, done, total == 0 ? 0 : Math.round(done * 1000.0 / total) / 10.0));
            }, archived, actor, actor).stream().filter(project -> fold(project.nome()).contains(search)).toList();
    }
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ProjectView get(UUID id) {
        var project = access.requireView(id);
        var progress = jdbc.queryForObject("""
            SELECT count(*) AS total, count(*) FILTER (WHERE estado = 'DONE') AS done
            FROM tarefa WHERE projeto_id = ?
            """, (rs, n) -> {
                long total = rs.getLong("total"), done = rs.getLong("done");
                return new Progress(total, done, total == 0 ? 0 : Math.round(done * 1000.0 / total) / 10.0);
            }, id);
        return jdbc.queryForObject("SELECT nome, descricao FROM projeto WHERE id = ?",
            (rs, n) -> new ProjectView(id, rs.getString("nome"), rs.getString("descricao"), project.archived(),
                accounts.profile(project.owner()), progress), id);
    }
}
