package pt.matriz.projects;

import java.util.Set;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.shared.RequestValidationException;

public final class ProjectInput {
    private static final Pattern NON_WHITE = Pattern.compile("\\S", Pattern.UNICODE_CHARACTER_CLASS);
    private ProjectInput() {}
    public record Create(String nome, String descricao) {
        public Create { requireName(nome); }
    }
    public record Patch(boolean hasName, String nome, boolean hasDescription, String descricao,
                        boolean hasArchived, Boolean arquivado) {
        public Patch {
            if (!hasName && !hasDescription && !hasArchived) throw new RequestValidationException("pedido", "Indica pelo menos um campo para alterar.");
            if (hasName) requireName(nome);
            if (hasArchived && arquivado == null) throw new RequestValidationException("arquivado", "Indica true ou false.");
        }
    }
    private static void requireName(String value) {
        if (value == null || !NON_WHITE.matcher(value).find()) throw new RequestValidationException("nome", "Indica um nome que não seja vazio ou apenas espaços.");
    }
    private static void object(JsonNode body, Set<String> allowed) {
        if (!body.isObject()) throw new RequestValidationException("pedido", "É necessário um objeto JSON.");
        body.fieldNames().forEachRemaining(field -> {
            if (!allowed.contains(field)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });
    }
    private static String text(JsonNode value, String field) {
        if (value == null || value.isNull()) return null;
        if (!value.isTextual()) throw new RequestValidationException(field, "É necessário texto.");
        return value.textValue();
    }
    public static Create create(JsonNode body) {
        object(body, Set.of("nome", "descricao"));
        return new Create(text(body.get("nome"), "nome"), text(body.get("descricao"), "descricao"));
    }
    public static Patch patch(JsonNode body) {
        object(body, Set.of("nome", "descricao", "arquivado"));
        Boolean archived = null;
        if (body.has("arquivado")) {
            if (!body.get("arquivado").isBoolean()) throw new RequestValidationException("arquivado", "Indica true ou false.");
            archived = body.get("arquivado").booleanValue();
        }
        return new Patch(body.has("nome"), text(body.get("nome"), "nome"), body.has("descricao"),
            text(body.get("descricao"), "descricao"), body.has("arquivado"), archived);
    }
}
