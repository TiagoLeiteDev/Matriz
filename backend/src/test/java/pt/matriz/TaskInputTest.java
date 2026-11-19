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

    @ParameterizedTest
    @ValueSource(strings = {"\u0085", "\u00a0", "\u2003", "\t\n", " \u0085\u00a0"})
    void rejectsUnicodeWhitespaceOnlyInCreateAndPatch(String title) {
        // Arrange — conservar a definição Unicode já usada pelo backend.
        var body = mapper.createObjectNode().put("titulo", title);

        // Act & Assert — criação e edição rejeitam títulos apenas com espaços Unicode.
        assertThatThrownBy(() -> TaskInput.create(body))
            .isInstanceOf(RequestValidationException.class).hasMessageStartingWith("titulo:");
        assertThatThrownBy(() -> TaskInput.patch(body))
            .isInstanceOf(RequestValidationException.class).hasMessageStartingWith("titulo:");
    }

    @ParameterizedTest
    @ValueSource(strings = {"\ufeff", " \u0085X ", "\u2003Tarefa\u00a0"})
    void preservesUnicodeTitlesWithNonWhitespaceContent(String title) {
        // Arrange — FEFF não é White_Space na regra existente do backend.
        var body = mapper.createObjectNode().put("titulo", title);

        // Act — validar criação e edição sem normalizar o conteúdo.
        var created = TaskInput.create(body);
        var patched = TaskInput.patch(body);

        // Assert — aceitar conteúdo não branco e preservá-lo exatamente.
        assertThat(created.titulo()).isEqualTo(title);
        assertThat(patched.titulo()).isEqualTo(title);
        assertThat(patched.hasTitle()).isTrue();
    }

}
