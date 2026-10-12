package com.hotel.macondo.controller.rest;


import org.springframework.beans.factory.annotation.Autowired;
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
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.CuentaClienteService;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

/**
 * Controlador REST para gestionar clientes.
 */
@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaClienteService cuentaClienteService;

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return el cliente encontrado
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    @GetMapping("/find/{id}")
    public Cliente findById(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    /**
     * Registra un nuevo cliente y crea su cuenta de usuario.
     *
     * @param registro DTO con los datos del cliente y la contraseña
     * @return el cliente creado
     */
    @PostMapping("/registro")
    public Cliente registrar(@RequestBody RegistroCliente registro) {
        if (registro.getCliente() == null) {
            throw new FormularioErroneoException("Faltan los datos del cliente.");
        }
        // El id lo genera la BD: si viniera en el body, save() sobrescribiria a otro cliente
        registro.getCliente().setId(null);
        cuentaClienteService.crearCuenta(registro.getCliente(), registro.getContrasena());
        return registro.getCliente();
    }

    /**
     * Actualiza los datos editables del perfil de un cliente.
     *
     * @param id identificador del cliente
     * @param cliente datos actualizados del cliente
     * @return el perfil actualizado
     */
    @PostMapping("/{id}/perfil")
    public Cliente actualizarPerfil(@PathVariable Long id, @RequestBody Cliente cliente) {
        if (!Boolean.TRUE.equals(cuentaClienteService.actualizarPerfil(id, cliente))) {
            throw new FormularioErroneoException(
                    "No fue posible actualizar el perfil. Verifica que el correo no esté en uso.");
        }
        return clienteService.buscarPorId(id);
    }

    /**
     * Actualiza la contraseña de la cuenta del cliente.
     *
     * @param id identificador del cliente
     * @param cambio datos para validar la contraseña actual y registrar la nueva
     */
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

    /**
     * Elimina la cuenta del cliente cuando no tiene reservas asociadas.
     *
     * @param id identificador del cliente
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        if (!Boolean.TRUE.equals(cuentaClienteService.eliminarCuenta(id))) {
            throw new PeticionImposible(
                    "No se puede eliminar la cuenta: el cliente registra reservas en su historial.");
        }
        return ResponseEntity.noContent().build();
    }
}