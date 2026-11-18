package pt.matriz.tasks;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "atribuicao_tarefa")
public class AtribuicaoTarefaEntity {
    @EmbeddedId
    private Key id;
    protected AtribuicaoTarefaEntity() {}

    @Embeddable
    public static class Key implements Serializable {
        @Column(name = "tarefa_id")
        private UUID recursoId;
        @Column(name = "utilizador_id")
        private UUID utilizadorId;
        protected Key() {}
        @Override public boolean equals(Object other) {
            return other instanceof Key key && Objects.equals(recursoId, key.recursoId)
                && Objects.equals(utilizadorId, key.utilizadorId);
        }
        @Override public int hashCode() { return Objects.hash(recursoId, utilizadorId); }
    }
}
