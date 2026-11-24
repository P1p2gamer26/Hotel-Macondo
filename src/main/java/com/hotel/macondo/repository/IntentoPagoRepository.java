package com.hotel.macondo.repository;

import com.hotel.macondo.entities.IntentoPago;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntentoPagoRepository extends JpaRepository<IntentoPago, Long> {
  Optional<IntentoPago> findByReferencia(String referencia);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select intento from IntentoPago intento where intento.transaccionId = :transaccionId")
  Optional<IntentoPago> findByTransaccionIdForUpdate(@Param("transaccionId") String transaccionId);
}
