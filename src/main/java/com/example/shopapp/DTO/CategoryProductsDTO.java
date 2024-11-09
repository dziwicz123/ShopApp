package com.example.shopapp.DTO;

import com.example.shopapp.config.model.Product;
import java.util.List;

public class CategoryProductsDTO {
    private String categoryName;
    private List<Product> products;

    // Getters and setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
