package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.service.ReservaService;
import java.util.List;

/**
 * Unico punto de entrada del portal de operador. Todas las pantallas cuelgan
 * de /operador y sus plantillas viven en templates/operador.
 */
@Controller
@RequestMapping("/operador")
public class OperadorController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("seccionActiva", "panel");
        return "operador/index";
    }

    // ===== RESERVAS =====

    /**
     * Lista las reservas del hotel con su estado actual.
     */
    @GetMapping("/reservas")
    public String reservas(
            @RequestParam(defaultValue = "todas") String filtro,
            Model model) {
        boolean mostrarSoloActivas = "activas".equalsIgnoreCase(filtro);

        List<Reserva> reservas = mostrarSoloActivas
                ? reservaService.obtenerReservasActivas()
                : reservaService.buscarTodas();

        model.addAttribute("reservas", reservas);
        // Metricas reales de la lista mostrada (antes eran valores quemados)
        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("activas", reservaService.contarPorEstado(reservas, "ACTIVA"));
        model.addAttribute("confirmadas", reservaService.contarPorEstado(reservas, "CONFIRMADA"));
        model.addAttribute("canceladas", reservaService.contarPorEstado(reservas, "CANCELADA"));
        model.addAttribute("filtro", mostrarSoloActivas ? "activas" : "todas");
        model.addAttribute("seccionActiva", "reservas");
        return "operador/reservas";
    }

    /**
     * Detalle de una reserva: cliente, habitacion asignada y cuenta con los
     * servicios consumidos y pagos. Si no existe, el servicio lanza
     * RecursoNoEncontradoException y la atrapa el GlobalExceptionHandler.
     */
    @GetMapping("/reservas/{numeroReserva}")
    public String detalleReserva(@PathVariable String numeroReserva, Model model) {
        model.addAttribute("reserva", reservaService.obtenerDetalle(numeroReserva));
        model.addAttribute("seccionActiva", "reservas");
        return "operador/detalle_reserva";
    }

    /** Cancela una reserva futura desde el portal del operador. */
    @PostMapping("/reservas/{numeroReserva}/cancelar")
    public String cancelarReserva(@PathVariable String numeroReserva) {
        reservaService.cancelar(numeroReserva);
        return "redirect:/operador/reservas";
    }

    // ===== CUENTA DE LA HABITACION =====

    /**
     * Formulario para buscar la cuenta de una habitacion por su id.
     */
    @GetMapping("/cuenta")
    public String buscarCuenta(Model model) {
        model.addAttribute("seccionActiva", "cuenta");
        return "operador/cuenta";
    }

    /**
     * Redirecciona a la cuenta de la habitacion consultada.
     */
    @GetMapping("/cuenta/buscar")
    public String buscarCuenta(@RequestParam int id) {
        return "redirect:/operador/cuenta/" + id;
    }

    /**
     * Muestra el detalle de la cuenta de una habitacion.
     */

    @GetMapping("/cuenta/{id}")
    public String cuentaHabitacion(@PathVariable Long id, Model model) {
        model.addAttribute("seccionActiva", "cuenta");
        return "operador/cuenta_habitacion";
    }

    // ===== CHECK-OUT =====

    /**
     * Pantalla de cierre de estancia y liquidacion de la cuenta.
     */
    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("seccionActiva", "checkout");
        return "operador/checkout";
    }

}
