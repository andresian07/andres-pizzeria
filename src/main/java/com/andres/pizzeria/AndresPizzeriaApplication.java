package com.andres.pizzeria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
public class AndresPizzeriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AndresPizzeriaApplication.class, args);
	}

}
