package com.example.milky_way_back.Global;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

// Secret Key 발급하는 클래스
@Configuration
public class JwtConfig {

    // 값 세팅 (문자열 초기화, 랜덤값)
    private static final int SECRET_KEY_LENGTH = 64;
    private static final Random RANDOM = new SecureRandom();

    // 시크릿키 생성
    private String generateRandomSecretKey() {
        byte[] randomBytes = new byte[SECRET_KEY_LENGTH];
        RANDOM.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    // 빈 등록
    @Bean
    public SecretKey jwtSecretKey() {
        String randomSecretKey = generateRandomSecretKey();
        return Keys.hmacShaKeyFor(randomSecretKey.getBytes());
    }

}
