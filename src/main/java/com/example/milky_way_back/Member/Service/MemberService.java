package com.example.milky_way_back.Member.Service;

import com.example.milky_way_back.Member.Dto.*;
import com.example.milky_way_back.Member.Entity.Auth;
import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.AuthRepository;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final SecretKey secretKey;

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

    // 회원가입 시 로그인 중복 확인
    public ResponseEntity<StatusResponse> signupIdDuplication(IdRequest idRequest) {

        Optional<Member> checkDuplication = memberRepository.findByMemberId(idRequest.getMemberId());

        if (checkDuplication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse((HttpStatus.OK.value()), "중복된 사용자 없음"));
        }

        return  ResponseEntity.status(HttpStatus.OK).body(new StatusResponse((HttpStatus.BAD_GATEWAY.value()),"중복된 사용자 있음"));
    }

    // 로그인
    @Transactional
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletRequest request) {

        // 회원 아이디가 이미 있는지 검증
        Member member = memberRepository.findByMemberId(loginRequest.getMemberId()).orElseThrow();

            // 비밀번호 검증
            if (passwordEncoder.matches(loginRequest.getMemberPassword(), member.getMemberPassword())) {

                UserDetails userDetails = new User(member.getMemberId(), member.getMemberPassword(), new ArrayList<>());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, member.getMemberRole(), userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 리프레스 토큰 있는지 확인
                // 없을 경우 -> 리프레시 + 어세스 둘 다 발급 2
                // 있을 경우 1) 어세스 토큰 만료 X -> 어세스 토큰만 반환 3
                // 있을 경우 2) 어세스 토큰 만료 O -> 어세스 토큰만 재생성해서 반환 4

                Optional<Auth> auth = authRepository.findByMember(member.getMemberNo());

                if(auth.isPresent()) {
                    String accessToken = jwtUtils.getJwtFromHeader(request); // 어세스 토큰 가져오기

                    if(jwtUtils.validateToken(accessToken)) {
                        return ResponseEntity.status(HttpStatus.OK).body(accessToken); // 3

                    } else {
                        return ResponseEntity.status(HttpStatus.OK).body(jwtUtils.createAccessToken(authentication, secretKey)); // 3
                    }

                } else {

                    return ResponseEntity.status(HttpStatus.OK).body(jwtUtils.createToken(authentication, secretKey)); // 1
                }

            } else { // 비밀번호 검증 실패시
                return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.BAD_GATEWAY.value(), "비밀번호 검증 실패"));
            }
    }

    // 로그아웃
    public ResponseEntity<StatusResponse> logout(LogoutRequest logoutRequest) {

        // 멤버 객체 가져오기
        Member member = memberRepository.findByMemberId(logoutRequest.getMemberId()).orElseThrow();

        // 해당 멤버 넘버로 리프레시 토큰 가져오기
        Auth auth = authRepository.findByMember(member.getMemberNo()).orElseThrow();
        String refershToken = auth.getAuthRefreshToken();

        // 리프레시 토큰 삭제
        authRepository.deleteByAuthRefreshToken(refershToken).orElseThrow();

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "로그아웃 성공"));

    }
}