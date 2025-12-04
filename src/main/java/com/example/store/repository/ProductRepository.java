package com.example.store.repository;

import com.example.store.model.Product;
import com.example.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller(User seller);
    List<Product> findBySellerId(Long sellerId);  // ADD THIS LINE
}