package com.hotel.macondo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.ServicioService;

@Controller
@RequestMapping("/cliente")
public class PortalClienteController {

        private final AdminController adminController;

        @Autowired
        private ClienteService clienteService;

        @Autowired
        private ServicioService servicioService;

        PortalClienteController(AdminController adminController) {
                this.adminController = adminController;
        }

        /**
         * Muestra el dashboard del cliente identificado por la URL.
         */
        @GetMapping("/{id}")
        public String mostrarDashboard(@PathVariable Integer id, Model model) {
                Cliente cliente = clienteService.buscarPorId(id);
                if (cliente == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
                }

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
}