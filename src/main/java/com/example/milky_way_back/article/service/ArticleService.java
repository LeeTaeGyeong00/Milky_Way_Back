package com.example.milky_way_back.article.service;

import com.example.milky_way_back.article.DTO.response.ArticleViewResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.AddArticle;
import com.example.milky_way_back.article.DTO.response.MyPageArticleResponse;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.repository.ArticleRepository;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Transactional
public class ArticleService {

    private final TokenProvider tokenProvider;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    public Article save(AddArticle request) {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // AddArticle에 회원 정보 설정
            request.setMemberId(memberId);
            // Article 엔티티로 변환
            Article article = request.toEntity(member);
            // 게시물 저장
            return articleRepository.save(article);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }

    //게시글 조회
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

//    public Article findById (long id) {
//        // SecurityContext에서 인증 정보 가져오기
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        Authentication authentication = securityContext.getAuthentication();
//
//        // 인증 정보에서 회원 ID 가져오기
//        String memberId = authentication.getName();
//        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
//        if (optionalMember.isPresent()) {
//            return articleRepository.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//        } else {
//            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
//            throw new MemberNotFoundException("Member not found with ID: " + memberId);
//        }
//
//    }
    public ArticleViewResponse findById(long id) {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            Article article = articleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

            return new ArticleViewResponse(article);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }
    public void delete(long id){
        articleRepository.deleteById(id);
    }
//    @Transactional
//    public Article update(long id, UpdateRequest request) {
//        Article article = articleRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found: "+id));
//
//        article.update(request.getTitle(), request.getContent());
//        return article;
//    } 수정 넣을건가요?
// 게시글 업데이트
    public Article updateRecruit(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        article.setRecruit(false);
        return article;
    }

    public List<MyPageArticleResponse> getArticlesByMemberId() {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();

        // 회원 ID로 회원을 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            // 회원이 존재하는 경우, 해당 회원이 작성한 모든 아티클을 조회
            Member member = optionalMember.get();
            List<Article> articles = articleRepository.findByMemberId(member);

            // Article을 MyPageArticleResponse로 변환하여 리스트에 추가
            List<MyPageArticleResponse> myPageArticles = new ArrayList<>();
            for (Article article : articles) {
                MyPageArticleResponse myPageArticleResponse = new MyPageArticleResponse();
                myPageArticleResponse.setArticle_no(article.getArticle_no());
                myPageArticleResponse.setArticleType(article.getArticleType());
                myPageArticleResponse.setTitle(article.getTitle());
                myPageArticleResponse.setFindMentor(article.isFindMentor());
                myPageArticleResponse.setRecruit(article.getRecruit());
                myPageArticleResponse.setApply(article.getApply());
                myPageArticleResponse.setApplyNow(article.getApplyNow());
                myPageArticleResponse.setLikes(article.getLikes());
                // 나머지 필드도 필요에 따라 추가
                myPageArticles.add(myPageArticleResponse);
            }

            return myPageArticles;
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }
}
