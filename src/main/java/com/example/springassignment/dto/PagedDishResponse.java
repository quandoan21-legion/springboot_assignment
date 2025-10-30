package com.example.springassignment.dto;

import java.util.List;

public record PagedDishResponse(
        List<DishResponse> content,
        int page,
        int limit,
        int totalPages,
        long totalElements
) {
}
