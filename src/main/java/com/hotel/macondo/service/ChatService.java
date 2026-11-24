package com.hotel.macondo.service;

import com.hotel.macondo.dto.ChatResponse;
import com.hotel.macondo.dto.ChatMessageRequest;

import java.util.List;

public interface ChatService {

    ChatResponse responder(String mensaje, List<ChatMessageRequest> historial);
}
