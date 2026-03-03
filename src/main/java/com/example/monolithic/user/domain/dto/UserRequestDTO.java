package com.example.monolithic.user.domain.dto;

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
public class UserRequestDTO {
    // 추후 Spring validation 이용한 패턴 검증하기 때문에 email, password, name, role 이런식으로 작성 x
    private String email;
    private String password;


    public User toEntity(){
        return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .build();
    }
}

