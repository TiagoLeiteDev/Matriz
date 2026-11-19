package pt.matriz;

import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.matriz.bootstrap.DemoSeeder;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pt.matriz.bootstrap.DemoSeeder.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProjectCrudTest {
    static final String PASSWORD = "FictitiousTestPassword42!";
    @Container static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:17.9-alpine");
    @DynamicPropertySource static void database(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", DB::getJdbcUrl);
        properties.add("spring.datasource.username", DB::getUsername);
        properties.add("spring.datasource.password", DB::getPassword);
    }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;
    @Autowired PlatformTransactionManager transactions;
    @BeforeEach void fixtures() {
        jdbc.execute("TRUNCATE utilizador, projeto, tarefa, comentario, colaboracao_projeto, atribuicao_tarefa CASCADE");
        new TransactionTemplate(transactions).executeWithoutResult(tx -> new DemoSeeder(jdbc, encoder, PASSWORD).run(null));
    }
    record Client(MockHttpSession session, String token) {}
    Client login(String email) throws Exception {
        var prepare = mvc.perform(get("/api/sessao/csrf")).andExpect(status().isOk()).andReturn();
        var session = (MockHttpSession) prepare.getRequest().getSession(false);
        String token = mapper.readTree(prepare.getResponse().getContentAsString()).get("token").asText();
        mvc.perform(post("/api/sessao").session(session).header("X-CSRF-Token", token).contentType("application/json")
            .content(mapper.writeValueAsString(java.util.Map.of("email", email, "palavraPasse", PASSWORD)))).andExpect(status().isOk());
        var fresh = mvc.perform(get("/api/sessao/csrf").session(session)).andExpect(status().isOk()).andReturn();
        return new Client(session, mapper.readTree(fresh.getResponse().getContentAsString()).get("token").asText());
    }
    MvcResult create(Client actor, String json, int expected) throws Exception {
        return mvc.perform(post("/api/projetos").session(actor.session()).header("X-CSRF-Token", actor.token())
            .contentType("application/json").content(json)).andExpect(status().is(expected)).andReturn();
    }
    MvcResult update(Client actor, UUID id, String json, int expected) throws Exception {
        return mvc.perform(patch("/api/projetos/" + id).session(actor.session()).header("X-CSRF-Token", actor.token())
            .contentType("application/json").content(json)).andExpect(status().is(expected)).andReturn();
    }
    UUID id(MvcResult result) throws Exception { return UUID.fromString(mapper.readTree(result.getResponse().getContentAsString()).get("id").asText()); }
    int count(String table) { return jdbc.queryForObject("SELECT count(*) FROM " + table, Integer.class); }

    @Test void createInfersOwnerAndPersistsForNewSession() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var result = create(ana, "{\"nome\":\"X\"}", 201);

        // Arrange — preparar o próximo cenário/dados.
        UUID id = id(result);

        // Assert — verificar o resultado.
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/projetos/" + id);
        assertThat(jdbc.queryForObject("SELECT proprietario_id FROM projeto WHERE id = ?", UUID.class, id)).isEqualTo(ANA);
        assertThat(mapper.readTree(result.getResponse().getContentAsString()).get("arquivado").asBoolean()).isFalse();

        // Arrange — preparar o próximo cenário/dados.
        var again = login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos/" + id).session(again.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("X")).andExpect(jsonPath("$.descricao").isEmpty())
            .andExpect(jsonPath("$.progresso.totalTarefas").value(0)).andExpect(jsonPath("$.proprietario.id").value(ANA.toString()));
    }

    @Test void contractDoesNotInventExampleMaximumsOrTrimValidText() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");
        String name = "  " + "N".repeat(151) + "  ", description = "D".repeat(2501);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var result = create(ana, mapper.writeValueAsString(java.util.Map.of("nome", name, "descricao", description)), 201);

        // Assert — verificar o resultado.
        var body = mapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("nome").asText()).isEqualTo(name);
        assertThat(body.get("descricao").asText()).isEqualTo(description);
    }

    @Test void invalidBodiesAreRejectedWithoutChangingData() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");
        for (String json : new String[]{"{}", "{\"nome\":null}", "{\"nome\":\" \t\"}".replace("\t", "\\t"),
            "{\"nome\":123}", "{\"nome\":\"Válido\",\"descricao\":false}", "[]"}) create(ana, json, 422);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        create(ana, "{", 400);
        create(ana, "{\"nome\":\"Válido\",\"proprietarioId\":\"" + BRUNO + "\"}", 400);

        // Assert — verificar o resultado.
        assertThat(count("projeto")).isEqualTo(2);
    }

    @Test void patchPreservesOmittedFieldsAndNullOnlyClearsDescription() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        UUID id = id(create(ana, "{\"nome\":\"Inicial\",\"descricao\":\"Conservar\"}", 201));
        var changed = mapper.readTree(update(ana, id, "{\"nome\":\"Alterado\"}", 200).getResponse().getContentAsString());

        // Assert — verificar o resultado.
        assertThat(changed.get("descricao").asText()).isEqualTo("Conservar");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var cleared = mapper.readTree(update(ana, id, "{\"descricao\":null}", 200).getResponse().getContentAsString());

        // Assert — verificar o resultado.
        assertThat(cleared.get("nome").asText()).isEqualTo("Alterado");
        assertThat(cleared.get("descricao").isNull()).isTrue();

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        update(ana, id, "{\"descricao\":\"\"}", 200);

        // Assert — verificar o resultado.
        assertThat(jdbc.queryForObject("SELECT descricao FROM projeto WHERE id = ?", String.class, id)).isEmpty();

        // Act & Assert — executar o percurso/pedido e verificar o resultado.
        for (String json : new String[]{"{}", "{\"nome\":null}", "{\"nome\":\"  \"}", "{\"arquivado\":null}",
            "{\"arquivado\":\"true\"}", "{\"descricao\":{}}"}) update(ana, id, json, 422);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        update(ana, id, "{\"proprietarioId\":\"" + BRUNO + "\"}", 400);

        // Assert — verificar o resultado.
        assertThat(jdbc.queryForObject("SELECT proprietario_id FROM projeto WHERE id = ?", UUID.class, id)).isEqualTo(ANA);
    }

    @Test void listOnlyContainsAccessibleProjectsAndRevocationTakesEffectImmediately() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test"); var bruno = login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos").session(ana.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(PROJETO_ANA.toString()));
        mvc.perform(get("/api/projetos").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        jdbc.update("INSERT INTO colaboracao_projeto VALUES (?, ?)", PROJETO_ANA, ANA);
        mvc.perform(get("/api/projetos").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id = ? AND utilizador_id = ?", PROJETO_ANA, BRUNO);
        mvc.perform(get("/api/projetos").session(bruno.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(PROJETO_BRUNO.toString()));
    }

    @Test void searchIgnoresCaseAndAccentsAndTreatsWildcardsAsLiteralText() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Arrange — preparar os dados do cenário.
        jdbc.update("UPDATE projeto SET nome = 'Preparar apresentação' WHERE id IN (?, ?)", PROJETO_ANA, PROJETO_BRUNO);
        mvc.perform(get("/api/projetos").session(ana.session()).param("q", "APRESENTACAO")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(PROJETO_ANA.toString()));
        create(ana, "{\"nome\":\"100%_Concluído O'Brien\"}", 201);

        // Arrange — preparar o próximo cenário/dados.
        for (String search : new String[]{"%", "_", "o'brien", "CONCLUIDO"}) {
            mvc.perform(get("/api/projetos").session(ana.session()).param("q", search)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        }

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos").session(ana.session()).param("q", "' OR 1=1 --")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/projetos").session(ana.session()).param("q", "")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test void archiveRestorationPreservesDataAndFiltersListsStrictly() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        update(ana, PROJETO_ANA, "{\"arquivado\":true}", 200);
        mvc.perform(get("/api/projetos").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/projetos").session(ana.session()).param("arquivado", "true")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));

        // Assert — verificar o resultado.
        assertThat(count("tarefa")).isEqualTo(3);
        assertThat(count("comentario")).isEqualTo(1);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        update(ana, PROJETO_ANA, "{\"arquivado\":false}", 200);
        mvc.perform(get("/api/projetos").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));

        // Arrange — preparar o próximo cenário/dados.
        for (String invalid : new String[]{"yes", "1", "null", ""}) {
            mvc.perform(get("/api/projetos").session(ana.session()).param("arquivado", invalid)).andExpect(status().isBadRequest());
        }
    }

    @Test void collaboratorAndUnrelatedUserCannotEditOrDeleteProjects() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test"); var bruno = login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        update(bruno, PROJETO_ANA, "{\"nome\":\"Ataque\"}", 403);
        update(ana, PROJETO_BRUNO, "{\"nome\":\"Ataque\"}", 403);

        // Arrange — preparar o próximo cenário/dados.
        for (var pair : java.util.List.of(java.util.Map.entry(bruno, PROJETO_ANA), java.util.Map.entry(ana, PROJETO_BRUNO))) {
            mvc.perform(delete("/api/projetos/" + pair.getValue()).session(pair.getKey().session()).header("X-CSRF-Token", pair.getKey().token()))
                .andExpect(status().isForbidden());
        }

        // Assert — verificar o resultado.
        assertThat(count("projeto")).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT nome FROM projeto WHERE id = ?", String.class, PROJETO_ANA)).isEqualTo("Projeto partilhado da Ana");
    }

    @Test void allWritesRequireCsrfAndUnauthenticatedRequestsAreDenied() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Act — executar a operação em teste.
        mvc.perform(post("/api/projetos").session(ana.session()).contentType("application/json").content("{\"nome\":\"X\"}"))
            .andExpect(status().isForbidden());
        mvc.perform(patch("/api/projetos/" + PROJETO_ANA).session(ana.session()).contentType("application/json").content("{\"nome\":\"X\"}"))
            .andExpect(status().isForbidden());

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(delete("/api/projetos/" + PROJETO_ANA).session(ana.session())).andExpect(status().isForbidden());
        mvc.perform(get("/api/projetos")).andExpect(status().isUnauthorized());
    }

    @Test void deleteCascadesWithoutOrphansAndDoesNotReappearOnSeed() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Act — executar a operação em teste.
        mvc.perform(delete("/api/projetos/" + PROJETO_ANA).session(ana.session()).header("X-CSRF-Token", ana.token()))
            .andExpect(status().isNoContent()).andExpect(content().string(""));

        // Assert — verificar o resultado.
        assertThat(count("projeto")).isEqualTo(1); assertThat(count("tarefa")).isEqualTo(2);
        assertThat(count("comentario")).isZero(); assertThat(count("atribuicao_tarefa")).isZero();
        assertThat(count("colaboracao_projeto")).isZero(); assertThat(count("utilizador")).isEqualTo(2);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos/" + PROJETO_ANA).session(ana.session())).andExpect(status().isNotFound());

        // Arrange — preparar o próximo cenário/dados.
        new TransactionTemplate(transactions).executeWithoutResult(tx -> new DemoSeeder(jdbc, encoder, PASSWORD).run(null));

        // Assert — verificar o resultado.
        assertThat(count("projeto")).isEqualTo(1);
    }

    @Test void failedCascadeRollsBackEntireProjectAndDoesNotExposeDatabaseDetails() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");
        jdbc.execute("CREATE FUNCTION reject_comment_delete() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'forced failure'; END; $$");
        jdbc.execute("CREATE TRIGGER reject_delete BEFORE DELETE ON comentario FOR EACH ROW EXECUTE FUNCTION reject_comment_delete()");
        try {
            mvc.perform(delete("/api/projetos/" + PROJETO_ANA).session(ana.session()).header("X-CSRF-Token", ana.token()))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.codigo").value("ERRO_INTERNO"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("forced failure"))));
            assertThat(count("projeto")).isEqualTo(2); assertThat(count("tarefa")).isEqualTo(3);
            assertThat(count("comentario")).isEqualTo(1); assertThat(count("atribuicao_tarefa")).isEqualTo(2);
            assertThat(count("colaboracao_projeto")).isEqualTo(1); assertThat(count("utilizador")).isEqualTo(2);
        } finally { jdbc.execute("DROP TRIGGER reject_delete ON comentario"); jdbc.execute("DROP FUNCTION reject_comment_delete()"); }
    }

    @Test void missingAndMalformedResourceIdsAreRejected() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        update(ana, UUID.randomUUID(), "{\"nome\":\"X\"}", 404);
        mvc.perform(delete("/api/projetos/" + UUID.randomUUID()).session(ana.session()).header("X-CSRF-Token", ana.token())).andExpect(status().isNotFound());
        mvc.perform(delete("/api/projetos/not-a-uuid").session(ana.session()).header("X-CSRF-Token", ana.token())).andExpect(status().isBadRequest());

        // Assert — verificar o resultado.
        assertThat(count("projeto")).isEqualTo(2);
    }
}
