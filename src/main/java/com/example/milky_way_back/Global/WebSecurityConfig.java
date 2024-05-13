package com.example.milky_way_back.Global;

import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Jwt.filter.JwtAuthenticationFilter;
import com.example.milky_way_back.Member.Jwt.filter.JwtAuthorizationFIlter;
import com.example.milky_way_back.Member.Repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtUtils jwtUtils;
    private final AuthRepository authRepository;
    private final JwtConfig jwtConfig;

    // authenticationManager bean 등록
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean(); /* todo authentication manager가 무엇이고 왜 쓰는가? */
    }

    // 기본 시큐리티 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // csrf 해제
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 방식 쓰지 않음
                .and()
                .authorizeRequests((request) -> request.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .antMatchers("/signup", "/login").permitAll() // 해당 경로는 모두 접근 가능
                        .antMatchers(HttpMethod.POST).permitAll() // post로 접근하는 건 모두 허용
                        .anyRequest().permitAll()) // 다른 경로도 모두 인증 없이 접근 가능

                .cors()
                .and()// cors config 적용
                .addFilterBefore(new JwtAuthorizationFIlter(jwtUtils, userDetailsService(), jwtConfig.jwtSecretKey()), JwtAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthorizationFIlter(jwtUtils, userDetailsService(),jwtConfig.jwtSecretKey()), JwtAuthenticationFilter.class);
    }
    // 패스워드 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
