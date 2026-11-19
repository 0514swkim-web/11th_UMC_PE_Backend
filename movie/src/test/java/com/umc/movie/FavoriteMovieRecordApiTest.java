package com.umc.movie;

import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.RatingRepository;
import com.umc.movie.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 8주차: 즐겨찾기 API 4개와 영화 기록 API의 성공·실패 규칙 (각 테스트는 끝나면 롤백)
// 더미 데이터: 1번 회원 = 수현(평점 550·155), 2번 회원 = 민서(평점 550), 즐겨찾기는 없음
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoriteMovieRecordApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    private String bearerOf(Long memberId) {
        return "Bearer " + jwtUtil.createAccessToken(memberId, "member" + memberId + "@umc.com");
    }

    private void addFavorite(Long memberId, long movieId) throws Exception {
        mockMvc.perform(post("/favorites")
                        .header(HttpHeaders.AUTHORIZATION, bearerOf(memberId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"movieId\": " + movieId + "}"))
                .andExpect(status().isCreated());
    }

    // ===== 실습 2: 워크북 Postman 1~7번 흐름 그대로 =====

    @Test
    void 즐겨찾기_등록_중복_목록_여부_해제_흐름() throws Exception {
        String token = bearerOf(1L);

        // 1. 등록 → 201
        mockMvc.perform(post("/favorites").header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"movieId\": 550}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.favoriteId").isNumber())
                .andExpect(jsonPath("$.result.movieId").value(550))
                .andExpect(jsonPath("$.result.createdAt").isString());

        // 2. 같은 요청 한 번 더 → 409 FAVORITE409_1
        mockMvc.perform(post("/favorites").header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"movieId\": 550}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("FAVORITE409_1"));

        // 3. 목록 → 한 개
        mockMvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].movieId").value(550));

        // 4. 여부 → true
        mockMvc.perform(get("/favorites/550").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.isFavorite").value(true));

        // 5. 해제 → 200 + result null
        mockMvc.perform(delete("/favorites/550").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result").value(nullValue()));

        // 6. 여부 → false
        mockMvc.perform(get("/favorites/550").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$.result.isFavorite").value(false));

        // 7. 목록 → 빈 배열
        mockMvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(0)));
    }

    @Test
    void 즐겨찾기_목록은_최근_등록순이고_내_것만_보인다() throws Exception {
        addFavorite(1L, 550);
        addFavorite(1L, 155);
        addFavorite(2L, 238); // 다른 회원 것

        mockMvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, bearerOf(1L)))
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[0].movieId").value(155))
                .andExpect(jsonPath("$.result[1].movieId").value(550));
    }

    @Test
    void 남의_즐겨찾기는_해제할_수_없다_404() throws Exception {
        addFavorite(1L, 550);

        // 민서(2번)가 550을 해제 → 민서의 즐겨찾기 중엔 없으니 404, 수현 것은 그대로
        mockMvc.perform(delete("/favorites/550").header(HttpHeaders.AUTHORIZATION, bearerOf(2L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FAVORITE404_1"));
        assertThat(favoriteRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).isTrue();
    }

    // ===== 실습 4: 실패 상황을 서비스 규칙으로 =====

    @Test
    void 토큰_없이_즐겨찾기_등록은_401() throws Exception {
        mockMvc.perform(post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"movieId\": 550}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH401_2"));
    }

    @Test
    void 없는_즐겨찾기_해제는_404() throws Exception {
        mockMvc.perform(delete("/favorites/99999").header(HttpHeaders.AUTHORIZATION, bearerOf(1L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FAVORITE404_1"));
    }

    @Test
    void movieId가_0이면_400() throws Exception {
        mockMvc.perform(post("/favorites").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"movieId\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("영화 ID는 양수여야 합니다."));
    }

    // ===== 실습 3: 영화 기록 (평점 + 즐겨찾기) =====

    private static final String RECORD_238 = """
            {"movieId": 238, "score": 5, "comment": "다시 보고 싶은 영화입니다.", "addToFavorites": true}
            """;

    @Test
    void 영화_기록은_평점과_즐겨찾기를_함께_저장한다_201() throws Exception {
        mockMvc.perform(post("/movie-records").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON).content(RECORD_238))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.rating.movieId").value(238))
                .andExpect(jsonPath("$.result.rating.score").value(5))
                .andExpect(jsonPath("$.result.favorite.movieId").value(238));

        assertThat(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 238L)).isTrue();
        assertThat(favoriteRepository.existsByMember_MemberIdAndMovieId(1L, 238L)).isTrue();
    }

    @Test
    void addToFavorites가_false면_평점만_저장하고_favorite은_null() throws Exception {
        mockMvc.perform(post("/movie-records").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"movieId\": 238, \"score\": 3, \"addToFavorites\": false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.rating.movieId").value(238))
                .andExpect(jsonPath("$.result.favorite").value(nullValue()));

        assertThat(favoriteRepository.existsByMember_MemberIdAndMovieId(1L, 238L)).isFalse();
    }

    @Test
    void 같은_영화_기록을_다시_보내면_RATING409_1() throws Exception {
        String token = bearerOf(1L);
        mockMvc.perform(post("/movie-records").header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON).content(RECORD_238))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/movie-records").header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON).content(RECORD_238))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RATING409_1"));

        // 실패한 두 번째 요청 때문에 새 행이 생기지 않음
        mockMvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$.result", hasSize(1)));
    }

    @Test
    void 이미_즐겨찾기한_영화면_FAVORITE409_1이고_평점도_저장되지_않는다() throws Exception {
        addFavorite(1L, 238);

        mockMvc.perform(post("/movie-records").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON).content(RECORD_238))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("FAVORITE409_1"));

        assertThat(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 238L)).isFalse();
    }

    @Test
    void 토큰_없이_영화_기록은_401() throws Exception {
        mockMvc.perform(post("/movie-records")
                        .contentType(MediaType.APPLICATION_JSON).content(RECORD_238))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH401_2"));
    }
}
