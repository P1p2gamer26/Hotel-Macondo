package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Servicio;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

  List<Servicio> findAllByOrderByIdAsc();

  List<Servicio> findByActivoTrueOrderByIdAsc();

  List<Servicio> findByActivoTrueOrderByIdAsc(Pageable pageable);

  List<Servicio> findByActivoTrueAndIdNotOrderByIdAsc(Long id, Pageable pageable);

  long countByActivoTrue();
}
