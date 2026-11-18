package pt.matriz.identity;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.userdetails.User;

public final class AccountPrincipal extends User {
    private final UUID id;
    private final String nome;
    public AccountPrincipal(UUID id, String nome, String email, String hash) {
        super(email, hash, List.of());
        this.id = id;
        this.nome = nome;
    }
    public UUID id() { return id; }
    public String nome() { return nome; }
}
