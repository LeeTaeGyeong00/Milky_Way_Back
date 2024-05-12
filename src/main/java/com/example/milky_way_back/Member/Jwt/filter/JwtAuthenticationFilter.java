package com.example.milky_way_back.Member.Jwt.filter;

import com.example.milky_way_back.Member.Dto.*;
import com.example.milky_way_back.Member.Entity.Auth;
import com.example.milky_way_back.Member.Entity.Role;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.AuthRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 인증을 담당하는 필터
/* todo Authentication이란? / UsernamePasswordAuthentication이란? */
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private SecretKey secretKey;
    private final JwtUtils jwtUtils;
    private AuthRepository authRepository;
    private final AuthenticationManager authenticationManager; // 추가


    // 생성자 설정
    public JwtAuthenticationFilter(JwtUtils jwtUtils, AuthenticationManager authenticationManager,
                                   AuthRepository authRepository, SecretKey secretKey) {

        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.secretKey = secretKey;

        setFilterProcessesUrl("/login"); // login이라는 url이 왔을 때 filter

        super.setAuthenticationManager(authenticationManager); // 부모 객체 적용
    }

    // 로그인 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException, ServletException {


        // JWT 토큰 생성
        TokenRequest tokenRequest = jwtUtils.createToken(authentication, secretKey);

        // 로그인 성공 응답
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setAccessToken(tokenRequest.getAccessToken());
        loginResponse.setMemberName(((UserDetailsImpl) authentication.getPrincipal()).getMember().getMemberName());
        loginResponse.setMemberId(((UserDetailsImpl) authentication.getPrincipal()).getMember().getMemberId());
        loginResponse.setMemberNo(((UserDetailsImpl) authentication.getPrincipal()).getMember().getMemberNo());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        response.getWriter().write(new ObjectMapper().writeValueAsString(loginResponse));

        // 리프레시 토큰 저장
        Auth auth = Auth.builder()
                .member(((UserDetailsImpl) authentication.getPrincipal()).getMember()) // 객체의 정보를 UserDetailImpl화 시켜서 적용
                .authRefreshToken(tokenRequest.getRefreshToken())
                .authExpiration(jwtUtils.getExpirationDate(tokenRequest.getRefreshToken(), secretKey))
                .build();

        authRepository.save(auth);

        StatusResponse responseDto = new StatusResponse(HttpStatus.OK.value(), "로그인 성공했습니다.");
    }

    // 로그인 실패시 (인증 정보 X)
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        StatusResponse responseDto = new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "로그인 정보가 유효하지 않습니다.");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));

    }

    // 인증 객체 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {

            // 로그인 요청일 경우
            if (isLoginRequest(request)) {
                LoginRequest requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

                return getAuthenticationManager().authenticate(
                        new UsernamePasswordAuthenticationToken( // userId랑 password로 인증 객체 저장
                                requestDto.getMemberId(),
                                requestDto.getMemberPassword(),
                                null)); /* todo 수정 */

            }

            // 로그아웃 요청일 경우
            if (isLogoutRequest(request)) { // 로그아웃 요청일 경우
                String memberId = extractMemberIdFromRequest(request);

                return getAuthenticationManager().authenticate(
                        new UsernamePasswordAuthenticationToken(memberId, null, null)
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return null;
    }

    // 헤더에서 아이디 갖고 오는 메서드
    private String extractMemberIdFromRequest(HttpServletRequest request) {
        String accessToken = jwtUtils.getJwtFromHeader(request);
        String memberId = jwtUtils.getUserIdFromAccessToken(accessToken);
        return memberId;
    }

    // 로그인인지 확인하는 메서드
    private boolean isLoginRequest(HttpServletRequest request) {
       return request.getRequestURI().contains("/login");
    }

    // 로그아웃인지 확인하는 메서드
    private boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/logout");
    }
}
