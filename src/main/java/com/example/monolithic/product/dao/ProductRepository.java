package com.example.monolithic.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.monolithic.product.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}