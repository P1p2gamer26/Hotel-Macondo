package com.hotel.macondo.dto;

import java.util.List;

public record ChatResponse(String respuesta, List<ChatActionType> acciones) {

    public ChatResponse {
        acciones = List.copyOf(acciones);
    }

    public ChatResponse(String respuesta) {
        this(respuesta, List.of());
    }
}
