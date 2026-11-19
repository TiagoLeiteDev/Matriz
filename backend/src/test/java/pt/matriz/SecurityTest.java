package pt.matriz;

import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.matriz.bootstrap.DemoSeeder;
import pt.matriz.identity.AccountPrincipal;
import pt.matriz.projects.ProjectAccess;
import pt.matriz.tasks.TaskAccess;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pt.matriz.bootstrap.DemoSeeder.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class SecurityTest {
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
    @Autowired ProjectAccess projects;
    @Autowired TaskAccess tasks;
    @Autowired org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext server;

    @BeforeEach void fixtures() {
        SecurityContextHolder.clearContext();
        jdbc.execute("TRUNCATE utilizador, projeto, tarefa, comentario, colaboracao_projeto, atribuicao_tarefa CASCADE");
        new TransactionTemplate(transactions).executeWithoutResult(tx -> new DemoSeeder(jdbc, encoder, PASSWORD).run(null));
    }
    record Prepared(MockHttpSession session, String token) {}
    Prepared prepare() throws Exception {
        var result = mvc.perform(get("/api/sessao/csrf")).andExpect(status().isOk()).andReturn();
        return new Prepared((MockHttpSession) result.getRequest().getSession(false),
            mapper.readTree(result.getResponse().getContentAsString()).get("token").asText());
    }
    String token(MockHttpSession session) throws Exception {
        var result = mvc.perform(get("/api/sessao/csrf").session(session)).andExpect(status().isOk()).andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
    MockHttpSession login(String email) throws Exception {
        Prepared before = prepare();
        mvc.perform(post("/api/sessao").session(before.session()).header("X-CSRF-Token", before.token())
            .contentType("application/json").content(mapper.writeValueAsString(java.util.Map.of("email", email, "palavraPasse", PASSWORD))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email));
        return before.session();
    }

    @Test void realServletContainerUsesApprovedTimeoutAndCookieOnlySessions() {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.

        // Assert — verificar o resultado.
        assertThat(server.getServletContext().getSessionTimeout()).isEqualTo(60);
        assertThat(server.getServletContext().getEffectiveSessionTrackingModes())
            .containsExactly(jakarta.servlet.SessionTrackingMode.COOKIE);
        assertThat(server.getServletContext().getSessionCookieConfig().getName()).isEqualTo("matriz_session");
        assertThat(server.getServletContext().getSessionCookieConfig().isHttpOnly()).isTrue();
    }

    @Test void loginRotatesSessionErasesCredentialsAndReturnsOnlyProfile() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        Prepared prepared = prepare();
        String old = prepared.session().getId();

        // Act — executar a operação em teste.
        mvc.perform(post("/api/sessao").session(prepared.session()).header("X-CSRF-Token", prepared.token())
            .contentType("application/json").content("{\"email\":\"ana@example.test\",\"palavraPasse\":\"" + PASSWORD + "\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(ANA.toString()))
            .andExpect(jsonPath("$.password_hash").doesNotExist()).andExpect(jsonPath("$.palavraPasse").doesNotExist());

        // Assert — verificar o resultado.
        assertThat(prepared.session().getId()).isNotEqualTo(old);
        SecurityContext context = (SecurityContext) prepared.session().getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        assertThat(context.getAuthentication().getCredentials()).isNull();
        assertThat(((AccountPrincipal) context.getAuthentication().getPrincipal()).getPassword()).isNull();

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/sessao").session(prepared.session())).andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Ana Demo")).andExpect(jsonPath("$.email").value("ana@example.test"));
    }

    @Test void wrongPasswordAndUnknownAccountHaveSameGenericResponse() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        String previous = null;
        for (String email : new String[]{"ana@example.test", "unknown@example.test"}) {
            Prepared p = prepare();
            var response = mvc.perform(post("/api/sessao").session(p.session()).header("X-CSRF-Token", p.token())
                .contentType("application/json").content("{\"email\":\"" + email + "\",\"palavraPasse\":\"incorrect\"}"))
                .andExpect(status().isUnauthorized()).andExpect(content().contentTypeCompatibleWith("application/problem+json")).andReturn();
            String body = response.getResponse().getContentAsString();
            assertThat(body).doesNotContain("password_hash", PASSWORD, "unknown@example.test");
            if (previous != null) assertThat(body).isEqualTo(previous);
            previous = body;
            mvc.perform(get("/api/sessao").session(p.session())).andExpect(status().isUnauthorized());
        }
    }

    @Test void csrfIsRequiredForLoginAndCannotBeBorrowedFromAnotherSession() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        Prepared a = prepare(), b = prepare();
        String body = "{\"email\":\"ana@example.test\",\"palavraPasse\":\"" + PASSWORD + "\"}";

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(post("/api/sessao").session(a.session()).contentType("application/json").content(body)).andExpect(status().isForbidden());

        // Act — executar a operação em teste.
        mvc.perform(post("/api/sessao").session(b.session()).header("X-CSRF-Token", a.token())
            .contentType("application/json").content(body)).andExpect(status().isForbidden());
    }

    @Test void logoutRejectsOldCsrfAndRevokesSession() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        Prepared p = prepare();

        // Act — executar a operação em teste.
        mvc.perform(post("/api/sessao").session(p.session()).header("X-CSRF-Token", p.token())
            .contentType("application/json").content("{\"email\":\"ana@example.test\",\"palavraPasse\":\"" + PASSWORD + "\"}"))
            .andExpect(status().isOk());

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(delete("/api/sessao").session(p.session()).header("X-CSRF-Token", p.token())).andExpect(status().isForbidden());

        // Act — executar a operação em teste.
        mvc.perform(delete("/api/sessao").session(p.session()).header("X-CSRF-Token", token(p.session())))
            .andExpect(status().isNoContent()).andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));

        // Assert — verificar o resultado.
        assertThat(p.session().isInvalid()).isTrue();

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/sessao")).andExpect(status().isUnauthorized());
    }

    @Test void missingOrUnknownSessionCannotReadAnyProtectedResource() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        for (String path : new String[]{"/api/sessao", "/api/projetos/" + PROJETO_ANA,
                "/api/tarefas/" + TAREFA_PARTILHADA, "/api/tarefas/" + TAREFA_PARTILHADA + "/comentarios",
                "/api/tarefas/" + TAREFA_PARTILHADA + "/responsaveis"}) {
            mvc.perform(get(path)).andExpect(status().isUnauthorized());
            mvc.perform(get(path).cookie(new jakarta.servlet.http.Cookie("matriz_session", "unknown")))
                .andExpect(status().isUnauthorized());
        }
    }

    @Test void identitiesHaveDifferentAccessAndPersonalTasksArePrivate() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var ana = login("ana@example.test"); var bruno = login("bruno@example.test");

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos/" + PROJETO_ANA).session(ana)).andExpect(status().isOk());
        mvc.perform(get("/api/projetos/" + PROJETO_ANA).session(bruno)).andExpect(status().isOk());
        mvc.perform(get("/api/projetos/" + PROJETO_BRUNO).session(ana)).andExpect(status().isForbidden());
        mvc.perform(get("/api/projetos/" + PROJETO_BRUNO).session(bruno)).andExpect(status().isOk());

        // Arrange — preparar o próximo cenário/dados.
        for (String suffix : new String[]{"", "/comentarios", "/responsaveis"}) {
            mvc.perform(get("/api/tarefas/" + TAREFA_PESSOAL + suffix).session(bruno)).andExpect(status().isForbidden());
            mvc.perform(get("/api/tarefas/" + TAREFA_PESSOAL + suffix).session(ana)).andExpect(status().isOk());
            mvc.perform(get("/api/tarefas/" + TAREFA_PRIVADA + suffix).session(ana)).andExpect(status().isForbidden());
        }

        // Act — executar a operação em teste.
        mvc.perform(get("/api/tarefas/" + TAREFA_PARTILHADA + "/comentarios").session(bruno))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].autor.id").value(BRUNO.toString()));
        mvc.perform(get("/api/tarefas/" + TAREFA_PARTILHADA + "/responsaveis").session(ana))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test void revokedMembershipAndHistoricalAssignmentDoNotGrantAccess() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno = login("bruno@example.test");

        // Arrange — preparar os dados do cenário.
        jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id = ? AND utilizador_id = ?", PROJETO_ANA, BRUNO);
        jdbc.update("UPDATE tarefa SET criador_id = ? WHERE id = ?", BRUNO, TAREFA_PARTILHADA);

        // Arrange — preparar o próximo cenário/dados.
        for (String path : new String[]{"/api/projetos/" + PROJETO_ANA, "/api/tarefas/" + TAREFA_PARTILHADA,
            "/api/tarefas/" + TAREFA_PARTILHADA + "/comentarios", "/api/tarefas/" + TAREFA_PARTILHADA + "/responsaveis"}) {
            mvc.perform(get(path).session(bruno)).andExpect(status().isForbidden());
        }
    }

    @Test void ownershipArchiveAndEligibilityAreCheckedFromAuthenticatedActor() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var bruno = login("bruno@example.test");
        var context = (SecurityContext) bruno.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        SecurityContextHolder.setContext(context);
        try {
            assertThatThrownBy(() -> projects.requireOwner(PROJETO_ANA)).isInstanceOf(AccessDeniedException.class);
            assertThat(projects.requireOwner(PROJETO_BRUNO).owner()).isEqualTo(BRUNO);
            tasks.requireEligible(TAREFA_PARTILHADA, ANA);
            jdbc.update("UPDATE projeto SET arquivado = true WHERE id = ?", PROJETO_ANA);
            assertThatThrownBy(() -> tasks.requireManage(TAREFA_PARTILHADA)).isInstanceOfSatisfying(ResponseStatusException.class,
                error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        } finally { SecurityContextHolder.clearContext(); }
        var ana = login("ana@example.test");
        SecurityContextHolder.setContext((SecurityContext) ana.getAttribute(SPRING_SECURITY_CONTEXT_KEY));
        try {
            tasks.requireEligible(TAREFA_PESSOAL, ANA);
            assertThatThrownBy(() -> tasks.requireEligible(TAREFA_PESSOAL, BRUNO)).isInstanceOf(AccessDeniedException.class);
            assertThatThrownBy(() -> tasks.requireTransfer(TAREFA_PESSOAL, PROJETO_BRUNO)).isInstanceOf(AccessDeniedException.class);
        } finally { SecurityContextHolder.clearContext(); }
    }

    @Test void identityInjectionAndInvalidRequestDataAreRejected() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        Prepared p = prepare();

        // Act — executar a operação em teste.
        mvc.perform(post("/api/sessao").session(p.session()).header("X-CSRF-Token", p.token()).contentType("application/json")
            .content("{\"email\":\"ana@example.test\",\"palavraPasse\":\"" + PASSWORD + "\",\"proprietarioId\":\"" + BRUNO + "\"}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/sessao").session(p.session()).header("X-CSRF-Token", p.token()).contentType("application/json")
            .content("{\"email\":\"invalid\",\"palavraPasse\":\" \"}")).andExpect(status().isUnprocessableEntity());

        // Arrange — preparar o próximo cenário/dados.
        var ana = login("ana@example.test");

        // Act — executar a operação em teste.
        mvc.perform(get("/api/sessao").session(ana).param("utilizadorId", BRUNO.toString()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(ANA.toString()));

        // Act & Assert — executar e verificar no mesmo pedido/helper ou asserção.
        mvc.perform(get("/api/projetos/not-a-uuid").session(ana)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/projetos/" + UUID.randomUUID()).session(ana)).andExpect(status().isNotFound());
    }

    @Test void hostileOriginIsRejectedEvenWithCorrectCsrf() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        Prepared p = prepare();

        // Act — executar a operação em teste.
        mvc.perform(post("/api/sessao").session(p.session()).header("X-CSRF-Token", p.token())
            .header("Origin", "https://attacker.example").contentType("application/json")
            .content("{\"email\":\"ana@example.test\",\"palavraPasse\":\"" + PASSWORD + "\"}"))
            .andExpect(status().isForbidden());
    }

    @Test void authenticatedUsersStillCannotCallUnimplementedWrites() throws Exception {
        // Arrange — preparar dados; fixtures partilhadas nos hooks quando aplicável.
        var session = login("ana@example.test");

        // Act — executar a operação em teste.
        mvc.perform(delete("/api/tarefas/" + TAREFA_PARTILHADA + "/comentarios/00000000-0000-4000-8000-000000000031").session(session).header("X-CSRF-Token", token(session))
            .contentType("application/json").content("{\"email\":\"outra@example.test\"}"))
            .andExpect(status().isForbidden());
    }
}
