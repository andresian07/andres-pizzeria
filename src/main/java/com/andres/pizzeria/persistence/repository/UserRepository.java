package com.andres.pizzeria.persistence.repository;

import com.andres.pizzeria.persistence.entity.UserEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
}
