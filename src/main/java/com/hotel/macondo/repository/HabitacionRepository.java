package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Habitacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

  List<Habitacion> findAllByOrderByIdAsc();

  Optional<Habitacion> findByNombreIgnoreCase(String nombre);

  List<Habitacion> findByCapacidadGreaterThanEqualOrderByIdAsc(int capacidad);

  long countByEstado(String estado);

  List<Habitacion> findByTipoHabitacionId(Long tipoId);

  boolean existsByTipoHabitacionId(Long tipoId);
}
