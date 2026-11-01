package com.umc.study.service;

import com.umc.study.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service // 비즈니스 로직을 수행하는 셰프 계층
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Map<String, Object>> getAllBooks() {
        // 지금은 가공 없이 창고지기가 가져온 목록을 그대로 반환
        return bookRepository.findAll();
    }
}
