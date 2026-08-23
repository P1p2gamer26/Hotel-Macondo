package com.hotel.macondo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operador {

    private Integer id;
    private String nombre;
    private Boolean activo;

    /**
     * Cancela una reserva futura cuando el operador esta habilitado.
     */
    public boolean cancelarReserva(Reserva reserva) {
        return Boolean.TRUE.equals(activo) && reserva != null && reserva.cancelar();
    }

    /**
     * Agrega un servicio a la cuenta indicada.
     */
    public boolean agregarServicio(Cuenta cuenta, Servicio servicio, int cantidad) {
        return Boolean.TRUE.equals(activo) && cuenta != null
                && cuenta.agregarItem(servicio, cantidad) != null;
    }

    /**
     * Realiza el checkout cuando no quedan saldos pendientes.
     */
    public boolean realizarCheckout(Reserva reserva, Cuenta cuenta) {
        return Boolean.TRUE.equals(activo) && reserva != null && cuenta != null
                && cuenta.estaSaldada() && reserva.finalizar();
    }
}
