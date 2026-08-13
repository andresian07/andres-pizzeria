package com.andres.pizzeria.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PizzaSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeEliminarPizzas() throws Exception {
        mockMvc.perform(delete("/api/pizzas/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeNoPuedeEliminarPizzas() throws Exception {
        mockMvc.perform(delete("/api/pizzas/9999"))
                .andExpect(status().isForbidden());
    }


    @Test
    void sinAutenticarNoPuedeEliminarPizzas() throws Exception {
        mockMvc.perform(delete("/api/pizzas/9999"))
                .andExpect(status().isUnauthorized());
    }
}
