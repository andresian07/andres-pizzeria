package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.CustomerCreateDto;
import com.andres.pizzeria.dto.CustomerUpdateDto;
import com.andres.pizzeria.persistence.entity.CustomerEntity;
import com.andres.pizzeria.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerEntity>> getAll(){
        return ResponseEntity.ok(this.customerService.getAll());
    }

    @GetMapping("/{idCustomer}")
    public ResponseEntity<CustomerEntity> get(@PathVariable String idCustomer){
        CustomerEntity customer = this.customerService.get(idCustomer);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerEntity> findByPhone(@PathVariable String phone){

        return ResponseEntity.ok(this.customerService.findByPhone(phone));
    }

    @PostMapping
    public ResponseEntity<CustomerEntity> add(@Valid @RequestBody CustomerCreateDto customer){
        return ResponseEntity.ok(this.customerService.save(customer));
    }

    @PutMapping("/{idCustomer}")
    public ResponseEntity<CustomerEntity> update(@PathVariable String idCustomer, @RequestBody CustomerUpdateDto customerDto){
        CustomerEntity updateCustomer = this.customerService.update(idCustomer, customerDto);
        if (updateCustomer != null) {
            return ResponseEntity.ok(updateCustomer);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idCustomer}")
    public ResponseEntity<CustomerEntity> delete(@PathVariable String idCustomer){
        CustomerEntity deleteCustomer = this.customerService.delete(idCustomer);
        if (deleteCustomer != null) {
            return ResponseEntity.ok(deleteCustomer);
        }
        return ResponseEntity.notFound().build();
    }
}