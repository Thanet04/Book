package com.example.Books;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudinary.Cloudinary;
import com.example.Books.Utility.JwtUtility;
import com.example.Books.bean.Book;
import com.example.Books.controller.BookController;
import com.example.Books.repository.BookRepository;
import com.example.Books.service.UserService;

@WebMvcTest(BookController.class) // โหลดเฉพาะ controller
@AutoConfigureMockMvc(addFilters = false)
class BooksApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;
    
    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtility jwtUtility;

    @MockBean
    private Cloudinary cloudinary;

    @Test
    void testGetAllBooks() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setDescription("Desc");
        book.setPrice("100");
        book.setImageUrl(null);
        book.setUser(null);

        // ปรับพารามิเตอร์ให้ตรงกับ method ของ repository
        when(bookRepository.findAllBooks(1, 0)).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books?page=0&size=1"))
               .andExpect(status().isOk());
    }
}
