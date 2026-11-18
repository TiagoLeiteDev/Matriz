package pt.matriz.tasks;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.matriz.identity.UserProfile;

@RestController
@RequestMapping("/api/tarefas/{id}")
public class AssignmentController {
    private final AssignmentCommands commands;
    private final TaskQueries queries;
    public AssignmentController(AssignmentCommands commands, TaskQueries queries) { this.commands = commands; this.queries = queries; }
    @GetMapping("/utilizadores-elegiveis") public List<UserProfile> eligible(@PathVariable UUID id) { return queries.eligible(id); }
    @PostMapping("/responsaveis") public ResponseEntity<TaskQueries.AssignmentView> add(@PathVariable UUID id, @RequestBody JsonNode body) {
        UUID user = CollaborationInput.assignee(body);
        return ResponseEntity.created(URI.create("/api/tarefas/" + id + "/responsaveis/" + user)).body(commands.add(id, user));
    }
    @DeleteMapping("/responsaveis/{user}") public ResponseEntity<Void> remove(@PathVariable UUID id, @PathVariable UUID user) {
        commands.remove(id, user); return ResponseEntity.noContent().build();
    }
}
