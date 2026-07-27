package com.andres.pizzeria.service;

import com.andres.pizzeria.dto.CustomerUpdateDto;
import com.andres.pizzeria.persistence.entity.CustomerEntity;
import com.andres.pizzeria.persistence.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerEntity> getAll(){
        return this.customerRepository.findAll();
    }

    public CustomerEntity get(String idCustomer){
        return this.customerRepository.findById(idCustomer).orElse(null);
    }

    public CustomerEntity save(CustomerEntity customer){
        return this.customerRepository.save(customer);
    }

    public CustomerEntity update(String idCustomer, CustomerUpdateDto customerDto) {
        Optional<CustomerEntity> optionalCustomer = this.customerRepository.findById(idCustomer);

        if (optionalCustomer.isPresent()) {
            CustomerEntity customerExistente = optionalCustomer.get();

            if (customerDto.name() != null) {
                customerExistente.setName(customerDto.name());
            }
            if (customerDto.address() != null) {
                customerExistente.setAddress(customerDto.address());
            }
            if (customerDto.email() != null) {
                customerExistente.setEmail(customerDto.email());
            }
            if (customerDto.phoneNumber() != null) {
                customerExistente.setPhoneNumber(customerDto.phoneNumber());
            }

            return this.customerRepository.save(customerExistente);
        }

        return null;
    }

    public CustomerEntity delete(String idCustomer){
        Optional<CustomerEntity> optionalCustomer = this.customerRepository.findById(idCustomer);
        if (optionalCustomer.isPresent()) {
            CustomerEntity customerABorrar = optionalCustomer.get();
            this.customerRepository.delete(customerABorrar);
            return customerABorrar;
        }

        return null;
    }

    public CustomerEntity findByPhone(String phone){
        return this.customerRepository.findByPhone(phone);
    }

}