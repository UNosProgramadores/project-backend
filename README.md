<h1 align="center">ParKing — Backend</h1>
<h3 align="center">API REST para la Gestión de Parqueaderos de Vehículos</h3>

---

## 👥 Nombre del Grupo

**UNosProgramadores**

---

## 📩 Integrantes y contacto

- **Nicolás Aguirre Velásquez:** [niaguirrev@unal.edu.co](mailto:niaguirrev@unal.edu.co)
- **Jhon Freddy Patiño Meza:** [jhpatinom@unal.edu.co](mailto:jhpatinom@unal.edu.co)

---

## 🚀 Sobre este repositorio

Este repositorio contiene el **backend de ParKing**, el servicio REST encargado
de toda la lógica del sistema: autenticación de usuarios, administración de
parqueaderos y su matriz de celdas, registro de ingresos/salidas de vehículos,
cálculo automático de tarifas y descuentos, generación de facturas y reportes
financieros.

Está construido sobre **Spring Boot**, expone una API REST consumida por el
frontend en Vue 3, y persiste la información en **PostgreSQL**.

### 🎯 Responsabilidades principales

- Exponer los endpoints REST que consume el frontend.
- Autenticar y autorizar usuarios mediante **JWT**.
- Modelar el parqueadero como una matriz configurable de celdas.
- Calcular automáticamente tarifas y descuentos según el tipo de vehículo y el tiempo de estancia.
- Registrar ingresos, salidas y generar facturas.
- Persistir toda la información en PostgreSQL a través de Spring Data JPA.

---

## 🏗️ Arquitectura por capas

El backend sigue una **arquitectura en capas** clásica de Spring Boot, con tres
capas transversales adicionales (`config`, `exception`, `security`) que
atraviesan el flujo principal de la petición:

| Capa                       | Paquete        | Rol                                     |
|-----------------------------|---------------|------------------------------------------|
| **Controller** (presentación) | `.controller`  | Endpoints REST                            |
| **DTO**                     | `.dto`         | Objetos de entrada/salida de la API       |
| **Service** (negocio)        | `.service`     | Lógica de negocio                         |
| **Repository** (datos)       | `.repository`  | Acceso a BD (Spring Data JPA)             |
| **Entity** (modelo)          | `.entity`      | Entidades JPA (mapeo a tablas)            |
| **Config**                  | `.config`      | Configuración (Security)                  |
| **Exception**                | `.exception`   | Manejo global de errores                  |
| **Security**                 | `.security`    | JWT / autenticación                       |

El flujo típico de una petición es:

```
Cliente (Vue 3)
      │  REST / JSON
      ▼
Security (JwtAuthFilter) → Controller → DTO → Service → Repository → Entity → PostgreSQL
      ▲
Exception (GlobalExceptionHandler) captura errores en cualquier punto del flujo
```

> El diagrama completo de la arquitectura (con las clases de cada capa) está
> disponible en `docs/parKing_arquitectura_final.html` / `.png`.

---

## 📦 Tecnologías utilizadas

- **Java 25**
- **Spring Boot 4.0.6** (Spring Web MVC, Spring Data JPA)
- **PostgreSQL 16** como motor de base de datos
- **Hibernate / JPA** como ORM
- **Docker** para levantar la base de datos en contenedor
- **Maven** (con Maven Wrapper) como gestor de dependencias y build
- **JWT** para autenticación y autorización basada en roles
- **SHA-256** para el hash de contraseñas
- **Tomcat embebido** como servidor de aplicaciones

---

## ⚙️ Puesta en marcha

### Requisitos previos

- Docker y Docker Compose
- Java 25 (JDK)
- Un archivo `.env` en la raíz del proyecto con las variables de la base de datos:

```env
DB_NAME=parking
DB_USER=parking_user
DB_PASSWORD=********
```

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/UNosProgramadores/project-backend.git
   cd project-backend
   ```

2. **Levantar la base de datos con Docker**
   ```bash
   docker compose up -d
   ```

3. **Ejecutar los scripts de inicialización** (creación de esquema y datos demo, disponibles en `scripts/`)

4. **Compilar y ejecutar el backend**
   ```bash
   ./mvnw clean install -DskipTests
   ./mvnw spring-boot:run
   ```

También se incluye un script `setup.sh` que automatiza estos pasos
(instalación de dependencias, levantamiento de la base de datos, ejecución de
scripts SQL y build del backend).

---

## 📁 Estructura del proyecto

```
project-backend/
├── src/main/java/com/parking/backend/
│   ├── controller/     # Endpoints REST
│   ├── dto/             # Objetos de entrada/salida de la API
│   ├── service/          # Lógica de negocio
│   ├── repository/       # Acceso a datos (Spring Data JPA)
│   ├── entity/            # Entidades JPA
│   ├── config/             # Configuración (Security)
│   ├── exception/           # Manejo global de errores
│   └── security/              # JWT / autenticación
├── scripts/              # Scripts SQL (init.sql, data_demo.sql)
├── docker_compose.yml    # Definición del contenedor de PostgreSQL
├── setup.sh / setup.bat  # Scripts de configuración inicial
└── pom.xml               # Definición del proyecto Maven
```

---

### Gracias por ver :)

https://docs.google.com/presentation/d/1uSWJbTZAvk7XzXV6JH_OCKCCbY0rUS-i8aD2ZzhhGck/edit?usp=sharing
