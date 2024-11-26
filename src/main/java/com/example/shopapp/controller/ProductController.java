package com.example.shopapp.controller;

import com.example.shopapp.DTO.ProductDTO;
import com.example.shopapp.config.model.Product;
import com.example.shopapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dto")
    public List<ProductDTO> getAllProductDTOs() {
        return productService.getAllProductDTOs();
    }

    @GetMapping("/filter")
    public List<ProductDTO> filterProducts(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<String> producers,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        System.out.println("Received parameters:");
        System.out.println("categoryIds: " + categoryIds);
        System.out.println("producers: " + producers);
        System.out.println("searchQuery: " + searchQuery);
        System.out.println("minPrice: " + minPrice);
        System.out.println("maxPrice: " + maxPrice);

        return productService.filterProducts(categoryIds, producers, searchQuery, minPrice, maxPrice);
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<ProductDTO> getProductDTOById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDTO productDTO = new ProductDTO(
                product.getId(),
                product.getProductName(),
                productService.calculateAverageRating(product),
                product.getPrice(),
                product.getImage(),
                product.getCutPrice(),
                product.getComments() != null ? product.getComments().size() : 0
        );
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/producers")
    public ResponseEntity<List<String>> getProducersByFilters(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String searchQuery
    ) {
        System.out.println("Received parameters for producers:");
        System.out.println("categoryIds: " + categoryIds);
        System.out.println("searchQuery: " + searchQuery);

        List<String> producers = productService.getProducersByFilters(categoryIds, searchQuery);
        return ResponseEntity.ok(producers);
    }

}
