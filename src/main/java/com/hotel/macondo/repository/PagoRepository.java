package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

  // Retorna los pagos de todas las reservas del cliente, del más reciente al más antiguo
  @Query(
      "SELECT p FROM Pago p JOIN p.cuenta c JOIN c.reserva r "
          + "WHERE r.cliente.id = :clienteId ORDER BY p.fechaPago DESC")
  List<Pago> buscarPorClienteId(@Param("clienteId") Long clienteId);
}
