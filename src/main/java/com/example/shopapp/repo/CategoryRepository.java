package com.example.shopapp.repo;

import com.example.shopapp.config.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
