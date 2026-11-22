package com.hotel.macondo.controller.rest;

import com.hotel.macondo.DTOs.Salida.PromocionAplicadaResponse;
import com.hotel.macondo.DTOs.Salida.PromocionResponse;
import com.hotel.macondo.entities.Promocion;
import com.hotel.macondo.service.PromocionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promociones")
public class PromocionRestController {
  private final PromocionService service;
  public PromocionRestController(PromocionService service) { this.service = service; }

  @GetMapping("/publicas")
  public List<PromocionResponse> publicas() { return service.listarPublicas().stream().map(PromocionResponse::desde).toList(); }

  @GetMapping
  public List<PromocionResponse> todas() { return service.listarAdmin().stream().map(PromocionResponse::desde).toList(); }

  @PostMapping
  public PromocionResponse crear(@RequestBody Promocion promocion) { return PromocionResponse.desde(service.guardar(promocion)); }

  @PutMapping("/{id}")
  public PromocionResponse actualizar(@PathVariable("id") Long id, @RequestBody Promocion promocion) {
    promocion.setId(id); return PromocionResponse.desde(service.guardar(promocion));
  }

  @DeleteMapping("/{id}")
  public void eliminar(@PathVariable("id") Long id) { service.eliminar(id); }

  @GetMapping("/calcular")
  public PromocionAplicadaResponse calcular(@RequestParam String codigo, @RequestParam LocalDate fechaEntrada,
      @RequestParam LocalDate fechaSalida, @RequestParam BigDecimal precioNoche) {
    return service.calcular(codigo, fechaEntrada, fechaSalida, precioNoche);
  }
}
