package com.andres.pizzeria.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemCreateDto(
        @NotNull Integer idPizza,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal price
) {}