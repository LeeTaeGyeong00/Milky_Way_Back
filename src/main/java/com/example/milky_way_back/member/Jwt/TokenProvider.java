package com.example.milky_way_back.member.Jwt;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Dto.TokenDto;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {

    private final Key secretKey;
    private final MemberRepository memberRepository;

    public TokenProvider(@Value("${jwt.secret.key}") String key, MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
    }

    // 유저 정보 가지고 refresh, access 토큰 생성
    public TokenDto createToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime(); // 현재 시간

        Member member = memberRepository.findByMemberId(authentication.getName()).orElseThrow();

        Date accessTokenExpire = new Date(now + 1800000); // 30분
        Date refreshTokenExpire = new Date(now + 86400000); // 1일

        // 어세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities) /* todo claim 추가 */
                .setExpiration(accessTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(refreshTokenExpire)
                .claim("auth", authorities) /* todo claim 추가 */
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberName(member.getMemberName())
                .build();
    }

    // access token만 재생성
    public TokenDto createAccessToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime(); // 현재 시간

        Date accessTokenExpire = new Date(now + 1800000); // 30분

        // 어세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities) /* todo claim 추가 */
                .setExpiration(accessTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    // jwt 복호화 하여 토큰 정보 꺼내기
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 검증
    public ResponseEntity<StatusResponse> validateToken(String token) {

        try {

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "vaild"));

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "expired"));
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "invalid"));
    }

    // claims 정보 가져오기
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
