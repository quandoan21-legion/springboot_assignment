package com.example.springassignment.controller;

import com.example.springassignment.dto.CategoryResponse;
import com.example.springassignment.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categories = categoryService.findAll().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .toList();
        return ResponseEntity.ok(categories);
    }
}
