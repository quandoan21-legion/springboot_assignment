package com.example.springassignment.dto;

import com.example.springassignment.entity.Category;
import com.example.springassignment.entity.Dish;
import com.example.springassignment.entity.DishStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record DishResponse(
        String id,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        Instant startDate,
        Instant lastModifiedDate,
        DishStatus status,
        CategoryResponse category
) {
    public static DishResponse from(Dish dish) {
        Category category = dish.getCategory();
        CategoryResponse categoryResponse = category == null
                ? null
                : new CategoryResponse(category.getId(), category.getName());
        return new DishResponse(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getImageUrl(),
                dish.getPrice(),
                dish.getStartDate(),
                dish.getLastModifiedDate(),
                dish.getStatus(),
                categoryResponse
        );
    }
}
