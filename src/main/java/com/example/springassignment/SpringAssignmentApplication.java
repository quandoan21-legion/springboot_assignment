package com.example.springassignment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Restaurant Dish Management API",
                version = "1.0",
                description = "REST API for managing restaurant dishes and categories"
        )
)
@SpringBootApplication
public class SpringAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAssignmentApplication.class, args);
    }
}
