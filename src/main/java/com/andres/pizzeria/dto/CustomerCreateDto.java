package com.andres.pizzeria.dto;

public record CustomerCreateDto(
        String idCustomer,
        String name,
        String address,
        String email,
        String phoneNumber
) {}