package com.hotel.macondo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.ServicioService;
import com.hotel.macondo.service.TestimonioService;
import com.hotel.macondo.service.UsuarioService;

@SpringBootTest
class MacondoApplicationTests {

    @Autowired
    HabitacionService habitacionService;

    @Autowired
    ServicioService servicioService;

    @Autowired
    TestimonioService testimonioService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    void contextLoads() {
        assertEquals(4, habitacionService.buscarTodas().size());
        assertEquals(6, servicioService.buscarTodos().size());
        assertEquals(3, testimonioService.buscarTodos().size());
    }

    @Test
    void habitacionesPorCapacidad() {
        assertEquals(1, habitacionService.buscarPorPersonas(6).size());
        assertEquals(4, habitacionService.buscarPorPersonas(1).size());
    }

    @Test
    void autenticaYAutorizaConRol() {
        Usuario usuario = usuarioService.autenticar(
                "operador@macondo.com", "operador123");

        assertNotNull(usuario);
        assertTrue(usuarioService.autorizar(usuario, Rol.OPERADOR));
        assertFalse(usuarioService.autorizar(usuario, Rol.ADMINISTRADOR));
        assertNull(usuarioService.autenticar(
                "operador@macondo.com", "clave-incorrecta"));
    }

    @Test
    void usuarioNoReferenciaPerfilesDeDominio() {
        assertFalse(Arrays.stream(Usuario.class.getDeclaredFields())
                .map(campo -> campo.getType())
                .anyMatch(tipo -> tipo == Cliente.class || tipo == Operador.class));
    }

    @Test
    void cuentaCalculaYLiquidaSuSaldo() {
        Servicio servicio = new Servicio(99, "Traslado", "Traslado local",
                null, false, BigDecimal.valueOf(50000), true);
        Cuenta cuenta = new Cuenta(99, null);

        assertNotNull(cuenta.agregarItem(servicio, 2));
        assertEquals(0, BigDecimal.valueOf(100000).compareTo(cuenta.getTotal()));
        assertNotNull(cuenta.pagar(BigDecimal.valueOf(100000), "TARJETA"));
        assertTrue(cuenta.estaSaldada());
        assertEquals("PAGADA", cuenta.getEstado());
    }
}
