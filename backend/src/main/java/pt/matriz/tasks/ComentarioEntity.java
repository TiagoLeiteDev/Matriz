package pt.matriz.tasks;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "comentario")
public class ComentarioEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "conteudo", columnDefinition = "text")
    private String conteudo;
    @Column(name = "tarefa_id")
    private UUID tarefaId;
    @Column(name = "autor_id")
    private UUID autorId;
    @Column(name = "criado_em")
    private Instant criadoEm;

    protected ComentarioEntity() {}
}
