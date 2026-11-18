package pt.matriz.tasks;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.identity.UserProfile;

@Service
public class AssignmentCommands {
    private final TaskCommands commands;
    private final TaskAccess access;
    private final JdbcTemplate jdbc;
    public AssignmentCommands(TaskCommands commands, TaskAccess access, JdbcTemplate jdbc) {
        this.commands = commands; this.access = access; this.jdbc = jdbc;
    }
    @Transactional
    public TaskQueries.AssignmentView add(UUID task, UUID user) {
        commands.lockForManage(task);
        access.requireEligible(task, user);
        int inserted = jdbc.update("INSERT INTO atribuicao_tarefa(tarefa_id, utilizador_id) VALUES (?, ?) ON CONFLICT DO NOTHING", task, user);
        if (inserted == 0) throw new ResponseStatusException(HttpStatus.CONFLICT);
        return jdbc.queryForObject("SELECT id, nome, email FROM utilizador WHERE id = ?",
            (rs, n) -> new TaskQueries.AssignmentView(new UserProfile(rs.getObject("id", UUID.class), rs.getString("nome"), rs.getString("email"))), user);
    }
    @Transactional
    public void remove(UUID task, UUID user) {
        commands.lockForManage(task);
        if (jdbc.update("DELETE FROM atribuicao_tarefa WHERE tarefa_id = ? AND utilizador_id = ?", task, user) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
