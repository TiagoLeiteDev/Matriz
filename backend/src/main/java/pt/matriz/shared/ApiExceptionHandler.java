package pt.matriz.shared;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private ResponseEntity<ApiProblem> problem(int status, String code, String message) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .header("Cache-Control", "no-store").body(ApiProblem.of(status, code, message));
    }
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiProblem> unauthenticated(AuthenticationException error) {
        return problem(401, "NAO_AUTENTICADO", "Credenciais inválidas ou sessão não válida.");
    }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiProblem> forbidden(AccessDeniedException error) {
        return problem(403, "ACESSO_NEGADO", "Não tens permissão para esta operação.");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiProblem> validation(MethodArgumentNotValidException error) {
        return problem(422, "VALIDACAO", "Verifica os campos do pedido.");
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiProblem> malformed(Exception error) {
        return problem(400, "PEDIDO_INVALIDO", "O formato do pedido é inválido.");
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiProblem> resource(ResponseStatusException error) {
        int status = error.getStatusCode().value();
        return problem(status, status == 404 ? "NAO_ENCONTRADO" : "CONFLITO",
            status == 404 ? "Recurso não encontrado." : "O estado do recurso impede esta operação.");
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiProblem> unexpected(Exception error) {
        return problem(500, "ERRO_INTERNO", "Não foi possível concluir a operação.");
    }
}
