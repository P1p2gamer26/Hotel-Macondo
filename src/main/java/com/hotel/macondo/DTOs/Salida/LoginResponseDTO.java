package com.hotel.macondo.DTOs.Salida;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String correo;
    private String rol;
    private List<String> roles;
    private ClienteDTO cliente;
}