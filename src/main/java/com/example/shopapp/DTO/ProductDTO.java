package com.example.shopapp.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;
    private float rating;
    private float price;
    private String image;
    private Float cutPrice;
    private int reviewCount;

    public ProductDTO(Long id, String productName, float rating, float price, String image, Float cutPrice, int reviewCount) {
        this.id = id;
        this.productName = productName;
        this.rating = rating;
        this.price = price;
        this.image = image;
        this.cutPrice = cutPrice;
        this.reviewCount = reviewCount;
    }
}
