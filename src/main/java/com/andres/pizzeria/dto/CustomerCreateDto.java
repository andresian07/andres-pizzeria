package com.andres.pizzeria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerCreateDto(
        @NotBlank String idCustomer,
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber
) {}