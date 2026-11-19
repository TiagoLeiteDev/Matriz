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
class CommentTest {
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

    MvcResult comment(Client actor, UUID task, Object content, int expected) throws Exception {
        return mvc.perform(post("/api/tarefas/"+task+"/comentarios").session(actor.session()).header("X-CSRF-Token",actor.token())
            .contentType("application/json").content(mapper.writeValueAsString(Map.of("conteudo",content)))).andExpect(status().is(expected)).andReturn();
    }
    @Test void authorDateContentAndNewSessionPersistence() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno=login("bruno@example.test");var before=java.time.Instant.now();

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var result=comment(bruno,TAREFA_PARTILHADA,"  Informação\nregistada  ",201);

        // Arrange — preparar o próximo cenário/dados.
        var body=mapper.readTree(result.getResponse().getContentAsString());UUID id=UUID.fromString(body.get("id").asText());

        // Assert — verificar o resultado.
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/tarefas/"+TAREFA_PARTILHADA+"/comentarios/"+id);
        assertThat(body.get("autor").get("id").asText()).isEqualTo(BRUNO.toString());assertThat(body.get("autor").has("passwordHash")).isFalse();
        assertThat(java.time.Instant.parse(body.get("criadoEm").asText())).isBetween(before.minusMillis(1),java.time.Instant.now());
        assertThat(jdbc.queryForObject("SELECT conteudo FROM comentario WHERE id=?",String.class,id)).isEqualTo("  Informação\nregistada  ");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var fresh=login("ana@example.test");mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/comentarios").session(fresh.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }
    @Test void boundariesIncludingSupplementaryUnicode() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");String emoji=new String(Character.toChars(0x1F600));

        // Act & Assert — executar o percurso/pedido e verificar o resultado.
        for(String text:List.of("X","a".repeat(2000),emoji.repeat(2000)))comment(ana,TAREFA_PESSOAL,text,201);
        comment(ana,TAREFA_PESSOAL,"a".repeat(2001),422);comment(ana,TAREFA_PESSOAL,emoji.repeat(2001),422);

        // Assert — verificar o resultado.
        assertThat(jdbc.queryForObject("SELECT count(*) FROM comentario WHERE tarefa_id=?",Integer.class,TAREFA_PESSOAL)).isEqualTo(3);
    }
    @Test void invalidAndForgedContentIsRejected() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");

        // Act & Assert — executar o percurso/pedido e verificar o resultado.
        for(Object text:List.of("","  ","\t\r\n","\u00a0\u2003",true,5))comment(ana,TAREFA_PESSOAL,text,422);
        for(String body:List.of("{}","{\"conteudo\":null}"))mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/comentarios").session(ana.session()).header("X-CSRF-Token",ana.token()).contentType("application/json").content(body)).andExpect(status().isUnprocessableEntity());
        for(String field:List.of("autorId","criadoEm","id"))mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/comentarios").session(ana.session()).header("X-CSRF-Token",ana.token()).contentType("application/json").content(mapper.writeValueAsString(Map.of("conteudo","Texto",field,BRUNO.toString())))).andExpect(status().isBadRequest());

        // Assert — verificar o resultado.
        assertThat(count("comentario")).isEqualTo(1);
    }
    @Test void personalForeignAndRevokedAccess() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");var bruno=login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        comment(ana,TAREFA_PESSOAL,"Privado",201);comment(bruno,TAREFA_PESSOAL,"Intrusão",403);comment(ana,TAREFA_PRIVADA,"Intrusão",403);comment(ana,UUID.randomUUID(),"Texto",404);
        mvc.perform(get("/api/tarefas/"+TAREFA_PESSOAL+"/comentarios").session(bruno.session())).andExpect(status().isForbidden());
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id=? AND utilizador_id=?",PROJETO_ANA,BRUNO);
        comment(bruno,TAREFA_PARTILHADA,"Revogado",403);mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/comentarios").session(bruno.session())).andExpect(status().isForbidden());
    }
    @Test void archivedReadOnly() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        var ana=login("ana@example.test");jdbc.update("UPDATE projeto SET arquivado=true WHERE id=?",PROJETO_ANA);
        comment(ana,TAREFA_PARTILHADA,"Novo",409);mvc.perform(get("/api/tarefas/"+TAREFA_PARTILHADA+"/comentarios").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void orderBreaksTiesAndLegacyLongContentRemainsReadable() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");UUID low=UUID.fromString("00000000-0000-4000-8000-000000000101"),high=UUID.fromString("00000000-0000-4000-8000-000000000102");
        var instant=java.sql.Timestamp.from(java.time.Instant.parse("2026-11-18T10:00:00Z"));

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        for(UUID id:List.of(high,low))jdbc.update("INSERT INTO comentario(id,conteudo,tarefa_id,autor_id,criado_em) VALUES(?,?,?,?,?)",id,"Antigo",TAREFA_PESSOAL,ANA,instant);
        UUID legacy=UUID.randomUUID();jdbc.update("INSERT INTO comentario(id,conteudo,tarefa_id,autor_id,criado_em) VALUES(?,?,?,?,?)",legacy,"a".repeat(2001),TAREFA_PESSOAL,BRUNO,java.sql.Timestamp.from(java.time.Instant.parse("2026-11-18T11:00:00Z")));
        mvc.perform(get("/api/tarefas/"+TAREFA_PESSOAL+"/comentarios").session(ana.session())).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(low.toString())).andExpect(jsonPath("$[1].id").value(high.toString())).andExpect(jsonPath("$[2].conteudo").value("a".repeat(2001)));
    }
    @Test void csrfAndMalformedPath() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(post("/api/tarefas/"+TAREFA_PESSOAL+"/comentarios").session(ana.session()).contentType("application/json").content("{\"conteudo\":\"X\"}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/tarefas/"+TAREFA_PESSOAL+"/comentarios")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/tarefas/bad/comentarios").session(ana.session()).header("X-CSRF-Token",ana.token()).contentType("application/json").content("{\"conteudo\":\"X\"}")).andExpect(status().isBadRequest());

        // Assert — verificar o resultado.
        assertThat(count("comentario")).isEqualTo(1);
    }
    @Test void insertFailureRollsBackAndDoesNotLeakDatabaseDetails() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana=login("ana@example.test");jdbc.execute("CREATE FUNCTION reject_comment() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'Test failure'; END $$");
        jdbc.execute("CREATE TRIGGER reject_comment BEFORE INSERT ON comentario FOR EACH ROW EXECUTE FUNCTION reject_comment()");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        try {var result=comment(ana,TAREFA_PESSOAL,"Não gravado",500);assertThat(result.getResponse().getContentAsString()).doesNotContain("Test failure","INSERT","reject_comment");assertThat(count("comentario")).isEqualTo(1);}
        finally {jdbc.execute("DROP TRIGGER reject_comment ON comentario");jdbc.execute("DROP FUNCTION reject_comment()");}
    }
}
