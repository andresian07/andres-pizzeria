package com.andres.pizzeria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreateDto(
        @NotBlank String idCustomer,
        @NotNull LocalDateTime date,
        @NotNull BigDecimal total,
        @NotNull Character method,
                 String additionalNotes,
        // cuando es un objeto anidado se debe usar la anotacion @Valid y @NotEmpty para listas
        @Valid @NotEmpty List<OrderItemCreateDto> items
) {}