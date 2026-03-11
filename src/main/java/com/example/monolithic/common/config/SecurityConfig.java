package com.example.monolithic.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.monolithic.common.auth.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration  // pre-loading
@RequiredArgsConstructor // 근데 이게 lazy-loading이면 문제가 생길수도
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 평문으로 들어오는 password를 BCrypt 알고리즘으로 해싱해주는 코드
        // 스프링 시큐리티가 기본적으로 사용하고 있는 알고리즘 방식이 BCrypt
        return new BCryptPasswordEncoder();
    }

    //CORS 설정(pre-flight에 대한 설정)
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){ // 네이밍 룰 안지키면 및에 defualt로 하면 못불러옴 반환타입이랑 메소드 이름이랑 일치하게(첫 글자는 소문자지만 ㅇㅇ)
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET" ,"POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 가장 중요한 메서드 설정 filter chain, cors configuration
    // 시큐리티가 filter까지 같이 연계해서 관리해야 하니까...
    // 실제 환경 설정이 이루어지는 부분
    // Cors에 대한 문제, 권한에 대한 문제
    // 원래는 필터는 서버가 동작하면 바로 동작했었는데, 아래에서 세팅을 하고 돌아가도록 하는 것
    // 이렇게 안하면 Security가 Filter를 관리할 수 없음
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{ // 말그대로 filter 가 순차적으로 실행되는 연쇄
        http.csrf(csrf -> csrf.disable()) // Cross-Site Request Forgery(사이트 위변조)를 diable해준 것, REST API 기반에서는 이걸 걸어준다.
            .cors(Customizer.withDefaults()) // 위에 CorsConfigurationSource 메서드(네이밍 룰을 일치하게 만든 메서드)를 자동으로 가져오겠다는 것 아니면 람다 함수로 불러와라
            .authorizeHttpRequests(auth -> auth 
                .requestMatchers( // String들만 받으므로 주의해라, 또한 버전에 따라서 문법들이 달라진다.                    
                    "/users/signin",
                    "/health/alive"
                ).permitAll() // 해당 end pointe들은 Token 없이도 접근 가능하게 해주는 것(무조건 허용)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // pre-flight
                //.requestMatchers("").authenticated() // 나중에 인증해야만 동작하는 end point 등록할 예정
                .requestMatchers("/adamin").hasRole("ADMIN") // 사용자 role이 ADMIN이여야만 /admin이라는 end point에 접근 가능
                .anyRequest().authenticated() // 실수라도 놓쳤던 엔드포인트들은 무조건 인증이 필요한 end point로 설정
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // session을 사용하는 곳이라면 무력화 하는 것

        // filterChain들이 jwtAuthenticationFilter보다 먼저 실행되어야 함
        // 즉, jwtAuthenticationFilter실행 전에 UsernamePasswordAuthenticationFilter가 먼저 실행되게 해줌
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    
}
