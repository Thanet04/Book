package com.example.Books.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Books.DTO.OrderDTO;
import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.Order;
import com.example.Books.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtility jwtUtility;

    //  userId จาก token
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        if (jwtUtility.isTokenExpired(jwt)) {
            throw new RuntimeException("Token expired");
        }
        return jwtUtility.extractUserId(jwt);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByUser(
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderDTO orderDTO) {
        Long userId = getUserIdFromToken(token);
        Order createdOrder = orderService.createOrderForUser(userId, orderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = getUserIdFromToken(token);
        boolean deleted = orderService.deleteOrderForUser(userId, orderId);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
