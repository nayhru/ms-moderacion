# ms-moderacion

Microservicio de moderación del **Sistema de Foros en Línea**. Permite que los usuarios denuncien temas o comentarios
y que un MODERADOR o ADMIN los sancione (ocultar, advertir o eliminar) cuando incumplen las normas del foro.
Valida los tokens emitidos por `ms-usuarios` y complementa a `ms-foro`, que administra los temas y comentarios.

Tecnologías: Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT), Oracle Database y Maven.
Puerto: `8083`.

## Origen: generado con foro-arquetipo

El proyecto se creó con el arquetipo propio `com.foro:foro-arquetipo:1.0.0` (carpeta `../foro-arquetipo`), que se
instala con `./mvnw install`. El primer commit del repositorio es el proyecto recién generado, sin cambios. Comando usado
desde la carpeta `Sumativa 2`:

```bash
./foro-arquetipo/mvnw -B org.apache.maven.plugins:maven-archetype-plugin:3.4.0:generate \
  -DarchetypeGroupId=com.foro \
  -DarchetypeArtifactId=foro-arquetipo \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.foro \
  -DartifactId=ms-moderacion \
  -Dpackage=com.foro.moderacion \
  -Dpuerto=8083 \
  "-Ddescripcion=Microservicio de moderacion de temas y comentarios del foro"
```

Del arquetipo vienen la configuración de seguridad JWT, CORS, el manejo global de errores, `application.properties`,
`Dockerfile`, `.gitignore` y Maven Wrapper. El CRUD de ejemplo se reemplazó por `Reporte` y `Sancion`.

## Estructura

```
src/main/java/com/foro/moderacion
├── config       Seguridad, CORS y JWT
├── controller   ReporteController y SancionController
├── dto          Datos de entrada y salida con sus validaciones (respuestas con @Builder de Lombok)
├── exception    Excepciones y manejo global de errores (@RestControllerAdvice)
├── model        Entidades Reporte y Sancion, y los enums TipoContenido, EstadoReporte y TipoSancion
├── repository   Repositorios JPA
└── service      Lógica de negocio
    └── estrategia   Patrón Factory + Strategy de los tipos de sanción
```

## Patrón de diseño: Factory + Strategy

Cada tipo de sanción tiene una regla y un efecto distintos, así que en vez de un `switch` en el servicio se usa una
estrategia por tipo y una factory que elige cuál aplicar según `TIPO_SANCION`.

| Archivo (`service/estrategia`) | Rol | Regla |
|---|---|---|
| `SancionStrategy.java` | Interfaz Strategy | `tipo()`, `bloqueaContenido()` y `aplicar(sancion, moderador)` |
| `OcultarSancionStrategy.java` | Estrategia OCULTAR | Oculta el contenido; no aplica si ya tiene una sanción ELIMINAR activa (409) |
| `AdvertenciaSancionStrategy.java` | Estrategia ADVERTENCIA | El contenido sigue visible; no aplica si ya está oculto o eliminado (409) |
| `EliminarSancionStrategy.java` | Estrategia ELIMINAR | Solo un ADMIN puede aplicarla (403) |
| `SancionFactory.java` | Factory | Recibe todas las estrategias que Spring encuentra y entrega la que corresponde al tipo |

`SancionServiceImpl` pide la estrategia a la factory al crear una sanción o al cambiar su tipo, y la usa también en
`/api/sanciones/activas` para decidir si el contenido está baneado (`bloqueaContenido()`). Cada estrategia deja su
propio log, por ejemplo `Estrategia OCULTAR: el TEMA 2 deja de mostrarse en el foro, moderador=moderador`.
Agregar un tipo nuevo solo requiere el valor en el enum y su clase de estrategia.

## Base de datos

Tablas `REPORTE` y `SANCION`, definidas en `database/script_ms_moderacion.sql` (se puede ejecutar varias veces).
Incluye 4 reportes y 3 sanciones de prueba sobre los temas y comentarios semilla de ms-foro.

- `REPORTE`: denuncia de un usuario sobre un `TEMA` o `COMENTARIO`, con estado `PENDIENTE`, `RESUELTO` o `DESCARTADO`.
- `SANCION`: sanción `OCULTAR`, `ADVERTENCIA` o `ELIMINAR` aplicada por un moderador. `ACTIVA` vale 1 o 0 (revocada)
  y `ID_REPORTE` es opcional, con llave foránea hacia `REPORTE`.

