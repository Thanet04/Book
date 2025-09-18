package com.example.Books.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Books.bean.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // ค้นหาหนังสือตามชื่อ (ใช้ Native SQL)
    @Query(value = "select * from books b where LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<Book> searchByTitle(@Param("title") String title);

    // ค้นหาหนังสือตามผู้แต่ง (ใช้ Native SQL)
    @Query(value = "select * from books b where LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))", nativeQuery = true)
    List<Book> searchByAuthor(@Param("author") String author);

    // ค้นหาหนังสือที่มีคำอธิบาย (JPQL)
    @Query("select b from Book b where b.description IS NOT NULL")
    List<Book> findBooksWithDescription();
}
