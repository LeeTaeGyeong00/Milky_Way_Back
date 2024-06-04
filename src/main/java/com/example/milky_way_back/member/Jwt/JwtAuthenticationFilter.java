package com.example.milky_way_back.member.Jwt;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Dto.TokenDto;
import com.example.milky_way_back.member.Entity.RefreshToken;
import com.example.milky_way_back.member.Repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // jwt 헤더 토큰 추출
        String token = getJwtToken((HttpServletRequest) servletRequest);
        try {

            if(token != null) {

                int status = tokenProvider.validateToken(token).getBody().getStatus();

                if (status == 200) {

                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {

                        @Override
                        public String getHeader(String name) {
                            if("Authorization".equals(name)) {
                                return token;
                            }
                            return super.getHeader(name);
                        }
                    };
                    filterChain.doFilter(wrappedRequest, response);

                } else if (status == 401) { // 토큰 이상 발생 시

                    ((HttpServletResponse) servletResponse).setContentType("application/json");
                    ((HttpServletResponse) servletResponse).setCharacterEncoding("UTF-8");
                    ((HttpServletResponse) servletResponse).getWriter().write(new ObjectMapper().writeValueAsString(tokenProvider.validateToken(token).getBody()));
                    ((HttpServletResponse) servletResponse).setStatus(status);

                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (NullPointerException e) {
            logger.error("토큰이 null입니다.", e);
            // 적절한 예외 처리 후 리턴 또는 다른 작업 수행
        } catch (Exception e) {
            logger.error("토큰 검증 중 오류가 발생했습니다.", e);
            // 적절한 예외 처리 후 리턴 또는 다른 작업 수행
        }

    }

    private String getJwtToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }



}
