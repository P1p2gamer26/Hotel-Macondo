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

### Asistente de chat

El backend consulta Ollama con el modelo `llama3.2` por defecto. Descarga el
modelo una vez con `ollama pull llama3.2` y asegúrate de que Ollama esté
ejecutándose. La URL predeterminada es `http://localhost:11434`; si Ollama está
en otro host, configura `OLLAMA_URL` en el proceso del backend. También puedes
cambiar el modelo mediante `OLLAMA_MODEL`.

El frontend solo necesita acceder al backend (`/api/chat`); no expongas el
puerto de Ollama a Internet. En producción, usa la dirección interna del host o
servicio donde se ejecute Ollama.

En cada consulta informativa, el backend agrega al contexto el catálogo activo de
servicios y los tipos de habitación actuales. La disponibilidad se consulta
directamente en el servicio de reservas (no la redacta el modelo): indica las
fechas de entrada y salida completas en formato `AAAA-MM-DD` y el número total
de huéspedes. Si la fecha es ambigua o faltan datos, el chat los solicitará sin
afirmar disponibilidad.

El chatbot no crea ni confirma reservas y nunca debe solicitar datos de tarjeta,
CVV, vencimiento, contraseñas o datos bancarios. Los pagos y reservas se
completan exclusivamente en los flujos oficiales del sitio; el chat ofrece
botones para abrir la sección correspondiente.

Las preguntas sobre reservar, registrarse, cancelar una reserva y hacer checkout
incluyen acciones de navegación para abrir la sección correspondiente. La
cancelación lleva a las reservas del cliente o al inicio de sesión; el acceso al
checkout operativo solo se muestra a personal autorizado.

Ejemplo de solicitud:

```json
{
  "mensaje": "¿Y tienen piscina?",
  "historial": [
    { "role": "user", "content": "¿Qué servicios ofrece el hotel?" },
    { "role": "assistant", "content": "El hotel cuenta con varios servicios para sus huéspedes." }
  ]
}
```

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

## Pagos de prueba con Wompi

El cliente puede pagar una parte o el saldo de una estadía activa desde su detalle.
El backend crea un intento pendiente y calcula la firma de integridad; el pago solo
se registra en la cuenta cuando llega un evento `transaction.updated` válido de Wompi.
La URL de retorno informa al cliente, pero no confirma el pago. El operador conserva
su flujo de checkout actual.

Para probar en sandbox, define estas variables en el proceso del backend. No guardes
los secretos en el repositorio:

```powershell
$env:WOMPI_ENVIRONMENT="test"
$env:WOMPI_PUBLIC_KEY="pub_test_..."
$env:WOMPI_INTEGRITY_SECRET="test_integrity_..."
$env:WOMPI_EVENTS_SECRET="test_events_..."
$env:WOMPI_FRONTEND_BASE_URL="http://localhost:4200"
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

Configura en el Dashboard de Wompi Sandbox la URL pública HTTPS de eventos
`https://<tu-backend>/api/pagos/wompi/eventos`. En desarrollo local, el webhook
requiere exponer el backend mediante un túnel HTTPS. En producción, usa llaves y
secreto de eventos de producción, `WOMPI_ENVIRONMENT=prod` y el dominio del frontend.
Una reserva con pagos confirmados no se puede cancelar desde el portal ni desde el
panel del operador.

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
