package com.hotel.macondo.controller.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.dto.ConsumoServicioSolicitud;
import com.hotel.macondo.dto.PagoSolicitud;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.service.ReservaService;
import com.hotel.macondo.service.CuentaService;

// Controlador REST para la gestión y administración del personal operador
@RestController
@RequestMapping("/api/operador")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorRestController {

    @Autowired
    private OperadorService operadorService;
    @Autowired
    private CuentaService cuentaService;
    @Autowired
    private ReservaService reservaService;

    // ==========================================
    // GESTION DE OPERADORES
    // ==========================================

    // Retorna todos los operadores registrados en el sistema
    @GetMapping
    public List<Operador> buscarTodos() {
        Collection<Operador> operadores = operadorService.buscarTodos();
        return new ArrayList<>(operadores);
    }

    // Registra un nuevo operador asignándolo como activo si el valor viene nulo
    @PostMapping
    public Operador crear(@RequestBody Operador operador) {
        if (operador.getActivo() == null) {
            operador.setActivo(true);
        }
        return operadorService.guardar(operador);
    }

    // Alterna el estado de activación del operador
    @PutMapping("/estado/{id}")
    public Operador cambiarEstado(@PathVariable Long id) {
        Operador operador = operadorService.cambiarEstado(id);
        if (operador == null) {
            throw new RecursoNoEncontradoException("Operador no encontrado");
        }
        return operador;
    }

    // Elimina un operador por su identificador
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        Operador operador = operadorService.buscarPorId(id);
        if (operador == null) {
            throw new RecursoNoEncontradoException("Operador no encontrado");
        }
        operadorService.eliminar(id);
    }

    // Añade servicios a la cuenta asociada al número público de reserva.
    @PostMapping("/cuenta/reserva/{numeroReserva}/servicios")
    public void añadirServicios(
            @PathVariable String numeroReserva,
            @RequestBody List<ConsumoServicioSolicitud> servicios) {
        Long cuentaId = reservaService.obtenerCuentaId(numeroReserva);
        cuentaService.agregarServicios(cuentaId, servicios);
    }

    // Paga el total de la cuenta asociada al número público de reserva.
    @PostMapping("/cuenta/reserva/{numeroReserva}/pago")
    public Pago pagarCuenta(@PathVariable String numeroReserva, @RequestBody PagoSolicitud solicitud) {
        return reservaService.pagarCuentaParaOperador(numeroReserva, solicitud == null ? null : solicitud.metodoPago());
    }
}