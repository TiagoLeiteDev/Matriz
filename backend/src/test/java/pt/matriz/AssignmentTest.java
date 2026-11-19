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
class AssignmentTest {
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

    MvcResult assign(Client actor, UUID task, UUID user, int expected) throws Exception {
        return mvc.perform(post("/api/tarefas/"+task+"/responsaveis").session(actor.session()).header("X-CSRF-Token",actor.token())
            .contentType("application/json").content(mapper.writeValueAsString(Map.of("utilizadorId",user))))
            .andExpect(status().is(expected)).andReturn();
    }
    void remove(Client actor, UUID task, UUID user, int expected) throws Exception {
        mvc.perform(delete("/api/tarefas/"+task+"/responsaveis/"+user).session(actor.session()).header("X-CSRF-Token",actor.token()))
            .andExpect(status().is(expected));
    }
    @Test void eligibleListsAreAuthorizedAndPersonalOnlyCreator() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");

        // Act — executar a operação em teste.
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/utilizadores-elegiveis").session(bruno.session()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].passwordHash").doesNotExist());
        mvc.perform(get("/api/tarefas/"+TAREFA_PESSOAL+"/utilizadores-elegiveis").session(ana.session()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(ANA.toString()));

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas/"+TAREFA_PESSOAL+"/utilizadores-elegiveis").session(bruno.session())).andExpect(status().isForbidden());
        mvc.perform(get("/api/tarefas/"+TAREFA_PRIVADA+"/utilizadores-elegiveis").session(ana.session())).andExpect(status().isForbidden());
    }
    @Test void multipleAssignmentsPersistAndRemovalOnlyDeletesOnePair() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Arrange — preparar os dados do cenário.
        jdbc.update("DELETE FROM atribuicao_tarefa WHERE tarefa_id=?",TAREFA_PARTILHADA);

        // Arrange — preparar o próximo cenário/dados.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var result=assign(bruno,TAREFA_PARTILHADA,ANA,201);

        // Assert — verificar o resultado.
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/tarefas/"+TAREFA_PARTILHADA+"/responsaveis/"+ANA);
        assertThat(mapper.readTree(result.getResponse().getContentAsString()).get("utilizador").get("id").asText()).isEqualTo(ANA.toString());

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        assign(ana,TAREFA_PARTILHADA,BRUNO,201);assign(ana,TAREFA_PARTILHADA,BRUNO,409);

        // Arrange — preparar o próximo cenário/dados.
        var fresh=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/responsaveis").session(fresh.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        remove(bruno,TAREFA_PARTILHADA,ANA,204);remove(bruno,TAREFA_PARTILHADA,ANA,404);

        // Assert — verificar o resultado.
        assertThat(count("atribuicao_tarefa")).isEqualTo(1);assertThat(count("utilizador")).isEqualTo(2);assertThat(count("tarefa")).isEqualTo(3);
    }
    @Test void personalOnlyCreatorEligibleAndNonexistentUserRejected() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        assign(ana,TAREFA_PESSOAL,ANA,201);assign(ana,TAREFA_PESSOAL,BRUNO,403);
        assign(ana,TAREFA_PESSOAL,UUID.randomUUID(),404);assign(bruno,TAREFA_PESSOAL,BRUNO,403);
        remove(bruno,TAREFA_PESSOAL,ANA,403);assign(ana,TAREFA_PRIVADA,ANA,403);
        assign(ana,UUID.randomUUID(),ANA,404);
    }
    @Test void unrelatedExistingAccountIsNotEligibleAndAssignmentNeverGrantsAccess() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        UUID outsider=UUID.randomUUID();

        // Arrange — preparar os dados do cenário.
        jdbc.update("INSERT INTO utilizador(id,nome,email,password_hash) VALUES (?, 'Outra', 'outra@example.test', ?)",outsider,encoder.encode(PASSWORD));
        var ana=login("ana@example.test");assign(ana,TAREFA_PARTILHADA,outsider,403);

        // Arrange — preparar o próximo cenário/dados.
        var other=login("outra@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/responsaveis").session(other.session())).andExpect(status().isForbidden());

        // Assert — verificar o resultado.
        assertThat(count("atribuicao_tarefa")).isEqualTo(2);
    }
    @Test void archivedTaskAllowsReadsButBlocksBothWrites() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var ana=login("ana@example.test");jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        assign(ana,TAREFA_PARTILHADA,ANA,409);remove(ana,TAREFA_PARTILHADA,ANA,409);

        // Arrange — preparar o próximo cenário/dados.
        for(String suffix:List.of("responsaveis","utilizadores-elegiveis"))
            mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/"+suffix).session(ana.session())).andExpect(status().isOk());

        // Assert — verificar o resultado.
        assertThat(count("atribuicao_tarefa")).isEqualTo(2);
    }
    @Test void strictBodyRejectsMalformedAndForgedIdentity() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");
        for(String body:List.of("{}","{\"utilizadorId\":null}","{\"utilizadorId\":3}","{\"utilizadorId\":\"bad\"}"))
            mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/responsaveis").session(ana.session()).header("X-CSRF-Token",ana.token())
                .contentType("application/json").content(body)).andExpect(status().isUnprocessableEntity());

        // Act — executar a operação em teste.
        mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/responsaveis").session(ana.session()).header("X-CSRF-Token",ana.token())
            .contentType("application/json").content("{\"utilizadorId\":\""+ANA+"\",\"autorId\":\""+BRUNO+"\"}")).andExpect(status().isBadRequest());
    }
    @Test void csrfUnauthenticatedAndRevokedMembershipAreRejected() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/utilizadores-elegiveis")).andExpect(status().isUnauthorized());

        // Act — executar a operação em teste.
        mvc.perform(post("/api/tarefas/"+TAREFA_PARTILHADA+"/responsaveis").session(bruno.session()).contentType("application/json")
            .content("{\"utilizadorId\":\""+ANA+"\"}")).andExpect(status().isForbidden());

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(delete("/api/tarefas/"+TAREFA_PARTILHADA+"/responsaveis/"+ANA).session(bruno.session())).andExpect(status().isForbidden());
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id=? AND utilizador_id=?",PROJETO_ANA,BRUNO);
        assign(bruno,TAREFA_PARTILHADA,ANA,403);remove(bruno,TAREFA_PARTILHADA,ANA,403);
        mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/utilizadores-elegiveis").session(bruno.session())).andExpect(status().isForbidden());
    }
    @Test void concurrentDuplicateRequestsReturn201And409OnlyOneRow() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var first=login("ana@example.test");var second=login("ana@example.test");
        var ready=new java.util.concurrent.CountDownLatch(2);var start=new java.util.concurrent.CountDownLatch(1);
        try(var pool=java.util.concurrent.Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Integer> a=()->{ready.countDown();start.await();return mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/responsaveis")
                .session(first.session()).header("X-CSRF-Token",first.token()).contentType("application/json").content("{\"utilizadorId\":\""+ANA+"\"}")).andReturn().getResponse().getStatus();};
            java.util.concurrent.Callable<Integer> b=()->{ready.countDown();start.await();return mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/responsaveis")
                .session(second.session()).header("X-CSRF-Token",second.token()).contentType("application/json").content("{\"utilizadorId\":\""+ANA+"\"}")).andReturn().getResponse().getStatus();};
            var x=pool.submit(a);var y=pool.submit(b);assertThat(ready.await(5,java.util.concurrent.TimeUnit.SECONDS)).isTrue();start.countDown();
            assertThat(List.of(x.get(15,java.util.concurrent.TimeUnit.SECONDS),y.get(15,java.util.concurrent.TimeUnit.SECONDS))).containsExactlyInAnyOrder(201,409);
        }

        // Assert — verificar o resultado.
        assertThat(jdbc.queryForObject("SELECT count(*) FROM atribuicao_tarefa WHERE tarefa_id=?",Integer.class,TAREFA_PESSOAL)).isEqualTo(1);
    }
}
