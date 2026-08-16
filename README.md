# Andres Pizzeria API

API REST para gestionar pizzas, clientes y pedidos de una pizzeria, construida con **Spring Boot 3.5** y **Java 21**. Proyecto personal de aprendizaje enfocado en practicar Spring Data JPA, Spring Security con JWT y buenas practicas de una API REST (validacion, manejo de errores, tests, documentacion).

## Contenido

- [Stack tecnico](#stack-tecnico)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Seguridad](#seguridad)
- [Como correrlo localmente](#como-correrlo-localmente)
- [Documentacion de la API](#documentacion-de-la-api)
- [Endpoints principales](#endpoints-principales)
- [Tests](#tests)

## Stack tecnico

- **Java 21** + **Spring Boot 3.5** (Gradle)
- **Spring Data JPA** + **PostgreSQL**
- **Spring Security** con autenticacion **JWT** (auth0 `java-jwt`)
- **Bean Validation** (`spring-boot-starter-validation`)
- **Springdoc OpenAPI / Swagger UI**
- **Lombok**
- **JUnit 5** + **Spring Security Test** (`MockMvc` + `@WithMockUser`)

## Arquitectura

Estructura en capas, un paquete por responsabilidad:

```
persistence/entity      Entidades JPA (mapeo a la base de datos)
persistence/repository  Interfaces Spring Data (ListCrudRepository)
persistence/audit       Auditoria de usuario y de cambios (listeners)
security                Configuracion de Spring Security, filtro y utilidades JWT
service                 Logica de negocio, orquesta los repositorios
web/controller           Controladores REST (@RestController), bajo /api/...
web/exception            Manejo centralizado de errores (@RestControllerAdvice)
dto                      Records de entrada/salida, desacoplados de las entidades
```

Puntos destacados del diseno:

- Cada controlador expone **DTOs dedicados** (`*CreateDto` para altas, `*UpdateDto` para actualizaciones parciales, `*ResponseDto` para lecturas) en vez de entidades JPA crudas, para no exponer ni dejar modificar campos que el cliente no deberia controlar (ids autogenerados, fechas de auditoria, etc).
- Los errores de validacion (`@Valid` + Bean Validation) y los recursos no encontrados se resuelven en un unico `GlobalExceptionHandler`, en vez de manejo de errores repetido en cada controlador.
- Uso variado de Spring Data JPA: metodos de consulta derivados por nombre, `@Query` JPQL y nativo (incluye una consulta con `GROUP BY`/`STRING_AGG` devuelta como proyeccion de interfaz), un `@Modifying` para updates masivos, paginacion/ordenamiento, y una llamada a un stored procedure de PostgreSQL.

## Modelo de datos

```
CustomerEntity (1) ---- (1) OrderEntity (1) ---- (N) OrderItemEntity (N) ---- (1) PizzaEntity
```

- `OrderEntity` referencia a `CustomerEntity` de forma unidireccional (solo lectura desde el pedido).
- `OrderEntity.items` usa cascada completa (`CascadeType.ALL` + `orphanRemoval`): guardar un pedido guarda/actualiza/elimina sus items automaticamente.
- `OrderItemEntity` tiene clave primaria compuesta (`idItem` + `idOrder`, via `@IdClass`), con `@MapsId` para heredar el id del pedido (generado por la base de datos) al insertar en cascada.
- Auditoria de fechas y usuario (`createdDate`, `lastModifiedDate`, `createdBy`, `lastModifiedBy`) centralizada en una superclase `@MappedSuperclass` (`AuditableEntity`), heredada por las entidades principales. El usuario auditado se obtiene del JWT autenticado en cada peticion (`AuditorAware`).
- Los roles de usuario se modelan con una tabla de union (`UserRoleEntity` + clave compuesta `UserRoleId`), permitiendo que un usuario tenga varios roles.

## Seguridad

Autenticacion **stateless** basada en JWT (sin sesiones): el cliente hace login una vez y reenvia el token en cada peticion protegida via header `Authorization: Bearer <token>`.

| Ruta | Regla de acceso |
|---|---|
| `GET /api/pizzas/**` | Publico (sin autenticacion) |
| `POST` / `PUT /api/pizzas/**` | Requiere estar autenticado (cualquier rol) |
| `DELETE /api/pizzas/**` | Rol `ADMIN` |
| `GET /api/orders/random-promo` | Autoridad `RANDOM_ORDER` (la tienen `ADMIN` y `EMPLOYEE`) |
| `/api/orders/**` (resto) | Rol `ADMIN` |
| `/api/customers/**` | Roles `ADMIN`, `EMPLOYEE` o `CUSTOMER` |
| `/api/auth/**` | Publico (login) |
| `/swagger-ui/**`, `/v3/api-docs/**` | Publico |

Un usuario sin token recibe `401 Unauthorized`; un usuario autenticado sin el rol/autoridad requerida recibe `403 Forbidden`.

## Como correrlo localmente

### Requisitos

- Java 21
- PostgreSQL corriendo localmente, con una base de datos llamada `pizzeria`

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/andresian07/andres-pizzeria.git
cd andres-pizzeria

# 2. (Opcional) configurar credenciales de base de datos si no son las de por defecto
export DB_USERNAME=postgres
export DB_PASSWORD=1234

# 3. Levantar la aplicacion
./gradlew bootRun        # Linux/Mac
.\gradlew.bat bootRun    # Windows PowerShell
```

Hibernate crea/actualiza el esquema automaticamente al arrancar (`spring.jpa.hibernate.ddl-auto=update`) a partir de las entidades — no hace falta correr scripts SQL a mano.

La API queda disponible en `http://localhost:8080`.

## Documentacion de la API

Con la aplicacion corriendo, la documentacion interactiva (Swagger UI) esta disponible en:

```
http://localhost:8080/swagger-ui.html
```

Alli se puede ver cada endpoint con su descripcion, los requisitos de autenticacion/rol, y probarlos directamente (pegando un JWT obtenido en `/api/auth/login`).

## Endpoints principales

### Auth

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/auth/login` | Inicia sesion y devuelve un JWT en el header `Authorization` |

### Pizzas

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/pizzas` | Lista pizzas paginadas |
| GET | `/api/pizzas/{id}` | Obtiene una pizza por id |
| GET | `/api/pizzas/available` | Pizzas disponibles, paginadas y ordenables |
| GET | `/api/pizzas/cheapest/top3desc` | Las 3 pizzas disponibles mas baratas |
| GET | `/api/pizzas/name/{name}` | Busca una pizza por nombre exacto |
| GET | `/api/pizzas/ingredient/{ingredient}` | Pizzas que contienen un ingrediente |
| GET | `/api/pizzas/withoutingredient/{ingredient}` | Pizzas que no contienen un ingrediente |
| GET | `/api/pizzas/vegan` | Pizzas veganas disponibles |
| POST | `/api/pizzas` | Crea una pizza |
| PUT | `/api/pizzas/{id}` | Actualiza una pizza (parcial) |
| PUT | `/api/pizzas/updateprice/{id}` | Actualiza solo el precio |
| DELETE | `/api/pizzas/{id}` | Elimina una pizza |

### Customers

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/customers` | Lista todos los clientes |
| GET | `/api/customers/{id}` | Obtiene un cliente por id |
| GET | `/api/customers/phone/{phone}` | Busca un cliente por telefono |
| GET | `/api/customers/{id}/orders` | Pedidos de un cliente |
| POST | `/api/customers` | Crea un cliente |
| PUT | `/api/customers/{id}` | Actualiza un cliente (parcial) |
| DELETE | `/api/customers/{id}` | Elimina un cliente |

### Orders

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/orders` | Lista todos los pedidos |
| GET | `/api/orders/{id}` | Obtiene un pedido por id |
| GET | `/api/orders/findbydate` | Pedidos creados hoy |
| GET | `/api/orders/outside` | Pedidos a domicilio o para recoger |
| GET | `/api/orders/ordersummary/{id}` | Resumen de items de un pedido |
| GET | `/api/orders/random-promo` | Toma un pedido de promocion aleatoria (stored procedure) |
| POST | `/api/orders` | Crea un pedido (con sus items) |
| PUT | `/api/orders/{id}` | Actualiza un pedido (parcial) |
| DELETE | `/api/orders/{id}` | Elimina un pedido |

Ver el listado completo, con requisitos de rol detallados, en Swagger UI.

## Tests

```bash
./gradlew test
```

Incluye:
- Tests de contexto de Spring Boot.
- Test de integracion contra PostgreSQL real (`OrderRepositoryTest`, requiere la base de datos levantada) para la llamada al stored procedure.
- Tests de seguridad por controlador (`MockMvc` + `@WithMockUser`) que verifican los codigos `200`/`401`/`403` segun rol y autoridad para `Order`, `Customer` y `Pizza`.