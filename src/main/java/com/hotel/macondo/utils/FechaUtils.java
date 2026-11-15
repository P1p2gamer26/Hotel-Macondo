package com.hotel.macondo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import com.hotel.macondo.errors.FormularioErroneoException;

public final class FechaUtils {

  // Utilidades para parseo, conversion y calculo de fechas

  private FechaUtils() {
  }

  // Convierte un texto en formato ISO a LocalDate
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

  // Calcula la diferencia en noches/dias entre dos fechas de estancia
  public static long calcularNoches(LocalDate inicio, LocalDate fin) {
    if (inicio == null || fin == null) {
      return 0;
    }
    return ChronoUnit.DAYS.between(inicio, fin);
  }

  // Convierte de forma segura un LocalDate a String ISO evitando
  // NullPointerException
  public static String toIsoString(LocalDate fecha) {
    return fecha == null ? null : fecha.toString();
  }
}