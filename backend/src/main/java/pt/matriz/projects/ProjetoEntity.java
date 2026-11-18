package pt.matriz.projects;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "projeto")
public class ProjetoEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "nome", columnDefinition = "text")
    private String nome;
    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;
    @Column(name = "arquivado")
    private boolean arquivado;
    @Column(name = "proprietario_id")
    private UUID proprietarioId;

    protected ProjetoEntity() {}
}
