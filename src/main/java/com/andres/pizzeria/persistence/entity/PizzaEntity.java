package com.andres.pizzeria.persistence.entity;

import com.andres.pizzeria.persistence.audit.AuditPizzaListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serializable;
import java.math.BigDecimal;


@EntityListeners({AuditingEntityListener.class, AuditPizzaListener.class})
@Entity
@Table(name = "pizza")
@Getter
@Setter
@NoArgsConstructor
public class PizzaEntity extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Transient
    private transient PizzaEntity snapshot;

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


    @Override
    public String toString() {
        return "PizzaEntity{" +
                "idPizza=" + idPizza +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", vegetarian=" + vegetarian +
                ", vegan=" + vegan +
                ", available=" + available +
                ", createdDate=" + getCreatedDate() +
                ", lastModifiedDate=" + getLastModifiedDate() +
                '}';
    }
}