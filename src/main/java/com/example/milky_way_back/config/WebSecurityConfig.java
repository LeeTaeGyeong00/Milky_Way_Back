package com.example.milky_way_back.config;

import com.example.milky_way_back.member.Jwt.JwtAuthenticationFilter;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;

    // 기본 시큐리티 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 방식 쓰지 않음
                .and()
                .authorizeRequests((request) -> request.requestMatchers(PathRequest.toStaticResources()
                                .atCommonLocations()).permitAll()
                        .antMatchers("/signup", "/login", "/signup/checkId").permitAll() // 해당 경로는 모두 접근 가능
                        .anyRequest().authenticated() /* todo 인증 권한 설정 */
                )
                .cors()
                .and()// cors config 적용
                .logout().disable()
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
    // 패스워드 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
