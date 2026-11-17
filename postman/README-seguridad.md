# Comprobación manual de contraseñas y DTO

La colección original `peticiones_postman.json` conserva las peticiones del proyecto.
La colección adicional `pruebas-seguridad.postman_collection.json` contiene peticiones HTTP sin scripts de pre-request ni de post-response.

## Uso en Postman

1. Arrancar el backend con el perfil `test` e importar la colección adicional. En Postman web seleccionar Desktop Agent.
2. Abrir cada petición y pulsar **Send**, siguiendo el orden. Comprobar manualmente **Status** y **Body**. No utilizar Runner ni Newman.
3. Tras el registro, copiar el campo `id` del cliente devuelto en la variable de colección `pruebaClienteId` y guardar su valor.
4. La variable `baseUrl` contiene `http://localhost:8080`.

El login ahora devuelve un campo `token` en `LoginResponseDTO`. Para las peticiones protegidas, copiar ese valor en **Authorization → Bearer Token → Token**. No escribir `Bearer` dentro del campo Token: Postman agrega el prefijo. Las peticiones de login y registro se envían con **No Auth**. La colección continúa sin scripts y el token se copia manualmente.

Para los pasos 04 y 07 utilizar el token del cliente registrado en el paso 01. El token del paso 02 puede utilizarse para el cambio de contraseña; el paso 06 devuelve otro token para continuar. Una contraseña incorrecta produce 401 aunque se tenga otro token: el login se realiza con No Auth.

Los tokens duran una hora. La llave de firma se genera al iniciar el backend, como en la clase; después de reiniciarlo es necesario volver a iniciar sesión. No se usa una sesión de servidor para autenticar las peticiones.

Los cuerpos incluyen datos de ejemplo editables: correo `laura.postman@macondo.com`, cédula `1099000101`, contraseña inicial `ClaveElegidaMacondo!` y nueva contraseña `NuevaClaveMacondo!`. Son entradas para la prueba, no contraseñas impuestas por la aplicación. Si cambias el correo o las claves, actualiza las peticiones relacionadas. Si el cliente ya existe, usa otro correo y otra cédula de prueba.

| Petición | Estado esperado | Revisión manual |
| --- | --- | --- |
| 01. Registro | 200 | Datos del cliente, id y ausencia de contraseña. |
| 02. Login con clave recibida | 200 | Cliente correcto y DTO sin contraseña, también en objetos anidados. |
| 03. Login con clave incorrecta | 401 | Rechazo del acceso. |
| 04. Cambio de contraseña | 204 | Cuerpo vacío. |
| 05. Login con clave anterior | 401 | Rechazo de la clave anterior. |
| 06. Login con nueva clave | 200 | Acceso correcto y DTO sin contraseña. |
| 07. Eliminación opcional | 204 | Cuerpo vacío; eliminar solo el cliente creado para esta prueba. |

El registro y el login permiten comprobar el DTO de respuesta. El hash almacenado no se observa en el JSON de respuesta.

## Permisos implementados

| Operación | Acceso |
| --- | --- |
| Login y registro | Público. |
| GET de tipos de habitación y testimonios; catálogo, recomendaciones, relacionados y detalle de servicios | Público. |
| Crear, actualizar o eliminar tipos; crear o actualizar servicios y cambiar su estado | ADMINISTRADOR. |
| Listar todos los servicios y consultar habitaciones físicas | ADMINISTRADOR u OPERADOR. |
| Crear, actualizar o eliminar habitaciones | ADMINISTRADOR. |
| Cambiar estado de una habitación | ADMINISTRADOR u OPERADOR. |
| Administrar operadores | ADMINISTRADOR. |
| Consultar y gestionar reservas desde el panel operativo; consumos y pagos | ADMINISTRADOR u OPERADOR. |
| Consultar perfil de cliente | Cliente propietario, ADMINISTRADOR u OPERADOR. |
| Actualizar perfil, cambiar contraseña o eliminar cuenta | Cliente propietario. |
| Rutas de reservas del portal cliente, incluida disponibilidad | Cliente propietario. |

En rutas del cliente se compara el ID de la URL con el cliente asociado al usuario autenticado. Los servicios de reservas conservan además sus comprobaciones de que la reserva pertenece a ese cliente. Los nombres de roles se usan con `hasAuthority`/`hasAnyAuthority` porque están guardados sin el prefijo `ROLE_`.

Revisión manual de seguridad: petición protegida sin token o con token inválido/vencido → 401; token válido sin el rol o sin ser propietario → 403; token válido y acceso permitido → estado correspondiente a la operación. Estas respuestas no requieren scripts de Postman.

Sustento de clase: CORS aproximadamente 36:08–40:32; PasswordEncoder 59:06; UserDetails y mapeo de roles 1:13–1:20; AuthenticationManager 1:30–1:38; JWT, filtro y SecurityContext 1:41–2:04; restricciones por rol alrededor de 2:15. La sintaxis JWT corresponde a [JJWT](https://github.com/jwtk/jjwt).
