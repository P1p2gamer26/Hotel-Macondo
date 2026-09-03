package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "cliente/perfil_cliente";
    }

    /** Actualiza solamente los datos personales editables del cliente. */
    @PostMapping("/perfil")
    public String actualizarPerfil(@PathVariable Integer id, Cliente cliente, Model model) {
        Cliente existente = clienteService.buscarPorId(id); // Obtenemos el usuario asociado al cliente para modificarlo
        String correPrevio = existente.getCorreo();
        String correoNuevo = cliente.getCorreo();

        if(!usuarioService.validarCorreo(correoNuevo)){
            return "redirect:/cliente/" + id + "/perfil";
        }

        // Actualiza los datos del cliente
        clienteService.actualizarInformacion(existente, cliente);

        // Se cambia el correo de la cuenta asociada al cliente
        usuarioService.actualizarCorreo(correPrevio, correoNuevo);

        return "redirect:/cliente/" + id + "/perfil";
    }

    /** Actualiza la contraseña del usuario asociado al cliente. */
    @PostMapping("/password")
    public String actualizarPassword(@PathVariable Integer id,
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model model) {

        // Toma de datos
        Cliente cliente = clienteService.buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorCorreo(cliente.getCorreo());

        // Valida que la contraseña actual sea correcta, que la nueva contraseña sea valida y que coincida con la confirmacion
        if (!usuario.iniciarSesion(cliente.getCorreo(), contrasenaActual) 
            || !usuarioService.validarContrasena(nuevaContrasena) 
            || !nuevaContrasena.equals(confirmarContrasena)) {
            model.addAttribute("cliente", cliente);
            return "cliente/perfil_cliente";
        }

        usuarioService.actualizarContrasena(cliente.getCorreo(), nuevaContrasena);
        return "redirect:/cliente/" + id + "/perfil?password=true";
    }

}