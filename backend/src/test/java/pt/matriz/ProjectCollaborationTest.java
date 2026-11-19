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
class ProjectCollaborationTest {
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

    MvcResult add(Client c, UUID project, String email, int expected) throws Exception {
        return mvc.perform(post("/api/projetos/"+project+"/colaboradores").session(c.session()).header("X-CSRF-Token",c.token()).contentType("application/json").content(mapper.writeValueAsString(Map.of("email",email)))).andExpect(status().is(expected)).andReturn();
    }
    void remove(Client c,UUID project,UUID user,int expected)throws Exception{
        mvc.perform(delete("/api/projetos/"+project+"/colaboradores/"+user).session(c.session()).header("X-CSRF-Token",c.token())).andExpect(status().is(expected));
    }
    @Test void ownerAddsExistingUserAndCannotDuplicateOrAddOwner()throws Exception{
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno=login("bruno@example.test");var ana=login("ana@example.test");
        var r=add(bruno,PROJETO_BRUNO,"ana@example.test",201);

        // Assert — verificar o resultado.
        assertThat(r.getResponse().getHeader("Location")).isEqualTo("/api/projetos/"+PROJETO_BRUNO+"/colaboradores/"+ANA);
        add(bruno,PROJETO_BRUNO,"ana@example.test",409);add(bruno,PROJETO_BRUNO,"bruno@example.test",409);add(bruno,PROJETO_BRUNO,"absent@example.test",404);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos/"+PROJETO_BRUNO).session(ana.session())).andExpect(status().isOk());
        add(ana,PROJETO_BRUNO,"ana@example.test",403);remove(ana,PROJETO_BRUNO,ANA,403);
        mvc.perform(get("/api/projetos/"+PROJETO_BRUNO+"/colaboradores").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].passwordHash").doesNotExist());
    }
    @Test void removalRevokesSessionAccessAndCleansAssignmentsButKeepsAuthorship()throws Exception{
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");remove(ana,PROJETO_ANA,BRUNO,204);

        // Assert — verificar o resultado.
        assertThat(count("atribuicao_tarefa")).isEqualTo(1);assertThat(count("comentario")).isEqualTo(1);assertThat(count("utilizador")).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT autor_id FROM comentario",UUID.class)).isEqualTo(BRUNO);

        // Act & Assert — executar o percurso/pedido e verificar o resultado.
        for(String path:List.of("/api/projetos/"+PROJETO_ANA,"/api/tarefas/"+TAREFA_PARTILHADA,"/api/tarefas/"+TAREFA_PARTILHADA+"/comentarios"))mvc.perform(get(path).session(bruno.session())).andExpect(status().isForbidden());
        remove(ana,PROJETO_ANA,BRUNO,404);remove(ana,PROJETO_ANA,ANA,409);
    }
    @Test void ownerCanManageArchivedCollaborationButTaskWritesRemainBlocked()throws Exception{
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var ana=login("ana@example.test");jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        remove(ana,PROJETO_ANA,BRUNO,204);add(ana,PROJETO_ANA,"bruno@example.test",201);
        mvc.perform(get("/api/projetos/"+PROJETO_ANA+"/colaboradores").session(ana.session())).andExpect(status().isOk());
        update(ana,TAREFA_PARTILHADA,"{\"estado\":\"DONE\"}",409);
    }
    @Test void failureInCleanupRollsBackMembershipAndAssignments()throws Exception{
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");jdbc.execute("CREATE FUNCTION reject_member_cleanup() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'test'; END $$");
        jdbc.execute("CREATE TRIGGER reject_member_cleanup BEFORE DELETE ON atribuicao_tarefa FOR EACH ROW EXECUTE FUNCTION reject_member_cleanup()");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        try{remove(ana,PROJETO_ANA,BRUNO,500);assertThat(count("colaboracao_projeto")).isEqualTo(1);assertThat(count("atribuicao_tarefa")).isEqualTo(2);}
        finally{jdbc.execute("DROP TRIGGER reject_member_cleanup ON atribuicao_tarefa");jdbc.execute("DROP FUNCTION reject_member_cleanup()");}
    }
    @Test void strictValidationAccessCsrfAndUnknownProject()throws Exception{
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");
        add(ana,PROJETO_BRUNO,"ana@example.test",403);add(ana,UUID.randomUUID(),"ana@example.test",404);add(ana,PROJETO_ANA,"   ",422);add(ana,PROJETO_ANA,"bad",422);

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos/"+PROJETO_ANA+"/colaboradores")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/projetos/"+PROJETO_ANA+"/colaboradores").session(ana.session()).contentType("application/json").content("{}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/projetos/"+PROJETO_ANA+"/colaboradores").session(ana.session()).header("X-CSRF-Token",ana.token()).contentType("application/json").content("{\"email\":\"bruno@example.test\",\"proprietarioId\":\""+ANA+"\"}")).andExpect(status().isBadRequest());
        remove(bruno,PROJETO_ANA,BRUNO,403);
    }
}
