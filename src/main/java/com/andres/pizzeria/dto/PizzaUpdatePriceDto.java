package com.andres.pizzeria.dto;

import lombok.Data;

@Data
public class PizzaUpdatePriceDto {
    private int idPizza;
    private double newPrice;

}
