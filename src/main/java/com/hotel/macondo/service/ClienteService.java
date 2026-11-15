package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;

public interface ClienteService {

    /**
     * Busca un cliente por identificador.
     * 
     * @throws RecursoNoEncontradoException si no se encuentra el cliente.
     */
    Cliente buscarPorId(Long id);

    /** Crea o actualiza un cliente. */
    Cliente guardar(Cliente cliente);

    /** Elimina un cliente por identificador. */
    void eliminar(Long id);

    /** Obtiene la reserva activa de un cliente. */
    Reserva obtenerReservaActiva(Cliente cliente);

    /** Calcula el número de noches de una reserva. */
    long calcularNoches(Reserva reserva);

}
