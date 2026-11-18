package pt.matriz.shared;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

public record ApiProblem(String type, String title, int status, String codigo, String detail) {
    public static ApiProblem of(int status, String code, String message) {
        return new ApiProblem("about:blank", message, status, code, message);
    }
    public static void write(ObjectMapper mapper, HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        mapper.writeValue(response.getWriter(), of(status, code, message));
    }
}
