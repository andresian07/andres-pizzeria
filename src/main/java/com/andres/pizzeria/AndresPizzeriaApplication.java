package com.andres.pizzeria;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
@OpenAPIDefinition(info = @Info(
        title = "Andres Pizzeria API",
        version = "1.0",
        description = "API REST para gestionar pizzas, clientes y pedidos de la pizzeria. " +
                "La mayoria de operaciones de escritura requieren autenticacion via JWT " +
                "(POST /api/auth/login) y algunas ademas requieren un rol especifico (ADMIN/EMPLOYEE/CUSTOMER)."
))
public class AndresPizzeriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AndresPizzeriaApplication.class, args);
	}

}
