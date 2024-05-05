package com.example.milky_way_back.Member.Service;

import com.example.milky_way_back.Member.Dto.LoginRequest;
import com.example.milky_way_back.Member.Dto.LogoutRequest;
import com.example.milky_way_back.Member.Dto.SignupRequest;
import com.example.milky_way_back.Member.Dto.StatusResponse;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
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

        Optional<Member> checkDuplication = memberRepository.findByMemberId(memberId);

        if (checkDuplication.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 존재"); /* todo 프론트에게 값 어떻게 보내주는가? */
        }

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

    // 로그인
    @Transactional
    public ResponseEntity<?> login(LoginRequest request, String accessToken) {

        // 회원이 있는지 검증
        Member member = memberRepository.findByMemberId(request.getMemberId()).orElseThrow();

            // 비밀번호 검증
            if (passwordEncoder.matches(request.getMemberPassword(), member.getMemberPassword())) {

                // 인증 정보 생성
                UserDetails userDetails = new User(member.getMemberId(), member.getMemberPassword(), new ArrayList<>());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, member.getMemberRole(), userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 리프레시 토큰 가져오기
                String refreshToken = authRepository.findByMember(member.getMemberNo())
                        .map(Auth::getAuthRefreshToken)
                        .orElse(null);

                // 리프레시 토큰이 없을 경우 access, refresh 둘 다 생성 후 access return, refresh save
                if(refreshToken == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(jwtUtils.createToken(authentication, secretKey));
                } else {
                    // 어세스 토큰 만료 확인
                    boolean accessTokenValid = jwtUtils.validateToken(accessToken);

                    if (!accessTokenValid) {
                        // 어세스 토큰 만료시 refresh token도 만료이므로 access reuturn, refresh save
                        return ResponseEntity.status(HttpStatus.OK).body(jwtUtils.createAccessToken(authentication, secretKey));

                    } else if (accessTokenValid) {
                        // 어세스 토큰 유효 -> 기존 access return
                        return ResponseEntity.status(HttpStatus.OK).body(accessToken);

                    } else {
                        // 그 외의 경우에는 예외 처리 또는 오류 상황을 처리합니다.
                        throw new RuntimeException("토큰 유효성 검증 실패");
                    }
                }
            } else {
                throw new RuntimeException("비밀번호 불일치");
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
        Auth logoutMember = authRepository.deleteByAuthRefreshToken(refershToken).orElseThrow();

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "로그아웃 성공"));

    }
}