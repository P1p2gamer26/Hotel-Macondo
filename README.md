# Hotel Macondo

## La idea

Hotel Macondo es un hotel boutique de lujo imaginado en la costa caribeña de
Colombia, en Cartagena de Indias, inspirado en el pueblo de *Cien años de
soledad*. La promesa de marca es la del lema: **"donde el realismo mágico
encuentra el mar"**. No se vende una habitación, se vende una estadía que se
recuerda como un cuento.

Eso manda sobre todas las decisiones del sitio:

- **Primero la emoción, después el precio.** Al entrar solo se ve la foto de la
  playa al atardecer a pantalla completa, el nombre y el lema. La tarifa
  aparece más abajo, cuando el visitante ya se enamoró del lugar.
- **Reservar siempre a un clic.** La caja de disponibilidad monta sobre el hero
  y el botón dorado "Reservar" vive fijo en el navbar, así el huésped puede
  cotizar desde cualquier punto de la página sin devolverse.
- **Cuatro niveles de experiencia.** Normal, Executive, VIP y Luxury, cada una
  con su etiqueta (ACOGEDORA, POPULAR, EXCLUSIVA, ÚNICO) para que el visitante
  se ubique de una sola pasada.
- **Prueba social al final.** Los testimonios cierran el recorrido, justo antes
  del llamado a reservar.

La identidad visual acompaña esa idea: la mariposa amarilla (la de Mauricio
Babilonia) como logo, el dorado del atardecer, el azul del Caribe y el crema de
la arena, con una serif de alto contraste para los títulos que le da el aire
literario.

## Cómo ejecutar

### Desarrollo

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

El backend quedará disponible en `http://localhost:8080`.
Usará la base H2 persistente `macondo-dev` y permitirá solicitudes desde
`http://localhost:4200`.

### Producción

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
mvn spring-boot:run
```

El backend quedará disponible en `http://localhost:8080`.
Usará la base H2 persistente `macondo-prod` y permitirá solicitudes desde
`https://macondo.kusare.fyi`.

También se puede publicar mediante Cloudflare Tunnel:

| Servicio | Origen local | URL pública |
|---|---|---|
| Frontend | `http://localhost:4200` | `https://macondo.kusare.fyi` |
| Backend | `http://localhost:8080` | `https://macondo-api.kusare.fyi` |

## Base de datos H2

Cada perfil usa una base H2 persistente en el directorio del proyecto:

| Perfil | Archivo JDBC |
|---|---|
| `dev` | `jdbc:h2:file:./macondo-dev` |
| `prod` | `jdbc:h2:file:./macondo-prod` |
| `test` | `jdbc:h2:file:./testdbmacondo` |

Con el backend iniciado, la consola se encuentra en:

```text
http://localhost:8080/h2
```

Credenciales:

```text
Usuario: sa
Contraseña: vacía
```

## Pagos de prueba con Mercado Pago

El cliente puede pagar una parte o el saldo de una estadía activa desde su detalle.
El backend crea una orden de Checkout Pro y registra el pago solo tras consultar el
estado real de la orden en Mercado Pago. También procesa notificaciones `order`
validadas con la firma del webhook. El retorno del checkout nunca se acepta como
prueba de pago por sí solo.

Agrega el Access Token de prueba al archivo `src/main/resources/application.properties`
del backend y reinicia la aplicación.

```properties
mercadopago.access-token=APP_USR-<access-token-de-prueba>
mercadopago.webhook-secret=
```

Agrega la firma secreta de Sandbox al configurar el webhook. Para la prueba local
con tarjeta, el backend confirma la orden cuando el cliente regresa del checkout;
la URL HTTPS pública se necesita para recibir notificaciones webhook.

Para recibir webhooks, configura el evento `Order` con la URL HTTPS pública
`https://<tu-backend>/api/pagos/mercadopago/eventos` y agrega la firma secreta.
Las variables `MP_ACCESS_TOKEN` y `MP_WEBHOOK_SECRET` tienen prioridad cuando
están definidas y permiten configurar las credenciales de producción.
Una reserva con pagos confirmados no se puede cancelar desde el portal ni desde
el panel del operador.

Los perfiles `default`, `dev` y `prod` cargan datos iniciales mediante
`DataLoader`. El perfil `test` usa `DataLoaderTest`.

## Dependencias del `pom.xml`

- `spring-boot-starter-web`
- `spring-boot-devtools`
- `lombok`
- `spring-boot-starter-thymeleaf`

(más `spring-boot-starter-test` para las pruebas)

Los repositorios usan Spring Data JPA con H2. No es necesario instalar otro
servidor de base de datos para ejecutar el proyecto localmente.

## Rutas

| URL | Descripción |
|---|---|
| `/` | Landing page (hero, disponibilidad, habitaciones, servicios, testimonios) |
| `/habitaciones` | Catálogo de tipos de habitación |
| `/habitaciones?personas=4` | Tipos filtrados por capacidad |
| `/habitaciones/{id}` | Detalle de un tipo de habitación |

## Estructura

```
src/main/java/com/hotel/macondo
├── MacondoApplication.java
├── controller/   HomeController, HabitacionController, ReservaController, AuthController
├── service/      HotelService, ReservaService, UsuarioService (+ Impl)
├── repository/   Habitacion, Servicio, Testimonio, Reserva, Usuario
└── entities/     Habitacion, Servicio, Testimonio, Reserva, Usuario (Lombok)

src/main/resources
├── templates/    fragmentos.html + las vistas
└── static/       css/styles.css, js/script.js, images/
```

Los archivos de reservas y de usuarios están vacíos y comentados por dentro con
lo que debe ir en cada uno.

## Pruebas

```bash
mvn test
```
