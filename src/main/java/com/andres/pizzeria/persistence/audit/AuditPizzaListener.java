package com.andres.pizzeria.persistence.audit;

import com.andres.pizzeria.persistence.entity.PizzaEntity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.springframework.util.SerializationUtils;

public class AuditPizzaListener {

    @PostLoad
    public void postLoad(PizzaEntity entity){
        System.out.println("POST LOAD");
        entity.setSnapshot(SerializationUtils.clone(entity));
    }

    @PostPersist
    @PostUpdate
    public void onPosPersist(PizzaEntity entity){
        System.out.println("POST PERSIST OR UPDATE");
        System.out.println("OLD VALUE: " + entity.getSnapshot());
        System.out.println("NEW VALUE: " + entity);
    }

    @PreRemove
    public void onPreDelete(PizzaEntity entity){
        System.out.println("PRE DELETE: " + entity);

    }

}
