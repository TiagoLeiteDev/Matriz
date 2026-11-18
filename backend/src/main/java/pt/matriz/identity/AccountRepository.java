package pt.matriz.identity;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
    private final JdbcTemplate jdbc;
    public AccountRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public AccountPrincipal credentials(String email) {
        // Exact lookup: no unapproved email normalization or global roles.
        return jdbc.query("SELECT id, nome, email, password_hash FROM utilizador WHERE email = ?",
            (rs, row) -> new AccountPrincipal(rs.getObject("id", UUID.class), rs.getString("nome"),
                rs.getString("email"), rs.getString("password_hash")), email)
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
    }
    public UserProfile profile(UUID id) {
        return jdbc.query("SELECT id, nome, email FROM utilizador WHERE id = ?",
            (rs, row) -> new UserProfile(rs.getObject("id", UUID.class), rs.getString("nome"), rs.getString("email")), id)
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Sessão inválida"));
    }
}
