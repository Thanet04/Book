package com.example.Books.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Books.DTO.ChangePassword;
import com.example.Books.DTO.Register;
import com.example.Books.DTO.UserDTO;
import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.User;
import com.example.Books.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtility jwtUtility;

    // ข้อมูล user จาก token
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(new UserDTO(user.getEmail(), user.getUsername(), user.getFullname())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(
            @RequestHeader("Authorization") String token,
            @RequestBody Register payload) {
        Long userId = getUserIdFromToken(token);
        return userService.updateUser(userId, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePassword request) {
        Long userId = getUserIdFromToken(token);
        boolean changed = userService.changePassword(userId, request);
        if (changed) return ResponseEntity.ok("Password changed successfully");
        return ResponseEntity.badRequest().body("Old password is incorrect");
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        boolean deleted = userService.deleteUser(userId);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody User request) {
        boolean success = userService.resetPassword(request.getEmail(), request.getPassword());
        if (success) return ResponseEntity.ok("Password reset successfully");
        return ResponseEntity.badRequest().body("Email not found");
    }

    // ฟังก์ชันช่วยแปลง token เป็น userId
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        if (jwtUtility.isTokenExpired(jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token หมดอายุ กรุณาเข้าสู่ระบบใหม่");
        }
        return jwtUtility.extractUserId(jwt);
    }
    
}
