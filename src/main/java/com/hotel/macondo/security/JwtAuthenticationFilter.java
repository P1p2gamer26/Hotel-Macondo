package com.hotel.macondo.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTGenerator jwtGenerator;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint authEntryPoint;

    public JwtAuthenticationFilter(JWTGenerator jwtGenerator,
            UserDetailsServiceImpl userDetailsService, JwtAuthEntryPoint authEntryPoint) {
        this.jwtGenerator = jwtGenerator;
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                String correo = jwtGenerator.getUsernameFromJWT(authorization.substring(7));
                UserDetails usuario = userDetailsService.loadUserByUsername(correo);
                if (!usuario.isEnabled()) {
                    throw new DisabledException("Acceso desactivado");
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                authEntryPoint.commence(request, response,
                        new BadCredentialsException("Token inválido o vencido", ex));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
