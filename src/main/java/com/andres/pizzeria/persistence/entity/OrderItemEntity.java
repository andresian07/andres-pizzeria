package com.andres.pizzeria.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "order_item")
@IdClass(OrderItemId.class)
@Getter
@Setter
public class OrderItemEntity extends AuditableEntity {

    @Id
    @Column(name = "id_item", nullable = false)
    private Integer idItem;

    // Escritura delegada a la relación "order" (ver @MapsId más abajo): así Hibernate
    // puede rellenar este valor automáticamente con el id generado del OrderEntity padre
    // en el momento del cascade, sin que el servicio tenga que asignarlo a mano.
    @Id
    @Column(name = "id_order", nullable = false, insertable = false, updatable = false)
    private Integer idOrder;

    @Column(name = "id_pizza", nullable = false)
    private Integer idPizza; // Asegúrate de que coincida con tu clase Pizza

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idOrder")
    @JsonIgnore
    @JoinColumn(name = "id_order", referencedColumnName = "id_order", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "id_pizza", referencedColumnName = "id_pizza", insertable = false, updatable = false)
    private PizzaEntity pizza;


}
