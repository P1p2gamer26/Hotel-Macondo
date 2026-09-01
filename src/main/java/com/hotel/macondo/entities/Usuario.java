package com.hotel.macondo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private Integer id;
    private String correo;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String contrasena;

    private Rol rol;

    /**
     * Valida las credenciales usando los datos de este usuario.
     */
    public boolean iniciarSesion(String correo, String contrasena) {
        return this.correo != null
                && this.correo.equalsIgnoreCase(correo)
                && this.contrasena != null
                && this.contrasena.equals(contrasena);
    }

    /**
     * Verifica si el usuario puede actuar con el rol solicitado.
     */
    public boolean tieneRol(Rol rolSolicitado) {
        return rol != null && rol == rolSolicitado;
    }
}
