package com.hotel.macondo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.ReservaService;
import com.hotel.macondo.service.TipoHabitacionService;

/**
 * Expone las reservas privadas de un cliente identificado en la URL.
 */
@Controller
@RequestMapping("/cliente/{id}")
public class ReservaController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    /** Muestra las reservas activas y futuras que pertenecen al cliente. */
    @GetMapping("/reservas")
    public String reservasActivas(@PathVariable Long id, Model model) {
        Cliente cliente = obtenerCliente(id);
        List<Reserva> reservas = reservaService.buscarActivasDeCliente(cliente);
        model.addAttribute("cliente", cliente);
        model.addAttribute("reservas", reservas);
        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("totalActivas", reservaService.contarPorEstado(reservas, "ACTIVA"));
        model.addAttribute("totalConfirmadas", reservaService.contarPorEstado(reservas, "CONFIRMADA"));
        model.addAttribute("totalPendientes", reservaService.contarPorEstado(reservas, "PENDIENTE"));
        model.addAttribute("fechaActual", LocalDate.now());
        return "cliente/reservas_activas";
    }

    /** Muestra las reservas finalizadas o canceladas del cliente. */
    @GetMapping("/historial")
    public String historialReservas(@PathVariable Long id, Model model) {
        Cliente cliente = obtenerCliente(id);
        List<Reserva> historial = reservaService.buscarHistorialDeCliente(cliente);
        model.addAttribute("cliente", cliente);
        model.addAttribute("historial", historial);
        model.addAttribute("totalHistorial", historial.size());
        model.addAttribute("nochesHistorial", reservaService.calcularNoches(historial));
        model.addAttribute("totalCanceladas", reservaService.contarPorEstado(historial, "CANCELADA"));
        return "cliente/historial_reservas";
    }

    /** Muestra el formulario AC19 para generar una reserva. */
    @GetMapping("/reservas/nueva")
    public String nuevaReserva(
            @PathVariable Long id,
            @RequestParam(required = false) Long tipoId,
            Model model) {
        cargarFormulario(id, tipoId, null, null, null, null, model);
        return "cliente/crear_reserva";
    }

    /** Reutiliza el formulario de reserva con los datos actuales precargados. */
    @GetMapping("/reservas/{reservaId}/editar")
    public String editarReserva(
            @PathVariable Long id,
            @PathVariable Long reservaId,
            Model model) {
        Reserva reserva = reservaService.buscarParaModificar(id, reservaId);
        Long tipoId = reserva.getHabitacion().getTipoHabitacion().getId();

        cargarFormulario(
                id,
                tipoId,
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getCantidadPersonas(),
                reserva,
                model);
        return "cliente/crear_reserva";
    }

    /** Consulta unidades disponibles sin crear todavía la reserva. */
    @PostMapping("/reservas/disponibilidad")
    public String consultarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Long tipoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            Model model) {
        List<Habitacion> disponibles = reservaService.consultarDisponibilidad(
                tipoId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas);

        cargarFormulario(
                id,
                tipoId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas,
                null,
                model);
        model.addAttribute("disponibilidadConsultada", true);
        model.addAttribute("cantidadDisponibles", disponibles.size());
        model.addAttribute("hayDisponibilidad", !disponibles.isEmpty());
        return "cliente/crear_reserva";
    }

    /** Consulta disponibilidad conservando la reserva que se está modificando. */
    @PostMapping("/reservas/{reservaId}/disponibilidad")
    public String consultarDisponibilidadModificacion(
            @PathVariable Long id,
            @PathVariable Long reservaId,
            @RequestParam Long tipoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            Model model) {
        Reserva reserva = reservaService.buscarParaModificar(id, reservaId);
        List<Habitacion> disponibles = reservaService.consultarDisponibilidad(
                reserva,
                tipoId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas);

        cargarFormulario(
                id,
                tipoId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas,
                reserva,
                model);
        model.addAttribute("disponibilidadConsultada", true);
        model.addAttribute("cantidadDisponibles", disponibles.size());
        model.addAttribute("hayDisponibilidad", !disponibles.isEmpty());
        return "cliente/crear_reserva";
    }

    /** Genera la reserva y asigna una unidad física disponible. */
    @PostMapping("/reservas")
    public String crearReserva(
            @PathVariable Long id,
            @RequestParam Long tipoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas) {
        reservaService.crear(
                id,
                tipoId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas);
        return "redirect:/cliente/" + id + "/reservas";
    }

    /** Backend para modificar una reserva futura; la vista se añadirá después. */
    @PostMapping("/reservas/{reservaId}/modificar")
    public String modificarReserva(
            @PathVariable Long id,
            @PathVariable Long reservaId,
            @RequestParam Long tipoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas) {
        reservaService.modificar(
                id,
                reservaId,
                tipoId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas);
        return "redirect:/cliente/" + id + "/reservas";
    }

    /** Backend para cancelar una reserva futura del cliente. */
    @PostMapping("/reservas/{reservaId}/cancelar")
    public String cancelarReserva(
            @PathVariable Long id,
            @PathVariable Long reservaId) {
        reservaService.cancelar(id, reservaId);
        return "redirect:/cliente/" + id + "/reservas";
    }

    private void cargarFormulario(
            Long clienteId,
            Long tipoId,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Integer cantidadPersonas,
            Reserva reserva,
            Model model) {
        Cliente cliente = obtenerCliente(clienteId);
        model.addAttribute("cliente", cliente);
        model.addAttribute("reserva", reserva);
        model.addAttribute("tiposHabitacion", tipoHabitacionService.buscarTodos());
        model.addAttribute("tipoId", tipoId);
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("cantidadPersonas", cantidadPersonas);
        model.addAttribute("fechaMinima", LocalDate.now());
    }

    /** Evita renderizar vistas privadas para identificadores inexistentes. */
    private Cliente obtenerCliente(Long id) {
        // Esta funcion puede lanzar una excepcion la cual es manejada por el GlobalExceptionHandler.
        Cliente cliente = clienteService.buscarPorId(id);
        return cliente;
    }
}
