package com.example.Books.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Books.bean.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId); //  order ของ user เฉพาะ
}
