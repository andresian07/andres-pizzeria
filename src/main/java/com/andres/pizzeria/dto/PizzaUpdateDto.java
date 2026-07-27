package com.andres.pizzeria.dto;



import java.math.BigDecimal;

/**
 * DTO optimizado para actualizaciones parciales en condiciones reales de negocio.
 * Solo permite modificar el precio y la disponibilidad operativa.
 */
public record PizzaUpdateDto(
        BigDecimal price,
        Boolean available
) {}