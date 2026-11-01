package com.umc.study;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Postman 대신 코드로 요청을 보내 응답을 확인하는 테스트 (테스트마다 DB 변경은 되돌림)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookRentalApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void 도서_전체_조회는_최신순_DTO() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].bookId").value(3))
                .andExpect(jsonPath("$[0].categoryName").value("과학"))
                .andExpect(jsonPath("$[0].isAvailable").value(true))
                // DB 컬럼명이 그대로 나가지 않음
                .andExpect(jsonPath("$[0].book_id").doesNotExist());
    }

    @Test
    void 도서_등록은_201() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"categoryId": 1, "title": "클린 코드", "description": "애자일 소프트웨어 장인 정신"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId", notNullValue()))
                .andExpect(jsonPath("$.categoryName").value("문학"));

        mockMvc.perform(get("/books"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].title").value("클린 코드"));
    }

    @Test
    void 빈_제목은_400() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"categoryId": 1, "title": " "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title은 비어 있을 수 없습니다."));
    }

    @Test
    void 없는_카테고리는_404() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"categoryId": 999, "title": "없는 카테고리 책"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 카테고리입니다."));
    }

    @Test
    void 카테고리별_도서_조회() throws Exception {
        mockMvc.perform(get("/books/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].categoryName", everyItem(is("문학"))));
    }

    @Test
    void 제목_키워드_검색() throws Exception {
        mockMvc.perform(get("/books").param("keyword", "우주"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("우주를 읽는 법"));
    }

    @Test
    void 대여_기록_생성시_반납_예정일은_7일_뒤() throws Exception {
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 1, "bookId": 3}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("우주를 읽는 법"))
                .andExpect(jsonPath("$.returnedAt").doesNotExist());

        Long days = jdbcTemplate.queryForObject(
                "SELECT DATEDIFF('DAY', rented_at, due_at) FROM rental WHERE user_id = 1 AND book_id = 3", Long.class);
        assertThat(days).isEqualTo(7L);
    }

    @Test
    void 없는_도서_대여는_404() throws Exception {
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 1, "bookId": 999}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void 반납_처리와_없는_대여_기록() throws Exception {
        mockMvc.perform(patch("/rentals/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedAt", notNullValue()));

        mockMvc.perform(patch("/rentals/999/return"))
                .andExpect(status().isNotFound());
    }
}
