package com.hotel.macondo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsuarioServiceIntegracionTest {

    @Autowired
    private CuentaClienteService cuentaClienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void UsuarioService_registrarCliente_ContrasenaRecibidaCodificada() {
        // Arrange
        Cliente cliente = new Cliente("Laura", "Prueba", "990001", "3005550101",
                "laura.prueba@macondo.test");
        String contrasena = "Clave elegida por Laura";

        // Act
        cuentaClienteService.crearCuenta(cliente, contrasena);
        UserEntity guardado = usuarioRepository.findByCorreoIgnoreCase(cliente.getCorreo()).orElseThrow();
        UserEntity autenticado = usuarioService.autenticar(cliente.getCorreo(), contrasena);

        // Assert
        assertThat(guardado.getContrasena()).isNotEqualTo(contrasena);
        assertThat(passwordEncoder.matches(contrasena, guardado.getContrasena())).isTrue();
        assertThat(passwordEncoder.matches("Otra clave", guardado.getContrasena())).isFalse();
        assertThat(guardado.getRoles()).extracting(Rol::getName).containsExactly("CLIENTE");
        assertThat(autenticado.getId()).isEqualTo(guardado.getId());
        assertThat(clienteRepository.findById(cliente.getId()).orElseThrow().getUsuario().getId())
                .isEqualTo(guardado.getId());
    }

    @Test
    public void UsuarioService_actualizarContrasena_NuevaClaveCodificadaYAnteriorRechazada() {
        // Arrange
        Cliente cliente = new Cliente("Laura", "Prueba", "990002", "3005550102",
                "laura.cambio@macondo.test");
        String anterior = "Clave anterior de Laura";
        String nueva = "Nueva clave de Laura";
        cuentaClienteService.crearCuenta(cliente, anterior);

        // Act
        Boolean actualizada = cuentaClienteService.actualizarContrasena(
                cliente.getId(), anterior, nueva, nueva);
        UserEntity guardado = usuarioRepository.findByCorreoIgnoreCase(cliente.getCorreo()).orElseThrow();

        // Assert
        assertThat(actualizada).isTrue();
        assertThat(guardado.getContrasena()).isNotEqualTo(nueva);
        assertThat(passwordEncoder.matches(nueva, guardado.getContrasena())).isTrue();
        assertThat(passwordEncoder.matches(anterior, guardado.getContrasena())).isFalse();
        assertThat(usuarioService.autenticar(cliente.getCorreo(), nueva).getId()).isEqualTo(guardado.getId());
        assertThatThrownBy(() -> usuarioService.autenticar(cliente.getCorreo(), anterior))
                .isInstanceOf(FormularioErroneoException.class);
    }

    @Test
    public void UsuarioService_datosIniciales_TodosLosRolesConContrasenaCodificada() {
        // Arrange
        String[] correos = {"admin@macondo.com", "operador@macondo.com", "ursula@macondo.com"};
        String[] contrasenas = {"admin", "ope", "123"};
        String[] roles = {"ADMINISTRADOR", "OPERADOR", "CLIENTE"};

        // Act
        for (int indice = 0; indice < correos.length; indice++) {
            UserEntity usuario = usuarioRepository.findByCorreoIgnoreCase(correos[indice]).orElseThrow();

            // Assert
            assertThat(usuario.getContrasena()).isNotEqualTo(contrasenas[indice]);
            assertThat(passwordEncoder.matches(contrasenas[indice], usuario.getContrasena())).isTrue();
            assertThat(usuario.getRoles()).extracting(Rol::getName).contains(roles[indice]);
        }
    }

    @Test
    public void UsuarioService_registrarSinContrasena_NoCreaClienteNiUsuario() {
        // Arrange
        Cliente cliente = new Cliente("Laura", "Prueba", "990003", "3005550103",
                "laura.vacia@macondo.test");
        long clientesIniciales = clienteRepository.count();
        long usuariosIniciales = usuarioRepository.count();

        // Act
        assertThatThrownBy(() -> cuentaClienteService.crearCuenta(cliente, "   "))
                // Assert
                .isInstanceOf(FormularioErroneoException.class)
                .hasMessage("La contraseña no puede estar vacía.");
        assertThat(clienteRepository.count()).isEqualTo(clientesIniciales);
        assertThat(usuarioRepository.count()).isEqualTo(usuariosIniciales);
    }
}
