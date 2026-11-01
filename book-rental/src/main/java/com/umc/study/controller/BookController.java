package com.umc.study.controller;

import com.umc.study.dto.BookResponse;
import com.umc.study.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // GET http://localhost:8080/books (최신 등록순)
    @GetMapping
    public List<BookResponse> getBooks() {
        return bookService.getBooks();
    }

    // GET http://localhost:8080/books/category/1
    @GetMapping("/category/{categoryId}")
    public List<BookResponse> getBooksByCategory(@PathVariable Long categoryId) {
        return bookService.getBooksByCategory(categoryId);
    }
}
