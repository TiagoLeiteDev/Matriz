package pt.matriz;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pt.matriz.shared.RequestValidationException;
import pt.matriz.tasks.TaskInput;
import static org.assertj.core.api.Assertions.*;

/** Ficha 04: T01–T03. Pure input validation; no Spring context or database. */
class TaskInputTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test void acceptsValidTitleWithoutChangingIt() {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var body = mapper.createObjectNode().put("titulo", "Preparar apresentação");

        // Act — executar a operação em teste.
        var input = TaskInput.create(body);

        // Assert — verificar o resultado.
        assertThat(input.titulo()).isEqualTo("Preparar apresentação");
    }

    @Test void acceptsOneNonWhitespaceCharacterAtMinimumBoundary() {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var body = mapper.createObjectNode().put("titulo", "X");

        // Act — executar a operação em teste.
        var input = TaskInput.create(body);

        // Assert — verificar o resultado.
        assertThat(input.titulo()).isEqualTo("X");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"titulo\":null}", "{\"titulo\":12}",
        "{\"titulo\":\"\"}", "{\"titulo\":\"   \"}"})
    void rejectsInvalidTitleOnCreation(String json) throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var body = mapper.readTree(json);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        assertThatThrownBy(() -> TaskInput.create(body))
            .isInstanceOf(RequestValidationException.class)
            .hasMessageStartingWith("titulo:");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"titulo\":null}", "{\"titulo\":\"\"}", "{\"titulo\":\"   \"}"})
    void rejectsExplicitInvalidTitleOnPatch(String json) throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var body = mapper.readTree(json);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        assertThatThrownBy(() -> TaskInput.patch(body))
            .isInstanceOf(RequestValidationException.class)
            .hasMessageStartingWith("titulo:");
    }

    @Test void patchDoesNotRequireOmittedTitle() {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var body = mapper.createObjectNode().put("estado", "DONE");

        // Act — executar a operação em teste.
        var input = TaskInput.patch(body);

        // Assert — verificar o resultado.
        assertThat(input.hasTitle()).isFalse();
        assertThat(input.titulo()).isNull();
        assertThat(input.hasState()).isTrue();
        assertThat(input.estado()).isEqualTo("DONE");
    }
}
