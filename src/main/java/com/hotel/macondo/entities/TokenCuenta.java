package com.hotel.macondo.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_tokens", indexes = {
    @Index(name = "idx_account_token_hash", columnList = "token_hash", unique = true),
    @Index(name = "idx_account_token_user_purpose", columnList = "user_id,purpose")
})
@Getter
@Setter
@NoArgsConstructor
public class TokenCuenta {
    public enum Proposito { VERIFICAR_CORREO, RECUPERAR_CONTRASENA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Proposito purpose;

    @Column(nullable = false)
    private Instant expiraEn;

    private Instant consumidoEn;

    @Column(nullable = false)
    private Instant creadoEn = Instant.now();
}
