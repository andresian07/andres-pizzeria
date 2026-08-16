package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.PizzaCreateDto;
import com.andres.pizzeria.dto.PizzaResponseDto;
import com.andres.pizzeria.dto.PizzaUpdateDto;
import com.andres.pizzeria.dto.PizzaUpdatePriceDto;
import com.andres.pizzeria.service.PizzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pizzas")
@Tag(name = "Pizzas", description = "Consulta, alta, actualizacion y baja de pizzas del menu. Las lecturas (GET) son publicas; escribir requiere estar autenticado, y borrar requiere rol ADMIN.")
public class PizzaController {
    private final PizzaService pizzaService;

    @Autowired
    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @Operation(summary = "Listar pizzas paginadas", description = "Devuelve todas las pizzas (disponibles o no) paginadas.")
    @GetMapping
    public ResponseEntity<Page<PizzaResponseDto>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8" ) int element){
        return ResponseEntity.ok(this.pizzaService.getAll(page, element));
    }

    @Operation(summary = "Obtener una pizza por id", description = "Devuelve 404 si no existe una pizza con ese id.")
    @GetMapping("/{idPizza}")
    public ResponseEntity<PizzaResponseDto> get(@PathVariable int idPizza){
        return ResponseEntity.ok(this.pizzaService.get(idPizza));
    }

    @Operation(summary = "Listar pizzas disponibles", description = "Devuelve solo las pizzas marcadas como disponibles, paginadas y ordenables por el campo indicado.")
    @GetMapping("/available")
    public ResponseEntity<Page<PizzaResponseDto>> getAvailable(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "8") int elements,@RequestParam(defaultValue = "price") String sortBy,@RequestParam(defaultValue = "DESC")String sortDirection){
        return ResponseEntity.ok(this.pizzaService.getAvailable(page, elements, sortBy, sortDirection));
    }

    @Operation(summary = "Top 3 pizzas mas baratas", description = "Devuelve las 3 pizzas disponibles mas baratas, ordenadas de mayor a menor precio.")
    @GetMapping("/cheapest/top3desc")
    public ResponseEntity<List<PizzaResponseDto>> getTop3CheapestDesc(){
        return ResponseEntity.ok(this.pizzaService.getTop3CheapestDesc());
    }

    @Operation(summary = "Buscar pizza por nombre exacto", description = "Busqueda case-insensitive por nombre exacto entre las pizzas disponibles. Devuelve 404 si no hay match.")
    @GetMapping("/name/{name}")
    public ResponseEntity<PizzaResponseDto> getByName(@PathVariable String name){
        return ResponseEntity.ok(this.pizzaService.getByName(name));
    }

    @Operation(summary = "Buscar pizzas por ingrediente", description = "Devuelve las pizzas disponibles cuya descripcion contiene el texto indicado (case-insensitive).")
    @GetMapping("/ingredient/{ingredient}")
    public ResponseEntity<List<PizzaResponseDto>> getByIngredient(@PathVariable String ingredient){
        return ResponseEntity.ok(this.pizzaService.getByIngredient(ingredient));
    }

    @Operation(summary = "Buscar pizzas sin un ingrediente", description = "Devuelve las pizzas disponibles cuya descripcion NO contiene el texto indicado (case-insensitive).")
    @GetMapping("/withoutingredient/{ingredient}")
    public ResponseEntity<List<PizzaResponseDto>> getByNotIngredient(@PathVariable String ingredient){
        return ResponseEntity.ok(this.pizzaService.getByNotIngredient(ingredient));
    }

    @Operation(summary = "Pizza mas barata con un ingrediente", description = "Entre las pizzas disponibles que contienen el ingrediente indicado, devuelve la mas barata. 404 si no hay ninguna.")
    @GetMapping("/ingredient/{ingredient}/first")
    public ResponseEntity<PizzaResponseDto> getFirstByIngredient(@PathVariable String ingredient){
        return this.pizzaService.getFirstByIngredient(ingredient)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Top 3 pizzas mas baratas con un ingrediente", description = "Entre las pizzas disponibles que contienen el ingrediente indicado, devuelve las 3 mas baratas.")
    @GetMapping("/ingredient/{ingredient}/top3")
    public ResponseEntity<List<PizzaResponseDto>> getTop3ByIngredient(@PathVariable String ingredient){
        return ResponseEntity.ok(this.pizzaService.getTop3ByIngredient(ingredient));
    }

    @Operation(summary = "Listar pizzas veganas", description = "Devuelve todas las pizzas disponibles marcadas como veganas.")
    @GetMapping("/vegan")
    public ResponseEntity<List<PizzaResponseDto>> getByVegan(){
        return ResponseEntity.ok(this.pizzaService.getByVegan());
    }

    @Operation(summary = "Cantidad de pizzas veganas", description = "Devuelve el numero de pizzas disponibles marcadas como veganas.")
    @GetMapping("/veganquantity")
    public ResponseEntity<Integer> getByVeganQuantity(){
        return ResponseEntity.ok(this.pizzaService.getByVeganQuantity());
    }



    @Operation(summary = "Crear una pizza", description = "Requiere estar autenticado. Valida los datos de entrada (nombre, descripcion, precio, etc.) con Bean Validation.")
    @PostMapping
    public ResponseEntity<PizzaResponseDto> add(@Valid @RequestBody PizzaCreateDto pizza){
        return ResponseEntity.ok(this.pizzaService.save(pizza));
    }




    @Operation(summary = "Actualizar una pizza", description = "Actualizacion parcial: solo se aplican los campos no nulos del body. Requiere estar autenticado. 404 si el id no existe.")
    @PutMapping("/{idPizza}")
    public ResponseEntity<PizzaResponseDto> update(@PathVariable int idPizza,@RequestBody PizzaUpdateDto pizzaDto){
        PizzaResponseDto updatePizza = this.pizzaService.update(idPizza, pizzaDto);
        return ResponseEntity.ok(updatePizza);

    }

    @Operation(summary = "Actualizar solo el precio de una pizza", description = "Update masivo via query nativa. Devuelve 204 si actualizo, 404 si el id no existe.")
    @PutMapping("/updateprice/{idPizza}")
    public ResponseEntity<Void> updatePrice(@PathVariable int idPizza, @RequestBody PizzaUpdatePriceDto pizzaDto){
        pizzaDto.setIdPizza(idPizza);
        boolean actualizado = this.pizzaService.updatePrice(pizzaDto);
        if(actualizado){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una pizza", description = "Requiere rol ADMIN. Devuelve la pizza eliminada, o 404 si el id no existe.")
    @DeleteMapping("/{idPizza}")
    public ResponseEntity<PizzaResponseDto> delete(@PathVariable int idPizza){
        PizzaResponseDto deletePizza = this.pizzaService.delete(idPizza);
        return ResponseEntity.ok(deletePizza);

    }
}
