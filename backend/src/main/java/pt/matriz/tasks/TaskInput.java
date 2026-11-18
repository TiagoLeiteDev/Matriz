package pt.matriz.tasks;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.shared.RequestValidationException;

public final class TaskInput {
    public static final Set<String> STATES = Set.of("TODO", "IN_PROGRESS", "DONE");
    public static final Set<String> PRIORITIES = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> FIELDS = Set.of("titulo", "descricao", "estado", "prioridade", "prazo", "projetoId");
    private static final Pattern NON_WHITE = Pattern.compile("\\S", Pattern.UNICODE_CHARACTER_CLASS);
    private TaskInput() {}
    public record Create(String titulo, String descricao, String estado, String prioridade, LocalDate prazo, UUID projetoId) {}
    public record Patch(boolean hasTitle, String titulo, boolean hasDescription, String descricao,
                        boolean hasState, String estado, boolean hasPriority, String prioridade,
                        boolean hasDeadline, LocalDate prazo, boolean hasProject, UUID projetoId) {}
    private static void object(JsonNode body) {
        if (!body.isObject()) throw invalid("pedido", "É necessário um objeto JSON.");
        body.fieldNames().forEachRemaining(field -> {
            if (!FIELDS.contains(field)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });
    }
    private static RequestValidationException invalid(String field, String message) { return new RequestValidationException(field, message); }
    private static String text(JsonNode body, String field, boolean required) {
        JsonNode value = body.get(field);
        if (value == null || value.isNull()) {
            if (required) throw invalid(field, "É obrigatório.");
            return null;
        }
        if (!value.isTextual()) throw invalid(field, "É necessário texto.");
        String text = value.textValue();
        if (required && !NON_WHITE.matcher(text).find()) throw invalid(field, "Não pode ser vazio ou apenas espaços.");
        return text;
    }
    private static String enumeration(JsonNode body, String field, Set<String> values, boolean required) {
        String value = text(body, field, required);
        if (value != null && !values.contains(value)) throw invalid(field, "Escolhe um dos valores permitidos.");
        return value;
    }
    private static LocalDate date(JsonNode body) {
        String value = text(body, "prazo", false);
        if (value == null) return null;
        try {
            if (!value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) throw new IllegalArgumentException();
            LocalDate date = LocalDate.parse(value);
            if (date.getYear() < 1) throw new IllegalArgumentException();
            return date;
        } catch (RuntimeException error) { throw invalid("prazo", "Indica uma data válida no formato AAAA-MM-DD."); }
    }
    private static UUID project(JsonNode body) {
        String value = text(body, "projetoId", false);
        if (value == null) return null;
        try {
            UUID id = UUID.fromString(value);
            if (!id.toString().equalsIgnoreCase(value)) throw new IllegalArgumentException();
            return id;
        } catch (IllegalArgumentException error) { throw invalid("projetoId", "Identificador inválido."); }
    }
    public static Create create(JsonNode body) {
        object(body);
        return new Create(text(body, "titulo", true), text(body, "descricao", false),
            body.has("estado") ? enumeration(body, "estado", STATES, true) : "TODO",
            body.has("prioridade") ? enumeration(body, "prioridade", PRIORITIES, false) : "MEDIUM", date(body), project(body));
    }
    public static Patch patch(JsonNode body) {
        object(body);
        if (body.isEmpty()) throw invalid("pedido", "Indica pelo menos um campo para alterar.");
        return new Patch(body.has("titulo"), text(body, "titulo", body.has("titulo")),
            body.has("descricao"), text(body, "descricao", false), body.has("estado"),
            enumeration(body, "estado", STATES, body.has("estado")), body.has("prioridade"),
            enumeration(body, "prioridade", PRIORITIES, false), body.has("prazo"), date(body), body.has("projetoId"), project(body));
    }
}
