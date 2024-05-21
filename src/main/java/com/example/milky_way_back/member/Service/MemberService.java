package com.example.milky_way_back.member.Service;

import com.example.milky_way_back.member.Dto.*;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Entity.RefreshToken;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.member.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public ResponseEntity<StatusResponse> signup(SignupRequest request) {

        String memberId = request.getId();
        String password = passwordEncoder.encode(request.getPassword()); // 비밀번호 암호화

        Member member = Member.builder()
                .memberId(memberId)
                .memberPassword(password)
                .memberName(request.getName())
                .memberPhoneNum(request.getTel())
                .memberEmail(request.getEmail())
                .memberRole(request.getRole())
                .build();

        memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "회원가입 성공"));
    }

    // 중복 확인
    public ResponseEntity<StatusResponse> duplicationIdCheck(IdRequest idRequest) {
        if(memberRepository.existsBymemberId(idRequest.getMemberId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse(HttpStatus.CONFLICT.value(), "중복 아이디 있음"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "중복 아이디 없음"));
        }
    }

    // 로그인
    @Transactional
    public TokenDto login(LoginRequest loginRequest) {

        String memberId = loginRequest.getMemberId();
        String memberPassword = loginRequest.getMemberPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, memberPassword);

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.createToken(authentication);

        Member member = memberRepository.findByMemberId(authentication.getName()).get();

        RefreshToken refreshToken = RefreshToken.builder()
                .member(member)
                .authRefreshToken(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

//    // 로그아웃
//    public ResponseEntity<StatusResponse> logout() {
//
//        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
//        String memberId = authentication.getName();
//
//        Member member = memberRepository.findByMemberId(memberId).orElseThrow();
//
//        RefreshToken refreshToken = refreshTokenRepository.findByMember(member.getMemberNo()).orElseThrow();
//
//        refreshTokenRepository.deleteByMember(refreshToken.getMember().getMemberNo()); // 리프레시 토큰 삭제
//
//        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "로그아웃 완료"));
//    }

    // 토큰 재발급
    @Transactional
    public TokenRequestAndResponseDto reissue(TokenRequestAndResponseDto tokenRequestDto) {

        if(!tokenProvider.validateToken(tokenRequestDto.getAccessToken())) {
            throw new RuntimeException("AccessToken이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        Member member = memberRepository.findByMemberId(authentication.getName()).get();

        RefreshToken refreshToken = refreshTokenRepository.findByMember(member.getMemberNo()).orElseThrow();

        TokenDto tokenDto = tokenProvider.createToken(authentication);

        TokenRequestAndResponseDto tokenRequestAndResponseDto =
                TokenRequestAndResponseDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .build(); // 클라이언트 어세스 토큰 재발급

        RefreshToken newRefreshToken = refreshToken.updateToken(tokenDto.getRefreshToken()); // 리프레시 재설정

        refreshTokenRepository.save(newRefreshToken);

        return tokenRequestAndResponseDto; // 클라이언트측에는 어세스 토큰만 전달
    }

}