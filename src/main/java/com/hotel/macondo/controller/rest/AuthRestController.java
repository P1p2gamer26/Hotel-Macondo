package com.hotel.macondo.controller.rest;

import java.util.List;
import java.util.Map;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.DTOs.Mapper.ClienteMapper;
import com.hotel.macondo.DTOs.Salida.ClienteDTO;
import com.hotel.macondo.DTOs.Salida.LoginResponseDTO;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.service.UsuarioService;
import com.hotel.macondo.security.JWTGenerator;
import com.hotel.macondo.service.FlujoCorreoCuentaService;

// Controlador REST para la autenticación y control de acceso.

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private FlujoCorreoCuentaService flujoCorreo;

    @Value("${app.frontend-base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    @GetMapping("/verificacion/confirmar")
    public ResponseEntity<Void> confirmarCorreoDesdeEnlace(@RequestParam("token") String token) {
        flujoCorreo.confirmarCorreo(token);
        String destino = frontendBaseUrl.replaceAll("/+$", "") + "/?correoVerificado=true";
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(destino)).build();
    }

    @PostMapping("/verificacion/reenviar")
    public ResponseEntity<Void> reenviarVerificacion(@RequestBody Map<String, String> body) {
        flujoCorreo.enviarVerificacion(body.get("correo"));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/verificacion/confirmar")
    public ResponseEntity<Void> confirmarCorreo(@RequestBody Map<String, String> body) {
        flujoCorreo.confirmarCorreo(body.get("token"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/contrasena/solicitar-recuperacion")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        flujoCorreo.solicitarRecuperacion(body.get("correo"));
        return ResponseEntity.accepted().body(Map.of("mensaje",
                "Si la cuenta Gmail existe, recibirás instrucciones para recuperar la contraseña."));
    }

    @PostMapping("/contrasena/restablecer")
    public ResponseEntity<Void> restablecerContrasena(@RequestBody Map<String, String> body) {
        flujoCorreo.restablecerContrasena(body.get("token"), body.get("nuevaContrasena"),
                body.get("confirmarContrasena"));
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // AUTENTICACIÓN / SESIÓN
    // ==========================================

    // Valida credenciales de acceso para cualquier rol (Cliente, Operador, Admin)
    // http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.get("contrasena");

        if (correo == null || contrasena == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan credenciales");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contrasena));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserEntity usuario = usuarioService.buscarPorCorreo(authentication.getName());

            if (usuario.getRoles().stream().anyMatch(rol -> "CLIENTE".equals(rol.getName()))
                    && !usuario.isCorreoVerificado()) {
                SecurityContextHolder.clearContext();
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Verifica tu correo antes de iniciar sesión.");
            }

            List<String> nombresRoles = usuario.getRoles().stream()
                    .map(Rol::getName)
                    .toList();

            String rolPrincipal = nombresRoles.isEmpty() ? null : nombresRoles.get(0);

            // Se envía el perfil básico convertido a DTO mediante MapStruct
            ClienteDTO clienteDTO = usuario.getCliente() != null
                    ? ClienteMapper.INSTANCE.convert(usuario.getCliente())
                    : null;

            LoginResponseDTO respuesta = new LoginResponseDTO(
                    usuario.getId(),
                    usuario.getCorreo(),
                    rolPrincipal,
                    nombresRoles,
                    clienteDTO,
                    jwtGenerator.generateToken(authentication));

            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
    }
}
