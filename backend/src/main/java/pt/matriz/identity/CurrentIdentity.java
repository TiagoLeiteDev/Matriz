package pt.matriz.identity;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CurrentIdentity {
    public UUID id() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AccountPrincipal user)) {
            throw new AuthenticationCredentialsNotFoundException("Sessão necessária");
        }
        return user.id();
    }
}
