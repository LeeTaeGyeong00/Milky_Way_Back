package com.example.milky_way_back.Member.Jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.milky_way_back.Member.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    @Override
    public void updateRefreshToken(String memberId, String refreshToken) {

    }
}
