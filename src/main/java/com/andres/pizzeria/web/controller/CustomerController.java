package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.CustomerCreateDto;
import com.andres.pizzeria.dto.CustomerResponseDto;
import com.andres.pizzeria.dto.CustomerUpdateDto;
import com.andres.pizzeria.dto.OrderResponseDto;
import com.andres.pizzeria.service.CustomerService;
import com.andres.pizzeria.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Consulta, alta, actualizacion y baja de clientes. Requiere estar autenticado con rol ADMIN, EMPLOYEE o CUSTOMER.")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @Autowired
    public CustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @Operation(summary = "Listar todos los clientes")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAll(){
        return ResponseEntity.ok(this.customerService.getAll());
    }

    @Operation(summary = "Obtener un cliente por id", description = "Devuelve 404 si no existe un cliente con ese id.")
    @GetMapping("/{idCustomer}")
    public ResponseEntity<CustomerResponseDto> get(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.customerService.get(idCustomer));
    }

    @Operation(summary = "Buscar cliente por telefono", description = "Devuelve 404 si ningun cliente tiene ese numero de telefono.")
    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerResponseDto> findByPhone(@PathVariable String phone){

        return ResponseEntity.ok(this.customerService.findByPhone(phone));
    }

    @Operation(summary = "Listar los pedidos de un cliente", description = "Solo accesible para ADMIN (ver OrderService.getCustomersOrders).")
    @GetMapping("/{idCustomer}/orders")
    public ResponseEntity<List<OrderResponseDto>> getCustomerOrders(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.orderService.getCustomersOrders(idCustomer));
    }

    @Operation(summary = "Crear un cliente", description = "El id del cliente lo asigna quien llama (no es autogenerado).")
    @PostMapping
    public ResponseEntity<CustomerResponseDto> add(@Valid @RequestBody CustomerCreateDto customer){
        return ResponseEntity.ok(this.customerService.save(customer));
    }

    @Operation(summary = "Actualizar un cliente", description = "Actualizacion parcial: solo se aplican los campos no nulos del body. 404 si el id no existe.")
    @PutMapping("/{idCustomer}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable String idCustomer, @RequestBody CustomerUpdateDto customerDto){
        return ResponseEntity.ok(this.customerService.update(idCustomer, customerDto));
    }

    @Operation(summary = "Eliminar un cliente", description = "Devuelve el cliente eliminado, o 404 si el id no existe.")
    @DeleteMapping("/{idCustomer}")
    public ResponseEntity<CustomerResponseDto> delete(@PathVariable String idCustomer){
        return ResponseEntity.ok(this.customerService.delete(idCustomer));
    }
}