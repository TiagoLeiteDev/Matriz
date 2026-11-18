package pt.matriz.tasks;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.shared.RequestValidationException;

public final class CollaborationInput {
    private CollaborationInput() {}
    private static String required(JsonNode body, String field) {
        if (!body.isObject()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        body.fieldNames().forEachRemaining(name -> {
            if (!name.equals(field)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });
        JsonNode value = body.get(field);
        if (value == null || !value.isTextual()) throw new RequestValidationException(field, "É necessário texto.");
        return value.textValue();
    }
    public static UUID assignee(JsonNode body) {
        String value = required(body, "utilizadorId");
        try {
            UUID id = UUID.fromString(value);
            if (!id.toString().equalsIgnoreCase(value)) throw new IllegalArgumentException();
            return id;
        } catch (IllegalArgumentException error) { throw new RequestValidationException("utilizadorId", "Identificador inválido."); }
    }
    public static String comment(JsonNode body) {
        String value = required(body, "conteudo");
        int length = value.codePointCount(0, value.length());
        if (length < 1 || length > 2000 || !java.util.regex.Pattern.compile("\\S", java.util.regex.Pattern.UNICODE_CHARACTER_CLASS).matcher(value).find())
            throw new RequestValidationException("conteudo", "Indica entre 1 e 2000 caracteres, não apenas espaços.");
        return value;
    }
}
