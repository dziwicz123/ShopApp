package com.example.shopapp.repo;

import com.example.shopapp.config.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ShopRepository extends JpaRepository<Shop, Long>{

}
