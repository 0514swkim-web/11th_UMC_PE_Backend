package com.umc.study;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    void 도서_전체_조회() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title").value("달빛 도서관"));
    }

    @Test
    void 도서_등록_후_목록에_추가된다() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"categoryId": 1, "title": "클린 코드", "description": "애자일 소프트웨어 장인 정신"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("도서 등록이 완료되었습니다!"));

        mockMvc.perform(get("/books"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].title").value("클린 코드"));
    }

    @Test
    void 카테고리별_도서_조회() throws Exception {
        mockMvc.perform(get("/books/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].category_id", org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is(1))));
    }

    @Test
    @Disabled("H2에는 MySQL의 DATE_ADD 함수가 없어서 실행 불가 → MySQL + Postman으로 확인")
    void 대여_기록_생성시_반납_예정일은_7일_뒤() throws Exception {
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 1, "bookId": 3}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("대여 기록이 생성되었습니다!"));

        Long days = jdbcTemplate.queryForObject(
                "SELECT DATEDIFF(due_at, rented_at) FROM rental WHERE user_id = 1 AND book_id = 3", Long.class);
        assertThat(days).isEqualTo(7L);
    }

    @Test
    void 반납_처리와_없는_대여_기록() throws Exception {
        mockMvc.perform(patch("/rentals/1/return"))
                .andExpect(status().isOk());
        Object returnedAt = jdbcTemplate.queryForObject(
                "SELECT returned_at FROM rental WHERE rental_id = 1", Object.class);
        assertThat(returnedAt).isNotNull();

        mockMvc.perform(patch("/rentals/999/return"))
                .andExpect(status().isNotFound());
    }
}
