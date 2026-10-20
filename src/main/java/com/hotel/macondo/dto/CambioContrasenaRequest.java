package com.hotel.macondo.dto;

/**
 * DTO DE ENTRADA (REQUEST) - CLIENTE (CAMBIAR CONTRASEÑA)
 * 
 * Transporta SOLO los datos requeridos para cambiar la contraseña de una cuenta
 * de cliente desde la pantalla de perfil
 * 
 * Se usa porque los campos de confirmación y contraseña actual NO EXISTEN como
 * columnas en Usuario,
 * por lo que este DTO sirve como contenedor temporal y seguro para que el
 * servicio valide las reglas
 * antes de actualizar el hash de acceso
 **/

public record CambioContrasenaRequest(
                String contrasenaActual,
                String nuevaContrasena,
                String confirmarContrasena) {
}