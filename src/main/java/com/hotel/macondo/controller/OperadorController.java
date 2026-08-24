package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.service.OperadorService;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    private final OperadorService service;

    @GetMapping("/reservas")
    public String reservas(Model model){
        model.addAttribute("seccionActiva", "reservas");
        return "reservas_operador";
    }

    @GetMapping("/cuenta")
    public String cuentaHabitacion(Model model){
        model.addAttribute("seccionActiva", "cuenta");
        return "cuenta_habitacion";
    }

    @GetMapping("/checkout")
    public String checkout(Model model){
        model.addAttribute("seccionActiva", "checkout");
        return "checkout_operador";
    }

    /**
     * Conecta el controlador con la interfaz de operadores.
     */
    @Autowired
    public OperadorController(OperadorService service) {
        this.service = service;
    }

    /**
     * Retorna los operadores guardados temporalmente.
     */
    @GetMapping
    @ResponseBody
    public Collection<Operador> listar() {
        return service.buscarTodos();
    }
}
