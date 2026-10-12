package com.hotel.macondo.dto;

/** Datos requeridos para cambiar la contraseña de una cuenta de cliente. */
public record CambioContrasenaRequest(
        String contrasenaActual,
        String nuevaContrasena,
        String confirmarContrasena) {
}
