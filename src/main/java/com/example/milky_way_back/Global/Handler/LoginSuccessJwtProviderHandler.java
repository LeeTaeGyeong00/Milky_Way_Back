package com.example.milky_way_back.Global.Handler;

import com.example.milky_way_back.Global.Jwt.JwtService;
import com.example.milky_way_back.Member.Repository.JwtRepository;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJwtProviderHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private JwtRepository jwtRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        String memberId = extractUsername(authentication); // 유저네임 추출
        String accessToken = jwtService.creatAccessToken(memberId); // username 기반으로 accessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // 리프레시 토큰까지 발급

        // accessToken과 refreshToken 전달(http method) 전달
        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);

        // 토큰 확인
        jwtRepository.findByAuthNo(memberId)
                .ifPresent(auth -> auth.updateRefreshToken(refreshToken));

        log.info("memberId: {}", memberId);
        log.info("access token: {}", accessToken);
        log.info("refresh token: {}", refreshToken);
    }

    // 유저 네임 추출
    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
