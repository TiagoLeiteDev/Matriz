package pt.matriz;

import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
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
class InfrastructureTest {
    @Container
    static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:17.9-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", DB::getJdbcUrl);
        properties.add("spring.datasource.username", DB::getUsername);
        properties.add("spring.datasource.password", DB::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired Flyway flyway;
    @Autowired PasswordEncoder encoder;
    @Autowired PlatformTransactionManager transactions;
    @Autowired MockMvc mvc;

    @BeforeEach
    void resetIsolatedContainer() {
        // Only the disposable Testcontainers database, never the development volume.
        jdbc.execute("TRUNCATE utilizador, projeto, tarefa, comentario, colaboracao_projeto, atribuicao_tarefa CASCADE");
    }

    void seed() {
        new TransactionTemplate(transactions).executeWithoutResult(tx ->
            new DemoSeeder(jdbc, encoder, "FictitiousTestPassword42!").run(null));
    }

    int count(String table) { return jdbc.queryForObject("SELECT count(*) FROM " + table, Integer.class); }

    @Test void migrationsAreVersionedAndNormalStartupDoesNotSeed() {
        assertThat(flyway.info().applied()).hasSize(1);
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(count("utilizador")).isZero();
    }

    @Test void seedContainsSixEntitiesAndDifferentAccessRelationships() {
        seed();
        assertThat(count("utilizador")).isEqualTo(2);
        assertThat(count("projeto")).isEqualTo(2);
        assertThat(count("tarefa")).isEqualTo(3);
        assertThat(count("comentario")).isEqualTo(1);
        assertThat(count("atribuicao_tarefa")).isEqualTo(2);
        assertThat(count("colaboracao_projeto")).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT proprietario_id FROM projeto WHERE id = ?", UUID.class, PROJETO_BRUNO)).isEqualTo(BRUNO);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM colaboracao_projeto WHERE projeto_id = ? AND utilizador_id = ?",
            Integer.class, PROJETO_BRUNO, ANA)).isZero();
        assertThat(jdbc.queryForObject("SELECT projeto_id FROM tarefa WHERE id = ?", UUID.class, TAREFA_PESSOAL)).isNull();
        String hash = jdbc.queryForObject("SELECT password_hash FROM utilizador WHERE id = ?", String.class, ANA);
        assertThat(hash).doesNotContain("FictitiousTestPassword42!");
        assertThat(encoder.matches("FictitiousTestPassword42!", hash)).isTrue();
    }

    @Test void repeatedSeedPreservesEditsRemovalsAndPasswordHashes() {
        seed();
        String hash = jdbc.queryForObject("SELECT password_hash FROM utilizador WHERE id = ?", String.class, ANA);
        jdbc.update("UPDATE tarefa SET titulo = 'Alteração preservada' WHERE id = ?", TAREFA_PARTILHADA);
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id = ?", PROJETO_ANA);
        jdbc.update("DELETE FROM comentario");
        seed();
        assertThat(count("utilizador")).isEqualTo(2);
        assertThat(count("tarefa")).isEqualTo(3);
        assertThat(count("colaboracao_projeto")).isZero();
        assertThat(count("comentario")).isZero();
        assertThat(jdbc.queryForObject("SELECT titulo FROM tarefa WHERE id = ?", String.class, TAREFA_PARTILHADA)).isEqualTo("Alteração preservada");
        assertThat(jdbc.queryForObject("SELECT password_hash FROM utilizador WHERE id = ?", String.class, ANA)).isEqualTo(hash);
    }

    @Test void seedRollsBackOnCollisionWithoutDeletingExistingData() {
        UUID existing = UUID.randomUUID();
        jdbc.update("INSERT INTO utilizador VALUES (?, 'Conta existente', 'bruno@example.test', 'hash-existente')", existing);
        assertThatThrownBy(this::seed).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(count("utilizador")).isEqualTo(1);
        assertThat(count("projeto")).isZero();
        assertThat(jdbc.queryForObject("SELECT id FROM utilizador", UUID.class)).isEqualTo(existing);
    }

    @Test void seedRejectsProjectIdCollisionWithoutGrantingAccessOrLeavingDemoAccounts() {
        UUID existing = UUID.randomUUID();
        jdbc.update("INSERT INTO utilizador VALUES (?, 'Conta existente', 'existing@example.test', 'hash-existente')", existing);
        jdbc.update("INSERT INTO projeto (id, nome, proprietario_id) VALUES (?, 'Projeto existente', ?)", PROJETO_ANA, existing);
        assertThatThrownBy(this::seed).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(count("utilizador")).isEqualTo(1);
        assertThat(count("projeto")).isEqualTo(1);
        assertThat(count("colaboracao_projeto")).isZero();
        assertThat(jdbc.queryForObject("SELECT proprietario_id FROM projeto WHERE id = ?", UUID.class, PROJETO_ANA)).isEqualTo(existing);
    }

    @Test void schemaRejectsWhitespaceInvalidEnumsOrphansAndDuplicateAssignments() {
        seed();
        assertThatThrownBy(() -> jdbc.update("UPDATE projeto SET nome = E' \t\n' WHERE id = ?", PROJETO_ANA)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE tarefa SET estado = 'Inválido' WHERE id = ?", TAREFA_PARTILHADA)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE tarefa SET prioridade = 'Inválida' WHERE id = ?", TAREFA_PARTILHADA)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE tarefa SET projeto_id = ? WHERE id = ?", UUID.randomUUID(), TAREFA_PARTILHADA)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("INSERT INTO atribuicao_tarefa VALUES (?, ?)", TAREFA_PARTILHADA, ANA)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void deletingProjectCascadesOnlyItsDependants() {
        seed();
        jdbc.update("DELETE FROM projeto WHERE id = ?", PROJETO_ANA);
        assertThat(count("projeto")).isEqualTo(1);
        assertThat(count("tarefa")).isEqualTo(2);
        assertThat(count("comentario")).isZero();
        assertThat(count("atribuicao_tarefa")).isZero();
        assertThat(count("colaboracao_projeto")).isZero();
        assertThat(count("utilizador")).isEqualTo(2);
    }

    @Test void databaseRoundTripRetainsOptionalFieldsAndPastDates() {
        seed();
        jdbc.update("UPDATE tarefa SET prioridade = NULL, prazo = DATE '2020-01-01' WHERE id = ?", TAREFA_PESSOAL);
        assertThat(jdbc.queryForObject("SELECT prazo::text FROM tarefa WHERE id = ?", String.class, TAREFA_PESSOAL)).isEqualTo("2020-01-01");
    }

    @Test void publicHealthHasNoDetailsAndBusinessApiRemainsBlocked() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP")).andExpect(jsonPath("$.components").doesNotExist());
        mvc.perform(get("/api/projetos")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/projetos").contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
    }
}
