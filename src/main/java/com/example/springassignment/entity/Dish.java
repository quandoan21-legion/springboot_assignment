package com.example.springassignment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dishes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Dish {

    @Id
    @Column(length = 32, nullable = false, updatable = false)
    private String id;               // Ví dụ "MN001"

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 1024)
    private String imageUrl;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, updatable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DishStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (id == null || id.isBlank()) {
            id = generateDishId();
        }
        if (startDate == null) {
            startDate = now;
        }
        lastModifiedDate = now;
        if (status == null) {
            status = DishStatus.ON_SALE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = Instant.now();
    }

    private String generateDishId() {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "MN" + random;
    }
}
