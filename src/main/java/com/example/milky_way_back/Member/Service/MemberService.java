package com.example.milky_way_back.Member.Service;

import antlr.Token;
import com.auth0.jwt.interfaces.Claim;
import com.example.milky_way_back.Member.Dto.*;
import com.example.milky_way_back.Member.Entity.Auth;
import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Entity.Role;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.AuthRepository;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import io.jsonwebtoken.Claims;
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

        if (checkDuplication.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse((HttpStatus.BAD_GATEWAY.value()), "중복된 사용자 있음"));
        }

        return  ResponseEntity.status(HttpStatus.OK).body(new StatusResponse((HttpStatus.OK.value()),"중복된 사용자 없음"));
    }

    // 로그인
    @Transactional
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletRequest request) {
        // 회원 아이디가 이미 있는지 검증
        Optional<Member> member = memberRepository.findByMemberId(loginRequest.getMemberId());

        String password = member.get().getMemberPassword();

            // 비밀번호 검증
            if (member.isPresent() && passwordEncoder.matches(loginRequest.getMemberPassword(), password)) {

                String id = member.get().getMemberId();
                Role role = member.get().getMemberRole();
                Long no = member.get().getMemberNo();

                UserDetails userDetails = new User(id, password, new ArrayList<>());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, role, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                Optional<Auth> auth = authRepository.findByMember(no);

                if(auth.isPresent()) {
                    String accessToken = jwtUtils.getJwtFromHeader(request);

                    if(jwtUtils.validateToken(accessToken)) {
                        return ResponseEntity.status(HttpStatus.OK).body(accessToken);

                    } else {
                        AccessTokenResponse accessTokenResponse = jwtUtils.createAccessToken(authentication, secretKey);
                        return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse.getAccessToken());
                    }

                } else {
                    TokenRequest tokenRequest = jwtUtils.createToken(authentication, secretKey);
                    return ResponseEntity.status(HttpStatus.OK).body(tokenRequest);
                }

            } else { // 비밀번호 검증 실패시
                return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.BAD_GATEWAY.value(), "비밀번호 검증 실패"));
            }
    }

    // 로그아웃
    public ResponseEntity<StatusResponse> logout(HttpServletRequest request) {

        String accessToken = jwtUtils.getJwtFromHeader(request);
        Claims claims = jwtUtils.getUserInfoFromToken(accessToken);

        if(claims != null) {
            Long memberNo = Long.parseLong(claims.get("memberNo").toString());
            authRepository.deleteByMember(memberNo);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "로그아웃 성공"));

    }
}