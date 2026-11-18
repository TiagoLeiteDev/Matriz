package pt.matriz.projects;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import pt.matriz.identity.CurrentIdentity;

@Service
public class ProjectAccess {
    private final JdbcTemplate jdbc;
    private final CurrentIdentity identity;
    public ProjectAccess(JdbcTemplate jdbc, CurrentIdentity identity) { this.jdbc = jdbc; this.identity = identity; }
    public record Project(UUID id, UUID owner, boolean archived) {}
    private Project find(UUID id) {
        return jdbc.query("SELECT id, proprietario_id, arquivado FROM projeto WHERE id = ?",
            (rs, n) -> new Project(rs.getObject("id", UUID.class), rs.getObject("proprietario_id", UUID.class), rs.getBoolean("arquivado")), id)
            .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    public boolean accessibleTo(UUID project, UUID user) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
            SELECT EXISTS (SELECT 1 FROM projeto p WHERE p.id = ? AND
            (p.proprietario_id = ? OR EXISTS (SELECT 1 FROM colaboracao_projeto c
                WHERE c.projeto_id = p.id AND c.utilizador_id = ?)))
            """, Boolean.class, project, user, user));
    }
    public Project requireView(UUID id) {
        UUID actor = identity.id();
        Project project = find(id);
        if (!accessibleTo(id, actor)) throw new AccessDeniedException("Sem acesso ao projeto");
        return project;
    }
    public Project requireOwner(UUID id) {
        Project project = requireView(id);
        if (!project.owner().equals(identity.id())) throw new AccessDeniedException("Exige proprietário");
        return project;
    }
    public Project requireActive(UUID id) {
        Project project = requireView(id);
        if (project.archived()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        return project;
    }
}
