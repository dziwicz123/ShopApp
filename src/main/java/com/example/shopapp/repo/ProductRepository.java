package com.example.shopapp.repo;

import com.example.shopapp.config.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    Optional<Product> findById(Long id);

    @Query("SELECT MAX(p.id) FROM Product p")
    Optional<Long> findMaxId();
}
