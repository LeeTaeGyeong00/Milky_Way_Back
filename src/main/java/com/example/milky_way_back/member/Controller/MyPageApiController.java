package com.example.milky_way_back.member.Controller;

import com.example.milky_way_back.member.Dto.MyPageRequest;
import com.example.milky_way_back.member.Dto.MyPageResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Jwt.JwtAuthenticationFilter;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Service.MemberService;
import com.example.milky_way_back.article.DTO.response.MyPageApplyResponse;
import com.example.milky_way_back.article.DTO.response.MyPageArticleResponse;
import com.example.milky_way_back.article.exception.UnauthorizedException;
import com.example.milky_way_back.article.service.ApplyService;
import com.example.milky_way_back.article.service.ArticleService;
import com.example.milky_way_back.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController //http 응답으로 객체 데이터를 json 형태로 변환
public class MyPageApiController {
    private final MemberService memberService;
    private final ArticleService articleService;
    private final ApplyService applyService;
    private final TokenProvider tokenProvider;
    @PostMapping("/info")
    public ResponseEntity<?> getMyPageInfo(@AuthenticationPrincipal UserDetails userDetails
                                           ,HttpServletRequest request
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
        try {
            // 회원 정보 조회
            MyPageResponse memberInfo = memberService.getMemberInfo();
            // 아티클 정보 조회
            List<MyPageArticleResponse> articles = articleService.getArticlesByMemberId();
            // 신청 정보 조회
            List<MyPageApplyResponse> applies = applyService.getAppliesByMemberId();

            // 결과 조합
            Map<String, Object> result = new HashMap<>();
            result.put("member", memberInfo);
            result.put("articles", articles);
            result.put("applies", applies);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching my page data");
        }
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateMemberInfo(@RequestBody MyPageRequest myPageRequest) {
        boolean updated = memberService.updateMemberInfo(myPageRequest);
        if (updated) {
            return ResponseEntity.ok("Member information updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }
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
