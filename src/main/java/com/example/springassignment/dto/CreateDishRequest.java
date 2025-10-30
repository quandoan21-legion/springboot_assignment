package com.example.springassignment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateDishRequest(
        @NotBlank(message = "name is required")
        @Size(min = 8, message = "name must be at least 8 characters long")
        String name,

        String description,

        String imageUrl,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.01", message = "price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "categoryId is required")
        Long categoryId
) {
}
