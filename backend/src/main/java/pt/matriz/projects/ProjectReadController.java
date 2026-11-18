package pt.matriz.projects;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projetos")
public class ProjectReadController {
    private final ProjectQueries queries;
    public ProjectReadController(ProjectQueries queries) { this.queries = queries; }
    @GetMapping("/{id}") public ProjectQueries.ProjectView get(@PathVariable UUID id) { return queries.get(id); }
}
