package com.andres.pizzeria.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "pizza")
@Getter
@Setter
@NoArgsConstructor
public class PizzaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pizza", nullable = false)
    private Integer idPizza;

    @Column(nullable = false, length = 30, unique = true)
    private String name;

    @Column(nullable = false, length = 200) // ◄ Subido a 200 para evitar errores con textos largos
    private String description;

    @Column(nullable = false, precision = 6, scale = 2) // ◄ Forma nativa de JPA para Decimal(5,2)
    private BigDecimal price;

    // Quitamos los columnDefinition de SMALLINT para usar el tipo BOOLEAN nativo de Postgres
    @Column(nullable = false)
    private Boolean vegetarian;

    @Column(nullable = false)
    private Boolean vegan;

    @Column(nullable = false)
    private Boolean available;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}