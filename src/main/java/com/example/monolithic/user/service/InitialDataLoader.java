package com.example.monolithic.user.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.monolithic.user.dao.UserRepository;
import com.example.monolithic.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class InitialDataLoader implements CommandLineRunner{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    //서버 기동이 끝난 시점에서 동시에 실행됨..., 보통 데이터 초기화에 사용    
    public void run(String... args) throws Exception {// ...args는 가변길이 문자열 변수
        System.out.println(">>>> initialDataLoader run called.....");
        if(userRepository.findById("admin@naver.com").isPresent()){
            return ;
        }
        User user = User.builder()
            .email("admin@naver.com")
            .password(passwordEncoder.encode("123456789"))
            .name("admin")
            .role("ADMIN")
            .build();

        userRepository.save(user);
    }

}
