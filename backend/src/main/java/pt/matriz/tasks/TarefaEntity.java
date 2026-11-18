package pt.matriz.tasks;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "tarefa")
public class TarefaEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "titulo", columnDefinition = "text")
    private String titulo;
    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;
    @Column(name = "estado", columnDefinition = "text")
    private String estado;
    @Column(name = "prioridade", columnDefinition = "text")
    private String prioridade;
    @Column(name = "prazo")
    private LocalDate prazo;
    @Column(name = "projeto_id")
    private UUID projetoId;
    @Column(name = "criador_id")
    private UUID criadorId;

    protected TarefaEntity() {}
}
