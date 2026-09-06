package com.hotel.macondo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hotel.macondo.entities.Admin;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.CuentaService;
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

    @Autowired
    CuentaService cuentaService;

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
    void usuarioReferenciaSusPerfilesDeDominio() throws NoSuchFieldException {
        assertEquals(Admin.class,
                Usuario.class.getDeclaredField("admin").getType());
        assertEquals(Cliente.class,
                Usuario.class.getDeclaredField("cliente").getType());
        assertEquals(Operador.class,
                Usuario.class.getDeclaredField("operador").getType());

        Usuario usuarioAdmin = usuarioService.buscarPorCorreo("admin@macondo.com");
        assertNotNull(usuarioAdmin.getAdmin());
        assertEquals("Administrador principal", usuarioAdmin.getAdmin().getNombre());
    }

    @Test
    void cuentaCalculaYLiquidaSuSaldo() {
        Servicio servicio = servicioService.buscarPorId(2L);
        Cuenta cuenta = cuentaService.guardar(
                new Cuenta("ABIERTA", BigDecimal.ZERO, LocalDateTime.now()));

        assertNotNull(cuentaService.agregarServicio(cuenta.getId(), servicio, 2));
        Cuenta cuentaConConsumo = cuentaService.buscarPorId(cuenta.getId());
        BigDecimal totalEsperado = servicio.getPrecio().multiply(BigDecimal.valueOf(2));
        assertEquals(0, totalEsperado.compareTo(cuentaConConsumo.getTotal()));

        assertNotNull(cuentaService.pagar(cuenta.getId(), totalEsperado, "TARJETA"));
        Cuenta cuentaPagada = cuentaService.buscarPorId(cuenta.getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(cuentaPagada.getTotal()));
        assertEquals("PAGADA", cuentaPagada.getEstado());
    }
}
