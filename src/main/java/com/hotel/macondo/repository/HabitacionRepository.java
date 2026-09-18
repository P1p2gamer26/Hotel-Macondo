package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Habitacion;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

  List<Habitacion> findAllByOrderByIdAsc();

  Optional<Habitacion> findByNombreIgnoreCase(String nombre);

  List<Habitacion> findByCapacidadGreaterThanEqualOrderByIdAsc(int capacidad);

  long countByEstado(String estado);

  List<Habitacion> findByTipoHabitacionId(Long tipoId);

  Optional<Habitacion> findFirstByTipoHabitacionIdAndEstadoOrderByIdAsc(
      Long tipoId, String estado);

  @Query(
      """
      SELECT h
      FROM Habitacion h
      JOIN FETCH h.tipoHabitacion t
      WHERE t.id = :tipoId
        AND UPPER(h.estado) = 'DISPONIBLE'
        AND h.capacidad >= :cantidadPersonas
        AND NOT EXISTS (
          SELECT r.id
          FROM Reserva r
          WHERE r.habitacion = h
            AND (:reservaExcluidaId IS NULL OR r.id <> :reservaExcluidaId)
            AND UPPER(r.estado) IN :estadosOcupados
            AND r.fechaInicio < :fechaSalida
            AND r.fechaFin > :fechaEntrada
        )
      ORDER BY h.id
      """)
  List<Habitacion> buscarDisponibles(
      @Param("tipoId") Long tipoId,
      @Param("fechaEntrada") LocalDate fechaEntrada,
      @Param("fechaSalida") LocalDate fechaSalida,
      @Param("cantidadPersonas") Integer cantidadPersonas,
      @Param("estadosOcupados") Collection<String> estadosOcupados,
      @Param("reservaExcluidaId") Long reservaExcluidaId);

  boolean existsByTipoHabitacionId(Long tipoId);
}
