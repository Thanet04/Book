package com.example.Books.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.example.Books.DTO.OrderDTO;
import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.Order;
import com.example.Books.bean.User;
import com.example.Books.repository.OrderRepository;
import com.example.Books.repository.UserRepository;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtility jwtUtility;

    //  userId จาก token
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        if (jwtUtility.isTokenExpired(jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token หมดอายุ กรุณาเข้าสู่ระบบใหม่");
        }
        return jwtUtility.extractUserId(jwt);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByUser(
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<Order> orders = orderRepository.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderDTO orderDTO) {
        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setTitle(orderDTO.getTitle());
        order.setQuantity(orderDTO.getQuantity());
        order.setPrice(orderDTO.getPrice());
        order.setUser(user);

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        Long userId = getUserIdFromToken(token);

        Optional<Order> optionalOrder = orderRepository.findById(orderId).filter(o -> o.getUser().getId().equals(userId));

        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        orderRepository.delete(optionalOrder.get());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body("ไม่มีคำสั่งซื้อสำหรับชำระเงิน");
        }

        double totalAmount = orders.stream().mapToDouble(o -> o.getPrice() * o.getQuantity()).sum();

        // mock: หลังจากชำระเงินสำเร็จ ลบคำสั่งซื้อทั้งหมดของ user
        orderRepository.deleteAll(orders);

        return ResponseEntity.ok("ชำระเงินสำเร็จ ยอดรวม: " + totalAmount + " บาท");
    }
}
