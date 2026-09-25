package com.hotel.macondo.service;

import java.util.List;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;

public interface ReservaService {

    /** Todas las reservas del hotel ordenadas por identificador. */
    List<Reserva> buscarTodas();

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

    /** Cuenta las reservas de una lista que coinciden con el estado indicado. */
    long contarPorEstado(List<Reserva> reservas, String estado);

    /**
     * Reserva con cliente, habitación y cuenta (consumos y pagos) cargados.
     * Lanza RecursoNoEncontradoException si el número no existe.
     */
    Reserva obtenerDetalle(String numeroReserva);

    /** Suma las noches correspondientes a una lista de reservas. */
    long calcularNoches(List<Reserva> reservas);

    /** Unidades físicas disponibles para un tipo y un intervalo de fechas. */
    List<Habitacion> consultarDisponibilidad(
        Long tipoId,
        String fechaEntrada,
        String fechaSalida,
        Integer cantidadPersonas);

    /**
     * Unidades disponibles al modificar una reserva, excluyendo la unidad que
     * esa misma reserva ocupa actualmente.
     */
    List<Habitacion> consultarDisponibilidad(
        Reserva reserva,
        Long tipoId,
        String fechaEntrada,
        String fechaSalida,
        Integer cantidadPersonas);

    /** Obtiene una reserva futura que el cliente indicado puede modificar. */
    Reserva buscarParaModificar(Long clienteId, Long reservaId);

    /** Genera una reserva y asigna la primera unidad física disponible. */
    Reserva crear(
        Long clienteId,
        Long tipoId,
        String fechaEntrada,
        String fechaSalida,
        Integer cantidadPersonas);

    /** Modifica una reserva futura y vuelve a comprobar su disponibilidad. */
    Reserva modificar(
        Long clienteId,
        Long reservaId,
        Long tipoId,
        String fechaEntrada,
        String fechaSalida,
        Integer cantidadPersonas);

    /** Cancela una reserva futura perteneciente al cliente indicado. */
    Reserva cancelar(Long clienteId, Long reservaId);

    /** Cancela una reserva futura buscándola por su número público. */
    Reserva cancelar(String numeroReserva);
}
