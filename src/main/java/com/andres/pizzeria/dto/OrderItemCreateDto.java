package com.andres.pizzeria.dto;

import java.math.BigDecimal;

public record OrderItemCreateDto(
        Integer idPizza,
        BigDecimal quantity,
        BigDecimal price
) {}