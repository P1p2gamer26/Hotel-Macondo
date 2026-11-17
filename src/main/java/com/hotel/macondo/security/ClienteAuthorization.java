package com.hotel.macondo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.repository.UsuarioRepository;

@Component
public class ClienteAuthorization {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public boolean esPropietario(Authentication authentication, String clienteId) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                        .noneMatch(rol -> rol.getAuthority().equals("CLIENTE"))) {
            return false;
        }
        return usuarioRepository.findByCorreoIgnoreCase(authentication.getName())
                .map(usuario -> usuario.getCliente() != null
                        && usuario.getCliente().getId().toString().equals(clienteId))
                .orElse(false);
    }

    public boolean puedeConsultar(Authentication authentication, String clienteId) {
        return authentication != null && (authentication.getAuthorities().stream()
                .anyMatch(rol -> rol.getAuthority().equals("ADMINISTRADOR")
                        || rol.getAuthority().equals("OPERADOR"))
                || esPropietario(authentication, clienteId));
    }
}
