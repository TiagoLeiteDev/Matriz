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
class DashboardTest {
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

    @Test void panelCountsPersonalAndAuthorizedProjectsWithExplicitScope()throws Exception{
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");
        mvc.perform(get("/api/painel?incluirIndependentes=true").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.utilizador.id").value(ANA.toString())).andExpect(jsonPath("$.projetos.length()").value(1)).andExpect(jsonPath("$.contagens.TODO").value(2)).andExpect(jsonPath("$.incluiIndependentes").value(true));
        mvc.perform(get("/api/painel?incluirIndependentes=false").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.contagens.TODO").value(1));
        mvc.perform(get("/api/painel?incluirIndependentes=true").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.projetos.length()").value(2)).andExpect(jsonPath("$.contagens.TODO").value(2));
    }
    @Test void allStatesAndArchiveExclusionIndependentOfListFilters()throws Exception{
        var ana=login("ana@example.test");jdbc.update("UPDATE tarefa SET estado='DONE' WHERE id=?",TAREFA_PESSOAL);jdbc.update("UPDATE tarefa SET estado='IN_PROGRESS' WHERE id=?",TAREFA_PARTILHADA);
        mvc.perform(get("/api/painel?incluirIndependentes=true&q=doesnotexist&estado=TODO").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.contagens.TODO").value(0)).andExpect(jsonPath("$.contagens.IN_PROGRESS").value(1)).andExpect(jsonPath("$.contagens.DONE").value(1));
        jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        mvc.perform(get("/api/painel?incluirIndependentes=true").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.projetos.length()").value(0)).andExpect(jsonPath("$.contagens.IN_PROGRESS").value(0)).andExpect(jsonPath("$.contagens.DONE").value(1));
    }
    @Test void emptyUserGetsZeroesAndRevocationTakesEffect()throws Exception{
        UUID other=UUID.randomUUID();jdbc.update("INSERT INTO utilizador(id,nome,email,password_hash) VALUES(?,'Outra','outra@example.test',?)",other,encoder.encode(PASSWORD));var user=login("outra@example.test");
        mvc.perform(get("/api/painel?incluirIndependentes=true").session(user.session())).andExpect(status().isOk()).andExpect(jsonPath("$.projetos.length()").value(0)).andExpect(jsonPath("$.contagens.TODO").value(0)).andExpect(jsonPath("$.contagens.IN_PROGRESS").value(0)).andExpect(jsonPath("$.contagens.DONE").value(0));
        var bruno=login("bruno@example.test");jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id=? AND utilizador_id=?",PROJETO_ANA,BRUNO);
        mvc.perform(get("/api/painel?incluirIndependentes=true").session(bruno.session())).andExpect(status().isOk()).andExpect(jsonPath("$.projetos.length()").value(1)).andExpect(jsonPath("$.contagens.TODO").value(1));
    }
    @Test void authenticationAndExplicitStrictBooleanRequired()throws Exception{
        mvc.perform(get("/api/painel?incluirIndependentes=true")).andExpect(status().isUnauthorized());var ana=login("ana@example.test");
        for(String query:List.of("","?incluirIndependentes=","?incluirIndependentes=1","?incluirIndependentes=TRUE"))mvc.perform(get("/api/painel"+query).session(ana.session())).andExpect(status().isBadRequest());
    }
}
