package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Promocion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {
  Optional<Promocion> findByCodigoIgnoreCase(String codigo);
  List<Promocion> findAllByOrderByIdDesc();
}
