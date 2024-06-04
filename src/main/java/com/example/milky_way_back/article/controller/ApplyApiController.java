package com.example.milky_way_back.article.controller;


import com.example.milky_way_back.article.exception.MemberNotFoundException;
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
import com.example.milky_way_back.resume.StudentResumeService;
import com.example.milky_way_back.resume.dto.BasicInfoResponse;
import com.example.milky_way_back.resume.dto.MemberInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final ApplyRepository applyRepository;
    private final StudentResumeService studentResumeService;

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
    @GetMapping("/posts/applylist/{id}")
    public ResponseEntity<List<ApplyResponse>> findMemberNamesByArticleNo(HttpServletRequest request,@PathVariable Long id) {
        try {
            List<ApplyResponse> memberNames = applyService.findMemberNamesByArticleNo(request, id);
            return new ResponseEntity<>(memberNames, HttpStatus.OK);
        } catch (MemberNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 기본 정보 조회
    @GetMapping("/apply/basicInfo/{id}")
    public ResponseEntity<BasicInfoResponse> applyInfoResponse(HttpServletRequest request, @PathVariable Long id) {
        // Apply 엔티티에서 applyNo를 기반으로 member 조회
        Apply apply = applyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Apply not found with ID: " + id));
        // memberNo를 사용하여 기본 정보 조회
        System.out.println(apply.getMemberId());
        System.out.println(apply.getMemberId().getMemberNo());
        return applyService.findBasicInfo(request ,apply.getMemberId().getMemberNo());
    }

    // 자격증, 경력 조회
    @GetMapping("/apply/careerAndCertification/{id}")
    public ResponseEntity<MemberInfoResponse> applyCareerAndCertificationResponse(HttpServletRequest request, @PathVariable Long id) {
        // Apply 엔티티에서 applyNo를 기반으로 member 조회
        Apply apply = applyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Apply not found with ID: " + id));
        // memberNo를 사용하여 경력 및 자격증 정보 조회
        System.out.println(apply.getMemberId());
        System.out.println(apply.getMemberId().getMemberNo());
        return applyService.findCareerAndCertification(request ,apply.getMemberId().getMemberNo());
    }
}
