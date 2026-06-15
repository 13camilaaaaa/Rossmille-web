# Contexto del Proyecto — ROSS MILLE Web

## Que es

Migracion del prototipo ROSS MILLE POS (Java Swing) a una aplicacion web profesional con Spring Boot REST API y frontend HTML/CSS/JS. Objetivo: pieza de portafolio que demuestra manejo profesional de Java backend moderno.

**Prototipo Swing original:** `/home/camil/proyectos/prototype-java`
**Este proyecto:** `/home/camil/proyectos/rossmille-web`

---

## Stack tecnologico

| Capa | Tecnologia |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.3.5 |
| ORM | Spring Data JPA + Hibernate 6 |
| Seguridad | Spring Security 6 + JWT (jjwt 0.11.5) |
| Contrasenas | BCryptPasswordEncoder (Spring Security) |
| Frontend | HTML + Bootstrap 5.3.2 + Vanilla JS |
| BD | MySQL 8.0 en Docker (mismo del prototipo) |
| PDF | Apache PDFBox 2.0.34 (dependencia incluida, se usa en Fase 6) |
| Build | Maven 3.9.6 (wrapper incluido en mvnw) |

---

## Base de datos

La BD es la misma del prototipo Swing. No se recrea ni se migra — Spring usa `ddl-auto: validate`.

- Motor: MySQL 8.0
- Contenedor Docker: `rossmille_mysql`
- Puerto: 3306
- BD: `rossmille_db`
- Usuario app: `RossMille` / `RossMillB01`
- Levantar: `docker compose up -d` (desde `/home/camil/proyectos/prototype-java/`)

### Schema (7 tablas existentes)

```
usuarios        id_usuario(varchar 10 PK), nombre_usuario, rol_usuarios,
                correo_usuario, telefono_usuario, contrasena(bcrypt)

clientes        id_clientes(varchar 10 PK), nombre, correo, telefono, direccion

productos       id(int AI PK), nombre, descripcion, talla, precio,
                stock, genero, categoria, color

ventas          id(int AI PK), id_cliente(FK nullable), id_empleado(FK),
                fecha, total, metodo_pago

detalle_venta   id(int AI PK), venta_id(FK), producto_id(FK),
                cantidad, precio_unitario

pedidos         id(int AI PK), id_cliente(FK), fecha_pedido, estado,
                total_estimado, observaciones

detalle_pedido  id(int AI PK), pedido_id(FK), producto_id(FK nullable),
                nombre_producto_personalizado, cantidad,
                precio_unitario_estimado, descripcion_personalizada
```

### Roles de usuario
- `Administrador` — acceso total
- `Empleado` — sin acceso a Usuarios ni Reporte

### Credenciales de prueba (BD local Docker)
- ID: 1234567 / Cargo: Administrador / Contrasena: Admin123

---

## Como correr el proyecto

```bash
# 1. Levantar la BD (desde el proyecto Swing)
cd /home/camil/proyectos/prototype-java
docker compose up -d

# 2. Correr la app Spring Boot
cd /home/camil/proyectos/rossmille-web
mvn spring-boot:run
# o sin Maven instalado globalmente:
./mvnw spring-boot:run

# 3. Abrir en el navegador
http://localhost:8080/login.html
```

Si Maven no esta en PATH: `sudo apt install maven` en WSL2.

---

## Estructura del proyecto (estado actual)

