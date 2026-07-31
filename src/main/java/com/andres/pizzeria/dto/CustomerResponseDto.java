package com.andres.pizzeria.dto;

public record CustomerResponseDto(
        String idCustomer,
        String name,
        String address,
        String email,
        String phoneNumber
) {}
