package com.example.monolithic.user.dao;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monolithic.user.domain.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findByEmailAndPassword(String email, String password);
    
}