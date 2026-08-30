package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.UsuarioService;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService serviceUsuario;
    @Autowired
    private ClienteService serviceCliente;

    /**
     * Muestra el formulario de inicio de sesion.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    /**
     * Devuelve al login.
     */
    @GetMapping("/logout")
    public String cerrarSesion() {
        return "redirect:/login";
    }

    /**
     * Muestra el formulario de registro de usuario en Bootstrap 5.
     */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        // Crea un cliente vacio que se llenara con la informacion del formulario
        Cliente cliente = new Cliente(null, "", "", "", "", "");

        // Le pasa el objeto cliente al model para que se pueda llenar con el formulario
        model.addAttribute("cliente", cliente);

        // Redirecciona a la pagina con el formulario
        return "registro";
    }

    /**
     * Procesa las credenciales y redirige dinamicamente usando el ID del usuario
     * autenticado.
     */
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam(name = "username") String correo,
            @RequestParam(name = "password") String contrasena,
            Model model) {

        Usuario usuario = serviceUsuario.autenticar(correo, contrasena);

        if (usuario == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        // Redirecciones por rol
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            return "redirect:/admin";
        } else if (usuario.getRol() == Rol.OPERADOR) {
            return "redirect:/operador";
        } else if (usuario.getRol() == Rol.CLIENTE) {
            // Redirige al dashboard del cliente con su ID correspondiente
            return "redirect:/cliente/" + usuario.getId();
        }

        return "redirect:/";
    }

    /*
    * Obtiene la informacion del cliente desde el formulario para poder guardarlo,
    * la contraseña la obtiene por separado ya que esta esta vinculada al usuario
    * no al cliente
    */
    @PostMapping("/registro")
    public String agregarCliente(@ModelAttribute("cliente") Cliente clienteNuevo, @RequestParam("contrasena") String contrasena, Model model){

        // Validacion y estandarizacion del correo
        String correo = clienteNuevo.getCorreo() != null
        ? clienteNuevo.getCorreo().trim().toLowerCase()
        : null;

        // Confirmacion de que el correo no es nulo
        if( correo == null || correo.isBlank()){
            model.addAttribute("error","El correo no puede ser vacio");
            return "registro";
        }

        // Confirmacion de que el correo no esta vinculado a otro usuario
        if(serviceUsuario.buscarPorCorreo(correo) != null){
            model.addAttribute("error","Este correo ya esta en uso");
            return "registro";
        }

        // Guarda el cliente en el repository y crea una copia que tiene el id asignado
        Cliente clienteGuardado = serviceCliente.guardar(clienteNuevo);

        // Crea el usuario relacionado al cliente
        Usuario usuario = new Usuario();
        usuario.setId(clienteGuardado.getId());
        usuario.setCorreo(clienteGuardado.getCorreo());
        usuario.setContrasena(contrasena);
        usuario.setRol(Rol.CLIENTE);

        // Guarda al usuario en el repository
        Usuario usuarioGuardado = serviceUsuario.registrar(usuario);

        // Confirmacion de que la creacion del usuario
        if (usuarioGuardado == null) {
            serviceCliente.eliminar(clienteGuardado.getId());
            model.addAttribute("error", "No se pudo crear la cuenta asociada");
            return "registro";
        }

        // Cuando se termina de crear la cuenta del cliente se redirecciona a la pagina de su cuenta
        return "redirect:/cliente/" + clienteNuevo.getId();
    }

}
