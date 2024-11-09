package com.example.shopapp.repo;

import com.example.shopapp.config.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
