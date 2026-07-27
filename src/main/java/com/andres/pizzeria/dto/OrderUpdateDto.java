package com.andres.pizzeria.dto;

public record OrderUpdateDto(
        Character method,
        String additionalNotes
) {}
