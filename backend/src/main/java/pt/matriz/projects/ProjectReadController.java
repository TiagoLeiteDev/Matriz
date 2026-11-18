package pt.matriz.projects;

import java.util.UUID;
import java.util.List;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projetos")
public class ProjectReadController {
    private final ProjectQueries queries;
    private final ProjectCommands commands;
    public ProjectReadController(ProjectQueries queries, ProjectCommands commands) { this.queries = queries; this.commands = commands; }
    @GetMapping public List<ProjectQueries.ProjectView> list(@RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) String arquivado) {
        if (arquivado != null && !arquivado.equals("true") && !arquivado.equals("false")) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        return queries.list(q, "true".equals(arquivado));
    }
    @GetMapping("/{id}") public ProjectQueries.ProjectView get(@PathVariable UUID id) { return queries.get(id); }
    @PostMapping public ResponseEntity<ProjectQueries.ProjectView> create(@RequestBody JsonNode body) {
        var project = commands.create(ProjectInput.create(body));
        return ResponseEntity.created(URI.create("/api/projetos/" + project.id())).body(project);
    }
    @PatchMapping("/{id}") public ProjectQueries.ProjectView patch(@PathVariable UUID id, @RequestBody JsonNode body) {
        return commands.patch(id, ProjectInput.patch(body));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) {
        commands.delete(id);
        return ResponseEntity.noContent().build();
    }
}
