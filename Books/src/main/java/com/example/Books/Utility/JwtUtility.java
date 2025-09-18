package com.example.Books.Utility;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.Books.bean.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtUtility {

    private final SecretKey SECRET_KEY;
    private final long EXPIRATION = 1000 * 60 * 60; // 1 ชั่วโมง

    public JwtUtility(@Value("${jwt.secret}") String secret) {
        // คาดว่า secret จะถูกเก็บใน application.properties หรือ env variable
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("id", user.getId()) 
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
