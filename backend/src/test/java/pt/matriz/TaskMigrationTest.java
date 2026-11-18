package pt.matriz;

import java.util.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.*;

@Testcontainers
class TaskMigrationTest {
    @Container static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:17.9-alpine");
    @Test void migratesExistingV1DataWithoutLosingIdsNullsOrRelations() {
        Flyway.configure().dataSource(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword()).target("1").load().migrate();
        var jdbc = new JdbcTemplate(new DriverManagerDataSource(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword()));
        UUID owner = UUID.randomUUID(), member = UUID.randomUUID(), project = UUID.randomUUID();
        jdbc.update("INSERT INTO utilizador VALUES (?, 'Owner Demo', 'owner@example.test', 'fictitious-hash')", owner);
        jdbc.update("INSERT INTO utilizador VALUES (?, 'Member Demo', 'member@example.test', 'fictitious-hash')", member);
        jdbc.update("INSERT INTO projeto (id,nome,proprietario_id) VALUES (?, 'Projeto existente', ?)", project, owner);
        jdbc.update("INSERT INTO colaboracao_projeto VALUES (?, ?)", project, member);
        Map<String,String> states = Map.of("Por Fazer","TODO","Em Curso","IN_PROGRESS","Em Revisão","IN_PROGRESS","Concluída","DONE");
        Map<String,String> priorities = Map.of("Baixa","LOW","Média","MEDIUM","Alta","HIGH","Crítica","HIGH");
        List<UUID> ids = new ArrayList<>(); List<String> expectedStates = new ArrayList<>(), expectedPriorities = new ArrayList<>();
        for (var state : states.entrySet()) for (String priority : Arrays.asList("Baixa","Média","Alta","Crítica",null)) {
            UUID id = UUID.randomUUID(); ids.add(id); expectedStates.add(state.getValue()); expectedPriorities.add(priority == null ? null : priorities.get(priority));
            jdbc.update("INSERT INTO tarefa (id,titulo,descricao,estado,prioridade,projeto_id,criador_id) VALUES (?, 'Conservar título', 'Conservar descrição', ?, ?, ?, ?)", id,state.getKey(),priority,project,owner);
        }
        UUID personal = UUID.randomUUID(), comment = UUID.randomUUID();
        jdbc.update("INSERT INTO tarefa (id,titulo,criador_id) VALUES (?, 'Pessoal existente', ?)", personal,owner);
        jdbc.update("INSERT INTO comentario VALUES (?, 'Conservar comentário', ?, ?, TIMESTAMPTZ '2026-11-18 12:00:00Z')", comment,ids.get(0),member);
        jdbc.update("INSERT INTO atribuicao_tarefa VALUES (?, ?)",ids.get(0),member);
        var flyway = Flyway.configure().dataSource(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword()).load();
        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(1);
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(flyway.info().applied()).hasSize(2);
        for (int n=0;n<ids.size();n++) {
            UUID id = ids.get(n);
            assertThat(jdbc.queryForObject("SELECT estado FROM tarefa WHERE id=?",String.class,id)).isEqualTo(expectedStates.get(n));
            assertThat(jdbc.queryForObject("SELECT prioridade FROM tarefa WHERE id=?",String.class,id)).isEqualTo(expectedPriorities.get(n));
            assertThat(jdbc.queryForObject("SELECT criador_id FROM tarefa WHERE id=?",UUID.class,id)).isEqualTo(owner);
            assertThat(jdbc.queryForObject("SELECT projeto_id FROM tarefa WHERE id=?",UUID.class,id)).isEqualTo(project);
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tarefa",Integer.class)).isEqualTo(21);
        assertThat(jdbc.queryForObject("SELECT prioridade FROM tarefa WHERE id=?",String.class,personal)).isNull();
        assertThat(jdbc.queryForObject("SELECT projeto_id FROM tarefa WHERE id=?",UUID.class,personal)).isNull();
        assertThat(jdbc.queryForObject("SELECT autor_id FROM comentario WHERE id=?",UUID.class,comment)).isEqualTo(member);
        for (String table : List.of("comentario","atribuicao_tarefa","colaboracao_projeto")) assertThat(jdbc.queryForObject("SELECT count(*) FROM "+table,Integer.class)).isEqualTo(1);
        UUID fresh = UUID.randomUUID(); jdbc.update("INSERT INTO tarefa (id,titulo,criador_id) VALUES (?, 'Nova', ?)",fresh,owner);
        assertThat(jdbc.queryForObject("SELECT estado FROM tarefa WHERE id=?",String.class,fresh)).isEqualTo("TODO");
        assertThat(jdbc.queryForObject("SELECT prioridade FROM tarefa WHERE id=?",String.class,fresh)).isEqualTo("MEDIUM");
        assertThatThrownBy(() -> jdbc.update("UPDATE tarefa SET estado='Por Fazer' WHERE id=?",fresh)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE tarefa SET prioridade='Crítica' WHERE id=?",fresh)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
