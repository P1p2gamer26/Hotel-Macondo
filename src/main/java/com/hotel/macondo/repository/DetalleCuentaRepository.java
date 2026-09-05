package com.hotel.macondo.repository;

import com.hotel.macondo.entities.DetalleCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCuentaRepository extends JpaRepository<DetalleCuenta, Long> {

  void deleteAllByCuentaId(Long cuentaId);
}
