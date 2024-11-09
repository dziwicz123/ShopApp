package com.example.shopapp.controller;

import com.example.shopapp.config.model.Basket;
import com.example.shopapp.config.model.User;
import com.example.shopapp.repo.BasketRepository;
import com.example.shopapp.repo.ProductRepository;
import com.example.shopapp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<?> createBasket(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        Basket basket = new Basket();
        basket.setUser(user);
        basket.setState(false);
        basket.setTotalPrice(0.0f);
        basketRepository.save(basket);

        return ResponseEntity.ok(basket);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Basket> getUserNewBasket(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        Basket newBasket = basketRepository.findByUserAndState(user, false);

        if (newBasket == null) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(newBasket);
    }


}
