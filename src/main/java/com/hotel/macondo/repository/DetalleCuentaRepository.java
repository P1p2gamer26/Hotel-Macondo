package com.hotel.macondo.repository;

import com.hotel.macondo.entities.DetalleCuenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCuentaRepository extends JpaRepository<DetalleCuenta, Long> {

  List<DetalleCuenta> findByCuentaIdOrderByFechaRegistroAsc(Long cuentaId);

  Optional<DetalleCuenta> findByIdAndCuentaId(Long id, Long cuentaId);

  void deleteAllByCuentaId(Long cuentaId);
}
