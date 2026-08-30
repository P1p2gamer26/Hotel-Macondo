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
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.service.ServicioService;
import com.hotel.macondo.service.TipoHabitacionService;

/**
 * Unico punto de entrada del panel administrativo. Todas las pantallas de
 * gestion cuelgan de /admin para separarlas del sitio publico.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OperadorService operadorService;
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    /**
     * Tablero de entrada del panel: resume en cuantos elementos activos hay
     * en cada seccion y ofrece el acceso a las tres pantallas de gestion.
     */
    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("totalOperadores", operadorService.buscarTodos().size());
        model.addAttribute("operadoresActivos", operadorService.buscarTodos().stream()
                .filter(operador -> Boolean.TRUE.equals(operador.getActivo()))
                .count());

        model.addAttribute("totalServicios", servicioService.buscarTodos().size());
        model.addAttribute("serviciosActivos", servicioService.buscarTodos().stream()
                .filter(Servicio::isActivo)
                .count());

        model.addAttribute("totalHabitaciones", habitacionService.buscarTodas().size());
        model.addAttribute("habitacionesDisponibles", habitacionService.buscarTodas().stream()
                .filter(habitacion -> "DISPONIBLE".equals(habitacion.getEstado()))
                .count());

        return "admin/index";
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
     * Actualiza nombre, categoria y precio de un servicio.
     */
    @PostMapping("/servicios/{id}")
    public String editarServicio(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam BigDecimal precio) {
        Servicio servicio = servicioService.buscarPorId(id);
        if (servicio != null) {
            servicio.actualizarDatos(nombre, categoria, precio);
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

    /**
     * Lista todos los tipos de habitacion registrados en el sistema.
     */
    @GetMapping("/tipos_habitacion")
    public String listarTiposHabitacion(Model model) {
        model.addAttribute("tiposHabitacion", tipoHabitacionService.buscarTodos());
        return "admin/tipos_habitacion";
    }

    /**
     * Guarda o actualiza un tipo de habitacion.
     */
    @PostMapping("/tipos_habitacion")
    public String guardarTipoHabitacion(@RequestParam(required = false) Integer id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precioNoche,
            @RequestParam Integer capacidadPersonas) {

        TipoHabitacion tipo = new TipoHabitacion(id, nombre, descripcion, precioNoche, capacidadPersonas);
        tipoHabitacionService.guardar(tipo);
        return "redirect:/admin/tipos_habitacion";
    }

    /**
     * Elimina un tipo de habitacion por su ID.
     */
    @PostMapping("/tipos_habitacion/{id}/eliminar")
    public String eliminarTipoHabitacion(@PathVariable Integer id) {
        tipoHabitacionService.eliminar(id);
        return "redirect:/admin/tipos_habitacion";
    }
}
