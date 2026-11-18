package pt.matriz.identity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessao")
public class SessionController {
    private final AuthenticationManager manager;
    private final SessionAuthenticationStrategy strategy;
    private final HttpSessionSecurityContextRepository contexts;
    private final HttpSessionCsrfTokenRepository csrf;
    private final CurrentIdentity identity;
    private final AccountRepository accounts;
    private final boolean secure;
    public SessionController(AuthenticationManager manager, SessionAuthenticationStrategy strategy,
            HttpSessionSecurityContextRepository contexts, HttpSessionCsrfTokenRepository csrf,
            CurrentIdentity identity, AccountRepository accounts,
            @Value("${server.servlet.session.cookie.secure:false}") boolean secure) {
        this.manager = manager; this.strategy = strategy; this.contexts = contexts;
        this.csrf = csrf; this.identity = identity; this.accounts = accounts; this.secure = secure;
    }
    // A record's generated toString would expose the submitted password.
    public static final class Credentials {
        @NotBlank @Email public String email;
        @NotEmpty public String palavraPasse;
    }
    public record CsrfResponse(String token) {}
    @GetMapping("/csrf") public CsrfResponse csrf(CsrfToken token) { return new CsrfResponse(token.getToken()); }
    @PostMapping public UserProfile login(@Valid @RequestBody Credentials credentials,
            HttpServletRequest request, HttpServletResponse response) {
        var authentication = manager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(credentials.email, credentials.palavraPasse));
        strategy.onAuthentication(authentication, request, response);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        return accounts.profile(identity.id());
    }
    @GetMapping public UserProfile profile() { return accounts.profile(identity.id()); }
    @DeleteMapping public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        csrf.saveToken(null, request, response);
        var handler = new SecurityContextLogoutHandler();
        handler.setSecurityContextRepository(contexts);
        handler.logout(request, response, auth);
        response.addHeader("Set-Cookie", ResponseCookie.from("matriz_session", "")
            .path("/").httpOnly(true).secure(secure).sameSite("Lax").maxAge(0).build().toString());
        return ResponseEntity.noContent().build();
    }
}
