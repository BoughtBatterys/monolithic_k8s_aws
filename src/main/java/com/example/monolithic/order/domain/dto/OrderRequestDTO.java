package com.example.monolithic.order.domain.dto;

import com.example.monolithic.order.domain.entity.Order;
import com.example.monolithic.product.domain.entity.Product;
import com.example.monolithic.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private long productId;
    private int qty;

    public Order toEntity(Product product, User user){
        return Order.builder( )
                    .qty(this.qty)
                    .product(product)
                    .user(user)
                    .build();
    }    
}
