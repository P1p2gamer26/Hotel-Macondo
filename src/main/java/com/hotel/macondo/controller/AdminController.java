package com.hotel.macondo.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.service.ServicioService;

/**
 * Unico punto de entrada del panel administrativo. Todas las pantallas de
 * gestion cuelgan de /admin para separarlas del sitio publico.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OperadorService operadorService;
    private final ServicioService servicioService;
    private final HabitacionService habitacionService;

    /**
     * Conecta el panel con los tres servicios que administra.
     */
    @Autowired
    public AdminController(OperadorService operadorService,
            ServicioService servicioService,
            HabitacionService habitacionService) {
        this.operadorService = operadorService;
        this.servicioService = servicioService;
        this.habitacionService = habitacionService;
    }

    /**
     * /admin no tiene pantalla propia: entra directo a operadores.
     */
    @GetMapping
    public String inicio() {
        return "redirect:/admin/operadores";
    }

    // ===== OPERADORES =====

    /**
     * Lista los operadores registrados.
     */
    @GetMapping("/operadores")
    public String listarOperadores(Model model) {
        model.addAttribute("operadores", operadorService.buscarTodos());
        return "admin/operadores";
    }

    /**
     * Crea un operador nuevo, activo por defecto.
     */
    @PostMapping("/operadores")
    public String crearOperador(@RequestParam String nombre) {
        operadorService.guardar(new Operador(null, nombre, true));
        return "redirect:/admin/operadores";
    }

    /**
     * Invierte el estado (activo/inactivo) de un operador.
     */
    @PostMapping("/operadores/{id}/estado")
    public String cambiarEstadoOperador(@PathVariable Integer id) {
        Operador operador = operadorService.buscarPorId(id);
        if (operador != null) {
            operador.setActivo(!Boolean.TRUE.equals(operador.getActivo()));
        }
        return "redirect:/admin/operadores";
    }

    /**
     * Elimina un operador del listado.
     */
    @PostMapping("/operadores/{id}/eliminar")
    public String eliminarOperador(@PathVariable Integer id) {
        operadorService.eliminar(id);
        return "redirect:/admin/operadores";
    }

    // ===== SERVICIOS =====

    /**
     * Lista el catalogo de servicios del hotel.
     */
    @GetMapping("/servicios")
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioService.buscarTodos());
        return "admin/servicios";
    }

    /**
     * Actualiza nombre, descripcion y precio de un servicio.
     */
    @PostMapping("/servicios/{id}")
    public String editarServicio(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio) {
        Servicio servicio = servicioService.buscarPorId(id);
        if (servicio != null) {
            servicio.actualizarDatos(nombre, descripcion, precio);
        }
        return "redirect:/admin/servicios";
    }

    /**
     * Activa o desactiva un servicio del catalogo.
     */
    @PostMapping("/servicios/{id}/estado")
    public String cambiarEstadoServicio(@PathVariable Integer id) {
        Servicio servicio = servicioService.buscarPorId(id);
        if (servicio != null) {
            servicio.setActivo(!servicio.isActivo());
        }
        return "redirect:/admin/servicios";
    }

    // ===== HABITACIONES =====

    /**
     * Lista las habitaciones con su estado operativo.
     */
    @GetMapping("/habitaciones")
    public String listarHabitaciones(Model model) {
        model.addAttribute("habitaciones", habitacionService.buscarTodas());
        return "admin/habitaciones";
    }

    /**
     * Pone la habitacion en mantenimiento o la devuelve a disponible.
     */
    @PostMapping("/habitaciones/{id}/estado")
    public String cambiarEstadoHabitacion(@PathVariable Integer id) {
        Habitacion habitacion = habitacionService.buscarPorId(id);
        if (habitacion != null) {
            if ("NO_DISPONIBLE".equals(habitacion.getEstado())) {
                habitacion.habilitar();
            } else {
                habitacion.deshabilitar();
            }
        }
        return "redirect:/admin/habitaciones";
    }
}
