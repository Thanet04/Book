package com.example.Books.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Books.bean.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b where b.user.id = :userId")
    List<Book> findByUser(@Param("userId") Long userId);
    
    // ค้นหาหนังสือตามชื่อ (ใช้ Native SQL)
    @Query(value = "select * from books b where lower(b.title) like lower(concat('%', :title, '%')) order by id limit :limit offset :offset", nativeQuery = true)
    List<Book> searchByTitle(@Param("title") String title, @Param("limit") int limit, @Param("offset") int offset);
    
    @Query(value = "select count(*) from books b where lower(b.title) like lower(concat('%', :title, '%'))", nativeQuery = true)
    int countByTitle(@Param("title") String title);

    // ค้นหาหนังสือตามผู้แต่ง (ใช้ Native SQL)
    @Query(value = "select * from books b where lower(b.author) like lower(concat('%', :author, '%'))", nativeQuery = true)
    List<Book> searchByAuthor(@Param("author") String author);

    // ค้นหาหนังสือที่มีคำอธิบาย (JPQL)
    @Query("select b from Book b where b.description IS NOT NULL")
    List<Book> findBooksDescription();

    @Query(value = "select * from books order by id limit :limit offset :offset", nativeQuery = true)
    List<Book> findAllBooks(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "select * from books where user_id = :userId order by id limit :limit offset :offset", nativeQuery = true)
    List<Book> findByUser(@Param("userId") Long userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "select count(*) from books", nativeQuery = true)
    int countAllBooks();

    @Query(value = "select count(*) from books where user_id = :userId", nativeQuery = true)
    int countBooksByUser(@Param("userId") Long userId);

    @Query(value = "select * from books  where id = :id and user_id = :userId", nativeQuery = true)
    Optional<Book> findByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);


}
