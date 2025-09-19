package com.example.Books.service;

import com.example.Books.bean.Order;
import com.example.Books.bean.User;
import com.example.Books.DTO.OrderDTO;
import com.example.Books.repository.OrderRepository;
import com.example.Books.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    //  Orders ของ user
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // สร้าง Order ของ user
    public Order createOrderForUser(Long userId, OrderDTO orderDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setTitle(orderDTO.getTitle());
        order.setQuantity(orderDTO.getQuantity());
        order.setPrice(orderDTO.getPrice());
        order.setUser(user);

        return orderRepository.save(order);
    }

    // อัปเดต Order ของ user
    public Optional<Order> updateOrderForUser(Long userId, Long orderId, OrderDTO orderDTO) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .map(order -> {
                    if (orderDTO.getTitle() != null) order.setTitle(orderDTO.getTitle());
                    order.setQuantity(orderDTO.getQuantity());
                    order.setPrice(orderDTO.getPrice());
                    return orderRepository.save(order);
                });
    }

    // ลบ Order ของ user
    public boolean deleteOrderForUser(Long userId, Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId)
                .filter(o -> o.getUser().getId().equals(userId));

        if (order.isPresent()) {
            orderRepository.delete(order.get());
            return true;
        }
        return false;
    }
}
