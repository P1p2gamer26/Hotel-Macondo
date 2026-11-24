package com.hotel.macondo.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.hotel.macondo.service.MercadoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos/mercadopago")
public class MercadoPagoWebhookController {
  private final MercadoPagoService mercadoPagoService;

  public MercadoPagoWebhookController(MercadoPagoService mercadoPagoService) {
    this.mercadoPagoService = mercadoPagoService;
  }

  @PostMapping("/eventos")
  public ResponseEntity<Void> recibirEvento(
      @RequestBody JsonNode evento,
      @RequestHeader(value = "x-signature", required = false) String signature,
      @RequestHeader(value = "x-request-id", required = false) String requestId,
      @RequestParam(name = "data.id", required = false) String dataId) {
    mercadoPagoService.procesarNotificacion(evento, signature, requestId, dataId);
    return ResponseEntity.ok().build();
  }
}
