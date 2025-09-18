package com.example.Books.DTO;

import lombok.Data;

@Data
public class OrderDTO {
    private String title;
    private int quantity;
    private double price;
}
