package com.example.milky_way_back.Member.Service;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Entity.Role;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginServiceTest {

    // login service 기본 세팅
    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager entityManager;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 비밀번호 설정

    ObjectMapper objectMapper = new ObjectMapper(); // 객체 직렬화 (데이터)

    private static String KEY_MEMBERID = "memberId";
    private static String KEY_PASSWORD = "password";
    private static String MEMBERID = "memberId";
    private static String PASSWORD = "123456789";

    private static String LOGIN_RUL = "/login";

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private void clear() {
        entityManager.flush();
        entityManager.clear();
    }

    @BeforeEach
    public void init() {
        memberRepository.save(Member.builder()
                        .memberId(MEMBERID)
                        .memberPassword(passwordEncoder.encode(PASSWORD))
                        .memberEmail("memberEmail")
                        .memberName("memberName")
                        .memberPhoneNum("01000000000")
                        .memberRole(Role.Student)
                .build());
        clear();
    }

    private Map getMemberIdPasswordMap(String memberId, String password) {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_MEMBERID, memberId);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private ResultActions perform(String url, MediaType mediaType, Map usernamePasswordMap) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(usernamePasswordMap)));

    }


    @Test // 로그인 성공 시
    public void login_successful() throws Exception {

        Map<String, String> map = getMemberIdPasswordMap(MEMBERID, PASSWORD);

        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader(accessHeader)).isNotNull();
        assertThat(result.getResponse().getHeader(refreshHeader)).isNotNull();

    }
}
