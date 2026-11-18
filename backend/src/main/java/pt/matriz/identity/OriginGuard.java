package pt.matriz.identity;

import java.io.IOException;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.matriz.shared.ApiProblem;

final class OriginGuard extends OncePerRequestFilter {
    private final Set<String> allowed;
    private final ObjectMapper mapper;
    OriginGuard(Set<String> allowed, ObjectMapper mapper) { this.allowed = allowed; this.mapper = mapper; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String origin = request.getHeader("Origin");
        boolean unsafe = !Set.of("GET", "HEAD", "OPTIONS", "TRACE").contains(request.getMethod());
        if (unsafe && origin != null && !allowed.contains(origin)) {
            ApiProblem.write(mapper, response, 403, "ACESSO_NEGADO", "Origem do pedido não permitida.");
            return;
        }
        chain.doFilter(request, response);
    }
}
