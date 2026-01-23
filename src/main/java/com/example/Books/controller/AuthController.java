package com.example.Books.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.example.Books.DTO.Auth;
import com.example.Books.DTO.AuthResponse;
import com.example.Books.DTO.Register;
import com.example.Books.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
public ResponseEntity<Map<String, String>> register(@RequestBody Register request) {
    try {
        authService.register(request);
        return ResponseEntity.ok(
            Map.of("message", "สมัครสมาชิกสำเร็จ")
        );
    } catch (RuntimeException e) {

        if ("USERNAME_EXISTS".equals(e.getMessage())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT) 
                .body(Map.of("message", "ชื่อผู้ใช้นี้ถูกใช้แล้ว"));
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("message", e.getMessage()));
    }
}


    @PostMapping("/login")
    public AuthResponse login(@RequestBody Auth request){
        return authService.authenticate((request));
    }
}
