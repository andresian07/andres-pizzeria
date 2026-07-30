package com.andres.pizzeria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreateDto(
        String idCustomer,
        LocalDateTime date,
        BigDecimal total,
        Character method,
        String additionalNotes,
        List<OrderItemCreateDto> items
) {}