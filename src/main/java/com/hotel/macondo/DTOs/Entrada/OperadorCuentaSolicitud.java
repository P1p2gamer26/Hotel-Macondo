package com.hotel.macondo.DTOs.Entrada;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OperadorCuentaSolicitud(
        String correo,
        String nombre,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String contrasena) {

    @Override
    public String toString() {
        return "OperadorCuentaSolicitud";
    }
}
