package pt.matriz.identity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.NullRequestCache;
import pt.matriz.shared.ApiProblem;

@Configuration
public class SecurityConfiguration {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean AuthenticationManager authenticationManager(AccountRepository accounts, PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(accounts::credentials);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }
    @Bean HttpSessionSecurityContextRepository contextRepository() {
        var repo = new HttpSessionSecurityContextRepository();
        repo.setDisableUrlRewriting(true);
        return repo;
    }
    @Bean HttpSessionCsrfTokenRepository csrfRepository() {
        var repo = new HttpSessionCsrfTokenRepository();
        repo.setHeaderName("X-CSRF-Token");
        return repo;
    }
    @Bean SessionAuthenticationStrategy sessionStrategy(HttpSessionCsrfTokenRepository csrf) {
        return new CompositeSessionAuthenticationStrategy(List.of(
            new ChangeSessionIdAuthenticationStrategy(), new CsrfAuthenticationStrategy(csrf)));
    }
    @Bean SecurityFilterChain security(HttpSecurity http, HttpSessionSecurityContextRepository context,
            HttpSessionCsrfTokenRepository csrf, ObjectMapper mapper,
            @Value("${matriz.security.allowed-origins}") String origins) throws Exception {
        Set<String> allowed = Arrays.stream(origins.split(",")).map(String::trim).collect(Collectors.toUnmodifiableSet());
        return http
            .securityContext(config -> config.securityContextRepository(context).requireExplicitSave(true))
            .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .requestCache(config -> config.requestCache(new NullRequestCache()))
            .csrf(config -> config.csrfTokenRepository(csrf))
            .addFilterBefore(new OriginGuard(allowed, mapper), CsrfFilter.class)
            .authorizeHttpRequests(access -> access
                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/assets/**", "/actuator/health", "/api/sessao/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/sessao").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sessao").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/sessao").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/projetos").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/projetos").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/projetos/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/projetos/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/projetos/*", "/api/tarefas/*",
                    "/api/tarefas/*/comentarios", "/api/tarefas/*/responsaveis").authenticated()
                .anyRequest().denyAll())
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, error) ->
                    ApiProblem.write(mapper, response, 401, "NAO_AUTENTICADO", "Inicia sessão para continuar."))
                .accessDeniedHandler((request, response, error) ->
                    ApiProblem.write(mapper, response, 403, "ACESSO_NEGADO", "Não tens permissão ou o token CSRF é inválido.")))
            .formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable()).rememberMe(remember -> remember.disable())
            .build();
    }
}
