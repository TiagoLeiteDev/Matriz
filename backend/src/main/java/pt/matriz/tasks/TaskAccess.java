package pt.matriz.tasks;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.identity.CurrentIdentity;
import pt.matriz.projects.ProjectAccess;

@Service
public class TaskAccess {
    private final JdbcTemplate jdbc;
    private final CurrentIdentity identity;
    private final ProjectAccess projects;
    public TaskAccess(JdbcTemplate jdbc, CurrentIdentity identity, ProjectAccess projects) {
        this.jdbc = jdbc; this.identity = identity; this.projects = projects;
    }
    public record Task(UUID id, String titulo, String descricao, String estado, String prioridade,
                       LocalDate prazo, UUID projetoId, UUID criadorId) {}
    public Task requireView(UUID id) {
        UUID actor = identity.id();
        Task task = jdbc.query("SELECT * FROM tarefa WHERE id = ?", (rs, n) -> new Task(
            rs.getObject("id", UUID.class), rs.getString("titulo"), rs.getString("descricao"),
            rs.getString("estado"), rs.getString("prioridade"), rs.getObject("prazo", LocalDate.class),
            rs.getObject("projeto_id", UUID.class), rs.getObject("criador_id", UUID.class)), id)
            .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (task.projetoId() == null) {
            if (!actor.equals(task.criadorId())) throw new AccessDeniedException("Tarefa pessoal privada");
        } else projects.requireView(task.projetoId());
        return task;
    }
    public Task requireManage(UUID id) {
        Task task = requireView(id);
        if (task.projetoId() != null) projects.requireActive(task.projetoId());
        return task;
    }
    public void requireEligible(UUID taskId, UUID selectedUser) {
        Task task = requireManage(taskId);
        if (!Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS (SELECT 1 FROM utilizador WHERE id = ?)", Boolean.class, selectedUser))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        boolean eligible = task.projetoId() == null ? task.criadorId().equals(selectedUser)
            : projects.accessibleTo(task.projetoId(), selectedUser);
        if (!eligible) throw new AccessDeniedException("Responsável inelegível");
    }
    public void requireTransfer(UUID taskId, UUID destinationProject) {
        requireManage(taskId);
        if (destinationProject != null) projects.requireActive(destinationProject);
        // Future write use case must perform COL-06 cleanup in the same transaction.
    }
}
