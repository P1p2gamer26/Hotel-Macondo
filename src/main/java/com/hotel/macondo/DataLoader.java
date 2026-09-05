package com.hotel.macondo;

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class DataLoader implements CommandLineRunner {

  private final TipoHabitacionRepository tipoHabitacionRepository;
  private final HabitacionRepository habitacionRepository;
  private final ServicioRepository servicioRepository;
  private final TestimonioRepository testimonioRepository;
  private final ClienteRepository clienteRepository;
  private final UsuarioRepository usuarioRepository;
  private final OperadorRepository operadorRepository;
  private final ReservaRepository reservaRepository;
  private final CuentaRepository cuentaRepository;

  public DataLoader(
      TipoHabitacionRepository tipoHabitacionRepository,
      HabitacionRepository habitacionRepository,
      ServicioRepository servicioRepository,
      TestimonioRepository testimonioRepository,
      ClienteRepository clienteRepository,
      UsuarioRepository usuarioRepository,
      OperadorRepository operadorRepository,
      ReservaRepository reservaRepository,
      CuentaRepository cuentaRepository) {
    this.tipoHabitacionRepository = tipoHabitacionRepository;
    this.habitacionRepository = habitacionRepository;
    this.servicioRepository = servicioRepository;
    this.testimonioRepository = testimonioRepository;
    this.clienteRepository = clienteRepository;
    this.usuarioRepository = usuarioRepository;
    this.operadorRepository = operadorRepository;
    this.reservaRepository = reservaRepository;
    this.cuentaRepository = cuentaRepository;
  }

  @Override
  public void run(String... args) {
    List<TipoHabitacion> tipos = cargarTiposHabitacion();
    List<Habitacion> habitaciones = cargarHabitaciones(tipos);
    cargarServicios();
    cargarTestimonios();

    List<Cliente> clientes = cargarClientes();
    cargarUsuariosYOperador(clientes);
    cargarReservasPrincipales();
    cargarReservasDeClientes(clientes, habitaciones);
    cargarCuenta();
  }

  private List<TipoHabitacion> cargarTiposHabitacion() {
    TipoHabitacion normal =
        tipoHabitacionRepository.save(
            new TipoHabitacion(
                "Normal",
                "Refugio intimo con vista al jardin tropical, cama queen y aire acondicionado.",
                BigDecimal.valueOf(350000),
                2));

    TipoHabitacion executive =
        tipoHabitacionRepository.save(
            new TipoHabitacion(
                "Executive",
                "Espacio amplio con sala de trabajo, banera de lujo y vista al mar Caribe.",
                BigDecimal.valueOf(580000),
                3));

    TipoHabitacion vip =
        tipoHabitacionRepository.save(
            new TipoHabitacion(
                "VIP",
                "Suite boutique con terraza privada, jacuzzi exterior y servicio de mayordomo.",
                BigDecimal.valueOf(950000),
                4));

    TipoHabitacion luxury =
        tipoHabitacionRepository.save(
            new TipoHabitacion(
                "Luxury",
                "Villa frente al mar con piscina privada y atencion personalizada 24 horas.",
                BigDecimal.valueOf(1800000),
                6));

    return List.of(normal, executive, vip, luxury);
  }

  private List<Habitacion> cargarHabitaciones(List<TipoHabitacion> tipos) {
    Habitacion normal =
        crearHabitacion(
            "Jardín Tropical",
            "ACOGEDORA",
            "/images/HabitacionNormal.avif",
            "1",
            tipos.get(0));
    Habitacion executive =
        crearHabitacion(
            "Horizonte Caribe",
            "POPULAR",
            "/images/HabitacionExecutive.avif",
            "2",
            tipos.get(1));
    Habitacion vip =
        crearHabitacion(
            "Terraza Macondo",
            "EXCLUSIVA",
            "/images/HabitacionVIP.avif",
            "3",
            tipos.get(2));
    Habitacion luxury =
        crearHabitacion(
            "Villa del Mar",
            "ÚNICO",
            "/images/HabitacionLuxury.avif",
            "4",
            tipos.get(3));

    return List.of(normal, executive, vip, luxury);
  }

  private Habitacion crearHabitacion(
      String nombre,
      String etiqueta,
      String imagen,
      String numero,
      TipoHabitacion tipo) {
    Habitacion habitacion =
        new Habitacion(
            nombre,
            etiqueta,
            tipo.getDescripcion(),
            tipo.getPrecioNoche(),
            tipo.getCapacidadPersonas(),
            imagen,
            numero,
            "DISPONIBLE",
            1);
    habitacion.aplicarTipo(tipo);
    return habitacionRepository.save(habitacion);
  }

  private void cargarServicios() {
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
            List.of("Masajes", "Hidroterapia", "Aromaterapia", "Tratamientos faciales")));

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
            List.of("Camastro reservado", "Toallas", "Bebida de bienvenida", "Servicio junto a la piscina"),
            List.of("Piscina", "Descanso", "Vista al mar")));

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
            List.of("Guía bilingüe", "Transporte incluido", "Snacks y agua", "Seguro de viaje"),
            List.of("Cartagena", "Islas", "Manglares", "Historia")));

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

  private List<Cliente> cargarClientes() {
    Cliente ana =
        clienteRepository.save(
            new Cliente(
                "Ana",
                "Torres",
                "1001001",
                "3001112233",
                "ana@macondo.com"));
    Cliente luis =
        clienteRepository.save(
            new Cliente(
                "Luis",
                "Mora",
                "1001002",
                "3004445566",
                "luis@macondo.com"));
    return List.of(ana, luis);
  }

  private void cargarUsuariosYOperador(List<Cliente> clientes) {
    usuarioRepository.save(
        new Usuario("admin@macondo.com", "admin123", Rol.ADMINISTRADOR));

    Usuario usuarioOperador =
        usuarioRepository.save(
            new Usuario("operador@macondo.com", "operador123", Rol.OPERADOR));
    Operador operador = new Operador("Recepcion principal", true);
    operador.asignarUsuario(usuarioOperador);
    operadorRepository.save(operador);

    Usuario usuarioAna = new Usuario("ana@macondo.com", "ana123", Rol.CLIENTE);
    usuarioAna.asignarCliente(clientes.get(0));
    usuarioRepository.save(usuarioAna);

    Usuario usuarioLuis = new Usuario("luis@macondo.com", "luis123", Rol.CLIENTE);
    usuarioLuis.asignarCliente(clientes.get(1));
    usuarioRepository.save(usuarioLuis);
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
    LocalDate entradaAna = LocalDate.now().plusDays(22);
    guardarReservaDeCliente(
        new Reserva(
            "MCD-2026-0915",
            entradaAna,
            entradaAna.plusDays(3),
            habitaciones.get(2).getCapacidad(),
            "ACTIVA",
            BigDecimal.ZERO),
        clientes.get(0),
        habitaciones.get(2));

    LocalDate salidaAna = LocalDate.now().minusDays(20);
    guardarReservaDeCliente(
        new Reserva(
            "MCD-2026-0801",
            salidaAna.minusDays(2),
            salidaAna,
            habitaciones.get(1).getCapacidad(),
            "FINALIZADA",
            BigDecimal.ZERO),
        clientes.get(0),
        habitaciones.get(1));

    LocalDate entradaLuis = LocalDate.now().plusDays(35);
    guardarReservaDeCliente(
        new Reserva(
            "MCD-2026-0928",
            entradaLuis,
            entradaLuis.plusDays(2),
            habitaciones.get(0).getCapacidad(),
            "ACTIVA",
            BigDecimal.ZERO),
        clientes.get(1),
        habitaciones.get(0));
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
