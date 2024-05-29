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
        public ResponseEntity<Apply> applyToPost(HttpServletRequest request,
                                                 ApplyRequest applyRequest, @PathVariable long id
        ) {

            // 받은 데이터로 지원 처리
            Apply saveApply = applyService.apply(request, id, applyRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saveApply);
        }

    //결과 지원 바꾸기
    @PutMapping("/update/{applyId}")
    public ResponseEntity<String> updateApplyResult(HttpServletRequest request,
                                                    @PathVariable Long applyId,
                                                    @RequestBody ChangeApplyResult requestBody) {
        String response = applyService.updateApplyResult(request, applyId, requestBody);
        return ResponseEntity.ok(response);
    }
    //지원 목록자 조회
    //게시판 작성자, 지원한 사람만 볼수 있도록 추후에 작업하기
    @GetMapping("/posts/applylist/{id}")
    public ResponseEntity<List<ApplyResponse>> findMemberNamesByArticleNo(@PathVariable Long id) {
        List<ApplyResponse> memberNames = applyService.findMemberNamesByArticleNo(id);
        return new ResponseEntity<>(memberNames, HttpStatus.OK);
    }
}
