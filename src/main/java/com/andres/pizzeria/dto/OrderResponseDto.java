package com.andres.pizzeria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Integer idOrder,
        String idCustomer,
        String customerName, // ◄ ¡Aquí está lo que buscas!
        LocalDateTime date,
        BigDecimal total,
        Character method,
        String additionalNotes
        // Aquí podrías mapear también los items simplificados si quisieras
) {}