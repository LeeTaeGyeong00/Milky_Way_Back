package com.example.milky_way_back.Member.Jwt.filter;


import com.example.milky_way_back.Member.Jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// jwt를 검증하고, 인가하는 필터
@Component
@Slf4j
public class JwtAuthorizationFIlter extends OncePerRequestFilter {

    private final JwtUtils jwtUtil;

    private SecretKey secretKey;

    private SecretKey getSecretKey() {
        return secretKey;
    }
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFIlter(JwtUtils jwtUtil, UserDetailsService userDetailsService, SecretKey secretKey) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.secretKey= secretKey;
    }

    // OncePerRequestFiter를 상속하여 필터 진행
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        String tokenValue = jwtUtil.getJwtFromHeader(httpServletRequest);

        if (StringUtils.hasText(tokenValue) && jwtUtil.validateToken(tokenValue)) {

            try {

                String username = jwtUtil.extractUsername(tokenValue, secretKey);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.error("인증에 실패함", e);
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // authentication 설정
    public void setAuthentication(String memberId) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(memberId);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // authentication 생성
    private Authentication createAuthentication(String memberId) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
