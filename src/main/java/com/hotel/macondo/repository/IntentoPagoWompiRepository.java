package com.hotel.macondo.repository;

import com.hotel.macondo.entities.IntentoPagoWompi;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntentoPagoWompiRepository extends JpaRepository<IntentoPagoWompi, Long> {
  Optional<IntentoPagoWompi> findByReferencia(String referencia);
}
