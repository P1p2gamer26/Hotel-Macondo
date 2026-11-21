package com.hotel.macondo.dto;

import java.time.LocalDateTime;

/** Vista de los comentarios propios del cliente: incluye la reserva para saber cuál ya está comentada. */
public record ComentarioClienteResponse(
    Long id, Long reservaId, int calificacion, String texto, LocalDateTime fechaCreacion, boolean publicado) {}
