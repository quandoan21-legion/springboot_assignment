package com.example.springassignment.controller;

import com.example.springassignment.dto.CreateDishRequest;
import com.example.springassignment.dto.DishResponse;
import com.example.springassignment.dto.PagedDishResponse;
import com.example.springassignment.dto.UpdateDishRequest;
import com.example.springassignment.entity.DishStatus;
import com.example.springassignment.service.DishService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Validated
public class DishController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "price", "startDate");

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<PagedDishResponse> getDishes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "ON_SALE") DishStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @Positive(message = "minPrice must be greater than 0") BigDecimal minPrice,
            @RequestParam(required = false) @Positive(message = "maxPrice must be greater than 0") BigDecimal maxPrice
    ) {
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 1");
        }
        if (limit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be >= 1");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortBy must be one of " + ALLOWED_SORT_FIELDS);
        }
        Sort.Direction direction = switch (sortDir.toLowerCase()) {
            case "asc" -> Sort.Direction.ASC;
            case "desc" -> Sort.Direction.DESC;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortDir must be asc or desc");
        };
        if (status == DishStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status cannot be DELETED");
        }
        if (minPrice != null && maxPrice != null && maxPrice.compareTo(minPrice) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxPrice must be greater than or equal to minPrice");
        }

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));
        Page<DishResponse> result = dishService.search(keyword, status, categoryId, minPrice, maxPrice, pageable);

        PagedDishResponse response = new PagedDishResponse(
                result.getContent(),
                page,
                limit,
                result.getTotalPages(),
                result.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishResponse> getDish(@PathVariable String id) {
        DishResponse dish = dishService.getById(id);
        return ResponseEntity.ok(dish);
    }

    @PostMapping
    public ResponseEntity<DishResponse> createDish(@Valid @RequestBody CreateDishRequest request) {
        DishResponse dish = dishService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dish);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishResponse> updateDish(
            @PathVariable String id,
            @Valid @RequestBody UpdateDishRequest request
    ) {
        DishResponse dish = dishService.update(id, request);
        return ResponseEntity.ok(dish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable String id) {
        dishService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
