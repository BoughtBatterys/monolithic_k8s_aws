package com.example.monolithic.product.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolithic.product.dao.ProductRepository;
import com.example.monolithic.product.domain.dto.ProductRequestDTO;
import com.example.monolithic.product.domain.dto.ProductResponseDTO;
import com.example.monolithic.product.domain.entity.Product;
import com.example.monolithic.user.dao.UserRepository;
import com.example.monolithic.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductResponseDTO productCreate(ProductRequestDTO request){
        System.out.println(">>>> Product Service productCreate");
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        System.out.println(">>>> auth getName : "+ auth.getName());
        User user = userRepository.findById(auth.getName())
                        .orElseThrow(()-> new RuntimeException("잘못된 사용자입니다."));
        Product product = productRepository.save(request.toEntity(user));
            
        return ProductResponseDTO.fromEntity(product);
    }
}
