package pt.matriz.projects;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.identity.CurrentIdentity;

@Service
public class ProjectCommands {
    private final JdbcTemplate jdbc;
    private final CurrentIdentity identity;
    private final ProjectAccess access;
    private final ProjectQueries queries;
    public ProjectCommands(JdbcTemplate jdbc, CurrentIdentity identity, ProjectAccess access, ProjectQueries queries) {
        this.jdbc = jdbc; this.identity = identity; this.access = access; this.queries = queries;
    }
    @Transactional
    public ProjectQueries.ProjectView create(ProjectInput.Create input) {
        UUID owner = identity.id(), id = UUID.randomUUID();
        jdbc.update("INSERT INTO projeto (id, nome, descricao, proprietario_id) VALUES (?, ?, ?, ?)", id, input.nome(), input.descricao(), owner);
        return queries.get(id);
    }
    void lockOwner(UUID id) {
        identity.id();
        if (jdbc.query("SELECT id FROM projeto WHERE id = ? FOR UPDATE", (rs, n) -> rs.getObject(1, UUID.class), id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        access.requireOwner(id);
    }
    @Transactional
    public ProjectQueries.ProjectView patch(UUID id, ProjectInput.Patch input) {
        lockOwner(id);
        jdbc.update("""
            UPDATE projeto SET nome = CASE WHEN ? THEN ? ELSE nome END,
                descricao = CASE WHEN ? THEN ? ELSE descricao END,
                arquivado = CASE WHEN ? THEN ? ELSE arquivado END
            WHERE id = ?
            """, input.hasName(), input.nome(), input.hasDescription(), input.descricao(), input.hasArchived(), input.arquivado(), id);
        return queries.get(id);
    }
    @Transactional
    public void delete(UUID id) {
        lockOwner(id);
        jdbc.update("DELETE FROM projeto WHERE id = ?", id);
    }
}
