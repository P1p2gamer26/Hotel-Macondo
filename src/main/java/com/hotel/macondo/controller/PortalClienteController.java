package com.hotel.macondo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

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

/**
 * Construye el portal privado de un cliente a partir de los servicios de
 * clientes y servicios. No accede directamente a los repositorios.
 */
@Controller
@RequestMapping("/cliente")
public class PortalClienteController {

        private static final Locale LOCALE_COLOMBIA = new Locale("es", "CO");
        private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter
                        .ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", LOCALE_COLOMBIA);

        @Autowired
        private ClienteService clienteService;
        @Autowired
        private ServicioService servicioService;

        /**
         * Muestra el dashboard del cliente identificado por la URL.
         *
         * @param id    identificador interno del cliente
         * @param model datos que consumira la plantilla Thymeleaf
         * @return la vista privada del cliente
         */
        @GetMapping("/{id}")
        public String mostrarDashboard(@PathVariable Integer id, Model model) {
                // Primero se valida que el cliente exista para no renderizar datos incompletos.
                Cliente cliente = clienteService.buscarPorId(id);
                if (cliente == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Cliente no encontrado");
                }

                // Las reservas activas e historicas pertenecen al comportamiento del agregado
                // Cliente.
                List<Reserva> reservasActivas = cliente.verReservasActivas();
                List<Reserva> historial = cliente.verHistorialReservas();
                Reserva reservaActiva = reservasActivas.stream().findFirst().orElse(null);
                Habitacion habitacionActiva = reservaActiva == null
                                ? null
                                : reservaActiva.getHabitaciones().stream().findFirst().orElse(null);
                long nochesActivas = reservaActiva == null
                                ? 0
                                : ChronoUnit.DAYS.between(reservaActiva.getFechaInicio(),
                                                reservaActiva.getFechaFin());

                // Las recomendaciones usan los servicios activos sin conocer su repositorio.
                List<Servicio> recomendaciones = servicioService.buscarTodos().stream()
                                .filter(Servicio::isActivo)
                                .limit(3)
                                .toList();

                model.addAttribute("cliente", cliente);
                model.addAttribute("reservaActiva", reservaActiva);
                model.addAttribute("habitacionActiva", habitacionActiva);
                model.addAttribute("nochesActivas", nochesActivas);
                model.addAttribute("cantidadActivas", reservasActivas.size());
                model.addAttribute("cantidadHistorial", historial.size());
                model.addAttribute("recomendaciones", recomendaciones);
                model.addAttribute("fechaActual", capitalizar(FORMATO_FECHA.format(LocalDate.now())));
                return "cliente/dashboard";
        }

        /** Da formato de encabezado a la fecha que se muestra en el dashboard. */
        private String capitalizar(String texto) {
                return texto.isEmpty()
                                ? texto
                                : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
        }
}