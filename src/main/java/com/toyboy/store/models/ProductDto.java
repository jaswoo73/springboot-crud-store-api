package com.toyboy.store.models;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class ProductDto {

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Brand is required")
    private String brand;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private double price;

    @NotEmpty(message = "Category is required")
    private String category;

    @Size(min = 10, message = "Description must be at least 10 characters long")
    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    private String description;

    private MultipartFile imageFile;

    public @NotEmpty(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "Name is required") String name) {
        this.name = name;
    }

    public @NotEmpty(message = "Brand is required") String getBrand() {
        return brand;
    }

    public void setBrand(@NotEmpty(message = "Brand is required") String brand) {
        this.brand = brand;
    }

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    public double getPrice() {
        return price;
    }

    public void setPrice(@Min(value = 0, message = "Price must be greater than or equal to 0") double price) {
        this.price = price;
    }

    public @NotEmpty(message = "Category is required") String getCategory() {
        return category;
    }

    public void setCategory(@NotEmpty(message = "Category is required") String category) {
        this.category = category;
    }

    public @Size(min = 10, message = "Description must be at least 10 characters long") @Size(max = 1000, message = "Description must be at most 1000 characters long") String getDescription() {
        return description;
    }

    public void setDescription(@Size(min = 10, message = "Description must be at least 10 characters long") @Size(max = 1000, message = "Description must be at most 1000 characters long") String description) {
        this.description = description;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
