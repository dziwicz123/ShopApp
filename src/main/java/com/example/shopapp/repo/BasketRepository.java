package com.example.shopapp.repo;

import com.example.shopapp.config.model.Basket;
import com.example.shopapp.config.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Basket findByUserAndState(User user, Boolean state);
}
