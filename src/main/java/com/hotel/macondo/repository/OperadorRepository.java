package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Operador;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

  List<Operador> findAllByOrderByIdAsc();

  long countByActivoTrue();
}
