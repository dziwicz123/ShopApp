package com.example.shopapp.repo;

import com.example.shopapp.config.model.Basket;
import com.example.shopapp.config.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    OrderDetails findByBasket(Basket basket);
}
