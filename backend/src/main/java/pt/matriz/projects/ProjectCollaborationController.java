package pt.matriz.projects;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.identity.UserProfile;
import pt.matriz.shared.RequestValidationException;

@RestController
@RequestMapping("/api/projetos/{id}/colaboradores")
public class ProjectCollaborationController {
    private final ProjectCollaboration collaboration;
    private final Validator validator;
    public ProjectCollaborationController(ProjectCollaboration collaboration, Validator validator) {this.collaboration=collaboration;this.validator=validator;}
    record Input(@NotBlank @Email String email) {}
    @GetMapping public List<UserProfile> list(@PathVariable UUID id) {return collaboration.list(id);}
    @PostMapping public ResponseEntity<UserProfile> add(@PathVariable UUID id,@RequestBody JsonNode body) {
        if(!body.isObject())throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        body.fieldNames().forEachRemaining(name->{if(!name.equals("email"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);});
        if(!body.hasNonNull("email")||!body.get("email").isTextual())throw new RequestValidationException("email","Indica um email válido.");
        String email=body.get("email").textValue();
        if(!validator.validate(new Input(email)).isEmpty())throw new RequestValidationException("email","Indica um email válido.");
        var user=collaboration.add(id,email);
        return ResponseEntity.created(URI.create("/api/projetos/"+id+"/colaboradores/"+user.id())).body(user);
    }
    @DeleteMapping("/{user}") public ResponseEntity<Void> remove(@PathVariable UUID id,@PathVariable UUID user) {
        collaboration.remove(id,user);return ResponseEntity.noContent().build();
    }
}
