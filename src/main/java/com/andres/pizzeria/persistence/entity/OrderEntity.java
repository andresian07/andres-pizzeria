package com.andres.pizzeria.persistence.entity;

import com.andres.pizzeria.persistence.audit.OrderAuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EntityListeners({AuditingEntityListener.class, OrderAuditListener.class})
@Entity
@Table(name = "pizza_order")
@Getter
@Setter
@NoArgsConstructor
public class OrderEntity implements Serializable {
    @Transient
    private transient OrderEntity snapshot;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order", nullable = false)
    private Integer idOrder;

    @Column(name = "id_customer", length = 15, nullable = false)
    private String idCustomer;

    @Column(nullable = false)
    private LocalDateTime date; // Hibernate lo mapeará automáticamente como TIMESTAMP en Postgres

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal total; // Forma nativa de JPA para definir DECIMAL(6,2)

    @Column(nullable = false, length = 1)
    private Character method; // Tipo ideal en Java para un CHAR(1) de base de datos

    @Column(name = "additional_notes", length = 200)
    private String additionalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_customer", referencedColumnName = "id_customer", insertable = false, updatable = false)
    private CustomerEntity customer;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Override
    public String toString() {
        return "OrderEntity{" +
                "idOrder=" + idOrder +
                ", idCustomer='" + idCustomer + '\'' +
                ", date=" + date +
                ", total=" + total +
                ", method=" + method +
                ", additionalNotes='" + additionalNotes + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}