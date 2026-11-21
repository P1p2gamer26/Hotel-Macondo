package com.hotel.macondo.dto;

/** Cuerpo de PUT /api/comentario/{id}?clienteId=... : nuevos valores del comentario del cliente. */
public record ComentarioEdicion(Integer calificacion, String texto) {}
