package com.example.Books.bean;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @Column
    private String price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // เชื่อมกับ User
}