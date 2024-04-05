package com.example.milky_way_back.Global.Jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.milky_way_back.Member.Repository.JwtRepository;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenValiditiyInSecond; // 프론트에게 보내는 토큰 만료 시간

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValiditiyInSecond; // 리프레시 토큰 만료 시간

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String MEMBERID_CLAIM = "MemberIdClaim";
    private static final String BEARER = "Bearer";

    private final MemberRepository memberRepository;
    private final JwtRepository jwtRepository;

    // 접근 토큰 발급
    @Override
    public String creatAccessToken(String memberId) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValiditiyInSecond * 1000))
                .withClaim(MEMBERID_CLAIM, memberId)
                .sign(Algorithm.HMAC512(secret));
    }

    // 리프레시 토큰 발급
    @Override
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValiditiyInSecond * 1000))
                .sign(Algorithm.HMAC512(secret));
    }


    // 토큰 헤더 설정
    @Override
    public void sendAccessTokenAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
    }

    // 접근 토큰만 보내기
    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
    }

    // 추출
    @Override // access token
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override // refresh token
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    @Override // username(memberId)
    public Optional<String> extractUsername(String accessToken) {
        try {
            return Optional.ofNullable(JWT
                    .require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(accessToken)
                    .getClaim(MEMBERID_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    // 헤더 설정
    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    // 유효성 확인
    @Override
    public boolean tokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token);

            return true;
        } catch (Exception e) {
            log.error("만료된 토큰", e.getMessage());
            return false;
        }
    }
}
