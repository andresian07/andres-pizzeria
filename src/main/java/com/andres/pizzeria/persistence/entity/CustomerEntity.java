package com.andres.pizzeria.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "customer")
@Getter
@Setter // Si usas Lombok
@NoArgsConstructor
public class CustomerEntity extends AuditableEntity{
    @Id
    @Column(name = "id_customer", length = 15, nullable = false)
    private String idCustomer;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 150)
    private String address;

    @Column(nullable = false,length = 50, unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

}