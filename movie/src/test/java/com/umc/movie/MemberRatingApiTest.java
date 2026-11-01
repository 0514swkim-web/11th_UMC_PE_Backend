package com.umc.movie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Postman 대신 코드로 요청을 보내 응답을 확인하는 테스트 (조회만 하므로 데이터 변경 없음)
@SpringBootTest
@AutoConfigureMockMvc
class MemberRatingApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 이미_있는_닉네임은_사용_불가() throws Exception {
        mockMvc.perform(get("/members/nickname/{nickname}", "수현"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void 없는_닉네임은_사용_가능() throws Exception {
        mockMvc.perform(get("/members/nickname/{nickname}", "처음보는닉네임"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void 이메일_중복_체크() throws Exception {
        mockMvc.perform(get("/members/email/{email}", "noco@umc.com"))
                .andExpect(jsonPath("$.available").value(false));
        mockMvc.perform(get("/members/email/{email}", "new@umc.com"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void 유저별_평점은_최신순() throws Exception {
        mockMvc.perform(get("/members/1/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].movieId").value(155))
                .andExpect(jsonPath("$[1].movieId").value(550))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    void 없는_유저의_평점_조회는_404() throws Exception {
        mockMvc.perform(get("/members/999/ratings"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 허용한_출처의_CORS_사전_요청은_통과() throws Exception {
        mockMvc.perform(options("/members/nickname/noco")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void 허용하지_않은_출처는_거절() throws Exception {
        mockMvc.perform(options("/members/nickname/noco")
                        .header("Origin", "http://evil.example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
