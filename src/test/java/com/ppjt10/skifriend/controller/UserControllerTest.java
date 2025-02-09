//package com.ppjt10.skifriend.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ppjt10.skifriend.dto.signupdto.SignupRequestDto;
//import com.ppjt10.skifriend.dto.userdto.UserPasswordUpdateDto;
//import com.ppjt10.skifriend.dto.userdto.UserProfileRequestDto;
//import com.ppjt10.skifriend.dto.userdto.UserProfileUpdateDto;
//import lombok.*;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.*;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.nio.charset.StandardCharsets;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@AutoConfigureMockMvc
//class UserControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private HttpHeaders headers;
//
//    private static ObjectMapper objectMapper = new ObjectMapper();
//
//    private String token = "";
//
//    private SignupRequestDto user1 = SignupRequestDto.builder()
//            .username("beomin12")
//            .nickname("버민")
//            .password("asdf12!!")
//            .phoneNum("01078945321")
//            .build();
//
//    private TestLoginDto user1Login = TestLoginDto.builder()
//            .username("beomin12")
//            .password("asdf12!!")
//            .build();
//
//    @BeforeEach
//    public void setup() {
//        headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("회원 가입")
//    void test1() throws JsonProcessingException {
//
//        // given
//        String requestBody = objectMapper.writeValueAsString(user1);
//        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//        // when
//        ResponseEntity<Object> response = restTemplate.postForEntity(
//                "/user/signup",
//                request,
//                Object.class);
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("로그인, JWT 토큰 받기")
//    void test2() throws JsonProcessingException {
//
//        // given
//        String requestBody = objectMapper.writeValueAsString(user1Login);
//        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//        // when
//        ResponseEntity<Object> response = restTemplate.postForEntity(
//                "/user/login",
//                request,
//                Object.class
//        );
//
//        // then
//        token = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//    @Test
//    @Order(3)
//    @DisplayName("유저 정보 가져오기")
//    void test3() throws JsonProcessingException {
//
//        headers.set("Authorization", token);
//        HttpEntity<Object> request = new HttpEntity<>(headers);
//
//        // when
//        ResponseEntity<Object> response = restTemplate.exchange(
//                "/user/info",
//                HttpMethod.GET,
//                request,
//                Object.class
//        );
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//    @Nested
//    @Order(4)
//    @DisplayName("유저 프로필 등록")
//    class UserProfileApply {
//        @Test
//        @DisplayName("프로필 작성 성공")
//        void test4() throws Exception {
//            headers.set("Authorization", token);
//
//            String content = objectMapper.writeValueAsString(new UserProfileRequestDto("남", "10대", "초보", "hihihi"));
//            MockMultipartFile file3 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));
//
//            ResultActions resultActions = mockMvc.perform(multipart("/user/profile")
//                            .file(file3).headers(headers)
//                            .accept(MediaType.APPLICATION_JSON)
//                            .characterEncoding("UTF-8"))
//                    .andExpect(status().isOk())
//                    .andDo(print());
//
//            System.out.println(resultActions);
//        }
//
//        @Test
//        @DisplayName("Gender Type 유효성 검사")
//        void test5() throws Exception {
//            headers.set("Authorization", token);
//
//            String content = objectMapper.writeValueAsString(new UserProfileRequestDto("남자", "10대", "초보", "hihihi"));
//            MockMultipartFile file3 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));
//
//            ResultActions resultActions = mockMvc.perform(multipart("/user/profile")
//                            .file(file3).headers(headers)
//                            .accept(MediaType.APPLICATION_JSON)
//                            .characterEncoding("UTF-8"))
//                    .andExpect(status().is4xxClientError())
//                    .andDo(print());
//
//            System.out.println(resultActions);
//        }
//
//        @Test
//        @DisplayName("AgeRange Type 유효성 검사")
//        void test6() throws Exception {
//            headers.set("Authorization", token);
//
//            String content = objectMapper.writeValueAsString(new UserProfileRequestDto("남", "십대", "초보", "hihihi"));
//            MockMultipartFile file3 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));
//
//            ResultActions resultActions = mockMvc.perform(multipart("/user/profile")
//                            .file(file3).headers(headers)
//                            .accept(MediaType.APPLICATION_JSON)
//                            .characterEncoding("UTF-8"))
//                    .andExpect(status().is4xxClientError())
//                    .andDo(print());
//
//            System.out.println(resultActions);
//        }
//
//        @Test
//        @DisplayName("Career Type 유효성 검사")
//        void test7() throws Exception {
//            headers.set("Authorization", token);
//
//            String content = objectMapper.writeValueAsString(new UserProfileRequestDto("남", "10대", "신입", "hihihi"));
//            MockMultipartFile file3 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));
//
//            ResultActions resultActions = mockMvc.perform(multipart("/user/profile")
//                            .file(file3).headers(headers)
//                            .accept(MediaType.APPLICATION_JSON)
//                            .characterEncoding("UTF-8"))
//                    .andExpect(status().is4xxClientError())
//                    .andDo(print());
//
//            System.out.println(resultActions);
//        }
//
//        @Nested
//        @Order(5)
//        @DisplayName("유저 프로필 수정")
//        class UserProfileEdit {
//            @Test
//            @DisplayName("프로필 수정 성공")
//            void test8() throws Exception {
//
//                String content = objectMapper.writeValueAsString(updateDto);
//
//                MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/info");
//
//                builder.with(request1 -> {
//                    request1.setMethod("PUT");
//                    return request1;
//                });
//
//                MockMultipartFile multipartFile3 = new MockMultipartFile("requestDto", "", "application/json", content.getBytes());
//                mockMvc.perform(builder.file(multipartFile3)
//                                .header("Authorization", token))
//                        .andExpect(status().isOk())
//                        .andDo(print());
//            }
//
//            @Test
//            @DisplayName("비밀번호 수정 성공")
//            void test9() throws Exception {
//                headers.set("Authorization", token);
//
//                UserPasswordUpdateDto userDto = UserPasswordUpdateDto.builder()
//                        .password(user1Login.getPassword())
//                        .newPassword("abcd1234!")
//                        .build();
//
//                // given
//                String requestBody = objectMapper.writeValueAsString(userDto);
//                HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//                // when
//                ResponseEntity<Object> response = restTemplate.exchange("/user/info/password", HttpMethod.PUT, request, Object.class);
//
//                // then
//                assertEquals(HttpStatus.OK, response.getStatusCode());
//            }
//        }
//    }
//
//    private UserProfileUpdateDto updateDto = UserProfileUpdateDto.builder()
//            .nickname("asdf")
//            .career("초보")
//            .selfIntro("안냥")
//            .build();
//
//    @Getter
//    @Setter
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class TestLoginDto {
//        private String username;
//        private String password;
//    }
//
//}
