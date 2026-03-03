package com.example.monolithic.product.domain.dto;

import com.example.monolithic.product.domain.entity.Product;

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
public class ProductResponseDTO {
    private long id;
    private String name;
    private int price;
    private int stockQty;


    public static ProductResponseDTO fromEntity(Product entity){
        return ProductResponseDTO.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .price(entity.getPrice())
                    .stockQty(entity.getStockQty())
                    .build();
    }    
}
