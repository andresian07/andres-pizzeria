package com.andres.pizzeria.persistence.repository;


import com.andres.pizzeria.persistence.entity.UserRoleEntity;
import com.andres.pizzeria.persistence.entity.UserRoleId;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRoleRepository extends ListCrudRepository<UserRoleEntity, UserRoleId> {
    List<UserRoleEntity> findByUsername(String username);
}
