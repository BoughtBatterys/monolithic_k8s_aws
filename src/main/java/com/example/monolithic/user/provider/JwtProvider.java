package com.example.monolithic.user.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// 토큰 생성
@Component
public class JwtProvider{
    // 내가 서명할 키가 필요
    // Spring이 있는 것으로 선택하자
    @Value("${jwt.secret}") // .env 파일의 내용을 yml에서 사용하게 되고 이를 java에서 사용하는 연계
    private String secret;

    private final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 30; // 단위: ms -> 30분
    private final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 단위: ms -> 7일 
    //협업에서는 일반적으로 RT는 14일

    // Key는 Security로 골라줘라
    // 토큰에 서명하는 메서드
    private Key getStringKey(){
        System.out.println(">>>> Provider jwt secret : " + secret);
        // 전달된 바이트 배열을 기반으로 SecretKey 객체 생성
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    // access token provider
    public String createAT(String email){
        System.out.println(">>>> Provider CreateAT : " + email);
        return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                    .signWith(getStringKey()) // 서명(도장) 찍은 것
                    .compact() ;
    }
    // refresh token provider
    public String createRT(String email){
        System.out.println(">>>> Provider CreateRT : " + email);
        return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                    .signWith(getStringKey())
                    .compact() ;// 위 설정들을 바탕으로 최종 JWT 문자열을 생성
    }

    // token에서 subject 추출(user의 email을 가져오는?)
    // Bearar xxxxx 
    // token으로부터 누가 사용자인지 꺼내는 것(이를 security context에 담아놓으면 사용자 상태 관리가 가능)
    public String getUserEmailFromToken(String token){
        System.out.println(">>>> Provider getUserEmailFromToken : " + token);
        if(token.startsWith("Bearer ")){ // 만약 시작이 Bearer로 시작했다면...
            token = token.substring(7); // bearar 를 잘라버리라는 것(그래야 토큰 값)
        }
        Claims claims = Jwts.parser() // JWT 파서 객체 생성
                            .setSigningKey(getStringKey()) // 토큰을 검증할 때 사용할 비밀키 지정(이거 내(서버)가 만든건지 확인)
                            .parseClaimsJws(token) //토큰을 파싱하고, 서명이 올바른지 검증(서명이 맞지 않으면 예외 발생)
                            .getBody();//Payload 안의 subject값을 꺼냄(이는 주로 email 등을 저장)
                            
        return claims.getSubject();
    }

    // AT 만료일 확인
    public long getATE(){
        return ACCESS_TOKEN_EXPIRY;
    }
    //RT 만료일 확인
    public long getRTE(){
        return REFRESH_TOKEN_EXPIRY;
    }
}
