package com.example.Books.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Books.DTO.ChangePassword;
import com.example.Books.DTO.Register;
import com.example.Books.bean.User;
import com.example.Books.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }    

    public Optional<User> updateUser(Long id, Register payload) {
        return userRepository.findById(id).map(user -> {
            if (payload.getEmail() != null && !payload.getEmail().isBlank()) {
                user.setEmail(payload.getEmail());
            }
            if (payload.getUsername() != null && !payload.getUsername().isBlank()) {
                user.setUsername(payload.getUsername());
            }
            return userRepository.save(user);
        });
    }       

    public boolean resetPassword(String email, String newPassword) {
        return userRepository.findByEmail(email).map(user -> {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
    

    public boolean changePassword(Long userId, ChangePassword request) {
        return userRepository.findById(userId).map(user -> {
            // ตรวจสอบรหัสเก่า (plaintext)
            if (!request.getCurrentPassword().equals(user.getPassword())) {
                return false;
            }

            // อัปเดตรหัสใหม่ (plaintext)
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
}


