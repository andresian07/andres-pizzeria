package com.andres.pizzeria.dto;

public record CustomerUpdateDto(
        String name,
        String address,
        String email,
        String phoneNumber
) {}