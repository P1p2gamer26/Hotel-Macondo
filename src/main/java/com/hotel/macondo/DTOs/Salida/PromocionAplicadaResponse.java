package com.hotel.macondo.DTOs.Salida;

import java.math.BigDecimal;

public record PromocionAplicadaResponse(String codigo, String nombre, BigDecimal subtotalEstadia,
    BigDecimal descuento, BigDecimal total) {}
