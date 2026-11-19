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
        // Arrange — sessão real, CSRF e projeto ativo das fixtures isoladas.
        var ana = login("ana@example.test");
        int before = count("tarefa");
        var request = mapper.createObjectNode()
            .put("titulo", "Preparar apresentação").put("descricao", "Plano")
            .put("projetoId", PROJETO_ANA.toString()).put("prazo", "2020-01-01");

        // Act — criar pela API, com estado e prioridade omitidos.
        var result = mvc.perform(post("/api/tarefas").session(ana.session())
            .header("X-CSRF-Token", ana.token()).contentType("application/json")
            .content(mapper.writeValueAsString(request))).andReturn();

        // Assert — contrato de criação e identidade inferida no backend.
        assertThat(result.getResponse().getStatus()).isEqualTo(201);
        assertThat(result.getResponse().getContentType()).startsWith("application/json");
        UUID taskId = id(result);
        String location = result.getResponse().getHeader("Location");
        assertThat(location).isEqualTo("/api/tarefas/" + taskId);
        var expected = mapper.createObjectNode().put("id", taskId.toString())
            .put("titulo", "Preparar apresentação").put("descricao", "Plano")
            .put("estado", "TODO").put("prioridade", "MEDIUM").put("prazo", "2020-01-01")
            .put("projetoId", PROJETO_ANA.toString()).put("criadorId", ANA.toString());
        assertThat(mapper.readTree(result.getResponse().getContentAsString())).isEqualTo(expected);
        assertThat(count("tarefa")).isEqualTo(before + 1);

        // Arrange — sessão independente para a consulta posterior.
        var again = login("ana@example.test");
        assertThat(again.session()).isNotSameAs(ana.session());

        // Act — consultar a localização devolvida pela API.
        var fetched = mvc.perform(get(location).session(again.session())).andReturn();

        // Assert — resposta completa e linha efetivamente persistida por outra consulta SQL.
        assertThat(fetched.getResponse().getStatus()).isEqualTo(200);
        assertThat(fetched.getResponse().getContentType()).startsWith("application/json");
        assertThat(mapper.readTree(fetched.getResponse().getContentAsString())).isEqualTo(expected);
        var stored = jdbc.queryForMap("SELECT * FROM tarefa WHERE id = ?", taskId);
        assertThat(stored.get("id")).isEqualTo(taskId);
        assertThat(stored.get("titulo")).isEqualTo("Preparar apresentação");
        assertThat(stored.get("descricao")).isEqualTo("Plano");
        assertThat(stored.get("estado")).isEqualTo("TODO");
        assertThat(stored.get("prioridade")).isEqualTo("MEDIUM");
        assertThat(stored.get("prazo").toString()).isEqualTo("2020-01-01");
        assertThat(stored.get("projeto_id")).isEqualTo(PROJETO_ANA);
        assertThat(stored.get("criador_id")).isEqualTo(ANA);
    }

    @Test void whitespaceTitleReturnsProblemAndPreservesDatabase() throws Exception {
        // Arrange — snapshots das seis tabelas da BD descartável antes do pedido inválido.
        var ana = login("ana@example.test");
        var tables = List.of("utilizador", "projeto", "tarefa", "comentario",
            "colaboracao_projeto", "atribuicao_tarefa");
        var before = new LinkedHashMap<String, List<Map<String, Object>>>();
        for (String table : tables) before.put(table, jdbc.queryForList("SELECT * FROM " + table));
        var request = mapper.createObjectNode().put("titulo", "   ")
            .put("projetoId", PROJETO_ANA.toString());

        // Act — pedido autenticado com CSRF válido para atingir a validação do título.
        var result = mvc.perform(post("/api/tarefas").session(ana.session())
            .header("X-CSRF-Token", ana.token()).contentType("application/json")
            .content(mapper.writeValueAsString(request))).andReturn();

        // Assert — erro contratual identificando o campo, sem criação ou alterações parciais.
        assertThat(result.getResponse().getStatus()).isEqualTo(422);
        assertThat(result.getResponse().getContentType()).startsWith("application/problem+json");
        assertThat(result.getResponse().getHeader("Location")).isNull();
        var problem = mapper.readTree(result.getResponse().getContentAsString());
        assertThat(problem.path("type").asText()).isEqualTo("about:blank");
        assertThat(problem.path("status").asInt()).isEqualTo(422);
        assertThat(problem.path("codigo").asText()).isEqualTo("VALIDACAO");
        assertThat(problem.path("title").asText()).isNotBlank();
        assertThat(problem.path("detail").asText()).startsWith("titulo:");
        for (String table : tables) {
            assertThat(jdbc.queryForList("SELECT * FROM " + table))
                .as("Tabela %s permanece inalterada", table)
                .containsExactlyInAnyOrderElementsOf(before.get(table));
        }
    }

    @Test void personalTaskHasOptionalNullPriorityAndIsPrivate() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        UUID id=id(create(ana,"{\"titulo\":\"Pessoal\",\"prioridade\":null}",201));
        mvc.perform(get("/api/tarefas/"+id).session(ana.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.projetoId").isEmpty()).andExpect(jsonPath("$.prioridade").isEmpty());
        mvc.perform(get("/api/tarefas/"+id).session(bruno.session())).andExpect(status().isForbidden());
        update(bruno,id,"{\"estado\":\"DONE\"}",403);
        mvc.perform(delete("/api/tarefas/"+id).session(bruno.session()).header("X-CSRF-Token",bruno.token())).andExpect(status().isForbidden());
    }
    @Test void badFieldsEnumsDatesAndForgedIdentityDoNotWrite() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");
        for(String body:List.of("{}","{\"titulo\":\"   \"}","{\"titulo\":null}","{\"titulo\":12}",
            "{\"titulo\":\"X\",\"descricao\":false}","{\"titulo\":\"X\",\"estado\":\"Em Curso\"}",
            "{\"titulo\":\"X\",\"estado\":null}","{\"titulo\":\"X\",\"prioridade\":\"Crítica\"}",
            "{\"titulo\":\"X\",\"prioridade\":\"\"}","{\"titulo\":\"X\",\"prazo\":\"2026-02-30\"}",
            "{\"titulo\":\"X\",\"projetoId\":\"invalid\"}")) create(ana,body,422);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        create(ana,"{\"titulo\":\"X\",\"criadorId\":\""+BRUNO+"\"}",400);

        // Assert — verificar o resultado.
        assertThat(count("tarefa")).isEqualTo(3);
    }
    @Test void validLongTextAndPastDeadlinesAreNotRestrictedByTeachingExamples() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var r=create(ana,mapper.writeValueAsString(Map.of("titulo","T".repeat(201),"descricao","D".repeat(3001),"prazo","2020-01-01")),201);

        // Assert — verificar o resultado.
        assertThat(mapper.readTree(r.getResponse().getContentAsString()).get("prazo").asText()).isEqualTo("2020-01-01");
    }
    @Test void patchPreservesOmittedFieldsAndClearsOnlyOptionalOnes() throws Exception {
        // Arrange — autenticar e criar a tarefa de referência.
        var ana = login("ana@example.test");
        UUID id = id(create(ana,"{\"titulo\":\"Inicial\",\"descricao\":\"Conservar\",\"prazo\":\"2026-11-19\"}",201));

        // Act & Assert — editar pela API e verificar o código HTTP no helper.
        var changed=mapper.readTree(update(ana,id,"{\"estado\":\"IN_PROGRESS\"}",200).getResponse().getContentAsString());

        // Assert — verificar o resultado.
        assertThat(changed.get("titulo").asText()).isEqualTo("Inicial");
        assertThat(changed.get("descricao").asText()).isEqualTo("Conservar");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var cleared=mapper.readTree(update(ana,id,"{\"descricao\":null,\"prioridade\":null,\"prazo\":null}",200).getResponse().getContentAsString());

        // Assert — verificar os resultados.
        for(String field:List.of("descricao","prioridade","prazo"))assertThat(cleared.get(field).isNull()).isTrue();
        assertThat(cleared.get("criadorId").asText()).isEqualTo(ANA.toString());

        // Act & Assert — executar o percurso/pedido e verificar o resultado.
        for(String body:List.of("{}","{\"titulo\":null}","{\"estado\":null}","{\"titulo\":\" \"}","{\"estado\":\"INVALID\"}"))update(ana,id,body,422);
        update(ana,id,"{\"criadorId\":\""+BRUNO+"\"}",400);
    }
    @Test void everyStateTransitionAndReopeningUpdateProjectProgress() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");
        for(String from:List.of("TODO","IN_PROGRESS","DONE"))for(String to:List.of("TODO","IN_PROGRESS","DONE")){
            update(ana,TAREFA_PARTILHADA,"{\"estado\":\""+from+"\"}",200);
            update(ana,TAREFA_PARTILHADA,"{\"estado\":\""+to+"\"}",200);
            mvc.perform(get("/api/projetos/"+PROJETO_ANA).session(ana.session())).andExpect(status().isOk())
                .andExpect(jsonPath("$.progresso.percentagem").value(to.equals("DONE")?100.0:0.0));
        }
    }
    @Test void listStartsFromAuthorizedSetAndNeverUsesAssignmentsAsAccess() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/tarefas").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/tarefas").session(ana.session()).param("projetoId",PROJETO_BRUNO.toString())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semProjeto","true")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id=? AND utilizador_id=?",PROJETO_ANA,BRUNO);
        mvc.perform(get("/api/tarefas").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void filtersIntersectAndSearchIgnoresCaseAccentsWithLiteralWildcards() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        UUID target=id(create(ana,"{\"titulo\":\"Preparar apresentação 100%\",\"projetoId\":\""+PROJETO_ANA+"\",\"estado\":\"IN_PROGRESS\",\"prioridade\":\"HIGH\",\"prazo\":\"2026-11-19\"}",201));
        create(ana,"{\"titulo\":\"Preparar apresentação\",\"projetoId\":\""+PROJETO_ANA+"\",\"estado\":\"IN_PROGRESS\",\"prioridade\":\"LOW\",\"prazo\":\"2026-11-19\"}",201);

        // Act — executar a operação em teste.
        mvc.perform(get("/api/tarefas").session(ana.session()).param("q","APRESENTACAO").param("projetoId",PROJETO_ANA.toString())
            .param("estado","IN_PROGRESS").param("prioridade","HIGH").param("prazoDe","2026-11-19").param("prazoAte","2026-11-19"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(target.toString()));

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas").session(ana.session()).param("q","%")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        create(ana,"{\"titulo\":\"Sem prioridade\",\"prioridade\":null}",201);

        // Act — executar a operação em teste.
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semProjeto","true").param("semPrioridade","true"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void malformedOrConflictingFiltersAre400() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");
        for(var pair:List.of(Map.entry("estado","Por Fazer"),Map.entry("prioridade","Crítica"),Map.entry("semProjeto","yes"),Map.entry("semPrioridade","1"),Map.entry("projetoId","invalid"),Map.entry("prazoDe","2026-02-30"))){
            mvc.perform(get("/api/tarefas").session(ana.session()).param(pair.getKey(),pair.getValue())).andExpect(status().isBadRequest());
        }

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semProjeto","true").param("projetoId",PROJETO_ANA.toString())).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tarefas").session(ana.session()).param("semPrioridade","true").param("prioridade","HIGH")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tarefas").session(ana.session()).param("prazoDe","2026-11-20").param("prazoAte","2026-11-19")).andExpect(status().isBadRequest());
    }
    @Test void creationRequiresExistingAccessibleActiveProject() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+UUID.randomUUID()+"\"}",404);
        create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+PROJETO_BRUNO+"\"}",403);
        jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        create(ana,"{\"titulo\":\"X\",\"projetoId\":\""+PROJETO_ANA+"\"}",409);

        // Assert — verificar o resultado.
        assertThat(count("tarefa")).isEqualTo(3);
    }
    @Test void archiveBlocksAllWritesButKeepsReadAndData() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var ana=login("ana@example.test");jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session())).andExpect(status().isOk());
        for(String body:List.of("{\"titulo\":\"Alterar\"}","{\"estado\":\"DONE\"}","{\"prioridade\":\"HIGH\"}","{\"projetoId\":null}"))update(ana,TAREFA_PARTILHADA,body,409);
        update(ana,TAREFA_PESSOAL,"{\"projetoId\":\""+PROJETO_ANA+"\"}",409);
        mvc.perform(delete("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session()).header("X-CSRF-Token",ana.token())).andExpect(status().isConflict());

        // Assert — verificar o resultado.
        assertThat(count("tarefa")).isEqualTo(3);assertThat(count("comentario")).isEqualTo(1);assertThat(count("atribuicao_tarefa")).isEqualTo(2);
    }
    @Test void transferCleansOnlyIneligibleAssignmentsAndPreservesCreatorAndComments() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno=login("bruno@example.test");var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var body=mapper.readTree(update(bruno,TAREFA_PARTILHADA,"{\"projetoId\":\""+PROJETO_BRUNO+"\"}",200).getResponse().getContentAsString());

        // Assert — verificar o resultado.
        assertThat(body.get("criadorId").asText()).isEqualTo(ANA.toString());
        assertThat(jdbc.queryForObject("SELECT utilizador_id FROM atribuicao_tarefa WHERE tarefa_id=?",UUID.class,TAREFA_PARTILHADA)).isEqualTo(BRUNO);
        assertThat(count("comentario")).isEqualTo(1);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session())).andExpect(status().isForbidden());
    }
    @Test void desassociationMayRevokeActorAndReturnsJsonNullInsteadOfEmptyResponse() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno=login("bruno@example.test");var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var result=update(bruno,TAREFA_PARTILHADA,"{\"projetoId\":null}",200);

        // Assert — verificar o resultado.
        assertThat(result.getResponse().getContentAsString()).isEqualTo("null");
        assertThat(result.getResponse().getContentType()).startsWith("application/json");
        assertThat(jdbc.queryForObject("SELECT utilizador_id FROM atribuicao_tarefa WHERE tarefa_id=?",UUID.class,TAREFA_PARTILHADA)).isEqualTo(ANA);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(bruno.session())).andExpect(status().isForbidden());
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA).session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.projetoId").isEmpty());

        // Assert — verificar o resultado.
        assertThat(count("comentario")).isEqualTo(1);
    }
    @Test void failedAssignmentCleanupRollsBackTransfer() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
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
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(delete("/api/tarefas/"+TAREFA_PARTILHADA).session(bruno.session()).header("X-CSRF-Token",bruno.token())).andExpect(status().isNoContent());

        // Assert — verificar o resultado.
        assertThat(count("tarefa")).isEqualTo(2);assertThat(count("comentario")).isZero();assertThat(count("atribuicao_tarefa")).isZero();
        assertThat(count("projeto")).isEqualTo(2);assertThat(count("utilizador")).isEqualTo(2);assertThat(count("colaboracao_projeto")).isEqualTo(1);
    }
    @Test void csrfAuthenticationAndMissingReferencesRemainEnforced() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(post("/api/tarefas").session(ana.session()).contentType("application/json").content("{\"titulo\":\"X\"}")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/tarefas/"+TAREFA_PESSOAL).session(ana.session()).contentType("application/json").content("{\"estado\":\"DONE\"}")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/tarefas/"+TAREFA_PESSOAL).session(ana.session())).andExpect(status().isForbidden());
        mvc.perform(get("/api/tarefas")).andExpect(status().isUnauthorized());
        update(ana,UUID.randomUUID(),"{\"estado\":\"DONE\"}",404);
        update(ana,TAREFA_PESSOAL,"{\"projetoId\":\""+PROJETO_BRUNO+"\"}",403);
    }
}
