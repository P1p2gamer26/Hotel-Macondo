package com.hotel.macondo.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    private Integer id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String correo;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas = new ArrayList<>();

    /**
     * Crea un cliente sin reservas previas.
     */
    public Cliente(Integer id, String nombre, String apellido, String cedula,
            String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.correo = correo;
    }

    /**
     * Actualiza los datos personales que el cliente puede modificar.
     */
    public void actualizarInformacion(String nombre, String apellido,
            String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    /**
     * Registra una reserva dentro del historial del cliente.
     */
    public void agregarReserva(Reserva reserva) {
        if (reserva != null && !reservas.contains(reserva)) {
            reservas.add(reserva);
        }
    }

    /**
     * Retorna las reservas vigentes o futuras que no han sido canceladas.
     */
    public List<Reserva> verReservasActivas() {
        LocalDate hoy = LocalDate.now();
        return reservas.stream()
                .filter(reserva -> !"CANCELADA".equals(reserva.getEstado()))
                .filter(reserva -> reserva.getFechaFin() != null
                        && !reserva.getFechaFin().isBefore(hoy))
                .toList();
    }

    /**
     * Retorna las reservas finalizadas o canceladas.
     */
    public List<Reserva> verHistorialReservas() {
        LocalDate hoy = LocalDate.now();
        return reservas.stream()
                .filter(reserva -> "CANCELADA".equals(reserva.getEstado())
                        || (reserva.getFechaFin() != null
                                && reserva.getFechaFin().isBefore(hoy)))
                .toList();
    }
}
