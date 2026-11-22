package com.hotel.macondo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.macondo.entities.TokenCuenta;
import com.hotel.macondo.entities.TokenCuenta.Proposito;

public interface TokenCuentaRepository extends JpaRepository<TokenCuenta, Long> {
    Optional<TokenCuenta> findByTokenHashAndPurpose(String tokenHash, Proposito purpose);
    Optional<TokenCuenta> findFirstByUsuarioIdAndPurposeOrderByCreadoEnDesc(Long usuarioId, Proposito purpose);
    List<TokenCuenta> findByUsuarioIdAndPurposeAndConsumidoEnIsNull(Long usuarioId, Proposito purpose);
}
