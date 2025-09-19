package com.example.Books.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.Book;
import com.example.Books.service.BookService;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    
    @Autowired
    private BookService bookService;
    @Autowired
    private JwtUtility jwtUtility;
    
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        if (jwtUtility.isTokenExpired(jwt)) {
            throw new RuntimeException("Token expired");
        }
        return jwtUtility.extractUserId(jwt);
    }
    
    // ข้อมูลหนังสือทั้งหมด
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Book>> getBookByUser(@RequestHeader("Authorization") String token){
        Long userId = getUserIdFromToken(token);
        List<Book> books = bookService.getBookByUser(userId);
        return ResponseEntity.ok(books);
    }
    
    // ข้อมูลหนังสือตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // สร้างหนังสือใหม่
    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestPart("book") Book book,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        Book savedBook = bookService.saveBookWithImage(book, file);
        return ResponseEntity.ok(savedBook);
    }
    
    // อัปเดตข้อมูลหนังสือ
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestPart("book") Book book,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        Book updatedBook = bookService.updateBookWithImage(id, book, file);
        return ResponseEntity.ok(updatedBook);
    }
    
    // ลบหนังสือ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // ค้นหาหนังสือตามชื่อ
    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchBooksByTitle(@RequestParam String q) {
        List<Book> books = bookService.searchBooksByTitle(q);
        return ResponseEntity.ok(books);
    }
    
    // ค้นหาหนังสือตามผู้แต่ง
    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchBooksByAuthor(@RequestParam String q) {
        List<Book> books = bookService.searchBooksByAuthor(q);
        return ResponseEntity.ok(books);
    }
    
}