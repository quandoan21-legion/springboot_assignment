package com.example.springassignment.repository;

import com.example.springassignment.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DishRepository extends JpaRepository<Dish, String>, JpaSpecificationExecutor<Dish> {
}