package com.example.milky_way_back.article.service;

import com.example.milky_way_back.article.DTO.MemberDTO;
import com.example.milky_way_back.article.DTO.ArticleDTO;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.exception.DuplicateApplyException;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.ApplyRequest;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.DTO.response.MyPageApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.exception.ArticleNotFoundException;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.repository.ArticleRepository;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApplyService {
    private final TokenProvider tokenProvider;
    private final ApplyRepository applyRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    //    public List<Apply> findAll() {
//        return applyRepository.findAll();
//    }
    @Transactional
    //회원 번호와 게시글 번호를 받아 지원 정보를 처리하는 메서드
    public Apply apply(String accessToken, Long articleNo, ApplyRequest request) {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            System.out.println(member);
            // 회원과 게시글 번호를 기반으로 기존 지원 내역 확인
            if (applyRepository.findByMemberAndArticleNo(member, articleNo).isPresent()) {
                throw new DuplicateApplyException("Member has already applied to this article.");
            }
            // AddArticle에 회원 정보 설정
            Apply apply = Apply.builder()
                    .article(articleRepository.findById(articleNo).orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + articleNo)))
                    .member(member) // Set the Member object directly
                    .build();
            // 게시물 저장
            return applyRepository.save(apply);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }


    public List<ApplyResponse> findMemberNamesByArticleNo(Long article_no) {
        return applyRepository.findMemberNamesByArticleNo(article_no);
    }

    public List<MyPageApplyResponse> getAppliesByMemberId() {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();

        // 회원 ID로 회원을 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            // 회원이 존재하는 경우, 해당 회원이 신청한 모든 apply를 조회
            Member member = optionalMember.get();
            List<Apply> applies = applyRepository.findByMemberId(member);

            // Apply를 MyPageApplyResponse로 변환하여 리스트에 추가
            List<MyPageApplyResponse> myPageApplies = new ArrayList<>();
            for (Apply apply : applies) {
                MyPageApplyResponse myPageApplyResponse = new MyPageApplyResponse();
                myPageApplyResponse.setApplyNo(apply.getApply_no());
                myPageApplyResponse.setApplyDate(apply.getApplyDate());
                myPageApplyResponse.setApplyResult(apply.getApplyResult());

// Apply의 Article 정보를 가져옴
                Article article = articleRepository.findById(apply.getArticle().getArticle_no())
                        .orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + apply.getArticle().getArticle_no()));

                // Convert Article entity to MyPageArticleDTO
                ArticleDTO articleDTO = new ArticleDTO();
                articleDTO.setArticleNo(article.getArticle_no());
                articleDTO.setTitle(article.getTitle());
                articleDTO.setConMethod(article.getConMethod());
                articleDTO.setConInfo(article.getConInfo());

                // Convert Member entity to ApplyMemberDTO
                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setMemberNo(apply.getArticle().getMemberId().getMemberNo());
                memberDTO.setMemberName(apply.getArticle().getMemberId().getMemberName());

                articleDTO.setMember(memberDTO);

                myPageApplyResponse.setApplyArticle(articleDTO);

                myPageApplies.add(myPageApplyResponse);
            }
            return myPageApplies;
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }
}