```
rossmille-web/
├── pom.xml                              Spring Boot 3.3.5, Java 21
├── mvnw                                 Maven Wrapper (descarga Maven si no esta instalado)
├── .gitignore
├── CONTEXTO_PROYECTO.md                 Este archivo
├── PLAN_TRABAJO.md                      Plan de fases
└── src/main/
    ├── java/com/rossmille/
    │   ├── RossmilleApplication.java
    │   ├── config/
    │   │   └── SecurityConfig.java      Chain de seguridad + CORS + rutas publicas
    │   ├── controller/
    │   │   └── AuthController.java      POST /api/auth/login
    │   ├── dto/
    │   │   ├── ApiResponse.java         Wrapper { ok, message, data }
    │   │   ├── LoginRequest.java        { id, cargo, contrasena }
    │   │   └── LoginResponse.java       { token, nombre, rol }
    │   ├── entity/
    │   │   └── Usuario.java             JPA entity mapeada a tabla usuarios
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java  @ControllerAdvice centralizado
    │   ├── repository/
    │   │   └── UsuarioRepository.java   findByIdUsuario()
    │   ├── security/
    │   │   ├── JwtAuthenticationFilter.java  Valida Bearer token en cada request
    │   │   ├── JwtTokenProvider.java         Genera y valida JWT HS256
    │   │   └── UserDetailsServiceImpl.java   Carga usuario por ID para Spring Security
    │   └── service/
    │       └── AuthService.java         Logica de login: ID + cargo + BCrypt + JWT
    └── resources/
        ├── application.yml
        └── static/
            ├── login.html               Formulario Bootstrap 5
            ├── dashboard.html           Hub de modulos (placeholder para fases 2-6)
            └── js/
                └── auth.js              fetch login, localStorage JWT, guardRoute()
```

---

## Decisiones tecnicas tomadas

### Autenticacion
- El login tiene tres campos (ID, cargo, contrasena) igual que el prototipo Swing
- `cargo` se valida contra `rol_usuarios` en la BD — previene que un empleado se loguee como admin
- JWT almacena: `sub` (idUsuario), `nombre`, `rol`
- Expiracion: 8 horas (28800000 ms)
- Secreto en `application.yml`, key de 47 bytes (suficiente para HS256)

### Seguridad
- Spring Security 6: sesiones STATELESS, CSRF desactivado
- Rutas publicas: `POST /api/auth/login`, `/login.html`, `/dashboard.html`, `/js/**`, `/css/**`
- Resto: requiere JWT valido en header `Authorization: Bearer <token>`
- BCrypt: mismo algoritmo que el prototipo Swing — las contrasenas existentes funcionan sin cambios

### Frontend
- JWT guardado en `localStorage` con clave `rm_token`
- Sesion (nombre, rol) guardada en `localStorage` con clave `rm_session`
- `guardRoute()` en auth.js redirige a login si no hay token
- Dashboard oculta modulos admin si `rol !== 'Administrador'`
- Bootstrap 5 via CDN

### JPA / Hibernate
- `ddl-auto: validate` — NO modifica el schema existente
- Sin anotacion de dialecto — Hibernate 6 lo autodetecta correctamente para MySQL 8

### Estructura de respuestas
Todas las respuestas de la API siguen el wrapper:
```json
{ "ok": true/false, "message": "...", "data": { ... } }
```

---

## API implementada (Fase 1)

| Metodo | Ruta | Auth | Descripcion |
|--------|------|------|-------------|
| POST | /api/auth/login | Publica | Login con ID + cargo + contrasena |

### Request
```json
POST /api/auth/login
Content-Type: application/json

{
  "id": "1234567",
  "cargo": "Administrador",
  "contrasena": "Admin123"
}
```

### Response exitosa (200)
```json
{
  "ok": true,
  "message": null,
  "data": {
    "token": "eyJhbGci...",
    "nombre": "Admin_Camila",
    "rol": "Administrador"
  }
}
```

### Response fallida (401)
```json
{
  "ok": false,
  "message": "Credenciales incorrectas",
  "data": null
}
```

---

## Dependencias del pom.xml

```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation
com.mysql:mysql-connector-j           (version gestionada por Spring BOM)
io.jsonwebtoken:jjwt-api:0.11.5
io.jsonwebtoken:jjwt-impl:0.11.5     (runtime)
io.jsonwebtoken:jjwt-jackson:0.11.5  (runtime)
org.projectlombok:lombok              (optional)
org.apache.pdfbox:pdfbox:2.0.34
spring-boot-starter-test              (test)
spring-security-test                  (test)
```
