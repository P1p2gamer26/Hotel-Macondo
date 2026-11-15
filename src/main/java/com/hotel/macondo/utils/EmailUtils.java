package com.hotel.macondo.utils;

import java.util.Locale;

public final class EmailUtils {

    private EmailUtils() {
    }

    // Normaliza el correo eliminando espacios en los extremos y pasandolo a
    // minusculas uniformes
    public static String normalizar(String correo) {
        if (correo == null) {
            return null;
        }
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    // Comprueba si dos correos electronicos son equivalentes tras ser normalizados
    public static boolean sonEquivalentes(String c1, String c2) {
        if (c1 == null && c2 == null) {
            return true;
        }
        if (c1 == null || c2 == null) {
            return false;
        }
        return normalizar(c1).equals(normalizar(c2));
    }
}