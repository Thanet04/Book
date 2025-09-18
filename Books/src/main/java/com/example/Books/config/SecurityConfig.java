package com.example.Books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())       // ปิด CSRF สำหรับ REST API
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());  // อนุญาตทุก request
        return http.build();
    }

}
