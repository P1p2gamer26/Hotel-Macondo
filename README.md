# Hotel Macondo

Landing page y sistema de reservas del Hotel Macondo, construido con Spring Boot + Thymeleaf + Bootstrap 5.

## Cómo ejecutar

```bash
./mvnw spring-boot:run
```

Y abrir http://localhost:8080

## Tecnologías (las mismas de los repos del curso)

| Repo del curso | Qué se usa aquí |
|---|---|
| 1.HTML-CSS | HTML semántico y CSS propio (`static/css/styles.css`) |
| 2.JS-FORMS | JavaScript de manipulación del DOM (`static/js/script.js`) y formularios |
| 3.2.Bootstrap | Bootstrap 5.3.5 por CDN, Bootstrap Icons, Google Fonts, navbar, carousel, cards |
| 4.SpringBootThymeleaf | Spring Boot 3.5.4, Thymeleaf, fragmentos, capas controller / service / repository / entities, Lombok |

### Dependencias del `pom.xml` (las 4 del profe)

- `spring-boot-starter-web`
- `spring-boot-devtools`
- `lombok`
- `spring-boot-starter-thymeleaf`

(más `spring-boot-starter-test` para las pruebas)

No hay base de datos: los repositorios usan `Map` en memoria, igual que `StudentRepository`.

## Rutas

| URL | Descripción |
|---|---|
| `/` | Landing page (hero, disponibilidad, habitaciones, servicios, testimonios) |
| `/habitaciones` | Listado de habitaciones |
| `/habitaciones?personas=4` | Filtro por capacidad |
| `/habitaciones/{id}` | Detalle de una habitación |

### Pendiente por implementar

La lógica de **iniciar sesión, registrarse y reservar** está borrada a propósito.
Los archivos siguen ahí, vacíos y comentados con lo que debe ir en cada uno:

```
controller/AuthController.java        login y registro
controller/ReservaController.java     CRUD de reservas
service/UsuarioService(+Impl).java
service/ReservaService(+Impl).java
repository/UsuarioRepository.java
repository/ReservaRepository.java
entities/Usuario.java
entities/Reserva.java
templates/login.html, registro.html, bienvenida.html
templates/reservar.html, reservas.html, detalle_reserva.html
```

Mientras tanto los botones "Iniciar Sesión", "Registrarse" y "Reservar" y el
formulario de disponibilidad quedan maquetados pero apuntando a `#`.

## Estructura

```
src/main/java/com/hotel/macondo
├── MacondoApplication.java
├── controller/   HomeController, HabitacionController, ReservaController, AuthController
├── service/      HotelService, ReservaService, UsuarioService (+ Impl)
├── repository/   Habitacion, Servicio, Testimonio, Reserva, Usuario (Map en memoria)
└── entities/     Habitacion, Servicio, Testimonio, Reserva, Usuario (Lombok)

src/main/resources
├── templates/    fragmentos.html + 9 vistas
└── static/       css/styles.css, js/script.js, images/*.avif
```

## Pruebas

```bash
./mvnw test
```
