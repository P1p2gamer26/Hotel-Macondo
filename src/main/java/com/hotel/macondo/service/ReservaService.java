package com.hotel.macondo.service;

import java.util.List;

import com.hotel.macondo.dto.ReservaOperadorResponse;
import com.hotel.macondo.dto.ReservaDetalleOperadorResponse;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Reserva;

public interface ReservaService {

        /**
         * Reservas resumidas con la información requerida por el panel del operador.
         */
        List<ReservaOperadorResponse> obtenerTodasParaOperador();

        /**
         * Cancela una reserva por su código y devuelve su representación para el
         * operador.
         */
        ReservaOperadorResponse cancelarParaOperador(String numeroReserva);

        /** Activa una reserva confirmada desde el panel del operador. */
        ReservaOperadorResponse activarParaOperador(String numeroReserva);

        /** Obtiene el detalle completo de una reserva para el panel del operador. */
        ReservaDetalleOperadorResponse obtenerDetalleParaOperador(String numeroReserva);

        /** Obtiene el detalle completo de una reserva por su identificador interno. */
        ReservaDetalleOperadorResponse obtenerDetalleParaOperador(Long reservaId);

        /** Reservas vigentes o futuras que pertenecen al cliente indicado. */
        List<Reserva> buscarActivasDeCliente(Cliente cliente);

        /** Reservas finalizadas o canceladas del cliente indicado. */
        List<Reserva> buscarHistorialDeCliente(Cliente cliente);

        /**
         * Reserva con cliente, habitación y cuenta (consumos y pagos) cargados.
         * Lanza RecursoNoEncontradoException si el número no existe.
         */
        Reserva obtenerDetalle(String numeroReserva);

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

        /** Busca la cuenta asociada a la reserva. */
        Long obtenerCuentaId(String numeroReserva);

        /** Busca el identificador de la reserva con cuenta abierta de la habitación. */
        Long obtenerIdReservaActivaPorNumeroHabitacion(String numeroHabitacion);

        /** Finaliza una reserva activa cuya cuenta ya fue pagada. */
        ReservaOperadorResponse finalizarParaOperador(String numeroReserva);

        /** Paga la cuenta de una reserva activa desde el panel del operador. */
        Pago pagarCuentaParaOperador(String numeroReserva, String metodoPago);

        /** Busca el identificador de la reserva activa de la habitación, sin importar el estado de la cuenta (check-out). */
        Long obtenerIdReservaEnCheckout(String numeroHabitacion);
}
