package com.example.Books.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Books.DTO.ChangePassword;
import com.example.Books.DTO.Register;
import com.example.Books.DTO.UserDTO;
import com.example.Books.bean.User;
import com.example.Books.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserID(@PathVariable Long id){
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(
                        new UserDTO(user.getEmail(), user.getUsername())
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Register payload) {
        return userService.updateUser(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody User request) {
        boolean success = userService.resetPassword(request.getEmail(), request.getPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.badRequest().body("Email not found");
        }
    }
    

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePassword request) {

        boolean changed = userService.changePassword(id, request);

        if (changed) {
            return ResponseEntity.ok("Password changed successfully");
        }
        return ResponseEntity.badRequest().body("Old password is incorrect");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

}
