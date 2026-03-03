package com.example.monolithic.user.domain.entity;


import com.example.monolithic.common.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="MONOLITHIC_USER_TBL")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    private String email;

    @Column(unique= true, nullable = false, length = 200)
    private String password;

    private String name;

    private String role; // 역할에 따른 인가의 범위가 다름  
    
}
