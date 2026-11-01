package com.umc.study.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository // 스프링 컨테이너에 "나 창고지기 부품이야!"라고 등록
@RequiredArgsConstructor
public class BookRepository {

    // 스프링이 application.yml을 읽어 미리 만들어 둔 DB 통신 도구
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM book";
        // Map의 Key는 컬럼명(title), Value는 실제 데이터(달빛 도서관)
        return jdbcTemplate.queryForList(sql);
    }

    public void save(Map<String, Object> body) {
        // book_id는 AUTO_INCREMENT라 생략, is_available은 기본 true
        String sql = "INSERT INTO book (category_id, title, description, is_available) VALUES (?, ?, ?, true)";

        // 문자열을 이어 붙이지 않고 ? 자리에 순서대로 바인딩 → SQL Injection 방지
        jdbcTemplate.update(
                sql,
                body.get("categoryId"),
                body.get("title"),
                body.get("description")
        );
    }
}
