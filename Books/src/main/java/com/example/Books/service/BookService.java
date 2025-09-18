package com.example.Books.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Books.bean.Book;
import com.example.Books.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public BookService(BookRepository bookRepository, Cloudinary cloudinary) {
        this.bookRepository = bookRepository;
        this.cloudinary = cloudinary;
    }

    // ดึงข้อมูลหนังสือทั้งหมด
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // ดึงข้อมูลหนังสือตาม ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // บันทึกหนังสือใหม่ + อัปโหลดรูป
    public Book saveBookWithImage(Book book, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "books")
            );
            String imageUrl = (String) uploadResult.get("secure_url");
            book.setImageUrl(imageUrl);
        }
        return bookRepository.save(book);
    }

    // อัปเดตข้อมูลหนังสือ + เปลี่ยนรูปใหม่ได้
    public Book updateBookWithImage(Long id, Book bookDetails, MultipartFile file) throws IOException {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setDescription(bookDetails.getDescription());

            try {
                if (file != null && !file.isEmpty()) {
                    Map uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap("folder", "books")
                    );
                    String imageUrl = (String) uploadResult.get("secure_url");
                    book.setImageUrl(imageUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("Upload failed", e);
            }

            return bookRepository.save(book);
        }).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    // ลบหนังสือ
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ค้นหาหนังสือตามชื่อ
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.searchByTitle(title);
    }

    // ค้นหาหนังสือตามผู้แต่ง
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.searchByAuthor(author);
    }
}
