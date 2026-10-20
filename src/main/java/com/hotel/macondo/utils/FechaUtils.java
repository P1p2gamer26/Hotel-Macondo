package com.hotel.macondo.utils;

import com.hotel.macondo.errors.FormularioErroneoException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public final class FechaUtils {

  private FechaUtils() {
  } // Evita instanciación

  public static LocalDate convertirFecha(String fecha, String nombreCampo) {
    try {
      if (fecha == null || fecha.isBlank()) {
        throw new DateTimeParseException("Fecha vacía", fecha == null ? "" : fecha, 0);
      }
      return LocalDate.parse(fecha);
    } catch (DateTimeParseException ex) {
      throw new FormularioErroneoException("La fecha de " + nombreCampo + " no tiene un formato válido.");
    }
  }

  public static long calcularNoches(LocalDate inicio, LocalDate fin) {
    if (inicio == null || fin == null)
      return 0;
    return ChronoUnit.DAYS.between(inicio, fin);
  }
}