package com.andres.pizzeria.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void takeRandomPizzaOrder_createsAnOrder() {
        Boolean orderTaken = orderRepository.takeRandomPizzaOrder("863264988", "D");
        assertTrue(orderTaken);
    }
}