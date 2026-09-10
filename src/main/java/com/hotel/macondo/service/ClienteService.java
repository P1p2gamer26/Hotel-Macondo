package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;

public interface ClienteService {

    /** Retorna todos los clientes registrados. */
    Collection<Cliente> buscarTodos();

    /** Busca un cliente por identificador. 
     * @throws RecursoNoEncontradoException si no se encuentra el cliente.
    */
    Cliente buscarPorId(Long id);

    /** Busca un cliente por cedula. 
     * @throws RecursoNoEncontradoException si no se encuentra el cliente.
    */
    Cliente buscarPorCedula(String cedula);

    /** Crea o actualiza un cliente. */
    Cliente guardar(Cliente cliente);

    /** Elimina un cliente por identificador. */
    void eliminar(Long id);

    /** Obtiene la reserva activa de un cliente. */
    Reserva obtenerReservaActiva(Cliente cliente);

    /** Obtiene la habitación activa de una reserva. */
    Habitacion obtenerHabitacionActiva(Reserva reserva);

    /** Calcula el número de noches de una reserva. */
    long calcularNoches(Reserva reserva);

    /** Cuenta el número de reservas activas de un cliente. */
    int contarReservasActivas(Cliente cliente);

    /** Cuenta el número de reservas en el historial de un cliente. */
    int contarHistorialReservas(Cliente cliente);

    /** Obtiene la fecha actual formateada. */
    String obtenerFechaActualFormateada();
}
