package com.hotel.macondo.dto;

import java.time.LocalDateTime;

/** Vista del comentario: solo el nombre de pila del cliente (sin apellido, cédula ni correo). */
public record ComentarioResponse(
    Long id, String nombre, int calificacion, String texto, LocalDateTime fechaCreacion, boolean publicado) {}
