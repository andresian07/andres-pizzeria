package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.OrderCreateDto;
import com.andres.pizzeria.dto.OrderResponseDto;
import com.andres.pizzeria.dto.OrderUpdateDto;
import com.andres.pizzeria.persistence.projection.OrderSummary;
import com.andres.pizzeria.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController (OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll(){
        List<OrderResponseDto> orderResponseDto = this.orderService.getAll();
        return ResponseEntity.ok(orderResponseDto);



    }

    @GetMapping("/{idOrder}")
    public ResponseEntity<OrderResponseDto> get(@PathVariable int idOrder){
        OrderResponseDto orderResponseDto = this.orderService.get(idOrder);
        return ResponseEntity.ok(orderResponseDto);




    }

    @GetMapping("/findbydate")
    public ResponseEntity<List<OrderResponseDto>> getTodayOrders(){
        return ResponseEntity.ok(this.orderService.getTodayOrders());
    }


    @GetMapping("/outside")
    public ResponseEntity<List<OrderResponseDto>> getOutsideOrders(){
        return ResponseEntity.ok(this.orderService.getOutsideOrders());
    }

    @GetMapping("/getcustomer/{idCustomer}")
    public ResponseEntity<List<OrderResponseDto>> getCustomersOrder(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.orderService.getCustomersOrders(idCustomer));
    }

    @GetMapping("/getcustomer/jpql/{idCustomer}")
    public ResponseEntity<List<OrderResponseDto>> getCustomersOrderJpql(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.orderService.getCustomersOrdersJpql(idCustomer));
    }

    @GetMapping("/ordersummary/{orderId}")
    public ResponseEntity<OrderSummary> getOrderSummary(@PathVariable int orderId){
        return ResponseEntity.ok(this.orderService.getOrderSummary(orderId));
    }


    @PostMapping
    public ResponseEntity<OrderResponseDto> add(@Valid @RequestBody OrderCreateDto order){
        return ResponseEntity.ok(this.orderService.save(order));
    }

    @PutMapping("/{idOrder}")
    public ResponseEntity<OrderResponseDto> update(@PathVariable int idOrder, @RequestBody OrderUpdateDto orderDto){
        OrderResponseDto updateOrder = this.orderService.update(idOrder, orderDto);
        return ResponseEntity.ok(updateOrder);
    }

    @GetMapping("/random-promo")
    public ResponseEntity<OrderSummary> takeRandomPizzaOrder(@RequestParam String idCustomer, @RequestParam String method){
        OrderSummary summary = this.orderService.takeRandomPizzaOrder(idCustomer, method);
        if (summary != null) {
            return ResponseEntity.ok(summary);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idOrder}")
    public ResponseEntity<OrderResponseDto> delete(@PathVariable int idOrder){
        return ResponseEntity.ok(this.orderService.delete(idOrder));
    }
}
