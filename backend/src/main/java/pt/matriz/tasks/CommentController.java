package pt.matriz.tasks;

import java.net.URI;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarefas/{id}/comentarios")
public class CommentController {
    private final CommentCommands commands;
    public CommentController(CommentCommands commands) { this.commands = commands; }
    @PostMapping public ResponseEntity<TaskQueries.CommentView> add(@PathVariable UUID id, @RequestBody JsonNode body) {
        var comment = commands.add(id, CollaborationInput.comment(body));
        return ResponseEntity.created(URI.create("/api/tarefas/" + id + "/comentarios/" + comment.id())).body(comment);
    }
}
