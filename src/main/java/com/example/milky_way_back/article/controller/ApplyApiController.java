package com.example.milky_way_back.article.controller;


import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.ChangeApplyResult;
import com.example.milky_way_back.article.DTO.request.ApplyRequest;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.exception.UnauthorizedException;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.repository.ArticleRepository;
import com.example.milky_way_back.article.service.ApplyService;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class ApplyApiController {
    private final ApplyService applyService;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ApplyRepository applyRepository;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    //지원 신청
        @PostMapping("/posts/apply/{id}")
        public ResponseEntity<Apply> applyToPost(@AuthenticationPrincipal UserDetails userDetails,
                                                 ApplyRequest applyRequest, @PathVariable long id, HttpServletRequest request
        ) {
            if (userDetails == null) {
                // 사용자가 인증되지 않은 경우
                throw new UnauthorizedException("사용자가 인증되지 않았습니다.");
            }
            // Jwt 토큰에서 회원 정보를 가져옴
            String accessToken = extractTokenFromRequest(request);
            if (accessToken == null) {
                throw new UnauthorizedException("토큰이 유효하지 않습니다.");
            }

            // 받은 데이터로 지원 처리
            Apply saveApply = applyService.apply(accessToken, id, applyRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saveApply);
        }

    //결과 지원 바꾸기
    @PutMapping("/update/{applyId}")
    public ResponseEntity<String> updateApplyResult(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long applyId, @RequestBody ChangeApplyResult requestBody) {
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();

        // 지원 정보 조회
        Optional<Apply> optionalApply = applyRepository.findById(applyId);
        if (!optionalApply.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("지원 정보를 찾을 수 없습니다.");
        }

        Apply apply = optionalApply.get();
        Member author = apply.getArticle().getMemberId();

        // 현재 로그인한 사용자와 게시판의 작성자가 일치하는지 확인
        if (!author.getMemberId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 지원자의 합격을 결정할 수 있습니다.");
        }

        // 작성자와 일치하면, 작업 수행
        String applyResult = requestBody.getApplyResult();

        // apply 엔터티의 applyResult 필드를 직접 수정
        apply.setApplyResult(applyResult);
        applyRepository.save(apply);

        // 만약 결과가 "합격"인 경우, Article 엔터티의 applyNow 필드를 1 증가
        if ("합격".equals(applyResult)) {
            Article article = apply.getArticle();
            int currentApplyNow = article.getApplyNow();
            article.setApplyNow(currentApplyNow + 1);
            articleRepository.save(article);
        }

        return ResponseEntity.ok("applyResult updated successfully");
    }
    //지원 목록자 조회
    @GetMapping("/posts/applylist/{id}")
    public ResponseEntity<List<ApplyResponse>> findMemberNamesByArticleNo(@PathVariable Long id) {
        List<ApplyResponse> memberNames = applyService.findMemberNamesByArticleNo(id);
        return new ResponseEntity<>(memberNames, HttpStatus.OK);
    }
    // HttpServletRequest에서 JWT 토큰 추출하는 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출
        }
        return null;
    }
}
