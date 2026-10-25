package com.hotel.macondo.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.dto.ReservaDetalleOperadorResponse;
import com.hotel.macondo.dto.ReservaIdentificadorResponse;
import com.hotel.macondo.dto.ReservaOperadorResponse;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.ReservaService;
import com.hotel.macondo.utils.FechaUtils;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Controlador REST para la gestión de reservas.
 * Separa las operaciones del panel de Operador de las operaciones de
 * autoservicio del Cliente.
 */
@RestController
@RequestMapping("/api/reserva")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaRestController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClienteService clienteService;

    // ==========================================
    // PANEL DE OPERADOR
    // ==========================================

    // Retorna el listado resumido de todas las reservas para la bandeja del
    // operador
    @GetMapping
    public List<ReservaOperadorResponse> obtenerTodas() {
        return reservaService.obtenerTodasParaOperador();
    }

    // Obtiene el desglose general (huésped, habitación, cuenta, consumos y pagos)
    // por código público
    @GetMapping("/{numeroReserva}")
    public ReservaDetalleOperadorResponse obtenerDetalle(
            @PathVariable String numeroReserva) {
        return reservaService.obtenerDetalleParaOperador(numeroReserva);
    }

    // Busca el ID de la reserva con cuenta abierta para el número visible de habitación.
    @GetMapping("/habitacion/{numeroHabitacion}/actual")
    public ReservaIdentificadorResponse obtenerReservaActualPorHabitacion(
            @PathVariable String numeroHabitacion) {
        return new ReservaIdentificadorResponse(
                reservaService.obtenerIdReservaActivaPorNumeroHabitacion(numeroHabitacion));
    }

    @GetMapping("/id/{reservaId}")
    public ReservaDetalleOperadorResponse obtenerDetallePorId(@PathVariable Long reservaId) {
        return reservaService.obtenerDetalleParaOperador(reservaId);
    }

    // Permite al operador cancelar una reserva
    @PutMapping("/{numeroReserva}/cancelar")
    public ReservaOperadorResponse cancelar(@PathVariable String numeroReserva) {
        return reservaService.cancelarParaOperador(numeroReserva);
    }

    @PutMapping("/{numeroReserva}/activar")
    public ReservaOperadorResponse activar(@PathVariable String numeroReserva) {
        return reservaService.activarParaOperador(numeroReserva);
    }

    // ==========================================
    // PANEL DEL CLIENTE
    // ==========================================

    // Obtiene las reservas activas (CONFIRMADA - EN CURSO) del cliente autenticado
    @GetMapping("/cliente/{id}/activas")
    public List<Reserva> activasDeCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return reservaService.buscarActivasDeCliente(cliente);
    }

    // Obtiene el historial de reservas (FINALIZADAS - CANCELADAS) del cliente
    @GetMapping("/cliente/{id}/historial")
    public List<Reserva> historialDeCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return reservaService.buscarHistorialDeCliente(cliente);
    }

    // Consulta disponibilidad de habitaciones para crear o editar una reserva del
    // cliente
    @GetMapping("/cliente/{id}/disponibilidad")
    @Operation(summary = "Consulta disponibilidad real para crear o modificar una reserva del cliente")
    public List<Habitacion> disponibilidadDeCliente(
            @PathVariable Long id,
            @RequestParam Long tipoId,
            @RequestParam(required = false) String fechaEntrada,
            @RequestParam(required = false) String fechaSalida,
            @RequestParam Integer cantidadPersonas,
            @RequestParam(required = false) Long reservaId) {
        clienteService.buscarPorId(id);
        if (reservaId != null) {
            Reserva reserva = reservaService.buscarParaModificar(id, reservaId);
            return reservaService.consultarDisponibilidad(
                    reserva, tipoId, fechaEntrada, fechaSalida, cantidadPersonas);
        }
        return reservaService.consultarDisponibilidad(
                tipoId, fechaEntrada, fechaSalida, cantidadPersonas);
    }

    // Obtiene los datos de una reserva editable del cliente para precargar el
    // formulario
    @GetMapping("/cliente/{id}/find/{reservaId}")
    @Operation(summary = "Obtiene una reserva futura que pertenece al cliente para precargar su edición")
    public Reserva reservaParaEditar(@PathVariable Long id, @PathVariable Long reservaId) {
        return reservaService.buscarParaModificar(id, reservaId);
    }

    // Registra una nueva reserva asignando habitación y abriendo cuenta hotelera.
    @PostMapping("/cliente/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una reserva, asigna una habitación disponible y abre su cuenta")
    public Reserva crearReservaDeCliente(
            @PathVariable Long id,
            @RequestParam Long tipoId,
            @RequestBody Reserva datos) {
        // Solo se utilizan los campos editables. El servicio genera identidad, estado y
        // precios.
        return reservaService.crear(
                id, tipoId,
                FechaUtils.toIsoString(datos.getFechaInicio()),
                FechaUtils.toIsoString(datos.getFechaFin()),
                datos.getCantidadPersonas());
    }

    // Modifica las fechas o personas de una reserva futura del cliente.
    @PutMapping("/cliente/{id}/{reservaId}")
    @Operation(summary = "Modifica una reserva futura del cliente y vuelve a validar su disponibilidad")
    public Reserva modificarReservaDeCliente(
            @PathVariable Long id,
            @PathVariable Long reservaId,
            @RequestParam Long tipoId,
            @RequestBody Reserva datos) {
        return reservaService.modificar(
                id, reservaId, tipoId,
                FechaUtils.toIsoString(datos.getFechaInicio()),
                FechaUtils.toIsoString(datos.getFechaFin()),
                datos.getCantidadPersonas());
    }

    // Cancela una reserva desde el portal del cliente
    @PutMapping("/cliente/{id}/{reservaId}/cancelar")
    @Operation(summary = "Cancela una reserva futura del cliente conservando su historial")
    public Reserva cancelarReservaDeCliente(@PathVariable Long id, @PathVariable Long reservaId) {
        return reservaService.cancelar(id, reservaId);
    }
}