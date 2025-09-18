package com.example.Books;

import com.example.Books.controller.BookController;
import com.example.Books.service.BookService;
import com.example.Books.Utility.JwtUtility;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = BookController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class // ปิด security สำหรับ test
)
@AutoConfigureMockMvc(addFilters = false) // ปิด filter ทั้งหมด (เช่น JWT filter)
class BooksApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService; // mock service layer

    @MockBean
    private JwtUtility jwtUtility; // mock JWT utility

    @Test
    void testGetAllBooks() throws Exception {
        // สมมติว่าไม่ต้องใช้ token จริง เพราะ security ถูก disable แล้ว
        mockMvc.perform(get("/api/books"))
               .andExpect(status().isOk());
    }
}
