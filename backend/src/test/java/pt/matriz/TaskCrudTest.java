package pt.matriz;

import java.util.*;
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

@SpringBootTest @AutoConfigureMockMvc @Testcontainers
class TaskCrudTest {
    static final String PASSWORD="FictitiousTestPassword42!";
    @Container static final PostgreSQLContainer<?> DB=new PostgreSQLContainer<>("postgres:17.9-alpine");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",DB::getJdbcUrl);r.add("spring.datasource.username",DB::getUsername);r.add("spring.datasource.password",DB::getPassword);
    }
    @Autowired MockMvc mvc; @Autowired ObjectMapper mapper; @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder; @Autowired PlatformTransactionManager transactions;
    @BeforeEach void fixtures() {
        jdbc.execute("TRUNCATE utilizador,projeto,tarefa,comentario,colaboracao_projeto,atribuicao_tarefa CASCADE");
        new TransactionTemplate(transactions).executeWithoutResult(tx -> new DemoSeeder(jdbc,encoder,PASSWORD).run(null));
    }
    record Client(MockHttpSession session,String token) {}
    Client login(String email) throws Exception {
        var before=mvc.perform(get("/api/sessao/csrf")).andExpect(status().isOk()).andReturn();
        var session=(MockHttpSession)before.getRequest().getSession(false);
        String token=mapper.readTree(before.getResponse().getContentAsString()).get("token").asText();
        mvc.perform(post("/api/sessao").session(session).header("X-CSRF-Token",token).contentType("application/json")
            .content(mapper.writeValueAsString(Map.of("email",email,"palavraPasse",PASSWORD)))).andExpect(status().isOk());
        var after=mvc.perform(get("/api/sessao/csrf").session(session)).andExpect(status().isOk()).andReturn();
        return new Client(session,mapper.readTree(after.getResponse().getContentAsString()).get("token").asText());
    }
    MvcResult create(Client actor,String body,int status) throws Exception {
        return mvc.perform(post("/api/tarefas").session(actor.session()).header("X-CSRF-Token",actor.token()).contentType("application/json").content(body))
            .andExpect(status().is(status)).andReturn();
    }
    MvcResult update(Client actor,UUID id,String body,int status) throws Exception {
        return mvc.perform(patch("/api/tarefas/"+id).session(actor.session()).header("X-CSRF-Token",actor.token()).contentType("application/json").content(body))
            .andExpect(status().is(status)).andReturn();
    }
    UUID id(MvcResult r) throws Exception { return UUID.fromString(mapper.readTree(r.getResponse().getContentAsString()).get("id").asText()); }
    int count(String table) { return jdbc.queryForObject("SELECT count(*) FROM "+table,Integer.class); }

    @Test void createDefaultsIdentityAndPersistenceInNewSession() throws Exception {
        var ana=login("ana@example.test");
        var result=create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+PROJETO_ANA+"\"}",201); UUID id=id(result);
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/tarefas/"+id);
        var again=login("ana@example.test");
        mvc.perform(get("/api/tarefas/"+id).session(again.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.criadorId").value(ANA.toString())).andExpect(jsonPath("$.estado").value("TODO"))
            .andExpect(jsonPath("$.prioridade").value("MEDIUM")).andExpect(jsonPath("$.titulo").value("X"));
    }
    @Test void personalTaskHasOptionalNullPriorityAndIsPrivate() throws Exception {
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");
        UUID id=id(create(ana,"{\"titulo\":\"Pessoal\",\"prioridade\":null}",201));
        mvc.perform(get("/api/tarefas/"+id).session(ana.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.projetoId").isEmpty()).andExpect(jsonPath("$.prioridade").isEmpty());
        mvc.perform(get("/api/tarefas/"+id).session(bruno.session())).andExpect(status().isForbidden());
        update(bruno,id,"{\"estado\":\"DONE\"}",403);
        mvc.perform(delete("/api/tarefas/"+id).session(bruno.session()).header("X-CSRF-Token",bruno.token())).andExpect(status().isForbidden());
    }
    @Test void badFieldsEnumsDatesAndForgedIdentityDoNotWrite() throws Exception {
        var ana=login("ana@example.test");
        for(String body:List.of("{}","{\"titulo\":\"   \"}","{\"titulo\":null}","{\"titulo\":12}",
            "{\"titulo\":\"X\",\"descricao\":false}","{\"titulo\":\"X\",\"estado\":\"Em Curso\"}",
            "{\"titulo\":\"X\",\"estado\":null}","{\"titulo\":\"X\",\"prioridade\":\"Crítica\"}",
            "{\"titulo\":\"X\",\"prioridade\":\"\"}","{\"titulo\":\"X\",\"prazo\":\"2026-02-30\"}",
            "{\"titulo\":\"X\",\"projetoId\":\"invalid\"}")) create(ana,body,422);
        create(ana,"{\"titulo\":\"X\",\"criadorId\":\""+BRUNO+"\"}",400);
        assertThat(count("tarefa")).isEqualTo(3);
    }
    @Test void validLongTextAndPastDeadlinesAreNotRestrictedByTeachingExamples() throws Exception {
        var ana=login("ana@example.test");
        var r=create(ana,mapper.writeValueAsString(Map.of("titulo","T".repeat(201),"descricao","D".repeat(3001),"prazo","2020-01-01")),201);
        assertThat(mapper.readTree(r.getResponse().getContentAsString()).get("prazo").asText()).isEqualTo("2020-01-01");
    }
    @Test void patchPreservesOmittedFieldsAndClearsOnlyOptionalOnes() throws Exception {
        var ana=login("ana@example.test");UUID id=id(create(ana,"{\"titulo\":\"Inicial\",\"descricao\":\"Conservar\",\"prazo\":\"2026-11-19\"}",201));
        var changed=mapper.readTree(update(ana,id,"{\"estado\":\"IN_PROGRESS\"}",200).getResponse().getContentAsString());
        assertThat(changed.get("titulo").asText()).isEqualTo("Inicial");assertThat(changed.get("descricao").asText()).isEqualTo("Conservar");
        var cleared=mapper.readTree(update(ana,id,"{\"descricao\":null,\"prioridade\":null,\"prazo\":null}",200).getResponse().getContentAsString());
        for(String field:List.of("descricao","prioridade","prazo"))assertThat(cleared.get(field).isNull()).isTrue();
        assertThat(cleared.get("criadorId").asText()).isEqualTo(ANA.toString());
        for(String body:List.of("{}","{\"titulo\":null}","{\"estado\":null}","{\"titulo\":\" \"}","{\"estado\":\"INVALID\"}"))update(ana,id,body,422);
        update(ana,id,"{\"criadorId\":\""+BRUNO+"\"}",400);
    }
    @Test void everyStateTransitionAndReopeningUpdateProjectProgress() throws Exception {
        var ana=login("ana@example.test");
        for(String from:List.of("TODO","IN_PROGRESS","DONE"))for(String to:List.of("TODO","IN_PROGRESS","DONE")){
            update(ana,TAREFA_PARTILHADA,"{\"estado\":\""+from+"\"}",200);
            update(ana,TAREFA_PARTILHADA,"{\"estado\":\""+to+"\"}",200);
            mvc.perform(get("/api/projetos/"+PROJETO_ANA).session(ana.session())).andExpect(status().isOk())
                .andExpect(jsonPath("$.progresso.percentagem").value(to.equals("DONE")?100.0:0.0));
        }
    }
    @Test void listStartsFromAuthorizedSetAndNeverUsesAssignmentsAsAccess() throws Exception {
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");
        mvc.perform(get("/api/tarefas").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/tarefas").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/tarefas").session(ana.session()).param("projetoId",PROJETO_BRUNO.toString())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semProjeto","true")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id=? AND utilizador_id=?",PROJETO_ANA,BRUNO);
        mvc.perform(get("/api/tarefas").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void filtersIntersectAndSearchIgnoresCaseAccentsWithLiteralWildcards() throws Exception {
        var ana=login("ana@example.test");
        UUID target=id(create(ana,"{\"titulo\":\"Preparar apresentação 100%\",\"projetoId\":\""+PROJETO_ANA+"\",\"estado\":\"IN_PROGRESS\",\"prioridade\":\"HIGH\",\"prazo\":\"2026-11-19\"}",201));
        create(ana,"{\"titulo\":\"Preparar apresentação\",\"projetoId\":\""+PROJETO_ANA+"\",\"estado\":\"IN_PROGRESS\",\"prioridade\":\"LOW\",\"prazo\":\"2026-11-19\"}",201);
        mvc.perform(get("/api/tarefas").session(ana.session()).param("q","APRESENTACAO").param("projetoId",PROJETO_ANA.toString())
            .param("estado","IN_PROGRESS").param("prioridade","HIGH").param("prazoDe","2026-11-19").param("prazoAte","2026-11-19"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(target.toString()));
        mvc.perform(get("/api/tarefas").session(ana.session()).param("q","%")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        create(ana,"{\"titulo\":\"Sem prioridade\",\"prioridade\":null}",201);
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semProjeto","true").param("semPrioridade","true"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void malformedOrConflictingFiltersAre400() throws Exception {
        var ana=login("ana@example.test");
        for(var pair:List.of(Map.entry("estado","Por Fazer"),Map.entry("prioridade","Crítica"),Map.entry("semProjeto","yes"),Map.entry("semPrioridade","1"),Map.entry("projetoId","invalid"),Map.entry("prazoDe","2026-02-30"))){
            mvc.perform(get("/api/tarefas").session(ana.session()).param(pair.getKey(),pair.getValue())).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semProjeto","true").param("projetoId",PROJETO_ANA.toString())).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semPrioridade","true").param("prioridade","HIGH")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tarefas").session(ana.session()).param("prazoDe","2026-11-20").param("prazoAte","2026-11-19")).andExpect(status().isBadRequest());
    }
    @Test void creationRequiresExistingAccessibleActiveProject() throws Exception {
        var ana=login("ana@example.test");
        create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+UUID.randomUUID()+"\"}",404);
        create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+PROJETO_BRUNO+"\"}",403);
        jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+PROJETO_ANA+"\"}",409);
        assertThat(count("tarefa")).isEqualTo(3);
    }
    @Test void archiveBlocksAllWritesButKeepsReadAndData() throws Exception {
        var ana=login("ana@example.test");jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session())).andExpect(status().isOk());
        for(String body:List.of("{\"titulo\":\"Alterar\"}","{\"estado\":\"DONE\"}","{\"prioridade\":\"HIGH\"}","{\"projetoId\":null}"))update(ana,TAREFA_PARTILHADA,body,409);
        update(ana,TAREFA_PESSOAL,"{\"projetoId\":\""+PROJETO_ANA+"\"}",409);
        mvc.perform(delete("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session()).header("X-CSRF-Token",ana.token())).andExpect(status().isConflict());
        assertThat(count("tarefa")).isEqualTo(3);assertThat(count("comentario")).isEqualTo(1);assertThat(count("atribuicao_tarefa")).isEqualTo(2);
    }
    @Test void transferCleansOnlyIneligibleAssignmentsAndPreservesCreatorAndComments() throws Exception {
        var bruno=login("bruno@example.test");var ana=login("ana@example.test");
        var body=mapper.readTree(update(bruno,TAREFA_PARTILHADA,"{\"projetoId\":\""+PROJETO_BRUNO+"\"}",200).getResponse().getContentAsString());
        assertThat(body.get("criadorId").asText()).isEqualTo(ANA.toString());
        assertThat(jdbc.queryForObject("SELECT utilizador_id FROM atribuicao_tarefa WHERE tarefa_id=?",UUID.class,TAREFA_PARTILHADA)).isEqualTo(BRUNO);
        assertThat(count("comentario")).isEqualTo(1);
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session())).andExpect(status().isForbidden());
    }
    @Test void desassociationMayRevokeActorAndReturnsJsonNullInsteadOfEmptyResponse() throws Exception {
        var bruno=login("bruno@example.test");var ana=login("ana@example.test");
        var result=update(bruno,TAREFA_PARTILHADA,"{\"projetoId\":null}",200);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("null");
        assertThat(result.getResponse().getContentType()).startsWith("application/json");
        assertThat(jdbc.queryForObject("SELECT utilizador_id FROM atribuicao_tarefa WHERE tarefa_id=?",UUID.class,TAREFA_PARTILHADA)).isEqualTo(ANA);
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(bruno.session())).andExpect(status().isForbidden());
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.projetoId").isEmpty());
        assertThat(count("comentario")).isEqualTo(1);
    }
    @Test void failedAssignmentCleanupRollsBackTransfer() throws Exception {
        var bruno=login("bruno@example.test");
        jdbc.execute("CREATE FUNCTION fail_assignment_delete() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'forced failure'; END; $$");
        jdbc.execute("CREATE TRIGGER fail_assignment BEFORE DELETE ON atribuicao_tarefa FOR EACH ROW EXECUTE FUNCTION fail_assignment_delete()");
        try {
            update(bruno,TAREFA_PARTILHADA,"{\"projetoId\":\""+PROJETO_BRUNO+"\"}",500);
            assertThat(jdbc.queryForObject("SELECT projeto_id FROM tarefa WHERE id=?",UUID.class,TAREFA_PARTILHADA)).isEqualTo(PROJETO_ANA);
            assertThat(count("atribuicao_tarefa")).isEqualTo(2);assertThat(count("comentario")).isEqualTo(1);
        }finally{jdbc.execute("DROP TRIGGER fail_assignment ON atribuicao_tarefa");jdbc.execute("DROP FUNCTION fail_assignment_delete()");}
    }
    @Test void deleteByCollaboratorCascadesOnlyTaskDependents() throws Exception {
        var bruno=login("bruno@example.test");
        mvc.perform(delete("/api/tarefas/"+TAREFA_PARTILHADA).session(bruno.session()).header("X-CSRF-Token",bruno.token())).andExpect(status().isNoContent());
        assertThat(count("tarefa")).isEqualTo(2);assertThat(count("comentario")).isZero();assertThat(count("atribuicao_tarefa")).isZero();
        assertThat(count("projeto")).isEqualTo(2);assertThat(count("utilizador")).isEqualTo(2);assertThat(count("colaboracao_projeto")).isEqualTo(1);
    }
    @Test void csrfAuthenticationAndMissingReferencesRemainEnforced() throws Exception {
        var ana=login("ana@example.test");
        mvc.perform(post("/api/tarefas").session(ana.session()).contentType("application/json").content("{\"titulo\":\"X\"}")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/tarefas/"+TAREFA_PESSOAL).session(ana.session()).contentType("application/json").content("{\"estado\":\"DONE\"}")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/tarefas/"+TAREFA_PESSOAL).session(ana.session())).andExpect(status().isForbidden());
        mvc.perform(get("/api/tarefas")).andExpect(status().isUnauthorized());
        update(ana,UUID.randomUUID(),"{\"estado\":\"DONE\"}",404);
        update(ana,TAREFA_PESSOAL,"{\"projetoId\":\""+PROJETO_BRUNO+"\"}",403);
    }
}
