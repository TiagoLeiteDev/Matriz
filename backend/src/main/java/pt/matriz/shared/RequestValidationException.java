package pt.matriz.shared;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String field, String message) { super(field + ": " + message); }
}
