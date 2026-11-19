package pt.matriz;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/** Falha temporária e deliberada da Ficha 05, isolada nesta branch de demonstração. */
class AAAFailureDemonstrationTest {
    @Test void controlledFailureStopsThePipeline() {
        // Arrange — resultado esperado deliberadamente incorreto para provar o gate da CI.
        int expected = 2;

        // Act — operação local sem serviços nem dados reais.
        int actual = 1;

        // Assert — este teste deve falhar; será removido no commit de recuperação.
        assertThat(actual).isEqualTo(expected);
    }
}
