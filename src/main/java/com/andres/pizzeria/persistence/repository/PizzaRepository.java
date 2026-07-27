package com.andres.pizzeria.persistence.repository;


import com.andres.pizzeria.dto.PizzaUpdatePriceDto;
import com.andres.pizzeria.persistence.entity.PizzaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends ListCrudRepository<PizzaEntity, Integer>{
    List<PizzaEntity> findALLByAvailableTrueOrderByPrice();
    List<PizzaEntity> findTop3ByAvailableTrueOrderByPrice();
    PizzaEntity findAllByAvailableTrueAndNameIgnoreCase(String name);
    List<PizzaEntity> findAllByAvailableTrueAndDescriptionContainingIgnoreCase(String description);
    Optional<PizzaEntity> findFirstByAvailableTrueAndDescriptionContainingIgnoreCaseOrderByPrice(String description);
    List<PizzaEntity> findTop3ByAvailableTrueAndDescriptionContainingIgnoreCaseOrderByPrice(String description);
    List<PizzaEntity> findAllByAvailableTrueAndDescriptionNotContainingIgnoreCase(String ingredient);
    List<PizzaEntity> findAllByAvailableTrueAndVeganTrue();
    int countByAvailableTrueAndVeganTrue();

    @Modifying
    @Transactional
    @Query("UPDATE PizzaEntity p SET p.price = :#{#newPizzaPrice.newPrice}, p.lastModifiedDate = CURRENT_TIMESTAMP WHERE p.idPizza = :#{#newPizzaPrice.idPizza}")
    int updatePrice(@Param("newPizzaPrice") PizzaUpdatePriceDto newPizzaPrice);
}
