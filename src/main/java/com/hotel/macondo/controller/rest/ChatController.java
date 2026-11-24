package com.hotel.macondo.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.dto.ChatMessageRequest;
import com.hotel.macondo.dto.ChatRequest;
import com.hotel.macondo.dto.ChatResponse;
import com.hotel.macondo.service.ChatService;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final int MAX_MENSAJE_LENGTH = 1000;
    private static final int MAX_HISTORIAL = 10;

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse conversar(@RequestBody ChatRequest request) {
        if (request == null || request.mensaje() == null || request.mensaje().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mensaje no puede estar vacío.");
        }
        if (request.mensaje().length() > MAX_MENSAJE_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El mensaje no puede superar los " + MAX_MENSAJE_LENGTH + " caracteres.");
        }

        List<ChatMessageRequest> historial = request.historial() == null ? List.of() : request.historial();
        if (historial.size() > MAX_HISTORIAL) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El historial no puede superar los " + MAX_HISTORIAL + " mensajes.");
        }
        for (ChatMessageRequest mensaje : historial) {
            if (mensaje == null
                    || mensaje.role() == null
                    || (!mensaje.role().equals("user") && !mensaje.role().equals("assistant"))
                    || mensaje.content() == null
                    || mensaje.content().isBlank()
                    || mensaje.content().length() > MAX_MENSAJE_LENGTH) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El historial contiene un mensaje inválido.");
            }
        }

        return chatService.responder(request.mensaje().trim(), historial);
    }
}
