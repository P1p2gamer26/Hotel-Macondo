package com.hotel.macondo.security;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        if (correo == null || correo.isBlank()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        UserEntity usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        boolean accesoActivo = usuario.getOperador() == null || Boolean.TRUE.equals(usuario.getOperador().getActivo());
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .authorities(mapRolesToAuthorities(usuario.getRoles()))
                .disabled(!accesoActivo)
                .build();
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Rol> roles) {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getName()))
                .collect(Collectors.toList());
    }
}
