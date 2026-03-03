package com.example.monolithic.user.ctrl;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.monolithic.user.domain.dto.UserRequestDTO;
import com.example.monolithic.user.domain.dto.UserResponseDTO;
import com.example.monolithic.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody UserRequestDTO request){
        System.out.println(">>>> blog / user ctrl path : /signin");
        System.out.println(">>>> params : " + request);

        Map<String, Object> map = userService.signIn(request);

        System.out.println(">>>> blog /response body : /login" + (UserResponseDTO)map.get("response"));
        System.out.println(">>>> blog /response at   : /login" + (String)map.get("access"));
        System.out.println(">>>> blog /response rt   : /login" + (String)map.get("refresh"));
        
        System.out.println(">>>> response body : "+ map.get("response"));
        HttpHeaders headers = new HttpHeaders();
        
        headers.add("Authorization", "Bearer "+(String)(map.get("access")) ) ;
        headers.add("Refresh-Token", (String)(map.get("refresh")) ) ;
        headers.add("Access-Control-Expose-Headers","Authorization, Refresh-Token");

        headers.add("Access-Controll", "Bearer " + (String)(map.get("access")));
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers) // header 심는 방식
                .body((String)(map.get("access")));
    }

}
