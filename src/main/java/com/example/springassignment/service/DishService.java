package com.example.springassignment.service;

import com.example.springassignment.dto.CreateDishRequest;
import com.example.springassignment.dto.DishResponse;
import com.example.springassignment.dto.UpdateDishRequest;
import com.example.springassignment.entity.Category;
import com.example.springassignment.entity.Dish;
import com.example.springassignment.entity.DishStatus;
import com.example.springassignment.repository.DishRepository;
import com.example.springassignment.repository.specification.DishSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public Page<DishResponse> search(
            String keyword,
            DishStatus status,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        Page<Dish> page = dishRepository.findAll(
                DishSpecifications.withFilters(keyword, status, categoryId, minPrice, maxPrice),
                pageable
        );
        return page.map(DishResponse::from);
    }

    @Transactional(readOnly = true)
    public DishResponse getById(String id) {
        Dish dish = dishRepository.findById(id)
                .filter(dish -> dish.getStatus() != DishStatus.DELETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        return DishResponse.from(dish);
    }

    @Transactional
    public DishResponse create(CreateDishRequest request) {
        Category category = categoryService.getById(request.categoryId());
        Dish dish = Dish.builder()
                .name(request.name())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .price(request.price())
                .category(category)
                .build();
        Dish saved = dishRepository.save(dish);
        return DishResponse.from(saved);
    }

    @Transactional
    public DishResponse update(String id, UpdateDishRequest request) {
        Dish existing = dishRepository.findById(id)
                .filter(dish -> dish.getStatus() != DishStatus.DELETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        if (existing.getStatus() == DishStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot update a deleted dish");
        }
        if (request.status() == DishStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use delete endpoint to remove dishes");
        }
        Category category = categoryService.getById(request.categoryId());
        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setImageUrl(request.imageUrl());
        existing.setPrice(request.price());
        existing.setCategory(category);
        existing.setStatus(request.status());
        Dish saved = dishRepository.save(existing);
        return DishResponse.from(saved);
    }

    @Transactional
    public void softDelete(String id) {
        Dish existing = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        if (existing.getStatus() == DishStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dish already deleted");
        }
        existing.setStatus(DishStatus.DELETED);
        dishRepository.save(existing);
    }
}
