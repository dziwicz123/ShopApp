package com.example.shopapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO {
    private Long id;
    private String street;
    private String city;
    private String postalCode;
    private Double latitude;
    private Double longitude;
}
