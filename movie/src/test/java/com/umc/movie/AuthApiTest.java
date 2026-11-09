package com.umc.movie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 7주차: 로그인(토큰 발급)과 인터셉터(토큰 검사) 확인
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthApiTest {

    @Autowired
    MockMvc mockMvc;

    private void signUp() throws Exception {
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "umc@example.com", "password": "Umc!1234", "nickname": "moviefan"}
                                """))
                .andExpect(status().isCreated());
    }

    private void loginExpect401(String email, String password) throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH401_1"))
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 로그인_성공은_200과_토큰() throws Exception {
        signUp();
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "umc@example.com", "password": "Umc!1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.nickname").value("moviefan"))
                .andExpect(jsonPath("$.result.accessToken").isString());
    }

    @Test
    void 비밀번호_틀림과_없는_이메일은_같은_401() throws Exception {
        signUp();
        loginExpect401("umc@example.com", "Wrong!1234");
        loginExpect401("nobody@example.com", "Umc!1234");
    }

    @Test
    void 토큰_없이_잠긴_경로는_401_AUTH401_2() throws Exception {
        mockMvc.perform(delete("/members/1/ratings/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH401_2"));
    }

    @Test
    void 위조된_토큰은_401_AUTH401_3() throws Exception {
        mockMvc.perform(delete("/members/1/ratings/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH401_3"));
    }
}
