package com.andres.pizzeria.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 1. Debe implementar Serializable obligatoriamente
public class OrderItemId implements Serializable {

    // 2. Los nombres y tipos de datos deben ser EXACTAMENTE iguales
    // a los que marcaste con @Id en tu clase OrderItemEntity
    private Integer idOrder;
    private Integer idItem;

    // 6. Sobreescribir equals y hashCode obligatoriamente.
    // Hibernate los usa para buscar y comparar los registros únicos en la base de datos.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(idOrder, that.idOrder) && Objects.equals(idItem, that.idItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrder, idItem);
    }
}