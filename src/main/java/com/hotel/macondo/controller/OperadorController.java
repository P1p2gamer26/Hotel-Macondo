package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.service.OperadorService;

/**
 * Unico punto de entrada del portal de operador. Todas las pantallas cuelgan
 * de /operador y sus plantillas viven en templates/operador.
 */
@Controller
@RequestMapping("/operador")
public class OperadorController {

    private final OperadorService service;

    /**
     * Conecta el controlador con la interfaz de operadores.
     */
    @Autowired
    public OperadorController(OperadorService service) {
        this.service = service;
    }

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
    public String reservas(Model model) {
        model.addAttribute("seccionActiva", "reservas");
        return "operador/reservas";
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
    public String cuentaHabitacion(@PathVariable Integer id, Model model) {
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

    // ===== CONSULTA DE APOYO =====

    /**
     * Retorna los operadores guardados temporalmente.
     */
    @ResponseBody
    public Collection<Operador> listar() {
        return service.buscarTodos();
    }
}
