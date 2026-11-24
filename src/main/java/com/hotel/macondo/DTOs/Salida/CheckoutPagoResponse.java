package com.hotel.macondo.DTOs.Salida;

import java.math.BigDecimal;

public record CheckoutPagoResponse(String referencia, String checkoutUrl, BigDecimal monto, String estado) {}
