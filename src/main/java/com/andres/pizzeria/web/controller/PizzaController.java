package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.PizzaCreateDto;
import com.andres.pizzeria.dto.PizzaResponseDto;
import com.andres.pizzeria.dto.PizzaUpdateDto;
import com.andres.pizzeria.dto.PizzaUpdatePriceDto;
import com.andres.pizzeria.service.PizzaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pizzas")
public class PizzaController {
    private final PizzaService pizzaService;

    @Autowired
    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public ResponseEntity<Page<PizzaResponseDto>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8" ) int element){
        return ResponseEntity.ok(this.pizzaService.getAll(page, element));
    }

    @GetMapping("/{idPizza}")
    public ResponseEntity<PizzaResponseDto> get(@PathVariable int idPizza){
        return ResponseEntity.ok(this.pizzaService.get(idPizza));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<PizzaResponseDto>> getAvailable(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "8") int elements,@RequestParam(defaultValue = "price") String sortBy,@RequestParam(defaultValue = "DESC")String sortDirection){
        return ResponseEntity.ok(this.pizzaService.getAvailable(page, elements, sortBy, sortDirection));
    }

    @GetMapping("/cheapest/top3desc")
    public ResponseEntity<List<PizzaResponseDto>> getTop3CheapestDesc(){
        return ResponseEntity.ok(this.pizzaService.getTop3CheapestDesc());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PizzaResponseDto> getByName(@PathVariable String name){
        return ResponseEntity.ok(this.pizzaService.getByName(name));
    }

    @GetMapping("/ingredient/{ingredient}")
    public ResponseEntity<List<PizzaResponseDto>> getByIngredient(@PathVariable String ingredient){
        return ResponseEntity.ok(this.pizzaService.getByIngredient(ingredient));
    }

    @GetMapping("/withoutingredient/{ingredient}")
    public ResponseEntity<List<PizzaResponseDto>> getByNotIngredient(@PathVariable String ingredient){
        return ResponseEntity.ok(this.pizzaService.getByNotIngredient(ingredient));
    }

    @GetMapping("/ingredient/{ingredient}/first")
    public ResponseEntity<PizzaResponseDto> getFirstByIngredient(@PathVariable String ingredient){
        return this.pizzaService.getFirstByIngredient(ingredient)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ingredient/{ingredient}/top3")
    public ResponseEntity<List<PizzaResponseDto>> getTop3ByIngredient(@PathVariable String ingredient){
        return ResponseEntity.ok(this.pizzaService.getTop3ByIngredient(ingredient));
    }

    @GetMapping("/vegan")
    public ResponseEntity<List<PizzaResponseDto>> getByVegan(){
        return ResponseEntity.ok(this.pizzaService.getByVegan());
    }

    @GetMapping("/veganquantity")
    public ResponseEntity<Integer> getByVeganQuantity(){
        return ResponseEntity.ok(this.pizzaService.getByVeganQuantity());
    }



    @PostMapping
    public ResponseEntity<PizzaResponseDto> add(@Valid @RequestBody PizzaCreateDto pizza){
        return ResponseEntity.ok(this.pizzaService.save(pizza));
    }




    @PutMapping("/{idPizza}")
    public ResponseEntity<PizzaResponseDto> update(@PathVariable int idPizza,@RequestBody PizzaUpdateDto pizzaDto){
        PizzaResponseDto updatePizza = this.pizzaService.update(idPizza, pizzaDto);
        return ResponseEntity.ok(updatePizza);

    }

    @PutMapping("/updateprice/{idPizza}")
    public ResponseEntity<Void> updatePrice(@PathVariable int idPizza, @RequestBody PizzaUpdatePriceDto pizzaDto){
        pizzaDto.setIdPizza(idPizza);
        boolean actualizado = this.pizzaService.updatePrice(pizzaDto);
        if(actualizado){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idPizza}")
    public ResponseEntity<PizzaResponseDto> delete(@PathVariable int idPizza){
        PizzaResponseDto deletePizza = this.pizzaService.delete(idPizza);
        return ResponseEntity.ok(deletePizza);

    }
}
