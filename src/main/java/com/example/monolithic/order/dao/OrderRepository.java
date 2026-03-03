package com.example.monolithic.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monolithic.order.domain.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

    
}