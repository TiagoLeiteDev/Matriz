package pt.matriz.projects;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.matriz.identity.AccountRepository;
import pt.matriz.identity.UserProfile;

@Service
public class ProjectQueries {
    private final ProjectAccess access;
    private final JdbcTemplate jdbc;
    private final AccountRepository accounts;
    public ProjectQueries(ProjectAccess access, JdbcTemplate jdbc, AccountRepository accounts) {
        this.access = access; this.jdbc = jdbc; this.accounts = accounts;
    }
    public record Progress(long totalTarefas, long tarefasConcluidas, double percentagem) {}
    public record ProjectView(UUID id, String nome, String descricao, boolean arquivado, UserProfile proprietario, Progress progresso) {}
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ProjectView get(UUID id) {
        var project = access.requireView(id);
        var progress = jdbc.queryForObject("""
            SELECT count(*) AS total, count(*) FILTER (WHERE estado = 'Concluída') AS done
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
