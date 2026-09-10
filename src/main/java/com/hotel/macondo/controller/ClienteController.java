package com.hotel.macondo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.CuentaClienteService;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.service.ServicioService;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private CuentaClienteService cuentaClienteService;
    @Autowired
    private ServicioService servicioService;

    /**
     * Muestra el dashboard del cliente identificado por la URL.
     */
    @GetMapping("/{id}")
    public String mostrarDashboard(@PathVariable Long id, Model model) {

        // Esta funcion puede lanzar un error pero este es manejado por el GlobalExceptionHandler
        Cliente cliente = clienteService.buscarPorId(id);

        // Toda la logica la calculan los servicios :D
        Reserva reservaActiva = clienteService.obtenerReservaActiva(cliente);
        Habitacion habitacionActiva = clienteService.obtenerHabitacionActiva(reservaActiva);
        long nochesActivas = clienteService.calcularNoches(reservaActiva);
        int cantidadActivas = clienteService.contarReservasActivas(cliente);
        int cantidadHistorial = clienteService.contarHistorialReservas(cliente);
        List<Servicio> recomendaciones = servicioService.obtenerRecomendaciones(3);
        String fechaActual = clienteService.obtenerFechaActualFormateada();

        // Datos al Model
        model.addAttribute("cliente", cliente);
        model.addAttribute("reservaActiva", reservaActiva);
        model.addAttribute("habitacionActiva", habitacionActiva);
        model.addAttribute("nochesActivas", nochesActivas);
        model.addAttribute("cantidadActivas", cantidadActivas);
        model.addAttribute("cantidadHistorial", cantidadHistorial);
        model.addAttribute("recomendaciones", recomendaciones);
        model.addAttribute("fechaActual", fechaActual);

        return "cliente/dashboard";
    }

    /** Renderiza el perfil del cliente solicitado. */
    @GetMapping("{id}/perfil")
    public String mostrarPerfil(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "cliente/perfil_cliente";
    }

    /** Actualiza solamente los datos personales editables del cliente. */
    @PostMapping("{id}/perfil")
    public String actualizarPerfil(@PathVariable Long id, Cliente cliente, Model model) {

        cuentaClienteService.actualizarPerfil(id, cliente);

        return "redirect:/cliente/" + id + "/perfil";
    }

    /** Actualiza la contraseña del usuario asociado al cliente. */
    @PostMapping("{id}/password")
    public String actualizarPassword(@PathVariable Long id,
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model model) {

        cuentaClienteService.actualizarContrasena(id, contrasenaActual, nuevaContrasena, confirmarContrasena);

        return "redirect:/cliente/" + id + "/perfil";
    }

    /**
     * Elimina la cuenta del cliente y su usuario relacionado
     */
    @GetMapping("/delete/{id}")
    public String eliminarCuenta(@PathVariable Long id) {

        cuentaClienteService.eliminarCuenta(id);

        return "redirect:/logout";
    }
}
