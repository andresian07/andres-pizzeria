package com.andres.pizzeria.service;

import com.andres.pizzeria.dto.OrderResponseDto;
import com.andres.pizzeria.dto.PizzaCreateDto;
import com.andres.pizzeria.dto.PizzaResponseDto;
import com.andres.pizzeria.dto.PizzaUpdateDto;
import com.andres.pizzeria.dto.PizzaUpdatePriceDto;
import com.andres.pizzeria.persistence.entity.OrderEntity;
import com.andres.pizzeria.persistence.entity.PizzaEntity;
import com.andres.pizzeria.persistence.repository.PizzaPagSortRepository;
import com.andres.pizzeria.persistence.repository.PizzaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {
    private final PizzaRepository pizzaRepository;
    private final PizzaPagSortRepository pizzaPagSortRepository;

    public PizzaService(PizzaRepository pizzaRepository, PizzaPagSortRepository pizzaPagSortRepository) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaPagSortRepository = pizzaPagSortRepository;
    }

    public Page<PizzaResponseDto> getAll(int page, int elements){
        Pageable pageRequest = PageRequest.of(page, elements);
        Page<PizzaEntity> pizzaEntities = this.pizzaPagSortRepository.findAll(pageRequest);
        Page<PizzaResponseDto> pizzaResponseDtos = pizzaEntities.map(pizza -> toResponseDto(pizza));
        return pizzaResponseDtos;
    }

    public PizzaResponseDto get(int idPizza){
        Optional<PizzaEntity> optionalPizza = this.pizzaRepository.findById(idPizza);
        if (optionalPizza.isPresent()) {
            PizzaEntity pizzaExistente = optionalPizza.get();
            return toResponseDto(pizzaExistente);
        }
        return null;
    }

    private PizzaResponseDto toResponseDto(PizzaEntity pizza) {
        return new PizzaResponseDto(
                pizza.getIdPizza(),
                pizza.getName(),
                pizza.getDescription(),
                pizza.getPrice(),
                pizza.getVegetarian(),
                pizza.getVegan(),
                pizza.getAvailable()
        );
    }

    public PizzaResponseDto save(PizzaCreateDto pizzaDto){
        PizzaEntity pizza = new PizzaEntity();
        pizza.setName(pizzaDto.name());
        pizza.setDescription(pizzaDto.description());
        pizza.setPrice(pizzaDto.price());
        pizza.setVegetarian(pizzaDto.vegetarian());
        pizza.setVegan(pizzaDto.vegan());
        pizza.setAvailable(pizzaDto.available());

        PizzaEntity pizzaGuardada = this.pizzaRepository.save(pizza);
        return toResponseDto(pizzaGuardada);
    }

    public PizzaResponseDto update(int idPizza, PizzaUpdateDto pizzaDto) {
        // 1. Buscamos la pizza en la base de datos (findById devuelve un Optional)
        Optional<PizzaEntity> optionalPizza = this.pizzaRepository.findById(idPizza);

        if (optionalPizza.isPresent()) {
            // 2. Extraemos la entidad real que estaba guardada
            PizzaEntity pizzaExistente = optionalPizza.get();

            // 3. Le aplicamos los cambios parciales del DTO (validando que no vengan nulos)
            if (pizzaDto.price() != null) {
                pizzaExistente.setPrice(pizzaDto.price());
            }
            if (pizzaDto.available() != null) {
                pizzaExistente.setAvailable(pizzaDto.available());
            }

            // 4. Guardamos la entidad actualizada y la devolvemos
            return toResponseDto(this.pizzaRepository.save(pizzaExistente));
        }

        // 5. ¿Qué pasa si el ID no existe? Devolvemos null (o una excepción)
        return null;
    }

    public PizzaResponseDto delete(int idPizza){
        Optional<PizzaEntity> optionalPizza = this.pizzaRepository.findById(idPizza);
        if (optionalPizza.isPresent()) {
            PizzaEntity pizzaABorrar = optionalPizza.get(); // 1. Extraemos la entidad real
            this.pizzaRepository.delete(pizzaABorrar);     // 2. La borramos (no lleva return)
            return toResponseDto(pizzaABorrar);             // 3. Devolvemos la pizza que borramos
        }

        return null; // Si no existía, devolvemos null
    }

    public Page<PizzaResponseDto> getAvailable(int page, int elements, String sortBy, String sortDirection){
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageRequest = PageRequest.of(page, elements, Sort.by(sortBy));
        Page<PizzaEntity> pizzaEntities = this.pizzaPagSortRepository.findByAvailableTrue(pageRequest);
        return pizzaEntities.map(pizza -> toResponseDto(pizza));
    }

    public List<PizzaResponseDto> getTop3CheapestDesc(){
        // La consulta trae las 3 mas baratas en orden ascendente; la BD no puede
        // invertir "despues" de haber elegido cuales son las 3 mas baratas.
        List<PizzaEntity> cheapest = this.pizzaRepository.findTop3ByAvailableTrueOrderByPrice();
        Collections.reverse(cheapest);
        return cheapest.stream().map(pizza -> toResponseDto(pizza)).toList();
    }

    public PizzaResponseDto getByName(String name){
        PizzaEntity pizza = this.pizzaRepository.findAllByAvailableTrueAndNameIgnoreCase(name);
        return pizza != null ? toResponseDto(pizza) : null;
    }

    public List<PizzaResponseDto> getByIngredient(String ingredient){
        List<PizzaEntity> pizzas = this.pizzaRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCase(ingredient);
        return pizzas.stream().map(pizza -> toResponseDto(pizza)).toList();
    }

    public List<PizzaResponseDto> getByNotIngredient(String ingredient){
        List<PizzaEntity> pizzas = this.pizzaRepository.findAllByAvailableTrueAndDescriptionNotContainingIgnoreCase(ingredient);
        return pizzas.stream().map(pizza -> toResponseDto(pizza)).toList();
    }

    public Optional<PizzaResponseDto> getFirstByIngredient(String ingredient){
        Optional<PizzaEntity> pizza = this.pizzaRepository.findFirstByAvailableTrueAndDescriptionContainingIgnoreCaseOrderByPrice(ingredient);
        return pizza.map(p -> toResponseDto(p));
    }

    public List<PizzaResponseDto> getTop3ByIngredient(String ingredient){
        List<PizzaEntity> pizzas = this.pizzaRepository.findTop3ByAvailableTrueAndDescriptionContainingIgnoreCaseOrderByPrice(ingredient);
        return pizzas.stream().map(pizza -> toResponseDto(pizza)).toList();
    }

    public List<PizzaResponseDto> getByVegan(){
        List<PizzaEntity> pizzas = this.pizzaRepository.findAllByAvailableTrueAndVeganTrue();
        return pizzas.stream().map(pizza -> toResponseDto(pizza)).toList();
    }

    public int getByVeganQuantity(){
        return this.pizzaRepository.countByAvailableTrueAndVeganTrue();
    }

    public boolean updatePrice(PizzaUpdatePriceDto dto){
        int filasActualizadas = this.pizzaRepository.updatePrice(dto);
        return filasActualizadas > 0;
    }

}
