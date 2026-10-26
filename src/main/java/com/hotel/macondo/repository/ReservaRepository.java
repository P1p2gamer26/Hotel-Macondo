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

  // Cuenta las otras reservas activas de la habitación, para no activar dos a la vez
  @Query(
      """
      SELECT COUNT(r)
      FROM Reserva r
      WHERE r.habitacion.id = :habitacionId
        AND r.id <> :reservaId
        AND UPPER(r.estado) = 'ACTIVA'
      """)
  long contarOtrasReservasActivasPorHabitacion(
      @Param("habitacionId") Long habitacionId,
      @Param("reservaId") Long reservaId);

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

  // Ids de reservas activas con cuenta abierta de una habitación por su número
  @Query(
      """
      SELECT r.id
      FROM Reserva r
      JOIN r.habitacion h
      JOIN r.cuenta c
      WHERE h.numero = :numeroHabitacion
        AND UPPER(r.estado) = 'ACTIVA'
        AND UPPER(c.estado) = 'ABIERTA'
      """)
  List<Long> buscarIdsActivosConCuentaAbiertaPorNumeroHabitacion(
      @Param("numeroHabitacion") String numeroHabitacion);

  // Ids de reservas activas de una habitación por su número (check-out)
  @Query(
      """
      SELECT r.id
      FROM Reserva r
      JOIN r.habitacion h
      WHERE h.numero = :numeroHabitacion
        AND UPPER(r.estado) = 'ACTIVA'
      """)
  List<Long> buscarIdsActivosPorNumeroHabitacion(
      @Param("numeroHabitacion") String numeroHabitacion);

  /**
   * Detalle completo de una reserva para el panel del operador: cliente,
   * habitacion con su tipo y la cuenta con sus consumos (servicios). Los pagos
   * se cargan aparte en el servicio porque hacer JOIN FETCH de dos listas a la
   * vez lanza MultipleBagFetchException.
   */
  @Query(
      """
      SELECT DISTINCT r
      FROM Reserva r
      LEFT JOIN FETCH r.cliente
      LEFT JOIN FETCH r.habitacion h
      LEFT JOIN FETCH h.tipoHabitacion
      LEFT JOIN FETCH r.cuenta c
      LEFT JOIN FETCH c.detalles d
      LEFT JOIN FETCH d.servicio
      WHERE r.numeroReserva = :numeroReserva
      """)
  Optional<Reserva> buscarDetalleCompleto(@Param("numeroReserva") String numeroReserva);

  // Carga el detalle completo de la reserva por su id (cliente, habitación, cuenta y consumos)
  @Query(
      """
      SELECT DISTINCT r
      FROM Reserva r
      LEFT JOIN FETCH r.cliente
      LEFT JOIN FETCH r.habitacion h
      LEFT JOIN FETCH h.tipoHabitacion
      LEFT JOIN FETCH r.cuenta c
      LEFT JOIN FETCH c.detalles d
      LEFT JOIN FETCH d.servicio
      WHERE r.id = :reservaId
      """)
  Optional<Reserva> buscarDetalleCompletoPorId(@Param("reservaId") Long reservaId);
}
