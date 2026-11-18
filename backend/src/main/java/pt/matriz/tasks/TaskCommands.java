package pt.matriz.tasks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.identity.CurrentIdentity;
import pt.matriz.projects.ProjectAccess;

@Service
public class TaskCommands {
    private final JdbcTemplate jdbc;
    private final TaskAccess access;
    private final ProjectAccess projects;
    private final TaskQueries queries;
    private final CurrentIdentity identity;
    public TaskCommands(JdbcTemplate jdbc, TaskAccess access, ProjectAccess projects, TaskQueries queries, CurrentIdentity identity) {
        this.jdbc = jdbc; this.access = access; this.projects = projects; this.queries = queries; this.identity = identity;
    }
    private void lockProjects(UUID... ids) {
        Arrays.stream(ids).filter(Objects::nonNull).distinct().sorted(Comparator.comparing(UUID::toString)).forEach(id -> {
            projects.requireActive(id);
            if (jdbc.query("SELECT id FROM projeto WHERE id = ? FOR UPDATE", (rs, n) -> rs.getObject(1, UUID.class), id).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            projects.requireActive(id);
        });
    }
    private TaskAccess.Task lockTask(UUID id, TaskAccess.Task initial) {
        if (jdbc.query("SELECT id FROM tarefa WHERE id = ? FOR UPDATE", (rs, n) -> rs.getObject(1, UUID.class), id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        TaskAccess.Task current = access.requireManage(id);
        if (!Objects.equals(initial.projetoId(), current.projetoId())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        return current;
    }
    @Transactional
    public TaskAccess.Task create(TaskInput.Create input) {
        UUID actor = identity.id(), id = UUID.randomUUID();
        lockProjects(input.projetoId());
        jdbc.update("""
            INSERT INTO tarefa (id, titulo, descricao, estado, prioridade, prazo, projeto_id, criador_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, id, input.titulo(), input.descricao(), input.estado(), input.prioridade(), input.prazo(), input.projetoId(), actor);
        return queries.get(id);
    }
    @Transactional
    public TaskAccess.Task patch(UUID id, TaskInput.Patch patch) {
        UUID actor = identity.id();
        var initial = access.requireView(id);
        UUID destination = patch.hasProject() ? patch.projetoId() : initial.projetoId();
        lockProjects(initial.projetoId(), destination);
        var current = lockTask(id, initial);
        jdbc.update("""
            UPDATE tarefa SET titulo = ?, descricao = ?, estado = ?, prioridade = ?, prazo = ?, projeto_id = ? WHERE id = ?
            """, patch.hasTitle() ? patch.titulo() : current.titulo(), patch.hasDescription() ? patch.descricao() : current.descricao(),
            patch.hasState() ? patch.estado() : current.estado(), patch.hasPriority() ? patch.prioridade() : current.prioridade(),
            patch.hasDeadline() ? patch.prazo() : current.prazo(), destination, id);
        if (!Objects.equals(current.projetoId(), destination)) {
            if (destination == null) jdbc.update("DELETE FROM atribuicao_tarefa WHERE tarefa_id = ? AND utilizador_id <> ?", id, current.criadorId());
            else jdbc.update("""
                DELETE FROM atribuicao_tarefa a WHERE a.tarefa_id = ? AND NOT EXISTS (
                    SELECT 1 FROM projeto p WHERE p.id = ? AND (p.proprietario_id = a.utilizador_id OR EXISTS (
                        SELECT 1 FROM colaboracao_projeto c WHERE c.projeto_id = p.id AND c.utilizador_id = a.utilizador_id)))
                """, id, destination);
        }
        if (destination == null && !current.criadorId().equals(actor)) return null;
        return queries.get(id);
    }
    @Transactional
    public void delete(UUID id) {
        var initial = access.requireView(id);
        lockProjects(initial.projetoId());
        lockTask(id, initial);
        jdbc.update("DELETE FROM tarefa WHERE id = ?", id);
    }
    // Called only inside a write transaction, using the same project-before-task lock order.
    TaskAccess.Task lockForManage(UUID id) {
        var initial = access.requireView(id);
        lockProjects(initial.projetoId());
        return lockTask(id, initial);
    }
}
