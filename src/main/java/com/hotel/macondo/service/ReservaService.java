package com.hotel.macondo.service;

import java.util.List;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Reserva;

public interface ReservaService {

    /** Reservas del hotel que siguen en un estado operativo abierto. */
    List<Reserva> obtenerReservasActivas();

    /** Reservas del hotel ya finalizadas o canceladas. */
    List<Reserva> obtenerHistorial();

    /** Busca una reserva por su numero. */
    Reserva buscarPorId(String id);

    /** Reservas vigentes o futuras que pertenecen al cliente indicado. */
    List<Reserva> buscarActivasDeCliente(Cliente cliente);

    /** Reservas finalizadas o canceladas del cliente indicado. */
    List<Reserva> buscarHistorialDeCliente(Cliente cliente);
}
