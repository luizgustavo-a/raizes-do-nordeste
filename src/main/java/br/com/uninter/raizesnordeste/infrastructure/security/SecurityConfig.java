package br.com.uninter.raizesnordeste.infrastructure.security;

import br.com.uninter.raizesnordeste.infrastructure.exception.StandardErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, ObjectMapper objectMapper) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                // Retorna 401 padronizado quando o token não for fornecido ou for inválido
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    StandardErrorDTO error = new StandardErrorDTO(
                            "NAO_AUTORIZADO",
                            "Acesso não autorizado. É necessário fornecer um token Bearer válido.",
                            request.getRequestURI()
                    );
                    objectMapper.writeValue(response.getOutputStream(), error);
                })
                // Retorna 403 padronizado quando o usuário não tiver a Role requerida
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    StandardErrorDTO error = new StandardErrorDTO(
                            "ACESSO_NEGADO",
                            "Você não possui o perfil necessário para acessar este recurso.",
                            request.getRequestURI()
                    );
                    objectMapper.writeValue(response.getOutputStream(), error);
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Rotas Públicas
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/unidades/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Consulta de auditoria restrita a gerentes e administradores
                .requestMatchers("/auditoria/**").hasAnyRole("GERENTE", "ADMIN")

                // Atualização de status operacional (Cozinha/Balcão)
                .requestMatchers(HttpMethod.PATCH, "/pedidos/*/status").hasAnyRole("ATENDENTE", "GERENTE", "ADMIN")

                // Pedidos e Pagamentos: Qualquer usuário autenticado (incluindo CLIENTE)
                .requestMatchers("/pedidos/**", "/pagamentos/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
