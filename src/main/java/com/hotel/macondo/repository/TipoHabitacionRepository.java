package com.hotel.macondo.repository;

import com.hotel.macondo.entities.TipoHabitacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {

  List<TipoHabitacion> findAllByOrderByIdAsc();

  List<TipoHabitacion> findByCapacidadPersonasGreaterThanEqualOrderByIdAsc(int capacidad);
}
