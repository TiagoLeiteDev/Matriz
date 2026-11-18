package pt.matriz.tasks;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record TaskFilter(String q, UUID projetoId, boolean semProjeto, String estado, String prioridade,
                         boolean semPrioridade, LocalDate prazoDe, LocalDate prazoAte) {
    public TaskFilter {
        if (projetoId != null && semProjeto || prioridade != null && semPrioridade
                || estado != null && !TaskInput.STATES.contains(estado)
                || prioridade != null && !TaskInput.PRIORITIES.contains(prioridade)
                || prazoDe != null && prazoAte != null && prazoDe.isAfter(prazoAte)) throw bad();
    }
    public static ResponseStatusException bad() { return new ResponseStatusException(HttpStatus.BAD_REQUEST); }
    public static boolean bool(String value) {
        if (value == null) return false;
        if (!value.equals("true") && !value.equals("false")) throw bad();
        return value.equals("true");
    }
    public static UUID id(String value) {
        if (value == null) return null;
        try { UUID id = UUID.fromString(value); if (!id.toString().equalsIgnoreCase(value)) throw bad(); return id; }
        catch (IllegalArgumentException error) { throw bad(); }
    }
    public static LocalDate date(String value) {
        if (value == null) return null;
        try {
            if (!value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) throw bad();
            LocalDate date = LocalDate.parse(value); if (date.getYear() < 1) throw bad(); return date;
        } catch (RuntimeException error) { throw bad(); }
    }
}
