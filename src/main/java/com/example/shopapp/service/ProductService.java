package com.example.shopapp.service;

import com.example.shopapp.DTO.ProductDTO;
import com.example.shopapp.config.model.Product;
import com.example.shopapp.config.model.Comment;
import com.example.shopapp.config.model.ProductQuantity;
import com.example.shopapp.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByProductNameContainingIgnoreCase(query);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<ProductDTO> getAllProductDTOs() {
        List<Product> products = getAllProducts();
        return products.stream()
                .map(product -> {
                    int reviewCount = product.getComments() != null ? product.getComments().size() : 0;
                    float averageRating = calculateAverageRating(product);
                    return new ProductDTO(
                            product.getId(),
                            product.getProductName(),
                            averageRating, // Dynamically calculated rating
                            product.getPrice(),
                            product.getImage(),
                            product.getCutPrice(),
                            reviewCount
                    );
                })
                .collect(Collectors.toList());
    }

    public float calculateAverageRating(Product product) {
        if (product.getComments() == null || product.getComments().isEmpty()) {
            return 0.0f; // No reviews, default to 0.0 rating
        }

        float sum = 0.0f;
        for (Comment comment : product.getComments()) {
            sum += comment.getRating(); // Access the rating field directly
        }

        return sum / product.getComments().size(); // Calculate average
    }

    public List<Product> updateProductsQuantity(List<Map<String, String>> productsData) {
        Map<Long, String> productsToUpdate = new HashMap<>();

        // Grupowanie danych według id produktu
        for (Map<String, String> data : productsData) {
            Long id = Long.parseLong(data.get("id"));
            String quantityType = data.get("quantityType");
            productsToUpdate.put(id, quantityType);
        }

        // Pobieranie produktów z bazy
        List<Product> products = productRepository.findAllById(productsToUpdate.keySet());

        if (products.size() != productsToUpdate.size()) {
            throw new RuntimeException("Niektóre produkty nie zostały znalezione");
        }

        // Aktualizacja ilości dla każdego produktu
        for (Product product : products) {
            String quantityType = productsToUpdate.get(product.getId());
            try {
                ProductQuantity newQuantity = ProductQuantity.valueOf(quantityType);
                product.setQuantityType(newQuantity);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Nieprawidłowy typ ilości dla produktu o id " + product.getId());
            }
        }

        // Zapisanie produktów
        productRepository.saveAll(products);

        // Pobranie i zwrócenie produktów w posortowanej kolejności
        return productRepository.findAllById(productsToUpdate.keySet())
                .stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    public List<ProductDTO> filterProducts(List<Long> categoryIds, List<String> producers, String searchQuery, Double minPrice, Double maxPrice) {
        System.out.println("Filtering with:");
        System.out.println("categoryIds: " + categoryIds);
        System.out.println("producers: " + producers);
        System.out.println("searchQuery: " + searchQuery);
        System.out.println("minPrice: " + minPrice);
        System.out.println("maxPrice: " + maxPrice);

        // Obsługa pustych list
        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }
        if (producers != null && producers.isEmpty()) {
            producers = null;
        }

        List<Product> products = productRepository.filterProducts(
                categoryIds,
                producers,
                searchQuery != null && !searchQuery.isEmpty() ? searchQuery.toLowerCase() : null,
                minPrice,
                maxPrice
        );

        return products.stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getProductName(),
                        calculateAverageRating(product),
                        product.getPrice(),
                        product.getImage(),
                        product.getCutPrice(),
                        product.getComments() != null ? product.getComments().size() : 0
                ))
                .collect(Collectors.toList());
    }

    public List<String> getProducersByFilters(List<Long> categoryIds, String searchQuery) {
        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }
        return productRepository.findProducersByFilters(
                categoryIds,
                searchQuery != null && !searchQuery.isEmpty() ? searchQuery.toLowerCase() : null
        );
    }

}
