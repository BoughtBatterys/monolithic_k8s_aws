package com.example.monolithic.order.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolithic.order.dao.OrderRepository;
import com.example.monolithic.order.domain.dto.OrderRequestDTO;
import com.example.monolithic.order.domain.dto.OrderResponseDTO;
import com.example.monolithic.order.domain.entity.Order;
import com.example.monolithic.product.dao.ProductRepository;
import com.example.monolithic.product.domain.entity.Product;
import com.example.monolithic.user.dao.UserRepository;
import com.example.monolithic.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    
    public OrderResponseDTO orderCreate(OrderRequestDTO request) {
        System.out.println(">>>> order service orderCreate");
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        System.out.println(">>>> auth getName : "+ auth.getName());
        User user = userRepository.findById(auth.getName())
                        .orElseThrow(()-> new RuntimeException("잘못된 사용자입니다."));

        Product product = productRepository.findById(request.getProductId())
                            .orElseThrow(()-> new RuntimeException("Product Not Found"));
    
        System.out.println(">>>> order service 재고관리!!!!");

        // MSA에서는 이러한 것을 보통 비동기 방식으로 처리함
        // 현재는 모놀리틱 방식이니까 동기방식으로 돌아가고 있는 것임(순차적으로 코드가 실행되고 있음)
        // 현재 모놀리틱이니까 간단하게 가능하지만... MSA에서는 어떻게 이걸 구현할거냐
        // 비동기 방식 구현 힘들수도??
        Integer qty = request.getQty();
        System.out.println("요청 수량 : " + qty);
        if(product.getStockQty() < qty){
            throw new RuntimeException(">>>> 재고부족");
        }else{
            product.updateStockQty(qty);
        }
        Order order = request.toEntity(product, user);
        
        return OrderResponseDTO.fromEntity(orderRepository.save(order));


    }
}
