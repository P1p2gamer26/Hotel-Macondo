package com.hotel.macondo.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTGenerator {

    private static final long JWT_EXPIRATION = 3_600_000;

    // Como en clase, la llave se genera al arrancar y se comparte al firmar y validar.
    private final SecretKey key = Jwts.SIG.HS512.key().build();

    public String generateToken(Authentication authentication) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        // El parser verifica la firma y la fecha de vencimiento antes de devolver el correo.
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
