# Apuntes rápidos

## Patrón de los 3 DTOs (Create / Update / Response)

Cada entidad (`Pizza`, `Customer`, `Order`) tiene tres DTOs porque cada uno resuelve
un problema distinto:

| DTO | Cuándo se usa | Reglas |
|---|---|---|
| `*CreateDto` | entra en el `POST` (crear algo nuevo) | sin id, todos los campos obligatorios (`@NotNull`, `@NotBlank`) |
| `*UpdateDto` | entra en el `PUT` (modificar algo existente) | el id va en la URL, no en el body; solo los campos que tiene sentido cambiar |
| `*ResponseDto` | siempre sale (`GET`, y lo que devuelven `create`/`update`/`delete`) | incluye el id, nunca lleva validaciones |

Nunca se devuelve la entidad JPA (`PizzaEntity`, `CustomerEntity`, `OrderEntity`)
directo desde un controller — siempre se convierte a `ResponseDto` con un método
tipo `toResponseDto(entity)` dentro del `Service`.

## La regla de oro cuando algo no compila

> Si cambio qué tipo devuelve un método (por ejemplo, de `Entity` a `ResponseDto`),
> tengo que revisar **cada `return` de adentro de ese método** y **cada lugar
> donde alguien lo llama** (normalmente el `Controller`).

Si me pierdo con los errores: **compilar y leer el mensaje exacto**, no adivinar.

```
.\gradlew.bat compileJava
```

El compilador siempre dice el archivo y la línea exacta:
```
CustomerController.java:53: error: incompatible types: CustomerResponseDto cannot be converted to CustomerEntity
```
Eso significa: "en esta línea declaraste un tipo, pero te está llegando otro" —
casi siempre porque el `Service` cambió de firma y el `Controller` (o viceversa)
todavía no se actualizó.

## Estado actual (2026-07-31)

`Pizza`, `Customer` y `Order` ya siguen el patrón completo: los tres controllers
devuelven `ResponseDto` en todos sus endpoints, sin ninguna entidad expuesta.

Próxima vez que se agregue una entidad nueva (o un campo nuevo a una existente),
este es el mismo patrón a repetir: `CreateDto` → `Service.save()` → `toResponseDto()`
→ `Controller` devuelve `ResponseEntity<XResponseDto>`.