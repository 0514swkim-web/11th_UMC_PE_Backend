package com.umc.movie;

import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.RatingRepository;
import com.umc.movie.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 8주차 트랜잭션 증명: 두 번째 저장(즐겨찾기)이 실패하면 먼저 저장한 평점도 rollback 되는가?
// - 일부러 클래스에 @Transactional을 붙이지 않음 → 서비스의 @Transactional이 진짜로 commit/rollback 함
// - FavoriteRepository를 가짜(Mock)로 바꿔서 save()가 무조건 실패하게 만듦
@SpringBootTest
@AutoConfigureMockMvc
class MovieRecordRollbackTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    RatingRepository ratingRepository;

    @MockitoBean
    FavoriteRepository favoriteRepository;

    @Test
    void 즐겨찾기_저장이_실패하면_평점도_함께_취소된다() throws Exception {
        // 중복 검사는 통과(false)하고, 실제 저장 단계에서 실패하도록
        given(favoriteRepository.existsByMember_MemberIdAndMovieId(any(), any())).willReturn(false);
        given(favoriteRepository.save(any()))
                .willThrow(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "TEST500_1",
                        "즐겨찾기 저장 중 오류(테스트용)"));

        String token = "Bearer " + jwtUtil.createAccessToken(2L, "minseo@umc.com");
        mockMvc.perform(post("/movie-records").header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"movieId\": 238, \"score\": 5, \"addToFavorites\": true}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("TEST500_1"));

        // ① 평점 save()는 성공했지만, ② 즐겨찾기 실패로 트랜잭션 전체가 rollback → 평점도 없음
        assertThat(ratingRepository.existsByMember_MemberIdAndMovieId(2L, 238L)).isFalse();
    }
}
