package com.hotel.macondo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Usuario;

@Service
public class CuentaClienteServiceImpl implements CuentaClienteService {
    
    @Autowired
    ClienteService clienteService;
    @Autowired
    UsuarioService usuarioService;

    /** {@inheritDoc} */
    @Override
    public Boolean crearCuenta(Cliente cliente, String contrasena) {
        // Validacion y estandarizacion del correo
        String correo = cliente.getCorreo();

        // Valida que el correo no este en uso
        if(!usuarioService.validarCorreo(correo)){
            return false;
        }

        // Guarda el cliente en el repository y crea una copia que tiene el id asignado
        Cliente clienteGuardado = clienteService.guardar(cliente);

        // Guarda al usuario en el repository
        Usuario usuarioGuardado = usuarioService.registrarCliente(clienteGuardado, contrasena);

        // Confirmacion de que la creacion del usuario
        if (usuarioGuardado == null) {
            clienteService.eliminar(clienteGuardado.getId());
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Boolean actualizarContrasena(Integer id, String contrasenaActual, String nuevaContrasena, String confirmarContrasena) {
        Cliente cliente = clienteService.buscarPorId(id);
        if(cliente == null){
            return false;
        }
        Usuario usuario = usuarioService.buscarPorCorreo(cliente.getCorreo());
        if(usuario == null){
            return false;
        }

        if (!usuario.iniciarSesion(cliente.getCorreo(), contrasenaActual) 
            || !usuarioService.validarContrasena(nuevaContrasena) 
            || !nuevaContrasena.equals(confirmarContrasena)) {
            return false;
        }

        usuarioService.actualizarContrasena(cliente.getCorreo(), nuevaContrasena);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Boolean actualizarPerfil(Integer id, Cliente cliente) {
        Cliente existente = clienteService.buscarPorId(id); // Obtenemos el usuario asociado al cliente para modificarlo
        String correoPrevio = existente.getCorreo();
        String correoNuevo = cliente.getCorreo();

        // Validamos que el correo nuevo no este en uso por otro usuario
        if(!correoNuevo.equals(correoPrevio) && !usuarioService.validarCorreo(correoNuevo)){
            return false;
        }

        // Validamos que el correo nuevo no este en uso por otro usuario
        if (existente == null || cliente == null || correoNuevo == null || correoNuevo.isBlank()) {
            return false;
        }

        // Actualiza los datos del cliente
        if (clienteService.actualizarInformacion(existente, cliente) == null) {
            return false;
        }

        // Se cambia el correo de la cuenta asociada al cliente
        if (usuarioService.actualizarCorreo(correoPrevio, correoNuevo) == null) {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Boolean eliminarCuenta(Integer id) {
        // Se busca el usuario asociado al cliente
        String correoUsuario = (clienteService.buscarPorId(id)).getCorreo();

        clienteService.eliminar(id);
        // Se elimina el usuario asociado al cliente usando el correo
        usuarioService.eliminar(correoUsuario);
        return true;
    }

}
