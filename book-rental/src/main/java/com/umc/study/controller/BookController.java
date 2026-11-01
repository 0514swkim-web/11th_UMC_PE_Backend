package com.umc.study.controller;

import com.umc.study.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController // 데이터를 JSON으로 응답하는 API 카운터
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // GET http://localhost:8080/books
    @GetMapping
    public List<Map<String, Object>> getBooks() {
        return bookService.getAllBooks();
    }
}
