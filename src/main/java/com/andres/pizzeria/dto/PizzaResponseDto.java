package com.andres.pizzeria.dto;

import java.math.BigDecimal;

public record PizzaResponseDto(
     Integer idPizza,
     String name,
     String description,
     BigDecimal price,
     Boolean vegetarian,
     Boolean vegan,
     Boolean available
) { }
