package com.example.shopapp.repo;

import com.example.shopapp.config.model.Basket;
import com.example.shopapp.config.model.OrderDetails;
import com.example.shopapp.config.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    OrderDetails findByBasket(Basket basket);
    List<OrderDetails> findByBasketUser(User user);
}
