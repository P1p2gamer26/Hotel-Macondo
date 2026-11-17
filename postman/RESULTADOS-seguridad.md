# Estado de la comprobación manual

La colección actual contiene 7 peticiones HTTP sin scripts. Deben enviarse individualmente con Send y revisarse mediante Status y Body, siguiendo las descripciones de cada petición.

La ejecución manual de esta versión en la interfaz de Postman está pendiente. Los resultados anteriores de Newman pertenecen a una versión con scripts que se retiró; no constituyen una comprobación manual de esta colección.

## Verificación de la implementación JWT

- Compilación del backend completada.
- Las 25 pruebas existentes de repositorios, servicios de reservas y controladores finalizaron sin fallos. No se añadieron clases ni casos de prueba. Los dos tests de controladores conservan sus mocks y ejecutan las peticiones sin filtros; los permisos se comprobaron contra el servidor real.
- Se realizaron 36 comprobaciones HTTP directas contra localhost, con resultados esperados. Esto no representa una ejecución en la interfaz de Postman.
- Se verificaron los logins de ADMINISTRADOR, OPERADOR y CLIENTE; acceso público al catálogo; 401 sin token, con token mal formado o firma alterada; 403 por rol insuficiente o ID de otro cliente.
- Se verificaron registro con contraseña recibida, acceso al perfil propio, disponibilidad, creación, consulta, modificación y cancelación de reserva propia, actualización de perfil y cambio de contraseña. La clave anterior produjo 401 y la nueva permitió el login.
- Una reserva ajena solicitada bajo el ID del cliente autenticado produjo 404 por la comprobación existente del servicio; cambiar el ID del cliente en la URL produjo 403.
- El token contiene `sub`, `iat` y `exp`, con una vigencia de 3600 segundos. El DTO de login no contiene contraseña y no se creó cookie de sesión. No se esperó una hora para comprobar el vencimiento; la validación de `exp` queda a cargo del parser JWT.
- Se usaron datos temporales en una base H2 en memoria, sin modificar las bases de datos en archivo.

La colección JSON permanece sin cambios y sin scripts. Angular todavía debe guardar y enviar el JWT para consumir las rutas protegidas; esa integración corresponde a la siguiente parte.
