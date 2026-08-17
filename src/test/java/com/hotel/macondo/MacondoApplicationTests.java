package com.hotel.macondo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hotel.macondo.service.HotelService;

// Prueba minima: si el contexto arranca y los repositorios en memoria traen
// el contenido esperado, la landing page se puede pintar completa.
@SpringBootTest
class MacondoApplicationTests {

	@Autowired
	HotelService hotelService;

	@Test
	void contextLoads() {
		assertEquals(4, hotelService.buscarHabitaciones().size());
		assertEquals(6, hotelService.buscarServicios().size());
		assertEquals(3, hotelService.buscarTestimonios().size());
	}

	@Test
	void habitacionesPorCapacidad() {
		// Solo la Luxury recibe 6 personas
		assertEquals(1, hotelService.buscarHabitacionesPorPersonas(6).size());
		assertEquals(4, hotelService.buscarHabitacionesPorPersonas(1).size());
	}

	/*
	 * AQUI VAN LAS PRUEBAS DE RESERVAR, INICIAR SESION Y REGISTRARSE
	 * (guardar/eliminar una reserva y autenticar un usuario).
	 */

}
