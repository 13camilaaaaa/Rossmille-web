# ROSS MILLE Web

Sistema de punto de venta (POS) web para la tienda de ropa ROSS MILLE.
Migracion del prototipo Java Swing a una aplicacion web con Spring Boot REST API y frontend HTML/Bootstrap 5.

---

## Tecnologias

| Capa | Tecnologia |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.3.5 |
| ORM | Spring Data JPA + Hibernate 6 |
| Seguridad | Spring Security 6 + JWT (jjwt 0.11.5) |
| Contrasenas | BCryptPasswordEncoder |
| Frontend | HTML + Bootstrap 5.3.2 + Vanilla JS |
| Base de datos | MySQL 8.0 en Docker |
| PDF | Apache PDFBox 2.0.34 |
| Build | Maven Wrapper (mvnw) |

---

## Requisitos previos

- Java 21 (JDK)
- Docker y Docker Compose
- Git

No se requiere Maven instalado globalmente: el proyecto incluye `mvnw`.

---

## Setup

### 1. Clonar el repositorio

```bash
git clone <url-del-repo>
cd rossmille-web
```

### 2. Levantar la base de datos

La BD MySQL 8.0 se levanta desde el proyecto Swing (comparten el mismo contenedor):

```bash
cd /ruta/al/prototipo-java
docker compose up -d
```

Verificar que el contenedor este corriendo:

```bash
docker ps
```

Verificar que las tablas existan:

```bash
docker exec rossmille_mysql mysql -uRossMille -pRossMillB01 rossmille_db -e "SHOW TABLES;"
```

Deben aparecer las 7 tablas: `usuarios`, `clientes`, `productos`, `ventas`, `detalle_venta`, `pedidos`, `detalle_pedido`.

### 3. Crear el primer administrador

Si la tabla `usuarios` esta vacia, ejecutar el script del prototipo:

```bash
cd /ruta/al/prototipo-java
python3 db/setup_admin.py
```

El script pedira ID, nombre y contrasena del administrador.

### 4. Correr la aplicacion

```bash
cd rossmille-web
./mvnw spring-boot:run
```

La app queda disponible en: `http://localhost:8080/login.html`

---

## Credenciales de prueba (BD local Docker)

| Campo | Valor |
|-------|-------|
| ID | 1234567 |
| Cargo | Administrador |
| Contrasena | Admin123 |

---

## Modulos

| Modulo | Ruta | Acceso |
|--------|------|--------|
| Login | /login.html | Publico |
| Dashboard | /dashboard.html | Todos |
| Vender (POS) | /vender.html | Todos |
| Productos | /productos.html | Todos |
| Clientes | /clientes.html | Todos |
| Pedidos | /pedidos.html | Todos |
| Usuarios | /usuarios.html | Solo Administrador |
| Reporte | /reporte.html | Solo Administrador |

---

## API REST

### Autenticacion (publica)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/auth/login | Login — retorna JWT |

Body: `{ "id": "...", "cargo": "...", "contrasena": "..." }`
Respuesta: `{ "ok": true, "data": { "token": "...", "nombre": "...", "rol": "..." } }`

### Productos

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/productos | Listar (con ?q= para buscar) |
| GET | /api/productos/stock-bajo | Stock <= 5 |
| GET | /api/productos/{id} | Obtener uno |
| POST | /api/productos | Crear |
| PUT | /api/productos/{id} | Actualizar |
| DELETE | /api/productos/{id} | Eliminar (requiere contrasena) |

### Clientes

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/clientes | Listar (con ?q= para buscar) |
| GET | /api/clientes/{id} | Obtener uno |
| POST | /api/clientes | Crear |
| PUT | /api/clientes/{id} | Actualizar |
| DELETE | /api/clientes/{id} | Eliminar — solo Administrador |
| GET | /api/clientes/{id}/compras | Historial de compras |

### Ventas

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/ventas | Crear venta con transaccion ACID |

### Pedidos

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/pedidos?tipo=activos | Pendiente + En Proceso |
| GET | /api/pedidos?tipo=historial | Atendidos |
| POST | /api/pedidos | Crear pedido |
| PUT | /api/pedidos/{id}/avanzar | Avanzar estado |
| DELETE | /api/pedidos/{id} | Eliminar — solo Administrador |

### Usuarios — solo Administrador

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/usuarios | Listar |
| POST | /api/usuarios | Crear |
| PUT | /api/usuarios/{id} | Actualizar |
| DELETE | /api/usuarios/{id} | Eliminar |

### Reporte — solo Administrador

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/reporte?desde=YYYY-MM-DD&hasta=YYYY-MM-DD | Ventas en JSON |
| GET | /api/reporte/pdf?desde=YYYY-MM-DD&hasta=YYYY-MM-DD | Descargar PDF |

---

## Estructura del proyecto

```
rossmille-web/
├── pom.xml
├── mvnw
├── src/main/
│   ├── java/com/rossmille/
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── ProductoController.java
│   │   │   ├── ClienteController.java
│   │   │   ├── VentaController.java
│   │   │   ├── PedidoController.java
│   │   │   ├── UsuarioController.java
│   │   │   └── ReporteController.java
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── resources/
│       ├── application.yml
│       └── static/
│           ├── login.html
│           ├── dashboard.html
│           ├── productos.html
│           ├── clientes.html
│           ├── vender.html
│           ├── pedidos.html
│           ├── usuarios.html
│           ├── reporte.html
│           └── js/
│               ├── auth.js
│               ├── api.js
│               ├── productos.js
│               ├── clientes.js
│               ├── vender.js
│               ├── pedidos.js
│               ├── usuarios.js
│               └── reporte.js
```

---

## Seguridad

- Contrasenas almacenadas con hash BCrypt
- Autenticacion stateless con JWT (expiracion 8 horas)
- Control de acceso por rol con `@PreAuthorize` en backend y guard de rutas en frontend
- Ventas con bloqueo pesimista (`SELECT FOR UPDATE`) para evitar condiciones de carrera en stock

---

## Base de datos

La app usa `ddl-auto: validate` — Hibernate verifica que las entidades coincidan con el schema
pero no lo modifica. El schema se crea desde `db/init.sql` del prototipo Swing.

Configuracion en `src/main/resources/application.yml`.

---

Desarrollado por Camila Navas.
