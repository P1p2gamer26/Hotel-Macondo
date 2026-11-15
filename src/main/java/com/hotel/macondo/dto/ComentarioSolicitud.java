package com.hotel.macondo.dto;

public record ComentarioSolicitud(Long clienteId, Long reservaId, Integer calificacion, String texto) {}