`ID_CONTENIDO` e `ID_USUARIO` son referencias a ms-foro y ms-usuarios sin llave foránea, porque cada microservicio
administra sus propios datos. Hibernate solo valida el esquema (`ddl-auto=validate`).

## Roles

| Rol | Privilegios |
|---|---|
| ADMIN | Gestiona reportes y sanciones; es el único que puede aplicar ELIMINAR |
| MODERADOR | Gestiona reportes y aplica sanciones OCULTAR y ADVERTENCIA |
| USUARIO | Crea reportes |
| Sin sesión | Consulta si un contenido está baneado |

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/reportes` | Con sesión | Denuncia un tema o comentario |
| GET | `/api/reportes?estado=` | MODERADOR o ADMIN | Lista reportes; el filtro por estado es opcional |
| GET | `/api/reportes/{id}` | MODERADOR o ADMIN | Obtiene un reporte |
| PUT | `/api/reportes/{id}` | MODERADOR o ADMIN | Cambia el estado de un reporte |
| DELETE | `/api/reportes/{id}` | MODERADOR o ADMIN | Elimina un reporte sin sanciones asociadas |
| POST | `/api/sanciones` | MODERADOR o ADMIN | Aplica una sanción; si trae `reporteId`, el reporte queda RESUELTO |
| GET | `/api/sanciones?tipoContenido=&tipoSancion=&activa=` | MODERADOR o ADMIN | Lista sanciones con filtros opcionales |
| GET | `/api/sanciones/{id}` | MODERADOR o ADMIN | Obtiene una sanción |
| PUT | `/api/sanciones/{id}` | MODERADOR o ADMIN | Cambia el tipo o el motivo de una sanción activa |
| PUT | `/api/sanciones/{id}/revocar` | MODERADOR o ADMIN | Revoca la sanción (quita el baneo) |
| DELETE | `/api/sanciones/{id}` | MODERADOR o ADMIN | Elimina una sanción |
| GET | `/api/sanciones/activas?tipo=TEMA&id=3` | Público | Indica si el contenido está baneado y sus sanciones activas |

Ejemplo de cuerpo para `POST /api/sanciones`:

```json
{
  "tipoContenido": "TEMA",
  "idContenido": 4,
  "tipoSancion": "OCULTAR",
  "motivo": "El tema repite una consulta ya publicada",
  "reporteId": 2
}
```

Los errores responden con el formato `ErrorResponse` (`fecha`, `estado`, `error`, `mensaje`, `ruta` y `detalles`
cuando hay campos inválidos): 400 datos inválidos, 401 sin token o token inválido, 403 rol sin permiso, 404 recurso
inexistente y 409 conflictos (reporte pendiente repetido, sanción activa duplicada, sanción ya revocada, etc.).

Las pruebas están en `postman/ms-moderacion.postman_collection.json`. Con `ms-usuarios` encendido, los logins de la
primera carpeta guardan los tokens de los 4 usuarios automáticamente.

## Configuración

Requisitos: JDK 21, una base Oracle Autonomous Database y su wallet.

1. Descomprime el wallet en la raíz del disco, por ejemplo `C:/Wallet_foro`. Las rutas con espacios fallan con `TNS_ADMIN`.
2. Ejecuta `database/script_ms_moderacion.sql` en la base de datos.
3. Copia `.env.example` como `.env` y completa la conexión. Usa el mismo `JWT_SECRET` que ms-usuarios. Este archivo no se sube a Git.
4. Levanta el servicio:

```bash
./mvnw spring-boot:run
```

Para correr las pruebas unitarias (no necesitan base de datos):

```bash
./mvnw test
```

## Docker

El `Dockerfile` compila con Maven en una primera etapa y deja solo el JRE 21 y el jar en la imagen final. El wallet y
las credenciales no van dentro de la imagen: el wallet se monta en `/app/wallet` (`TNS_ADMIN`) y la configuración se
entrega con variables de entorno.

```bash
docker build -t ms-moderacion:1.0.0 .
docker run -p 8083:8083 -v C:/Wallet_foro:/app/wallet:ro --env-file .env \
  -e "DB_URL=jdbc:oracle:thin:@foro_tp?TNS_ADMIN=/app/wallet" ms-moderacion:1.0.0
```
