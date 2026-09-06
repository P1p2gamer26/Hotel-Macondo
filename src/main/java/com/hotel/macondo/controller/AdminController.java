package com.hotel.macondo.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("totalOperadores", operadorService.contarTodos());
        model.addAttribute("operadoresActivos", operadorService.contarActivos());

        model.addAttribute("totalServicios", servicioService.contarTodos());
        model.addAttribute("serviciosActivos", servicioService.contarActivos());

        model.addAttribute("totalHabitaciones", habitacionService.contarTodas());
        model.addAttribute("habitacionesDisponibles", habitacionService.contarDisponibles());

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
        operadorService.guardar(new Operador(nombre, true));
        return "redirect:/admin/operadores";
    }

    /**
     * Invierte el estado (activo/inactivo) de un operador.
     */
    @PostMapping("/operadores/{id}/estado")
    public String cambiarEstadoOperador(@PathVariable Long id) {
        operadorService.cambiarEstado(id);
        return "redirect:/admin/operadores";
    }

    /**
     * Elimina un operador del listado.
     */
    @PostMapping("/operadores/{id}/eliminar")
    public String eliminarOperador(@PathVariable Long id) {
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
    public String editarServicio(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam BigDecimal precio) {
        Servicio servicio = servicioService.buscarPorId(id);
        if (servicio != null) {
            servicio.setNombre(nombre);
            servicio.setCategoria(categoria);
            servicio.setPrecio(precio);
            servicioService.guardar(servicio);
        }
        return "redirect:/admin/servicios";
    }

    /**
     * Activa o desactiva un servicio del catalogo.
     */
    @PostMapping("/servicios/{id}/estado")
    public String cambiarEstadoServicio(@PathVariable Long id) {
        servicioService.cambiarEstado(id);
        return "redirect:/admin/servicios";
    }

    // ===== HABITACIONES =====

    /**
     * Lista las habitaciones con su estado operativo. El formulario de
     * creacion y edicion se maneja de forma inline con JavaScript.
     */
    @GetMapping("/habitaciones")
    public String listarHabitaciones(Model model) {
        model.addAttribute("habitaciones", habitacionService.buscarTodas());
        model.addAttribute("tiposHabitacion", tipoHabitacionService.buscarTodos());
        return "admin/habitaciones";
    }

    /**
     * Crea o actualiza una habitacion a partir del formulario inline.
     * El tipo de habitacion llega como identificador y el servicio lo aplica
     * como fuente de verdad de los datos comerciales de la habitacion.
     */
    @PostMapping("/habitaciones/guardar")
    public String guardarHabitacion(@ModelAttribute("habitacion") Habitacion habitacion,
            @RequestParam(required = false) Long tipoId,
            RedirectAttributes redirectAttributes) {
        if (habitacionService.guardar(habitacion, tipoId) == null) {
            redirectAttributes.addFlashAttribute("errorHabitacion",
                    "No se puede guardar: selecciona un tipo valido y usa un nombre distinto al tipo.");
        }
        return "redirect:/admin/habitaciones";
    }

    /**
     * Pone la habitacion en mantenimiento o la devuelve a disponible.
     */
    @PostMapping("/habitaciones/{id}/estado")
    public String cambiarEstadoHabitacion(@PathVariable Long id) {
        habitacionService.cambiarEstado(id);
        return "redirect:/admin/habitaciones";
    }

    /**
     * Elimina una habitacion del inventario.
     */
    @PostMapping("/habitaciones/{id}/eliminar")
    public String eliminarHabitacion(@PathVariable Long id) {
        habitacionService.eliminar(id);
        return "redirect:/admin/habitaciones";
    }

    // ===== TIPOS DE HABITACION =====

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
    public String guardarTipoHabitacion(@RequestParam(required = false) Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precioNoche,
            @RequestParam Integer capacidadPersonas) {

        TipoHabitacion tipo = id == null
                ? new TipoHabitacion(nombre, descripcion, precioNoche, capacidadPersonas)
                : tipoHabitacionService.buscarPorId(id);
        if (tipo == null) {
            return "redirect:/admin/tipos_habitacion";
        }
        tipo.setNombre(nombre);
        tipo.setDescripcion(descripcion);
        tipo.setPrecioNoche(precioNoche);
        tipo.setCapacidadPersonas(capacidadPersonas);
        tipoHabitacionService.guardar(tipo);
        return "redirect:/admin/tipos_habitacion";
    }

    /**
     * Elimina un tipo de habitacion por su ID. No elimina si todavia hay
     * habitaciones asignadas a ese tipo.
     */
    @PostMapping("/tipos_habitacion/{id}/eliminar")
    public String eliminarTipoHabitacion(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        if (!tipoHabitacionService.eliminar(id)) {
            redirectAttributes.addFlashAttribute("errorTipo",
                    "No se puede eliminar: hay habitaciones asignadas a este tipo.");
        }
        return "redirect:/admin/tipos_habitacion";
    }
}
