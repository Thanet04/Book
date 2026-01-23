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

    private final UserRepository userRepository;
    private final JwtUtility jwtutil;

    public void register(Register request){
        String username = request.getUsername().toLowerCase().trim();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("USERNAME_EXISTS");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);
    }

    public AuthResponse authenticate(Auth request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("user not found"));
    
        if(!request.getPassword().equals(user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }
    
        String token = jwtutil.generateToken(user);
        return new AuthResponse(token);
    }    
    
}
