package com.example.shopapp.repo;

import com.example.shopapp.config.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Product p WHERE "
            + "(:categoryIds IS NULL OR p.category.id IN :categoryIds) AND "
            + "(:producers IS NULL OR p.producer IN :producers) AND "
            + "(:searchQuery IS NULL OR LOWER(p.productName) LIKE %:searchQuery%) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> filterProducts(@Param("categoryIds") List<Long> categoryIds,
                                 @Param("producers") List<String> producers,
                                 @Param("searchQuery") String searchQuery,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice);



    @Query("SELECT DISTINCT p.producer FROM Product p WHERE p.producer IS NOT NULL")
    List<String> findAllProducers();

    @Query("SELECT DISTINCT p.producer FROM Product p WHERE "
            + "(:categoryIds IS NULL OR p.category.id IN :categoryIds) AND "
            + "(:searchQuery IS NULL OR LOWER(p.productName) LIKE %:searchQuery%) AND "
            + "p.producer IS NOT NULL")
    List<String> findProducersByFilters(@Param("categoryIds") List<Long> categoryIds,
                                        @Param("searchQuery") String searchQuery);

}
