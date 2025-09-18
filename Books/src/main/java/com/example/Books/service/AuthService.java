package com.example.Books.service;

import org.springframework.stereotype.Service;

import com.example.Books.DTO.Auth;
import com.example.Books.DTO.AuthResponse;
import com.example.Books.DTO.Register;
import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.User;
import com.example.Books.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository UserRepository;
    private final JwtUtility jwtutil;

    public void register(Register request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        UserRepository.save(user);
    }

    public AuthResponse authenticate(Auth request){
        User user = UserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("user not found"));
    
        if(!request.getPassword().equals(user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }
    
        String token = jwtutil.generateToken(user);
        return new AuthResponse(token);
    }    
    
}
