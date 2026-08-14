package com.andres.pizzeria.service;

import com.andres.pizzeria.dto.*;
import com.andres.pizzeria.persistence.entity.CustomerEntity;
import com.andres.pizzeria.persistence.entity.OrderEntity;
import com.andres.pizzeria.persistence.entity.PizzaEntity;
import com.andres.pizzeria.persistence.repository.CustomerRepository;
import com.andres.pizzeria.web.exception.NotFoundException;
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

    public List<CustomerResponseDto> getAll(){
        List<CustomerEntity> customers = this.customerRepository.findAll();
        List<CustomerResponseDto> allCustomers = customers.stream().map(customer -> toResponseDto(customer)).toList();
        return allCustomers;

    }

    public CustomerResponseDto get(String idCustomer){
        Optional<CustomerEntity> optionalCustomer = this.customerRepository.findById(idCustomer);
        CustomerEntity customerExistente = optionalCustomer.orElseThrow(() -> new NotFoundException("Cliente no encontrado con id " + idCustomer));
        return toResponseDto(customerExistente);

    }

    public CustomerResponseDto save(CustomerCreateDto customerDto){
        CustomerEntity customer = new CustomerEntity();
        customer.setIdCustomer(customerDto.idCustomer());
        customer.setName(customerDto.name());
        customer.setAddress(customerDto.address());
        customer.setEmail(customerDto.email());
        customer.setPhoneNumber(customerDto.phoneNumber());

        CustomerEntity customerGuardado = this.customerRepository.save(customer);
        return toResponseDto(customerGuardado);
    }

    private CustomerResponseDto toResponseDto(CustomerEntity customer) {
        return new CustomerResponseDto(
               customer.getIdCustomer(),
               customer.getName(),
               customer.getAddress(),
               customer.getEmail(),
               customer.getPhoneNumber()
        );
    }


    public CustomerResponseDto update(String idCustomer, CustomerUpdateDto customerDto) {
        Optional<CustomerEntity> optionalCustomer = this.customerRepository.findById(idCustomer);

        CustomerEntity customerExistente = optionalCustomer.orElseThrow(() -> new NotFoundException("Cliente no encontrado con id " + idCustomer));
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

        return toResponseDto(this.customerRepository.save(customerExistente));

    }

    public CustomerResponseDto delete(String idCustomer){
        Optional<CustomerEntity> optionalCustomer = this.customerRepository.findById(idCustomer);
        CustomerEntity customerABorrar = optionalCustomer.orElseThrow(() -> new NotFoundException("Cliente no encontrado con id " + idCustomer));
        this.customerRepository.delete(customerABorrar);
        return toResponseDto(customerABorrar);


    }

    public CustomerResponseDto findByPhone(String phone){
        CustomerEntity customerPhone = this.customerRepository.findByPhone(phone);
        if (customerPhone != null ){
            return toResponseDto(customerPhone);
        }
        throw new NotFoundException("Cliente no encontrado con teléfono " + phone);
    }

}