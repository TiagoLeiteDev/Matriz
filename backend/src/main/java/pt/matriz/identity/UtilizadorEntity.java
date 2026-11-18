package pt.matriz.identity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "utilizador")
public class UtilizadorEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "nome", columnDefinition = "text")
    private String nome;
    @Column(name = "email", columnDefinition = "text")
    private String email;
    @Column(name = "password_hash", columnDefinition = "text")
    private String passwordHash;

    protected UtilizadorEntity() {}
}
