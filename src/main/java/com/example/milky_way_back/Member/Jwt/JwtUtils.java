package com.example.milky_way_back.Member.Jwt;

import com.example.milky_way_back.Member.Dto.AccessTokenRequest;
import com.example.milky_way_back.Member.Dto.TokenRequest;
import com.example.milky_way_back.Member.Entity.Auth;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// jwt를 발급 및 관리하는 클래스
// 이 클래스를 기반으로 jwt를 인가하고 인증할 수 있도록 함
@Component /* todo 컴포넌트는 어떤 어노테이션인가? */
@Slf4j
public class JwtUtils {
    public static final String ACCESS_HEADER = "ACCESS_HEADER";
    public static final String BEARER = "Bearer ";

    private final SecretKey secretKey;
    private String accessToken;

    private SecretKey getSecretKey() {
        return secretKey;
    }

    // 생성자를 통해 SecretKey 주입 받도록 수정
    public JwtUtils(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    long now = (new Date()).getTime();
    private final Date TOKENTIME = new Date(now + 86400000);

    // 토큰 생성 메서드
    public TokenRequest createToken(Authentication authentication, SecretKey secretKey) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) /* todo GrantedAuthority가 무엇인가? */
                .collect(Collectors.joining(","));

                // 어세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(TOKENTIME)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 리프레시 토큰 dto build
        return TokenRequest.builder()
                .grantType(BEARER)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 어세스 토큰 만료 시 재생성
    public AccessTokenRequest createAccessToken(Authentication authentication, SecretKey secretKey) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) /* todo GrantedAuthority가 무엇인가? */
                .collect(Collectors.joining(","));

        // 어세스 토큰 생성
        accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(TOKENTIME)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return AccessTokenRequest.builder()
                .accessToken(accessToken)
                .build();
    }

    // 토큰 만료 시간 가져오기(리프레시/어세스 토큰 둘 다)
    public Instant getExpirationDate(String token, SecretKey secretKey) {
        Date expiration = Jwts.parser() // jwt 정보 가져오기
                .setSigningKey(secretKey) // 시크릿 키로 판별
                .parseClaimsJws(token) // 토큰값
                .getBody() // 토큰 내용
                .getExpiration(); // 내용 중 만료시간 가져오기

        return expiration.toInstant();
    }

    // header에서 jwt 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_HEADER);

        // bearer으로 시작할 경우 추출하여 정보 전달
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }

        return null; /* todo exception */
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder() // 정보 가져오기
                .setSigningKey(secretKey) // 시크릿 키
                .build()
                .parseClaimsJws(token) // claim 확인
                .getBody();
    }


    // 토큰 정보 검증 메서드
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // tokeㅜ 존재
            return true;

        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");

        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");

        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 어세스 토큰의 인증 정보 가져오기
    public Authentication getAuthentication(String accessToken) {

        Claims claims = getUserInfoFromToken(accessToken); // accessToken의 Claimm 가져오기

        // 권한이 없을 경우
        if (claims.get("auth")== null) {
            throw new RuntimeException("권한 없는 토큰");
        }

        // 관리자 권한
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims
                        .get("auth")
                        .toString()
                        .split(","))

                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 유저 정보
        UserDetails principal = new User(claims.getSubject(), "", authorities); // 유저 메인 정보에 클레임 제목. 비밀번호, 인증 정보 저장
        return new UsernamePasswordAuthenticationToken(principal, "", authorities); // 유저 정보를 담은 객체와 정보를 리턴

    }

}
