package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Cuenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

  List<Cuenta> findAllByOrderByIdAsc();
}
