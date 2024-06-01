package com.example.milky_way_back.member.Jwt;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
            if(token != null && tokenProvider.validateToken(token).getBody().getStatus() == 200) {

                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication); // 객체 저장

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

            } else if(token != null && tokenProvider.validateToken(token).getBody().getStatus() == 401) {

                ((HttpServletResponse) servletResponse).setContentType("application/json");
                ((HttpServletResponse) servletResponse).setCharacterEncoding("UTF-8");
                ((HttpServletResponse) servletResponse).getWriter().write(new ObjectMapper().writeValueAsString(tokenProvider.validateToken(token).getBody()));
                ((HttpServletResponse) servletResponse).setStatus(tokenProvider.validateToken(token).getBody().getStatus());


            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            logger.info("잘못된 접근");
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_BAD_GATEWAY);
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
