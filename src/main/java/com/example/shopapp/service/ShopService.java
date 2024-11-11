package com.example.shopapp.service;

import com.example.shopapp.config.model.Shop;
import com.example.shopapp.repo.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    // Retrieve all shops
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    // Retrieve a shop by ID
    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }

    // Add a new shop
    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    // Update an existing shop
    public Shop updateShop(Long id, Shop shopDetails) {
        return shopRepository.findById(id).map(shop -> {
            shop.setStreet(shopDetails.getStreet());
            shop.setCity(shopDetails.getCity());
            shop.setPostalCode(shopDetails.getPostalCode());
            shop.setLatitude(shopDetails.getLatitude());
            shop.setLongitude(shopDetails.getLongitude());
            return shopRepository.save(shop);
        }).orElseThrow(() -> new RuntimeException("Shop not found with id " + id));
    }

    // Delete a shop by ID
    public void deleteShop(Long id) {
        if (shopRepository.existsById(id)) {
            shopRepository.deleteById(id);
        } else {
            throw new RuntimeException("Shop not found with id " + id);
        }
    }
}
