package com.hotel.macondo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.TokenCuenta;
import com.hotel.macondo.entities.TokenCuenta.Proposito;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.repository.TokenCuentaRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import com.hotel.macondo.utils.EmailUtils;

@Service
public class FlujoCorreoCuentaService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UsuarioRepository usuarios;
    private final TokenCuentaRepository tokens;
    private final CorreoTransaccionalService correo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    @Value("${app.backend-base-url:http://localhost:8080}")
    private String backendBaseUrl;

    public FlujoCorreoCuentaService(UsuarioRepository usuarios, TokenCuentaRepository tokens,
            CorreoTransaccionalService correo, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.correo = correo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void enviarVerificacion(String direccion) {
        UserEntity usuario = buscarCuentaGmail(direccion);
        if (usuario == null || usuario.isCorreoVerificado()) return;
        emitirYEnviar(usuario, Proposito.VERIFICAR_CORREO, 24,
                "/api/auth/verificacion/confirmar?token=", "Verifica tu correo - Hotel Macondo",
                "Abre este enlace para verificar tu dirección de correo. El enlace vence en 24 horas:");
    }

    @Transactional
    public void confirmarCorreo(String token) {
        TokenCuenta registro = buscarToken(token, Proposito.VERIFICAR_CORREO);
        registro.getUsuario().setCorreoVerificado(true);
        usuarios.save(registro.getUsuario());
        registro.setConsumidoEn(Instant.now());
        tokens.save(registro);
    }

    @Transactional
    public void solicitarRecuperacion(String direccion) {
        UserEntity usuario = buscarCuentaGmail(direccion);
        if (usuario == null) return;
        emitirYEnviar(usuario, Proposito.RECUPERAR_CONTRASENA, 30,
                "/restablecer-contrasena?token=", "Recupera tu contraseña - Hotel Macondo",
                "Abre este enlace para elegir una contraseña nueva. El enlace vence en 30 minutos:");
    }

    @Transactional
    public void restablecerContrasena(String token, String nueva, String confirmar) {
        if (nueva == null || nueva.isBlank() || !nueva.equals(confirmar)) {
            throw new FormularioErroneoException("La contraseña y su confirmación deben coincidir.");
        }
        TokenCuenta registro = buscarToken(token, Proposito.RECUPERAR_CONTRASENA);
        UserEntity usuario = registro.getUsuario();
        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuarios.save(usuario);
        registro.setConsumidoEn(Instant.now());
        tokens.save(registro);
    }

    private UserEntity buscarCuentaGmail(String direccion) {
        String normalizado = EmailUtils.normalizar(direccion);
        if (normalizado == null || !normalizado.matches("^[a-z0-9._%+-]+@gmail\\.com$")) return null;
        return usuarios.findByCorreoIgnoreCase(normalizado).orElse(null);
    }

    private void emitirYEnviar(UserEntity usuario, Proposito proposito, long minutos,
            String ruta, String asunto, String instruccion) {
        Instant limiteReenvio = Instant.now().minus(1, ChronoUnit.MINUTES);
        if (tokens.findFirstByUsuarioIdAndPurposeOrderByCreadoEnDesc(usuario.getId(), proposito)
                .map(TokenCuenta::getCreadoEn).filter(creado -> creado.isAfter(limiteReenvio)).isPresent()) {
            return;
        }
        tokens.findByUsuarioIdAndPurposeAndConsumidoEnIsNull(usuario.getId(), proposito)
                .forEach(anterior -> { anterior.setConsumidoEn(Instant.now()); tokens.save(anterior); });
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String claro = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        TokenCuenta registro = new TokenCuenta();
        registro.setUsuario(usuario);
        registro.setTokenHash(hash(claro));
        registro.setPurpose(proposito);
        registro.setExpiraEn(Instant.now().plus(minutos, proposito == Proposito.VERIFICAR_CORREO
                ? ChronoUnit.HOURS : ChronoUnit.MINUTES));
        tokens.save(registro);
        String base = (proposito == Proposito.VERIFICAR_CORREO ? backendBaseUrl : frontendBaseUrl)
                .replaceAll("/+$", "");
        correo.enviarEnlace(usuario.getCorreo(), asunto, base + ruta + claro, instruccion);
    }

    private TokenCuenta buscarToken(String claro, Proposito proposito) {
        if (claro == null || claro.isBlank()) throw new FormularioErroneoException("El enlace no es válido o expiró.");
        TokenCuenta registro = tokens.findByTokenHashAndPurpose(hash(claro), proposito)
                .filter(t -> t.getConsumidoEn() == null && t.getExpiraEn().isAfter(Instant.now()))
                .orElseThrow(() -> new FormularioErroneoException("El enlace no es válido o expiró."));
        return registro;
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }
}
