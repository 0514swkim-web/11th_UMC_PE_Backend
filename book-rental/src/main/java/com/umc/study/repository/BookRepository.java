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
}
