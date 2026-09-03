package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;

public interface ClienteService {

    /** Retorna todos los clientes registrados. */
    Collection<Cliente> buscarTodos();

    /** Busca un cliente por identificador. */
    Cliente buscarPorId(Integer id);

    /** Busca un cliente por cedula. */
    Cliente buscarPorCedula(String cedula);

    /** Crea o actualiza un cliente. */
    Cliente guardar(Cliente cliente);

    /** Elimina un cliente por identificador. */
    void eliminar(Integer id);

    /** Actualiza toda la informacion del cliente */
    Cliente actualizarInformacion(Cliente cliente, Cliente informacion);

    Reserva obtenerReservaActiva(Cliente cliente);

    Habitacion obtenerHabitacionActiva(Reserva reserva);

    long calcularNoches(Reserva reserva);

    int contarReservasActivas(Cliente cliente);

    int contarHistorialReservas(Cliente cliente);

    String obtenerFechaActualFormateada();
}
