package com.example.shopapp.config.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private float rating;

    private float price;

    private String image;

    private String producer;

    @Enumerated(EnumType.STRING)
    private ProductQuantity quantityType;

    @Column(name = "cut_price")
    private Float cutPrice;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<Comment> comments;
}
