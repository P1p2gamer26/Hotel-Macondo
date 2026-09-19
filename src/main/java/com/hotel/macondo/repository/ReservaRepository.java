package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Reserva;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

  List<Reserva> findAllByOrderByIdAsc();

  Optional<Reserva> findByNumeroReserva(String numeroReserva);

  boolean existsByNumeroReserva(String numeroReserva);

  List<Reserva> findByEstadoInOrderByIdAsc(Collection<String> estados);

  @Query(
      """
      SELECT r
      FROM Reserva r
      LEFT JOIN FETCH r.habitacion h
      LEFT JOIN FETCH h.tipoHabitacion
      WHERE r.cliente = :cliente
        AND UPPER(r.estado) IN ('ACTIVA', 'CONFIRMADA', 'PENDIENTE')
        AND r.fechaFin >= :fechaActual
      ORDER BY r.fechaInicio
      """)
  List<Reserva> buscarVigentesPorCliente(
      @Param("cliente") Cliente cliente, @Param("fechaActual") LocalDate fechaActual);

  @Query(
      """
      SELECT r
      FROM Reserva r
      LEFT JOIN FETCH r.habitacion h
      LEFT JOIN FETCH h.tipoHabitacion
      WHERE r.cliente = :cliente
        AND (UPPER(r.estado) = 'CANCELADA' OR r.fechaFin < :fechaActual)
      ORDER BY r.fechaInicio DESC
      """)
  List<Reserva> buscarHistoricasPorCliente(
      @Param("cliente") Cliente cliente, @Param("fechaActual") LocalDate fechaActual);

  @Query(
      """
      SELECT COUNT(r)
      FROM Reserva r
      WHERE r.cliente = :cliente
        AND UPPER(r.estado) IN ('ACTIVA', 'CONFIRMADA', 'PENDIENTE')
        AND r.fechaFin >= :fechaActual
      """)
  long contarVigentesPorCliente(
      @Param("cliente") Cliente cliente, @Param("fechaActual") LocalDate fechaActual);

  @Query(
      """
      SELECT COUNT(r)
      FROM Reserva r
      WHERE r.cliente = :cliente
        AND (UPPER(r.estado) = 'CANCELADA' OR r.fechaFin < :fechaActual)
      """)
  long contarHistoricasPorCliente(
      @Param("cliente") Cliente cliente, @Param("fechaActual") LocalDate fechaActual);

  /**
   * Reservas que usan la habitacion indicada. Trae el cliente en la misma
   * consulta porque el aviso de borrado se pinta despues del redirect, cuando
   * las entidades ya estan desligadas de la sesion.
   */
  @Query(
      """
      SELECT r
      FROM Reserva r
      JOIN FETCH r.habitacion h
      LEFT JOIN FETCH h.tipoHabitacion
      LEFT JOIN FETCH r.cliente
      WHERE h.id = :idHabitacion
      ORDER BY r.fechaInicio
      """)
  List<Reserva> buscarPorHabitacion(@Param("idHabitacion") Long idHabitacion);
}
