package pt.matriz.tasks;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarefas")
public class TaskReadController {
    private final TaskQueries queries;
    public TaskReadController(TaskQueries queries) { this.queries = queries; }
    @GetMapping("/{id}") public TaskAccess.Task get(@PathVariable UUID id) { return queries.get(id); }
    @GetMapping("/{id}/comentarios") public List<TaskQueries.CommentView> comments(@PathVariable UUID id) { return queries.comments(id); }
    @GetMapping("/{id}/responsaveis") public List<TaskQueries.AssignmentView> assignments(@PathVariable UUID id) { return queries.assignments(id); }
}
