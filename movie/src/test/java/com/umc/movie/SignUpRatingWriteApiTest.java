package com.umc.movie;

import com.umc.movie.repository.MemberRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 6주차 저장 API의 성공·실패 응답을 코드로 확인 (7주차: 평점은 /ratings + 토큰으로 변경) (각 테스트는 끝나면 롤백되어 서로 영향 없음)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignUpRatingWriteApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtUtil jwtUtil;

    // 7주차: 평점 쓰기는 토큰으로 "누가"를 정함 (1번 = 수현, 2번 = 민서)
    private String bearerOf(Long memberId) {
        return "Bearer " + jwtUtil.createAccessToken(memberId, "member" + memberId + "@umc.com");
    }

    private static final String SIGN_UP_BODY = """
            {"email": "umc@example.com", "password": "Umc!1234", "nickname": "moviefan"}
            """;

    @Test
    void 회원가입_성공은_201이고_password는_응답에_없다() throws Exception {
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON).content(SIGN_UP_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.email").value("umc@example.com"))
                .andExpect(jsonPath("$.result.password").doesNotExist());

        String saved = memberRepository.findAll().stream()
                .filter(m -> m.getEmail().equals("umc@example.com"))
                .findFirst().orElseThrow().getPassword();
        assertThat(saved).startsWith("$2a$").hasSize(60).isNotEqualTo("Umc!1234");
    }

    @Test
    void 잘못된_이메일_형식은_400() throws Exception {
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "wrong-email", "password": "Umc!1234", "nickname": "newbie"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON4001"))
                .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    void 이미_있는_이메일은_409() throws Exception {
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "noco@umc.com", "password": "Umc!1234", "nickname": "newbie"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("COMMON4092"))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }

    @Test
    void 평점_등록_201_후_같은_영화는_409() throws Exception {
        String body = """
                {"movieId": 680, "score": 5, "comment": "펄프 픽션 최고"}
                """;
        mockMvc.perform(post("/ratings").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.ratingId").isNumber())
                .andExpect(jsonPath("$.result.movieId").value(680));

        mockMvc.perform(post("/ratings").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RATING409_1"))
                .andExpect(jsonPath("$.message").value("이미 이 영화에 평점을 남기셨습니다."));
    }

    @Test
    void 별점_범위_밖은_400() throws Exception {
        mockMvc.perform(post("/ratings").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"movieId": 680, "score": 6}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("별점은 5점 이하여야 합니다."));
    }

    @Test
    void 토큰_없이_평점_등록은_401() throws Exception {
        mockMvc.perform(post("/ratings").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"movieId": 680, "score": 5}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH401_2"));
    }

    @Test
    void 평점_수정_200_삭제_200() throws Exception {
        mockMvc.perform(patch("/ratings/1").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score": 3, "comment": "다시 보니 보통"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.score").value(3));

        mockMvc.perform(get("/members/1/ratings"))
                .andExpect(jsonPath("$[1].score").value(3));

        mockMvc.perform(delete("/ratings/1").header(HttpHeaders.AUTHORIZATION, bearerOf(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("평점을 삭제했습니다."));

        mockMvc.perform(get("/members/1/ratings"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void 없는_평점은_404_남의_평점은_403() throws Exception {
        mockMvc.perform(delete("/ratings/999").header(HttpHeaders.AUTHORIZATION, bearerOf(1L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RATING404_1"));

        // 3번 평점은 2번 회원(민서) 것 → 1번 회원(수현) 토큰은 멀쩡해도 403
        mockMvc.perform(patch("/ratings/3").header(HttpHeaders.AUTHORIZATION, bearerOf(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score": 1}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("RATING403_1"));

        // 같은 평점이라도 주인(2번) 토큰이면 통과
        mockMvc.perform(delete("/ratings/3").header(HttpHeaders.AUTHORIZATION, bearerOf(2L)))
                .andExpect(status().isOk());
    }
}
