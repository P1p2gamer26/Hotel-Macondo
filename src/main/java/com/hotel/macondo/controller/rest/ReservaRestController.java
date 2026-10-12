package com.hotel.macondo.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.dto.ReservaOperadorResponse;
import com.hotel.macondo.dto.ReservaDetalleOperadorResponse;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.ReservaService;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

/**
 * Controlador REST para gestionar reservas.
 */
@RestController
@RequestMapping("/api/reserva")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaRestController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Obtiene las reservas del hotel con los datos necesarios para la vista del operador.
     *
     * @return lista resumida de reservas ordenadas por identificador
     */
    @GetMapping
    public List<ReservaOperadorResponse> obtenerTodas() {
        return reservaService.obtenerTodasParaOperador();
    }

    /**
     * Cancela una reserva futura del operador.
     *
     * @param numeroReserva código público de la reserva
     * @return reserva actualizada
     */
    @PostMapping("/{numeroReserva}/cancelar")
    public ReservaOperadorResponse cancelar(@PathVariable String numeroReserva) {
        return reservaService.cancelarParaOperador(numeroReserva);
    }

    /**
     * Obtiene el detalle de una reserva por su código público.
     *
     * @param numeroReserva código público de la reserva
     * @return detalle de la reserva
     */
    @GetMapping("/{numeroReserva}")
    public ReservaDetalleOperadorResponse obtenerDetalle(
            @PathVariable String numeroReserva) {
        return reservaService.obtenerDetalleParaOperador(numeroReserva);
    }

    /**
     * Obtiene las reservas activas de un cliente.
     *
     * @param id identificador del cliente
     * @return lista de reservas activas del cliente
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    @GetMapping("/cliente/{id}/activas")
    public List<Reserva> activasDeCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return reservaService.buscarActivasDeCliente(cliente);
    }

    /**
     * Obtiene el historial de reservas de un cliente.
     *
     * @param id identificador del cliente
     * @return lista de reservas del historial del cliente
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    @GetMapping("/cliente/{id}/historial")
    public List<Reserva> historialDeCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return reservaService.buscarHistorialDeCliente(cliente);
    }
}