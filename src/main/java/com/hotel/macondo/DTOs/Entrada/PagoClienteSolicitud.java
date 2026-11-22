package com.hotel.macondo.DTOs.Entrada;

import java.math.BigDecimal;

public record PagoClienteSolicitud(BigDecimal monto, String metodoPago) {}
