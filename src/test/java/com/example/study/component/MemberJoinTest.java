package com.example.study.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc  // 이미 뜬 스프링 컨텍스트에 MockMvc 추가
public class MemberJoinTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("약한 패스워드로 회원가입 시도")
    void weak_password_throw_exception() throws Exception {

        String requestBody = """
            {
              "loginId": "testuser01",
              "password": "weak123",
              "email": "testuser@test.com",
              "name": "홍길동",
              "address": {
                "zipCode": "12345",
                "baseAddress": "서울특별시 강남구 테헤란로 123",
                "detailAddress": "삼성빌딩 10층"
              }
            }
            """;

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상 패스워드로 회원가입 성공")
    void join_success() throws Exception {

        String requestBody = """
            {
              "loginId": "testuser01",
              "password": "StrongPassword123!",
              "email": "testuser@test.com",
              "name": "홍길동",
              "address": {
                "zipCode": "12345",
                "baseAddress": "서울특별시 강남구 테헤란로 123",
                "detailAddress": "삼성빌딩 10층"
              }
            }
            """;

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());
    }
}
