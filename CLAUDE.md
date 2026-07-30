# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Spring Boot 3.5 REST API (Java 21, Gradle) for a pizzeria: pizzas, customers, and orders backed by PostgreSQL via Spring Data JPA. Base package: `com.andres.pizzeria`.

## Commands

This project uses the Gradle wrapper — always invoke it via `./gradlew` (or `gradlew.bat` on Windows), not a globally installed Gradle.

```
./gradlew build              # compile + run tests + package
./gradlew bootRun             # run the app locally (needs Postgres running, see below)
./gradlew test                 # run all tests
./gradlew test --tests "com.andres.pizzeria.AndresPizzeriaApplicationTests"   # run a single test class
./gradlew test --tests "com.andres.pizzeria.AndresPizzeriaApplicationTests.contextLoads"  # single test method
```

On Windows PowerShell use `.\gradlew.bat` instead of `./gradlew`.

There are currently only two tests: `AndresPizzeriaApplicationTests` (checks context loads) and `OrderRepositoryTest` (`@SpringBootTest` + `@Transactional`, covers the `takeRandomPizzaOrder` stored-procedure call) — no unit/integration tests exist yet for services or controllers.

### Database

The app connects directly to PostgreSQL (`src/main/resources/application.properties`):
- `jdbc:postgresql://localhost:5432/pizzeria`, user/password default to `postgres`/`1234` but are overridable via the `DB_USERNAME`/`DB_PASSWORD` environment variables (`${DB_USERNAME:postgres}` / `${DB_PASSWORD:1234}`) — no real credentials are hardcoded in source control.
- `spring.jpa.hibernate.ddl-auto=update` — Hibernate auto-updates the schema from the entities on startup. There are no Flyway/Liquibase migrations; schema changes happen by editing `@Entity` classes and letting Hibernate sync them.
- A local PostgreSQL instance with a `pizzeria` database must exist before running `bootRun` or any test that boots the Spring context.

## Architecture

Classic layered structure, one package per concern:

```
persistence/entity      JPA @Entity classes (DB-mapped)
persistence/repository  Spring Data interfaces (ListCrudRepository)
service                 business logic, orchestrates repositories
web/controller          @RestController, REST endpoints under /api/...
dto                     request/response records decoupled from entities
```

Repositories extend `ListCrudRepository<Entity, IdType>` (not the more common `JpaRepository`) — a Spring Data interface that returns `List` instead of `Iterable`. Custom finder methods are declared using Spring Data's method-name query derivation (e.g. `findALLByAvailableTrueOrderByPrice`, `findAllByAvailableTrueAndDescriptionContainingIgnoreCase` in `PizzaRepository`) rather than `@Query`.

### Entity relationships and fetch strategy

Domain model: `CustomerEntity` (1) → `OrderEntity` (1) → `OrderItemEntity` (N) ← `PizzaEntity`.

- `OrderEntity.customer` is a `@ManyToOne(fetch = FetchType.LAZY)` back to `CustomerEntity`, mapped read-only (`insertable = false, updatable = false`) alongside a plain `idCustomer` column used for writes. It's annotated `@JsonIgnore` to avoid serializing lazy proxies. This relationship is unidirectional — `CustomerEntity` has no inverse `@OneToMany`.
- `OrderEntity.items` is a `@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)`. Saving an `OrderEntity` with a populated `items` list now cascades: Hibernate persists/updates/deletes the child `OrderItemEntity` rows along with the order. Nothing currently reads this collection back out through the API (no DTO exposes it yet), but it is now writable end-to-end via `POST /api/orders`.
- `OrderItemEntity` has a composite primary key (`idItem` + `idOrder`) via `@IdClass(OrderItemId.class)`. `idItem` is assigned manually (it's a per-order line number, not a global sequence) — `OrderService.save()` fills it in (1, 2, 3...) for any item that doesn't already have one. `idOrder` is **not** set manually: the `order` association is annotated `@MapsId("idOrder")`, so Hibernate copies the parent's generated `idOrder` into the composite key automatically at cascade-insert time. This is required because `OrderEntity.idOrder` is `GenerationType.IDENTITY` (unknown until insert) — without `@MapsId` the child rows would try to insert with a null `id_order` and violate the `NOT NULL` constraint.
- Because of `@MapsId`, `OrderItemEntity.order` is the **writable** side of that FK column (no `insertable = false, updatable = false`), which is the opposite of the read-only pattern used for `Customer`/`Pizza` references below. `OrderService.save()` must call `item.setOrder(order)` for every item before saving — JPA's `mappedBy` cascade does not populate the child's association back-reference automatically. `OrderItemEntity.order` is `@JsonIgnore`d to avoid infinite recursion when serializing `Order → items → item.order → items → ...`.
- The recurring pattern for **read-only** navigation (`OrderEntity.customer`, `OrderItemEntity.pizza`, and `OrderItemEntity.idOrder` itself): a plain FK column marked `insertable = false, updatable = false` on the `@ManyToOne` used only for reads, with a separate writable plain column for writes. Use `@MapsId` instead (as with `order` above) only when you need cascade-persisting a child whose key depends on a not-yet-generated parent id.

### Notable gaps / in-progress state

- `Customer` now has full CRUD (`CustomerRepository`, `CustomerService`, `CustomerController` under `/api/customers`), mirroring the `Pizza` pattern, including a `CustomerUpdateDto` for partial updates (`name`, `address`, `email`, `phoneNumber`).
- `dto/OrderUpdateDto.java` is now implemented (`method`, `additionalNotes` record) — no longer the empty placeholder it used to be.
- `Order` and `Pizza` controllers already return response DTOs (`OrderResponseDto`, `PizzaResponseDto`) rather than entities. `CustomerController` is the exception — it still returns `CustomerEntity` directly from every endpoint, unlike the other two. Bring it in line with the `Order`/`Pizza` DTO pattern (a `CustomerResponseDto`) when touching that controller.
- All three controllers' `add()` endpoints take the raw entity as `@RequestBody` (`OrderEntity`, `PizzaEntity`, `CustomerEntity`) instead of a dedicated create DTO — callers can set fields that shouldn't be externally controlled (e.g. `idOrder`, `createdDate`). Worth introducing `*CreateDto` records, following the existing `*UpdateDto` pattern.
- No input validation anywhere (`jakarta.validation`, `@Valid`, `@NotNull`, `@Positive`, etc.) — nothing stops a negative pizza price or a blank customer name from being persisted.
- Controllers/services return `null`/404 for missing entities rather than throwing exceptions; there's no global `@ExceptionHandler`/`@ControllerAdvice` yet, so there's no consistent error-response shape.
- Test coverage is limited to the two tests noted above — `OrderService`, `PizzaService`, `CustomerService`, and all three controllers are untested.
- No Flyway/Liquibase migrations — schema evolves only via `ddl-auto=update` (see Database section above), fine for learning but not representative of a production setup.
- No Spring Security dependency yet — the API has no authentication/authorization. Expected to be addressed once the Security coursework starts.
- Custom `@EntityListeners` (`AuditPizzaListener`, `OrderAuditListener` in `persistence/audit/`) log before/after state on `@PostLoad`/`@PostPersist`/`@PostUpdate`/`@PreRemove` by keeping a cloned `snapshot` field on the entity (`Serializable`, `@Transient`). This is in addition to the existing `AuditingEntityListener` (`createdDate`/`lastModifiedDate`) — both listeners are registered together in `@EntityListeners({...})`.