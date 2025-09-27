package com.example.Books.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String register(@RequestBody Register request){
        authService.register(request);
        return "User Register Succrssfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody Auth request){
        return authService.authenticate((request));
    }
}
