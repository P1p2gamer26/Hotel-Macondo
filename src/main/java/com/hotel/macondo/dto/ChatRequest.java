package com.hotel.macondo.dto;

import java.util.List;

public record ChatRequest(String mensaje, List<ChatMessageRequest> historial) {
}
