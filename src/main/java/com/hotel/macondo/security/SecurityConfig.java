package com.hotel.macondo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTGenerator jwtGenerator,
            UserDetailsServiceImpl userDetailsService, JwtAuthEntryPoint authEntryPoint,
            ClienteAuthorization clienteAuthorization) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado")))
                .authorizeHttpRequests(requests -> requests
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/h2/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**", "/api/cliente/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/verificacion/confirmar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagos/mercadopago/eventos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/testimonio", "/api/tipo-habitacion",
                                "/api/tipo-habitacion/find/{id}", "/api/servicio/catalogo",
                                "/api/servicio/recomendaciones", "/api/servicio/relacionados/{id}",
                                "/api/servicio/find/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/promociones/publicas", "/api/promociones/calcular").permitAll()

                        // Estas reglas van antes de las rutas generales de reservas y operadores.
                        .requestMatchers(HttpMethod.GET, "/api/cliente/find/{id}")
                        .access((authentication, context) -> new AuthorizationDecision(
                                clienteAuthorization.puedeConsultar(authentication.get(),
                                        context.getVariables().get("id"))))
                        .requestMatchers(HttpMethod.POST, "/api/cliente/{id}/perfil",
                                "/api/cliente/{id}/password", "/api/cliente/delete/{id}")
                        .access((authentication, context) -> new AuthorizationDecision(
                                clienteAuthorization.esPropietario(authentication.get(),
                                        context.getVariables().get("id"))))
                        .requestMatchers("/api/reserva/cliente/{id}", "/api/reserva/cliente/{id}/**")
                        .access((authentication, context) -> new AuthorizationDecision(
                                clienteAuthorization.esPropietario(authentication.get(),
                                        context.getVariables().get("id"))))

                        .requestMatchers("/api/operador/cuenta/**", "/api/reserva", "/api/reserva/**")
                        .hasAnyAuthority("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers("/api/promociones", "/api/promociones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/habitacion", "/api/habitacion/**",
                                "/api/servicio").hasAnyAuthority("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/habitacion/estado/{id}")
                        .hasAnyAuthority("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers("/api/operador", "/api/operador/**", "/api/habitacion",
                                "/api/habitacion/**", "/api/tipo-habitacion", "/api/tipo-habitacion/**",
                                "/api/servicio", "/api/servicio/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/comentario/publicados").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/comentario/{id}/publicar")
                        .hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/comentario").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/comentario/cliente/{clienteId}")
                        .hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/comentario/{id}").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/comentario/{id}").hasAuthority("CLIENTE")
                        .anyRequest().denyAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtGenerator, userDetailsService, authEntryPoint),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
