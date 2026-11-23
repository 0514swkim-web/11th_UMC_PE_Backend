package com.umc.movie.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 9주차 미션 보너스: 문서(/v3/api-docs JSON)의 "토큰 필요 여부"가 실제 설정과 맞는지 코드로 확인
// 누가 @SecurityRequirement를 지우거나 공개 API에 잘못 붙이면 여기서 빨간불
@SpringBootTest
@AutoConfigureMockMvc
class SwaggerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("토큰이 필요한 평점·내 정보·즐겨찾기 API 문서에는 자물쇠(bearerAuth)가 붙어 있다")
    void protectedApisHaveLock() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/ratings'].post.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/ratings/{ratingId}'].patch.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/ratings/{ratingId}'].delete.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/members/me'].get.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/members/me'].patch.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/favorites'].get.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/favorites/{movieId}'].get.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/favorites/{movieId}'].delete.security[0].bearerAuth").exists());
    }

    @Test
    @DisplayName("토큰 없이 쓰는 공개 API 문서에는 자물쇠가 없다")
    void publicApisHaveNoLock() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/members'].post.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/auth/login'].post.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/members/{memberId}/ratings'].get.security").doesNotExist());
    }
}
