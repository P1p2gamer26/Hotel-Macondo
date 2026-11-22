package com.hotel.macondo.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.hotel.macondo.service.WompiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos/wompi")
public class WompiWebhookController {
  private final WompiService wompiService;
  public WompiWebhookController(WompiService wompiService) { this.wompiService = wompiService; }

  @PostMapping("/eventos")
  public ResponseEntity<Void> recibirEvento(@RequestBody JsonNode evento,
      @RequestHeader(value = "X-Event-Checksum", required = false, defaultValue = "") String checksum) {
    wompiService.procesarEvento(evento, checksum);
    return ResponseEntity.ok().build();
  }
}
