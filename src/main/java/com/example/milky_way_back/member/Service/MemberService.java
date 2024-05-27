package com.example.milky_way_back.member.Service;

import com.example.milky_way_back.article.DTO.response.MyPageArticleResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.entity.Dibs;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.exception.UnauthorizedAccessException;
import com.example.milky_way_back.article.repository.DibsRepository;
import com.example.milky_way_back.member.Dto.*;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Entity.RefreshToken;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Jwt.UserDetailService;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.member.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final DibsRepository dibsRepository;

    @Autowired
    private UserDetailsService userDetailService;

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
    public TokenDto  updateMemberInfo(MyPageRequest myPageRequest) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentMemberId = authentication.getName();
//        System.out.println(myPageRequest.getMemberId());
//        System.out.println(currentMemberId);
//        // Check if the current user's memberId matches the provided memberId
//        if (!currentMemberId.equals(myPageRequest.getMemberId())) {
//            throw new UnauthorizedAccessException("You are not authorized to update this user's information");
//        }
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();

        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            boolean isUpdated = false;
            // Update only if the field is not null
            // Handle memberId change
            // Handle memberId change
//            if (myPageRequest.getNewMemberId() != null && !myPageRequest.getNewMemberId().equals(memberId)) {
//                // Check if new memberId is already in use
//                if (memberRepository.findByMemberId(myPageRequest.getNewMemberId()).isPresent()) {
//                    throw new IllegalArgumentException("The new memberId is already in use");
//                }
//                member.setMemberId(myPageRequest.getNewMemberId());
//                isUpdated = true;
//                //리팩토링 할때 duplicationIdCheck 이부분을 쓰기위한 IdRequest 생성자 쓸수있는지 물어보기
//            }
//            // Check if the new memberId is different from the current memberId
//            if (myPageRequest.getNewMemberId() != null && !myPageRequest.getNewMemberId().equals(memberId)) {
//                ResponseEntity<StatusResponse> response = duplicationIdCheck(new IdRequest(myPageRequest.getNewMemberId()));
//                if (response.getStatusCode() == HttpStatus.OK) {
//                    member.setMemberId(myPageRequest.getNewMemberId());
//                    isUpdated = true;
//                } else {
//                    throw new IllegalArgumentException("The new memberId is already in use");
//                }
//            }

            if (myPageRequest.getMemberPassword() != null) {
                String encodedPassword = passwordEncoder.encode(myPageRequest.getMemberPassword());
                member.setMemberPassword(encodedPassword);
                isUpdated = true;
            }
            if (myPageRequest.getMemberName() != null) {
                member.setMemberName(myPageRequest.getMemberName());
                isUpdated = true;
            }
            if (myPageRequest.getMemberPhoneNum() != null) {
                member.setMemberPhoneNum(myPageRequest.getMemberPhoneNum());
                isUpdated = true;
            }
            if (myPageRequest.getMemberEmail() != null) {
                member.setMemberEmail(myPageRequest.getMemberEmail());
                isUpdated = true;
            }
            member.setLastModifiedDate(LocalDateTime.now());
            memberRepository.save(member);

            // Update authentication token if any information has been changed
            if (isUpdated) {
                return updateAuthenticationToken(member.getMemberId());
            }
        }
        return null; // No update was made or member not found
    }
    private TokenDto updateAuthenticationToken(String newMemberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = userDetailService.loadUserByUsername(newMemberId);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Generate new tokens
        TokenDto newTokenDto = tokenProvider.createToken(newAuth);

        // Log the new access token
        System.out.println("New JWT Access Token: " + newTokenDto.getAccessToken());

        // Return the new tokens
        return newTokenDto;
    }
    public List<MyPageArticleResponse> getLikedArticlesByMemberId(String memberId) {
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
        Member member = optionalMember.get();

        // 회원이 좋아요를 누른 게시물 목록 조회
        List<Dibs> dibsList = dibsRepository.findByMemberNo(member);
        List<MyPageArticleResponse> likedArticles = new ArrayList<>();

        for (Dibs dibs : dibsList) {
            Article article = dibs.getArticleNo();

            MyPageArticleResponse response = new MyPageArticleResponse();
            response.setCardArticle_no(article.getArticle_no());
            response.setCardArticleType(article.getArticleType());
            response.setCardTitle(article.getTitle());
            response.setCardFindMentor(article.isFindMentor());
            response.setCardRecruit(article.getRecruit());
            response.setCardApply(article.getApply());
            response.setCardApplyNow(article.getApplyNow());
            response.setCardLikes(article.getLikes());
            response.setCardEndDay(article.getEndDay());
            response.setCardStartDay(article.getStartDay());

            likedArticles.add(response);
        }

        return likedArticles;
    }
}