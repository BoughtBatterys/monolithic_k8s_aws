package com.example.monolithic.user.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolithic.user.dao.UserRepository;
import com.example.monolithic.user.domain.dto.UserRequestDTO;
import com.example.monolithic.user.domain.dto.UserResponseDTO;
import com.example.monolithic.user.domain.entity.User;
import com.example.monolithic.user.provider.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository    userRepository;
    private final PasswordEncoder   passwordEncoder;
    private final JwtProvider jwtProvider;

    //redis
    @Qualifier("tokenRedis")
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long REFRESH_TOEKN_TTL = 60 * 60 * 24 * 7; //7일

    public Map<String, Object> signIn(UserRequestDTO request){
        User entity = userRepository.findById(request.getEmail())
                            .orElseThrow(()-> new RuntimeException("Not Found"));
        
        if(!passwordEncoder.matches(request.getPassword(), entity.getPassword())){
            throw new RuntimeException("로그인에 실패하였습니다.");
        }

        Map<String, Object> map = new HashMap<>();
        
        System.out.println(">>>> 2. blog/user service 토큰 생성");
        String at = jwtProvider.createAT(entity.getEmail());
        String rt = jwtProvider.createRT(entity.getEmail());

        System.out.println(">>>> 3. blog/user service RT토큰 Redis에 저장");
        System.out.println(">>>> RefreshTokenService save token");
        redisTemplate.opsForValue()
            .set("RT" + entity.getEmail(), rt, REFRESH_TOEKN_TTL, TimeUnit.SECONDS);
        // expire date는 REFRESH_TOEKN_TTL이고, 이의 단위는 초단위

        map.put("response", UserResponseDTO.fromEntity(entity));
        map.put("access", at);
        map.put("refresh", rt);
        return map;
    }

    // public void logout(String accessToken){
    //     System.out.println(">>>> user service logout");
    //     String email = jwtProvider.getUserEmailFromToken(accessToken);
    //     refreshTokenService.deleteToken(email);
    // }

}
