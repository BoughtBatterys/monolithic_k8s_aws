package com.example.monolithic.product.domain.dto;

import com.example.monolithic.product.domain.entity.Product;
import com.example.monolithic.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter // 보통 그냥 리퀘스트 바디로 받지 않고 DTO로 받는 경우 Setter가 있어야 값을 바인딩함
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private String name;
    private int price;
    private int stockQty;
    
    public Product toEntity(User user){
        return Product.builder()
                    .name(this.name)
                    .price(this.price)
                    .stockQty(this.stockQty)
                    .user(user)
                    .build();
    }
}
