package com.example.monolithic.common.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}") // .env 파일의 내용을 yml에서 사용하게 되고 이를 java에서 사용하는 연계
    private String secret;
    private Key key;
   
    @PostConstruct 
    private void init(){
        System.out.println(">>>> Provider jwt secret : " + secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain chain) throws ServletException, IOException {

        System.out.println(">>>> JwtAuthenticationFilter doFilterInterval");

        String endPoint = request.getRequestURI();
        System.out.println(">>> JwtAuthenticationFilter User EndPoint : " + endPoint);
        String method = request.getMethod();
        System.out.println(">>> JwtAuthenticationFilter User Method : " + method);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {//HTTP 요청 메서드가 OPTIONS인지 체크해서 CORS 허가 헤더 붙여줌
            // response.setStatus(HttpServletResponse.SC_OK);
            // response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            // response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            // response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            // response.setHeader("Access-Control-Allow-Credentials", "true");

            chain.doFilter(request, response); // Controller로 연결해준다.
            return ;
        }

        String authHeader = request.getHeader("Authorization");
        // 만약 token이 있다면 Bearer가 있을 것임(우리가 Token 보낼 때 Auth 맨 앞에 Bearer 넣어서 보낼거니까)
        System.out.println(">>>> JwtFilter authHeader : " + authHeader);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){// authHeader가 없거나, 시작이 Bearer가 아닌 경우
            System.out.println(">>>> JwtFilter Not Authorization");
            chain.doFilter(request, response); //어차피 모든 동작 확인을 지금 filterchain들에서 하고 있으니...
            return ; // reject라고 생각해라, 토큰이 없거나 Bearer가 아닌 경우 reject 하겠다는 것
        }
        String token = authHeader.substring(7);
        System.out.println(">>>> JwtFilter token : " + token);
        System.out.println(">>>> jwtFilter token vlidation check");
        try{
            // Claims == JWT 데이터 -> Payload 부분에 들어있는 정보
            // 다만, Payload 전체가 Claims이고, Claims는 그 안의 개별 데이터 항목
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
            // Jwts 만들 때 Subject에 email(사용자)를 저장했었음(사용자의 기본 정보가 될 수도 있음)
            String email = claims.getSubject();
            System.out.println(">>>> JwtAuthenticationFilter claims get email : " + email);

            // JwtProvider에 의해서 Role 입력된 경우에만 해당
            String role = claims.get("role", String.class);
            System.out.println(">>>> JwtAuthenticationFilter claims get role : " + role);

            // spring Security 인증정보를 담는 객체(Principal, Credential, Authorities) - 누구의 요청인지 
            // UserNamePasswordAuthenticationToken  이걸로 사용자 정보(ex email)들을 감싸고
            // 이 객체를 SecurityContextHolder에 심는다.
            // principal : 주체(사용자)가 누구냐?, Credential: 비밀번호 같이 기밀성이 필요한 것, authorities: 역할??
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    role != null ? 
                        java.util.List.of(()-> "ROLE_" + role) :
                        java.util.List.of()
                );

                // 사용자의 요청과 인증정보객체를 연결
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Spring SecurityContext 저장 -> ctrl에서 필요할 때 꺼낼 수 있음
                // 사용자의 상태 정보를 확인할 수 있다는 것임(get 사용하면 된다)
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(Exception e){
            e.printStackTrace();
        }

        chain.doFilter(request, response);// 위의 try문에서 예외가 발생하지 않아서 여기까지 왔을 때 실행

    }

}
