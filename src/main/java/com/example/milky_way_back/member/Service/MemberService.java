package com.example.milky_way_back.member.Service;

import com.example.milky_way_back.article.exception.UnauthorizedAccessException;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

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

    // 로그아웃
    @Transactional
    public ResponseEntity<StatusResponse> logout(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 없습니다."));

        refreshTokenRepository.deleteByMember(member);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "로그아웃 완료"));
    }

    // 리프레시 토큰 확인 후 재발급 관련
    @Transactional
    public TokenDto reissue(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("회원 정보가 없습니다."));

        RefreshToken refreshToken = refreshTokenRepository.findByMember(member).orElseThrow();

        boolean refreshTokenValid = tokenProvider.validateToken(refreshToken.getAuthRefreshToken());

        // 리프레시 토큰이 만료가 안 됐을 경우
        if(refreshTokenValid) {
            return tokenProvider.createAccessToken(authentication);
        } else { // 리프레시 토큰도 만료됐을 경우
            return TokenDto.builder()
                    .grantType("Bearer")
                    .refreshToken(refreshToken.getAuthRefreshToken())
                    .accessToken(request.getHeader("Authorization"))
                    .build();
        }
    }


    public MyPageResponse getMemberInfo() {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();

        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // 회원 정보를 MyPageResponse DTO로 매핑
            MyPageResponse myPageResponse = new MyPageResponse();
            myPageResponse.setMemberId(member.getMemberId());
            myPageResponse.setMemberName(member.getMemberName());
            myPageResponse.setMemberPhoneNum(member.getMemberPhoneNum());
            myPageResponse.setMemberEmail(member.getMemberEmail());
            return myPageResponse;
        } else {
            // 회원 정보가 없을 경우 처리
            // 예: throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
            return null;
        }
    }

    @Transactional
    public boolean updateMemberInfo(MyPageRequest myPageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentMemberId = authentication.getName();
        System.out.println(myPageRequest.getMemberId());
        System.out.println(currentMemberId);
        // Check if the current user's memberId matches the provided memberId
        if (!currentMemberId.equals(myPageRequest.getMemberId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this user's information");
        }

        Optional<Member> optionalMember = memberRepository.findByMemberId(currentMemberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // Update only if the field is not null
            if (myPageRequest.getMemberPassword() != null) {
                String encodedPassword = passwordEncoder.encode(myPageRequest.getMemberPassword());
                member.setMemberPassword(encodedPassword);
            }
            if (myPageRequest.getMemberName() != null) {
                member.setMemberName(myPageRequest.getMemberName());
            }
            if (myPageRequest.getMemberPhoneNum() != null) {
                member.setMemberPhoneNum(myPageRequest.getMemberPhoneNum());
            }
            if (myPageRequest.getMemberEmail() != null) {
                member.setMemberEmail(myPageRequest.getMemberEmail());
            }
            member.setLastModifiedDate(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false; // Member not found
    }
}