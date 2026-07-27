package com.andres.pizzeria.service;

import com.andres.pizzeria.dto.CustomerUpdateDto;
import com.andres.pizzeria.dto.OrderResponseDto;
import com.andres.pizzeria.dto.OrderUpdateDto;
import com.andres.pizzeria.dto.PizzaUpdateDto;
import com.andres.pizzeria.persistence.entity.CustomerEntity;
import com.andres.pizzeria.persistence.entity.OrderEntity;
import com.andres.pizzeria.persistence.entity.OrderItemEntity;
import com.andres.pizzeria.persistence.entity.PizzaEntity;
import com.andres.pizzeria.persistence.projection.OrderSummary;
import com.andres.pizzeria.persistence.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderResponseDto> getAll(){
        // el finAll() recibe solo el orderEntity asi que debe ser de ese tipo y
        // despues lo convertimos a OrderResponseDto para poderlo pasar por la banda y devolver la lista
        List<OrderEntity> orders = this.orderRepository.findAll();
        List<OrderResponseDto> allOrders = orders.stream().map(order -> toResponseDto(order)).toList();
        return allOrders;
    }

    public OrderResponseDto get(int idOrder){
        OrderEntity order = this.orderRepository.findById(idOrder).orElse(null);
        if( order != null){
            return  toResponseDto(order);
        }

        return null;
    }

    public List<OrderResponseDto> getTodayOrders(){
        LocalDateTime today = LocalDateTime.now().toLocalDate().atTime(0,0);
        List<OrderEntity> orders = this.orderRepository.findAllByDateAfter(today);
        List<OrderResponseDto> allOrders = orders.stream().map(order -> toResponseDto(order)).toList();
        return allOrders;
    }

    private OrderResponseDto toResponseDto(OrderEntity order) {
        return new OrderResponseDto(
                order.getIdOrder(),
                order.getIdCustomer(),
                order.getCustomer().getName(),
                order.getDate(),
                order.getTotal(),
                order.getMethod(),
                order.getAdditionalNotes()
        );
    }


    public List<OrderResponseDto> getOutsideOrders(){
        List<OrderEntity> orders = this.orderRepository.findAllByMethodIn(List.of('D', 'C'));
        return orders.stream().map(order -> toResponseDto(order)).toList();
    }

    public List<OrderResponseDto> getCustomersOrders(String idCustomer){
        List<OrderEntity> orders = this.orderRepository.findCustomerOrders(idCustomer);
        return orders.stream().map(order -> toResponseDto(order)).toList();
    }

    public List<OrderResponseDto> getCustomersOrdersJpql(String idCustomer){
        List<OrderEntity> orders = this.orderRepository.findCustomerOrdersJpql(idCustomer);
        return orders.stream().map(order -> toResponseDto(order)).toList();
    }

    public OrderSummary getOrderSummary(int orderId){
        return this.orderRepository.findSummary(orderId);
    }

    public OrderResponseDto save(OrderEntity order){
        if (order.getItems() != null) {
            int lineNumber = 1;
            for (OrderItemEntity item : order.getItems()) {
                item.setOrder(order); // enlaza el lado dueño de la relación antes del cascade
                if (item.getIdItem() == null) {
                    item.setIdItem(lineNumber);
                }
                lineNumber++;
            }
        }
        OrderEntity savedOrder = this.orderRepository.save(order);
        // volvemos a buscarla para tener el customer cargable: el order recién
        // llegado del @RequestBody nunca lo trajo, por el @JsonIgnore
        OrderEntity fullOrder = this.orderRepository.findById(savedOrder.getIdOrder()).orElseThrow();
        return toResponseDto(fullOrder);
    }

    public OrderResponseDto update(int idOrder, OrderUpdateDto orderUpdateDto) {
        Optional<OrderEntity> optionalOrder = this.orderRepository.findById(idOrder);

        if (optionalOrder.isPresent()) {
            OrderEntity orderExistente = optionalOrder.get();

            if (orderUpdateDto.method() != null) {
                orderExistente.setMethod(orderUpdateDto.method());
            }
            if (orderUpdateDto.additionalNotes() != null) {
                orderExistente.setAdditionalNotes(orderUpdateDto.additionalNotes());
            }

            OrderEntity updatedOrder = this.orderRepository.save(orderExistente);
            return toResponseDto(updatedOrder);
        }

        return null;
    }

    public OrderResponseDto delete(int idOrder
    ){
        Optional<OrderEntity> optionalOrder = this.orderRepository.findById(idOrder);
        if (optionalOrder.isPresent()) {
            OrderEntity orderABorrar = optionalOrder.get(); // 1. Extraemos la entidad real
            OrderResponseDto deletedOrderDto = toResponseDto(orderABorrar); // 2. La convertimos antes de borrarla
            this.orderRepository.delete(orderABorrar);     // 3. La borramos (no lleva return)
            return deletedOrderDto;                         // 4. Devolvemos el DTO de la order que borramos
        }

        return null; // Si no existía, devolvemos null
    }
}
