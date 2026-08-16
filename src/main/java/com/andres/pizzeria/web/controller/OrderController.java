package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.OrderCreateDto;
import com.andres.pizzeria.dto.OrderResponseDto;
import com.andres.pizzeria.dto.OrderUpdateDto;
import com.andres.pizzeria.persistence.projection.OrderSummary;
import com.andres.pizzeria.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Consulta, alta, actualizacion y baja de pedidos. La mayoria de rutas requieren rol ADMIN; random-promo requiere la autoridad RANDOM_ORDER.")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController (OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Listar todos los pedidos", description = "Requiere rol ADMIN.")
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll(){
        List<OrderResponseDto> orderResponseDto = this.orderService.getAll();
        return ResponseEntity.ok(orderResponseDto);



    }

    @Operation(summary = "Obtener un pedido por id", description = "Requiere rol ADMIN. Devuelve 404 si no existe.")
    @GetMapping("/{idOrder}")
    public ResponseEntity<OrderResponseDto> get(@PathVariable int idOrder){
        OrderResponseDto orderResponseDto = this.orderService.get(idOrder);
        return ResponseEntity.ok(orderResponseDto);




    }

    @Operation(summary = "Pedidos de hoy", description = "Devuelve los pedidos creados desde la medianoche de hoy.")
    @GetMapping("/findbydate")
    public ResponseEntity<List<OrderResponseDto>> getTodayOrders(){
        return ResponseEntity.ok(this.orderService.getTodayOrders());
    }


    @Operation(summary = "Pedidos para llevar/domicilio", description = "Devuelve los pedidos cuyo metodo es D (domicilio) o C (recoger/carryout).")
    @GetMapping("/outside")
    public ResponseEntity<List<OrderResponseDto>> getOutsideOrders(){
        return ResponseEntity.ok(this.orderService.getOutsideOrders());
    }

    @Operation(summary = "Pedidos de un cliente (query derivada)", description = "Requiere rol ADMIN.")
    @GetMapping("/getcustomer/{idCustomer}")
    public ResponseEntity<List<OrderResponseDto>> getCustomersOrder(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.orderService.getCustomersOrders(idCustomer));
    }

    @Operation(summary = "Pedidos de un cliente (JPQL)", description = "Misma info que /getcustomer/{idCustomer} pero implementada con una consulta JPQL en vez de query derivada. Requiere rol ADMIN.")
    @GetMapping("/getcustomer/jpql/{idCustomer}")
    public ResponseEntity<List<OrderResponseDto>> getCustomersOrderJpql(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.orderService.getCustomersOrdersJpql(idCustomer));
    }

    @Operation(summary = "Resumen de un pedido", description = "Devuelve un resumen (proyeccion SQL con GROUP BY/STRING_AGG) de los items de un pedido. Requiere rol ADMIN.")
    @GetMapping("/ordersummary/{orderId}")
    public ResponseEntity<OrderSummary> getOrderSummary(@PathVariable int orderId){
        return ResponseEntity.ok(this.orderService.getOrderSummary(orderId));
    }


    @Operation(summary = "Crear un pedido", description = "Requiere rol ADMIN. Acepta una lista de items (pizza + cantidad); el total y los items se guardan en cascada.")
    @PostMapping
    public ResponseEntity<OrderResponseDto> add(@Valid @RequestBody OrderCreateDto order){
        return ResponseEntity.ok(this.orderService.save(order));
    }

    @Operation(summary = "Actualizar un pedido", description = "Actualizacion parcial (metodo de pago y/o notas adicionales). Requiere rol ADMIN. 404 si el id no existe.")
    @PutMapping("/{idOrder}")
    public ResponseEntity<OrderResponseDto> update(@PathVariable int idOrder, @RequestBody OrderUpdateDto orderDto){
        OrderResponseDto updateOrder = this.orderService.update(idOrder, orderDto);
        return ResponseEntity.ok(updateOrder);
    }

    @Operation(summary = "Tomar un pedido de promocion aleatoria", description = "Ejecuta el stored procedure take_random_pizza_order para el cliente indicado. Requiere la autoridad RANDOM_ORDER (la tienen ADMIN y EMPLOYEE). Devuelve 400 si el procedimiento no pudo tomar el pedido.")
    @GetMapping("/random-promo")
    public ResponseEntity<OrderSummary> takeRandomPizzaOrder(@RequestParam String idCustomer, @RequestParam String method){
        OrderSummary summary = this.orderService.takeRandomPizzaOrder(idCustomer, method);
        if (summary != null) {
            return ResponseEntity.ok(summary);
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Eliminar un pedido", description = "Requiere rol ADMIN. Devuelve el pedido eliminado, o 404 si el id no existe.")
    @DeleteMapping("/{idOrder}")
    public ResponseEntity<OrderResponseDto> delete(@PathVariable int idOrder){
        return ResponseEntity.ok(this.orderService.delete(idOrder));
    }
}
