package com.andres.pizzeria.persistence.repository;

import com.andres.pizzeria.persistence.entity.OrderEntity;
import com.andres.pizzeria.persistence.projection.OrderSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends ListCrudRepository<OrderEntity, Integer> {
    List<OrderEntity> findAllByDateAfter(LocalDateTime date);

    List<OrderEntity> findAllByMethodIn(List<Character> methods);

    @Query(value = "SELECT * FROM pizza_order WHERE id_customer = :id", nativeQuery = true)
    List<OrderEntity> findCustomerOrders(@Param("id") String idCistomer);

    @Query("SELECT o FROM OrderEntity o WHERE o.idCustomer = :id")
    List<OrderEntity> findCustomerOrdersJpql(@Param("id") String idCustomer);

    @Query(
            value = """
            SELECT
                o.id_order AS idOrder,
                c.name AS customerName,
                o.date AS orderDate,
                o.total AS orderTotal,
                STRING_AGG(p.name, ', ') AS pizzaName
            FROM pizza_order o
            INNER JOIN customer c
                ON o.id_customer = c.id_customer
            INNER JOIN order_item oi
                ON oi.id_order = o.id_order
            INNER JOIN pizza p
                ON p.id_pizza = oi.id_pizza
            WHERE o.id_order = :orderId
            GROUP BY o.id_order, c.name, o.date, o.total;
        """,
            nativeQuery = true
    )
    OrderSummary findSummary(@Param("orderId") int orderId);


}