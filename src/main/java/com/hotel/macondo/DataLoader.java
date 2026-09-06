package com.hotel.macondo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hotel.macondo.entities.Admin;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.entities.Testimonio;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.repository.AdminRepository;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.OperadorRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.ServicioRepository;
import com.hotel.macondo.repository.TestimonioRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import com.hotel.macondo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class DataLoader implements CommandLineRunner {

        @Autowired
        private AdminRepository adminRepository;

        @Autowired
        private TipoHabitacionRepository tipoHabitacionRepository;

        @Autowired
        private HabitacionRepository habitacionRepository;

        @Autowired
        private ServicioRepository servicioRepository;

        @Autowired
        private TestimonioRepository testimonioRepository;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private OperadorRepository operadorRepository;

        @Autowired
        private ReservaRepository reservaRepository;

        @Autowired
        private CuentaRepository cuentaRepository;

        @Override
        public void run(String... args) {
                List<TipoHabitacion> tipos = cargarTiposHabitacion();
                List<Habitacion> habitaciones = cargarHabitaciones(tipos);
                cargarServicios();
                cargarTestimonios();

                List<Cliente> clientes = cargarClientes();
                cargarUsuariosYPerfiles(clientes);
                cargarReservasPrincipales();
                cargarReservasDeClientes(clientes, habitaciones);
                cargarCuenta();
        }

        private List<TipoHabitacion> cargarTiposHabitacion() {
                List<TipoHabitacion> tipos = new ArrayList<>();

                tipos.add(tipoHabitacionRepository.save(
                                TipoHabitacion.builder()
                                                .nombre("Castaño Fundacional")
                                                .descripcion("Refugio íntimo con vista al gran patio de Macondo, cama queen y brisa fresca.")
                                                .precioNoche(BigDecimal.valueOf(280000))
                                                .capacidadPersonas(2)
                                                .build()));

                tipos.add(tipoHabitacionRepository.save(
                                TipoHabitacion.builder()
                                                .nombre("Orfebrería Buendía")
                                                .descripcion("Espacio distinguido con detalles artesanales en oro, balcón y sala de lectura.")
                                                .precioNoche(BigDecimal.valueOf(450000))
                                                .capacidadPersonas(3)
                                                .build()));

                tipos.add(tipoHabitacionRepository.save(
                                TipoHabitacion.builder()
                                                .nombre("Mariposas Amarillas")
                                                .descripcion(
                                                                "Suite boutique luminosa decorada con motivos botánicos, cama king size y terraza caribeña.")
                                                .precioNoche(BigDecimal.valueOf(650000))
                                                .capacidadPersonas(4)
                                                .build()));

                tipos.add(tipoHabitacionRepository.save(
                                TipoHabitacion.builder()
                                                .nombre("Cuarto de Melquíades")
                                                .descripcion(
                                                                "Suite ejecutiva con estudio privado, selección de libros clásicos y vista al río.")
                                                .precioNoche(BigDecimal.valueOf(980000))
                                                .capacidadPersonas(2)
                                                .build()));

                tipos.add(tipoHabitacionRepository.save(
                                TipoHabitacion.builder()
                                                .nombre("Cien Años Presidencial")
                                                .descripcion(
                                                                "Villa exclusiva frente al mar con piscina privada y atención personalizada 24 horas.")
                                                .precioNoche(BigDecimal.valueOf(1900000))
                                                .capacidadPersonas(6)
                                                .build()));

                return tipos;
        }

        private List<Habitacion> cargarHabitaciones(List<TipoHabitacion> tipos) {
                List<Habitacion> habitaciones = new ArrayList<>();

                // matriz de nombres tematicos (5 pisos x 10 nombres inspirados en la obra)
                String[][] nombresPorPiso = {
                                { // piso 1: castano fundacional
                                                "Sombra del Castaño", "Río de Piedras", "Sendero del Galeón",
                                                "Trocha de la Ciénaga",
                                                "Brisa del Río", "Tierra Colorada", "Remanso de la Ceiba",
                                                "Platanal Dorado",
                                                "Canto de Chicharras", "Rocío Matinal"
                                },
                                { // piso 2: orfebreria buendia
                                                "Pescaíto de Oro", "Taller de Orfebrería", "Yunque y Fuego",
                                                "Fragua Colonial",
                                                "Muralla y Sol", "Reloj de Arena", "Sala de Tertulia", "Alcandora Real",
                                                "Baúl de Recuerdos", "Crisol del Mar"
                                },
                                { // piso 3: mariposas amarillas
                                                "Nube de Mariposas", "Patio de Begonias", "Remanso de Remedios",
                                                "Balcón de la Ciénaga",
                                                "Jardín Encantado", "Susurro del Alba", "Mirador de Amaranta",
                                                "Llovizna de Flores",
                                                "Sueño de Guayaba", "Azahares del Viento"
                                },
                                { // piso 4: cuarto de melquiades
                                                "Cuarto del Hielo", "Taller de Alquimia", "Espejo de Agua",
                                                "Salón de Imanes",
                                                "Astrolabio Mayor", "Gabinete Gitano", "Prisma del Sol",
                                                "Brújula Antigua",
                                                "Viento del Trópico", "Atalaya Secreta"
                                },
                                { // piso 5: cien anos presidencial
                                                "Galeón Perdido", "Ciudad de los Espejos", "Horizonte Caribe",
                                                "Refugio del Patriarca",
                                                "Cúpula Macondo", "Terraza del Olvido", "Mirador del Océano",
                                                "Brisa de Ultramar",
                                                "Palacio de Palmeras", "Cumbre Solitaria"
                                }
                };

                String[] imagenesPorTipo = {
                                "/images/HabitacionNormal.avif",
                                "/images/HabitacionExecutive.avif",
                                "/images/HabitacionVIP.avif",
                                "/images/HabitacionExecutive.avif",
                                "/images/HabitacionLuxury.avif"
                };

                String[] etiquetas = { "ACOGEDORA", "POPULAR", "EXCLUSIVA", "HISTÓRICA", "ÚNICA" };

                // generacion sistematica de las 50 habitaciones fisicas
                for (int piso = 1; piso <= 5; piso++) {
                        TipoHabitacion tipo = tipos.get(piso - 1);
                        String etiqueta = etiquetas[piso - 1];
                        String imagen = imagenesPorTipo[piso - 1];

                        for (int hab = 1; hab <= 10; hab++) {
                                String numero = String.format("%d%02d", piso, hab);
                                String nombre = nombresPorPiso[piso - 1][hab - 1] + " " + numero;

                                Habitacion h = Habitacion.builder()
                                                .nombre(nombre)
                                                .etiqueta(etiqueta)
                                                .descripcion(tipo.getDescripcion())
                                                .precio(tipo.getPrecioNoche())
                                                .capacidad(tipo.getCapacidadPersonas())
                                                .imagen(imagen)
                                                .numero(numero)
                                                .estado("DISPONIBLE")
                                                .piso(piso)
                                                .build();

                                h.aplicarTipo(tipo);
                                habitaciones.add(habitacionRepository.save(h));
                        }
                }

                return habitaciones;
        }

        private List<Cliente> cargarClientes() {
                List<Cliente> clientes = new ArrayList<>();

                // 10 clientes con identificadores cortos y correos con formato
                // nombre@macondo.com
                String[][] datos = {
                                { "Úrsula", "Iguarán", "101", "300101", "ursula@macondo.com" },
                                { "José Arcadio", "Buendía", "102", "300102", "josearcadio@macondo.com" },
                                { "Aureliano", "Buendía", "103", "300103", "aureliano@macondo.com" },
                                { "Amaranta", "Buendía", "104", "300104", "amaranta@macondo.com" },
                                { "Rebeca", "Montiel", "105", "300105", "rebeca@macondo.com" },
                                { "Pietro", "Crespi", "106", "300106", "pietro@macondo.com" },
                                { "Gerineldo", "Márquez", "107", "300107", "gerineldo@macondo.com" },
                                { "Petra", "Cotes", "108", "300108", "petra@macondo.com" },
                                { "Mauricio", "Babilonia", "109", "300109", "mauricio@macondo.com" },
                                { "Meme", "Buendía", "110", "300110", "meme@macondo.com" }
                };

                for (String[] d : datos) {
                        clientes.add(clienteRepository.save(
                                        Cliente.builder()
                                                        .nombre(d[0])
                                                        .apellido(d[1])
                                                        .cedula(d[2])
                                                        .telefono(d[3])
                                                        .correo(d[4])
                                                        .build()));
                }

                return clientes;
        }

        private void cargarUsuariosYPerfiles(List<Cliente> clientes) {
                // administrador general
                Usuario userAdmin = usuarioRepository.save(
                                Usuario.builder()
                                                .correo("admin@macondo.com")
                                                .contrasena("admin")
                                                .rol(Rol.ADMINISTRADOR)
                                                .build());
                Admin admin = new Admin("Administrador Principal");
                admin.asignarUsuario(userAdmin);
                adminRepository.save(admin);

                // operador de recepcion
                Usuario userOperador = usuarioRepository.save(
                                Usuario.builder()
                                                .correo("operador@macondo.com")
                                                .contrasena("ope")
                                                .rol(Rol.OPERADOR)
                                                .build());
                Operador operador = new Operador("Recepción Principal", true);
                operador.asignarUsuario(userOperador);
                operadorRepository.save(operador);

                // usuarios asociados a cada cliente con contrasena 123
                for (Cliente c : clientes) {
                        Usuario u = Usuario.builder()
                                        .correo(c.getCorreo())
                                        .contrasena("123")
                                        .rol(Rol.CLIENTE)
                                        .build();
                        u.asignarCliente(c);
                        usuarioRepository.save(u);
                }
        }

        private void cargarServicios() {
                // 1. spa y bienestar
                servicioRepository.save(
                                crearServicio(
                                                "Spa & Bienestar",
                                                "Bienestar",
                                                "60-90 min",
                                                "Sumérgete en una experiencia sensorial de relajación profunda con masajes terapéuticos, aromaterapia caribeña y tratamientos faciales con ingredientes de la región.",
                                                "Nuestro spa combina técnicas milenarias de bienestar con ingredientes naturales del Caribe: aceite de coco, flores de cayena, sales del mar y hierbas aromáticas de la Sierra Nevada. Cada sesión está diseñada para despertar los sentidos y restaurar el equilibrio del cuerpo y la mente. Disfruta de masajes relajantes, tratamientos de hidroterapia, envolturas de arcilla caribeña y aromaterapia en cabinas privadas con vista al jardín tropical.",
                                                "/images/Spa.avif",
                                                true,
                                                120000,
                                                "Lunes a domingo: 8:00 a.m. - 8:00 p.m.",
                                                List.of(
                                                                "Masaje de 60 min",
                                                                "Aromaterapia incluida",
                                                                "Baño de vapor",
                                                                "Infusión de hierbas",
                                                                "Cabina privada",
                                                                "Toallas de lujo"),
                                                List.of("Masajes", "Hidroterapia", "Aromaterapia",
                                                                "Tratamientos faciales")));

                // 2. restaurante gourmet
                servicioRepository.save(
                                crearServicio(
                                                "Restaurante Gourmet",
                                                "Gastronomía",
                                                "Almuerzo & cena",
                                                "La cocina caribeña elevada a su máxima expresión. Platos elaborados con productos locales y técnicas contemporáneas, con vista panorámica al mar.",
                                                "Una experiencia culinaria donde los sabores del Caribe se encuentran con técnicas contemporáneas. Nuestro menú celebra los productos locales, la pesca del día y las recetas que han pasado de generación en generación.",
                                                "/images/Restaurante.avif",
                                                false,
                                                85000,
                                                "Todos los días: 12:00 m. - 10:30 p.m.",
                                                List.of(
                                                                "Menú degustación",
                                                                "Maridaje de vinos",
                                                                "Vista al mar",
                                                                "Reserva garantizada",
                                                                "Opción Vegana",
                                                                "Menú infantil"),
                                                List.of("Cocina caribeña", "Maridaje", "Cena", "Productos locales")));

                // 3. piscina infinity
                servicioRepository.save(
                                crearServicio(
                                                "Piscina Infinity",
                                                "Bienestar",
                                                "Acceso diario",
                                                "Nada hacia el horizonte infinito del Caribe desde nuestra piscina de borde abierto frente al mar.",
                                                "Una piscina serena frente al Caribe, con camastros, bebidas frescas y atención durante todo el día.",
                                                "/images/Piscina.avif",
                                                false,
                                                70000,
                                                "Lunes a domingo: 7:00 a.m. - 9:00 p.m.",
                                                List.of("Camastro reservado", "Toallas", "Bebida de bienvenida",
                                                                "Servicio junto a la piscina"),
                                                List.of("Piscina", "Descanso", "Vista al mar")));

                // 4. playa privada
                servicioRepository.save(
                                crearServicio(
                                                "Playa Privada",
                                                "Bienestar",
                                                "Acceso diario",
                                                "Arena blanca, sombra natural y el Caribe a pocos pasos de tu habitación.",
                                                "Disfruta de un sector reservado de playa con servicio personalizado, zonas de descanso y actividades tranquilas frente al mar.",
                                                "/images/PlayaPriv.avif",
                                                false,
                                                60000,
                                                "Lunes a domingo: 7:00 a.m. - 6:00 p.m.",
                                                List.of("Sombrilla", "Camastro", "Toalla", "Bebida de bienvenida"),
                                                List.of("Playa", "Descanso", "Caribe")));

                // 5. tours guiados
                servicioRepository.save(
                                crearServicio(
                                                "Tours Guiados",
                                                "Aventura",
                                                "Medio día / Día completo",
                                                "Descubre los rincones más mágicos de la costa caribeña con nuestros guías expertos. Cartagena histórica, islas del Rosario, manglares y más.",
                                                "Recorre Cartagena y sus alrededores con anfitriones locales que conocen cada historia, sabor y paisaje de la región.",
                                                "/images/Guiado.avif",
                                                false,
                                                95000,
                                                "Salidas programadas todos los días.",
                                                List.of("Guía bilingüe", "Transporte incluido", "Snacks y agua",
                                                                "Seguro de viaje"),
                                                List.of("Cartagena", "Islas", "Manglares", "Historia")));

                // 6. eventos especiales
                servicioRepository.save(
                                crearServicio(
                                                "Eventos Especiales",
                                                "Exclusivo",
                                                "A medida",
                                                "Celebra los momentos más importantes de tu vida en el escenario perfecto. Bodas, aniversarios, reuniones corporativas con decoración y catering de lujo.",
                                                "Creamos celebraciones a medida frente al mar, desde encuentros privados hasta bodas y eventos corporativos completos.",
                                                "/images/Eventos.avif",
                                                false,
                                                3500000,
                                                "Programación personalizada.",
                                                List.of(
                                                                "Coordinador de eventos",
                                                                "Decoración temática",
                                                                "Catering gourmet",
                                                                "Fotografía profesional"),
                                                List.of("Bodas", "Celebraciones", "Eventos corporativos")));
        }

        private Servicio crearServicio(
                        String nombre,
                        String categoria,
                        String duracion,
                        String descripcion,
                        String descripcionDetalle,
                        String imagen,
                        boolean destacado,
                        long precio,
                        String horario,
                        List<String> incluidos,
                        List<String> etiquetas) {
                return new Servicio(
                                nombre,
                                descripcion,
                                categoria,
                                imagen,
                                destacado,
                                BigDecimal.valueOf(precio),
                                true,
                                duracion,
                                descripcionDetalle,
                                horario,
                                incluidos,
                                etiquetas);
        }

        private void cargarTestimonios() {
                testimonioRepository.save(
                                new Testimonio(
                                                "Hotel Macondo es un sueño hecho realidad. La combinación de lujo, naturaleza y la magia del Caribe colombiano me dejó sin palabras. Regresaré sin duda.",
                                                "Valentina Ospina",
                                                "Bogotá, Colombia",
                                                5,
                                                "/images/IconoP1.avif"));
                testimonioRepository.save(
                                new Testimonio(
                                                "Nunca imaginé que un hotel pudiera transmitir tanta poesía. El restaurante es excepcional y el servicio es de otro planeta. Una experiencia completamente transformadora.",
                                                "Martín Delgado",
                                                "Ciudad de México, México",
                                                5,
                                                "/images/IconoP2.avif"));
                testimonioRepository.save(
                                new Testimonio(
                                                "Vine buscando descanso y encontré magia pura. La suite VIP con terraza frente al mar y el spa con rituales caribeños fueron absolutamente perfectos.",
                                                "Sofía Benítez",
                                                "Madrid, España",
                                                5,
                                                "/images/IconoP3.avif"));
        }

        private void cargarReservasPrincipales() {
                reservaRepository.save(
                                new Reserva(
                                                "MHC-2025-001",
                                                LocalDate.now(),
                                                LocalDate.now().plusDays(3),
                                                2,
                                                "ACTIVA",
                                                new BigDecimal("2850000")));
                reservaRepository.save(
                                new Reserva(
                                                "MHC-2025-002",
                                                LocalDate.now().plusDays(5),
                                                LocalDate.now().plusDays(8),
                                                2,
                                                "CONFIRMADA",
                                                new BigDecimal("1740000")));
                reservaRepository.save(
                                new Reserva(
                                                "MHC-2024-089",
                                                LocalDate.now().minusDays(10),
                                                LocalDate.now().minusDays(7),
                                                1,
                                                "FINALIZADA",
                                                new BigDecimal("1050000")));
                reservaRepository.save(
                                new Reserva(
                                                "MHC-2023-211",
                                                LocalDate.now().minusDays(30),
                                                LocalDate.now().minusDays(25),
                                                4,
                                                "CANCELADA",
                                                new BigDecimal("5700000")));
        }

        private void cargarReservasDeClientes(
                        List<Cliente> clientes, List<Habitacion> habitaciones) {
                LocalDate entradaUrsula = LocalDate.now().plusDays(10);
                guardarReservaDeCliente(
                                new Reserva(
                                                "MCD-2026-0915",
                                                entradaUrsula,
                                                entradaUrsula.plusDays(3),
                                                habitaciones.get(0).getCapacidad(),
                                                "ACTIVA",
                                                BigDecimal.ZERO),
                                clientes.get(0),
                                habitaciones.get(0));

                LocalDate salidaUrsula = LocalDate.now().minusDays(15);
                guardarReservaDeCliente(
                                new Reserva(
                                                "MCD-2026-0801",
                                                salidaUrsula.minusDays(2),
                                                salidaUrsula,
                                                habitaciones.get(10).getCapacidad(),
                                                "FINALIZADA",
                                                BigDecimal.ZERO),
                                clientes.get(0),
                                habitaciones.get(10));

                LocalDate entradaJoseArcadio = LocalDate.now().plusDays(20);
                guardarReservaDeCliente(
                                new Reserva(
                                                "MCD-2026-0928",
                                                entradaJoseArcadio,
                                                entradaJoseArcadio.plusDays(4),
                                                habitaciones.get(20).getCapacidad(),
                                                "ACTIVA",
                                                BigDecimal.ZERO),
                                clientes.get(1),
                                habitaciones.get(20));
        }

        private void guardarReservaDeCliente(
                        Reserva reserva, Cliente cliente, Habitacion habitacion) {
                reserva.asignarCliente(cliente);
                reserva.agregarHabitacion(habitacion);
                reservaRepository.save(reserva);
        }

        private void cargarCuenta() {
                cuentaRepository.save(
                                new Cuenta("ABIERTA", BigDecimal.ZERO, LocalDateTime.now()));
        }
}