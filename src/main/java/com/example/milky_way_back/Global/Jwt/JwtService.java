package com.example.milky_way_back.Global.Jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface JwtService {

    // 토큰 생성
    String creatAccessToken(String memberId);
    String createRefreshToken();


    // 토큰 보내기
    void sendAccessToken(HttpServletResponse response, String accessToken);
    void sendAccessTokenAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);

    // 헤더 설정
    void setAccessTokenHeader(HttpServletResponse response, String accessToken);
    void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

    // 추출
    Optional<String> extractAccessToken(HttpServletRequest request);
    Optional<String> extractRefreshToken(HttpServletRequest request);
    Optional<String> extractUsername(String accessToken);

    // 유효 확인
    boolean tokenValid(String token);


}
