package com.hotel.macondo.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.dto.CambioContrasenaRequest;
import com.hotel.macondo.dto.RegistroCliente;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.CuentaClienteService;

// Controlador REST para la gestión de cuentas y perfil del Cliente.

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaClienteService cuentaClienteService;

    // ==========================================
    // REGISTRO Y CONSULTAS DE CLIENTE
    // ==========================================

    // Registra un nuevo cliente y genera su cuenta de usuario asociada
    // http://localhost:8080/api/cliente/registro
    @PostMapping("/registro")
    public Cliente registrar(@RequestBody RegistroCliente registro) {
        if (registro.getCliente() == null) {
            throw new FormularioErroneoException("Faltan los datos del cliente.");
        }
        // El id lo genera la BD: si viniera en el body, save() sobrescribiría a otro
        // cliente
        registro.getCliente().setId(null);
        cuentaClienteService.crearCuenta(registro.getCliente(), registro.getContrasena());
        return registro.getCliente();
    }

    // Busca un cliente por su identificador único
    // http://localhost:8080/api/cliente/find/{id}
    @GetMapping("/find/{id}")
    public Cliente findById(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    // ==========================================
    // CRUD DE PERFIL Y CUENTA DEL CLIENTE
    // ==========================================

    // Actualiza la información personal de contacto del cliente
    // http://localhost:8080/api/cliente/{id}/perfil
    @PostMapping("/{id}/perfil")
    public Cliente actualizarPerfil(@PathVariable Long id, @RequestBody Cliente cliente) {
        if (!Boolean.TRUE.equals(cuentaClienteService.actualizarPerfil(id, cliente))) {
            throw new FormularioErroneoException(
                    "No fue posible actualizar el perfil. Verifica que el correo no esté en uso.");
        }
        return clienteService.buscarPorId(id);
    }

    // Valida y actualiza la contraseña de acceso del cliente
    // http://localhost:8080/api/cliente/{id}/password
    @PostMapping("/{id}/password")
    public ResponseEntity<Void> actualizarContrasena(
            @PathVariable Long id,
            @RequestBody CambioContrasenaRequest cambio) {
        if (cambio == null) {
            throw new FormularioErroneoException("Faltan los datos para cambiar la contraseña.");
        }
        if (!Boolean.TRUE.equals(cuentaClienteService.actualizarContrasena(
                id,
                cambio.contrasenaActual(),
                cambio.nuevaContrasena(),
                cambio.confirmarContrasena()))) {
            throw new FormularioErroneoException(
                    "No fue posible cambiar la contraseña. Verifica la contraseña actual y la confirmación.");
        }
        return ResponseEntity.noContent().build();
    }

    // Da de baja la cuenta del cliente siempre que NO registre historial de
    // reservas
    // http://localhost:8080/api/cliente/delete/{id}
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        if (!Boolean.TRUE.equals(cuentaClienteService.eliminarCuenta(id))) {
            throw new PeticionImposible(
                    "No se puede eliminar la cuenta: el cliente registra reservas en su historial.");
        }
        return ResponseEntity.noContent().build();
    }
}