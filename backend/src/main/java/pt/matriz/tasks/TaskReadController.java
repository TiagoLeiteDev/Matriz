package pt.matriz.tasks;

import java.util.List;
import java.util.UUID;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarefas")
public class TaskReadController {
    private final TaskQueries queries;
    private final TaskCommands commands;
    private final ObjectMapper mapper;
    public TaskReadController(TaskQueries queries, TaskCommands commands, ObjectMapper mapper) {
        this.queries = queries; this.commands = commands; this.mapper = mapper;
    }
    @GetMapping public List<TaskAccess.Task> list(@RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) String projetoId, @RequestParam(required = false) String semProjeto,
            @RequestParam(required = false) String estado, @RequestParam(required = false) String prioridade,
            @RequestParam(required = false) String semPrioridade, @RequestParam(required = false) String prazoDe,
            @RequestParam(required = false) String prazoAte) {
        return queries.list(new TaskFilter(q, TaskFilter.id(projetoId), TaskFilter.bool(semProjeto), estado,
            prioridade, TaskFilter.bool(semPrioridade), TaskFilter.date(prazoDe), TaskFilter.date(prazoAte)));
    }
    @PostMapping public ResponseEntity<TaskAccess.Task> create(@RequestBody JsonNode body) {
        var task = commands.create(TaskInput.create(body));
        return ResponseEntity.created(URI.create("/api/tarefas/" + task.id())).body(task);
    }
    @PatchMapping("/{id}") public JsonNode patch(@PathVariable UUID id, @RequestBody JsonNode body) {
        return mapper.valueToTree(commands.patch(id, TaskInput.patch(body)));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) {
        commands.delete(id); return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}") public TaskAccess.Task get(@PathVariable UUID id) { return queries.get(id); }
    @GetMapping("/{id}/comentarios") public List<TaskQueries.CommentView> comments(@PathVariable UUID id) { return queries.comments(id); }
    @GetMapping("/{id}/responsaveis") public List<TaskQueries.AssignmentView> assignments(@PathVariable UUID id) { return queries.assignments(id); }
}
