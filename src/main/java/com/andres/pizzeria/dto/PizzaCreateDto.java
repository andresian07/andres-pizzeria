package com.andres.pizzeria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;


public record PizzaCreateDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive BigDecimal price,
        @NotNull Boolean vegetarian,
        @NotNull Boolean vegan,
        @NotNull Boolean available
) {}