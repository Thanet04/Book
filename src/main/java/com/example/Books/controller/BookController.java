package com.example.Books.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.Book;
import com.example.Books.bean.User;
import com.example.Books.repository.BookRepository;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private Cloudinary cloudinary;

    
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        if (jwtUtility.isTokenExpired(jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token หมดอายุ กรุณาเข้าสู่ระบบใหม่");
        }
        return jwtUtility.extractUserId(jwt);
    }
    
    // ข้อมูลหนังสือทั้งหมด
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam int page,
            @RequestParam int size) {

        int offset = page * size;
        List<Book> books = bookRepository.findAllBooks(size, offset);
        int total = bookRepository.countAllBooks();

        Map<String, Object> response = Map.of(
                "books", books,
                "total", total
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<List<Book>> getBookByUser(
            @RequestHeader("Authorization") String token,
            @RequestParam int page,
            @RequestParam int size) {

        Long userId = getUserIdFromToken(token);
        int offset = page * size;
        List<Book> books = bookRepository.findByUser(userId, size, offset);
        return ResponseEntity.ok(books);
    }

    // ข้อมูลหนังสือตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        Optional<Book> book = bookRepository.findByIdAndUser(id,userId);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // สร้างหนังสือใหม่
    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestHeader("Authorization") String token,
            @RequestPart("book") Book book,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        // ดึง userId จาก token
        Long userId = getUserIdFromToken(token);

        // สร้าง User object ชั่วคราวและเซ็ต id
        User user = new User();
        user.setId(userId);
        book.setUser(user);

        // อัปโหลดรูปถ้ามี
        if (file != null && !file.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "books")
            );
            book.setImageUrl((String) uploadResult.get("secure_url"));
        }

        // บันทึกหนังสือ
        Book saved = bookRepository.save(book);

        return ResponseEntity.ok(saved);
    }
    
    // อัปเดตข้อมูลหนังสือ
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestPart("book") Book bookDetails,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        Long userId = getUserIdFromToken(token);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setDescription(bookDetails.getDescription());
        book.setPrice(bookDetails.getPrice());

        if (file != null && !file.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "books")
            );
            book.setImageUrl((String) uploadResult.get("secure_url"));
        }

        Book updated = bookRepository.save(book);
        return ResponseEntity.ok(updated);
    }
 
    // ลบหนังสือ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        Long userId = getUserIdFromToken(token);
        Book book = bookRepository.findById(id).orElse(null);

        if (book == null) return ResponseEntity.notFound().build();
        if (!book.getId().equals(userId)) return ResponseEntity.status(403).build();

        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ค้นหาหนังสือตามชื่อ
    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchBooksByTitle(@RequestParam String q) {
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }
    
    // ค้นหาหนังสือตามผู้แต่ง
    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchBooksByAuthor(@RequestParam String q) {
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }
    
}