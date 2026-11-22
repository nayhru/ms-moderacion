# ms-moderacion

Microservicio de moderacion de temas y comentarios del foro.

Forma parte del **Sistema de Foros en Línea**. Las operaciones protegidas requieren los tokens emitidos por
ms-usuarios; este microservicio solo los valida.

Proyecto creado con el arquetipo `com.foro:foro-arquetipo:1.0.0`.

Tecnologías: Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT), Oracle Database y Maven.
Puerto: `8083`.

## Estructura

```
src/main/java/com/foro/moderacion
├── config       Seguridad, CORS y JWT
├── controller   Controladores REST
├── dto          Datos de entrada y salida, con sus validaciones
├── exception    Excepciones y manejo global de errores (@RestControllerAdvice)
├── model        Entidades JPA
├── repository   Repositorios JPA
└── service      Interfaces e implementaciones con la lógica de negocio
```

El recurso `Ejemplo` (`/api/ejemplos`) es un CRUD mínimo que sirve de modelo. Reemplázalo por las entidades de tu
microservicio junto con su script en `database/`.

## Endpoints de ejemplo

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/ejemplos`, `/api/ejemplos/{id}` | Público | Consulta ejemplos |
| POST | `/api/ejemplos` | Con sesión | Crea un ejemplo |
| PUT | `/api/ejemplos/{id}` | Autor, MODERADOR o ADMIN | Actualiza un ejemplo |
| DELETE | `/api/ejemplos/{id}` | Autor, MODERADOR o ADMIN | Elimina un ejemplo |

## Configuración

Requisitos: JDK 21, una base Oracle Autonomous Database y su wallet.

1. Descomprime el wallet en la raíz del disco, por ejemplo `C:/Wallet_foro`. Las rutas con espacios fallan con `TNS_ADMIN`.
2. Ejecuta los scripts de `database/` en la base de datos.
3. Copia `.env.example` como `.env` y completa la conexión. Este archivo no se sube a Git.
4. Levanta el servicio:

```bash
./mvnw spring-boot:run
```

## Docker

```bash
docker build -t ms-moderacion .
docker run -p 8083:8083 -v C:/Wallet_foro:/app/wallet \
  -e DB_URL="jdbc:oracle:thin:@foro_tp?TNS_ADMIN=/app/wallet" \
  -e DB_USER=ADMIN -e DB_PASSWORD=... -e JWT_SECRET=... ms-moderacion
```
