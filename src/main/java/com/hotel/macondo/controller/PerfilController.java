package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.UsuarioService;

/**
 * Gestiona el perfil del cliente indicado en la ruta privada.
 */
@Controller
@RequestMapping("/cliente/{id}")
public class PerfilController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    /** Renderiza el perfil del cliente solicitado. */
    @GetMapping("/perfil")
    public String mostrarPerfil(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", obtenerCliente(id));
        return "cliente/perfil_cliente";
    }

    /** Actualiza solamente los datos personales editables del cliente. */
    @PostMapping("/perfil")
    public String actualizarPerfil(@PathVariable Integer id, Cliente cliente) {
        Cliente existente = obtenerCliente(id);
        existente.actualizarInformacion(
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getCorreo());
        clienteService.guardar(existente);
        return "redirect:/cliente/" + id + "/perfil?exito=true";
    }

    /** Actualiza la contraseña del usuario asociado al cliente. */
    @PostMapping("/password")
    public String actualizarPassword(@PathVariable Integer id,
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model model) {

        Cliente cliente = obtenerCliente(id);
        Usuario usuario = usuarioService.buscarPorCorreo(cliente.getCorreo());

        // Verifica que la contraseña actual del cliente sea correcta antes de permitir el cambio.
        if (usuario == null || !usuario.iniciarSesion(cliente.getCorreo(), contrasenaActual)) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("errorPassword", "La contraseña actual es incorrecta");
            return "cliente/perfil_cliente";
        }

        // Comprueba que la nueva contraseña no esté vacía ni contenga solo espacios.
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("errorPassword", "La nueva contraseña no puede estar vacía");
            return "cliente/perfil_cliente";
        }

        // Verifica que la nueva contraseña coincida con la contraseña de confirmación.
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("errorPassword", "La nueva contraseña y la confirmación no coinciden");
            return "cliente/perfil_cliente";
        }

        usuarioService.actualizarContrasena(cliente.getCorreo(), nuevaContrasena);
        return "redirect:/cliente/" + id + "/perfil?password=true";
    }

    /** Centraliza la validacion del identificador usado por ambas operaciones. */
    private Cliente obtenerCliente(Integer id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return cliente;
    }
}