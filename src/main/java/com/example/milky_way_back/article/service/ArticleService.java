package com.example.milky_way_back.article.service;

import com.example.milky_way_back.article.DTO.request.LikeRequest;
import com.example.milky_way_back.article.DTO.response.ArticleListView;
import com.example.milky_way_back.article.DTO.response.ArticleViewResponse;
import com.example.milky_way_back.article.DTO.response.LikeResponse;
import com.example.milky_way_back.article.entity.Dibs;
import com.example.milky_way_back.article.exception.ArticleNotFoundException;
import com.example.milky_way_back.article.exception.DuplicateLikeException;
import com.example.milky_way_back.article.exception.UnauthorizedException;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.repository.DibsRepository;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.AddArticle;
import com.example.milky_way_back.article.DTO.response.MyPageArticleResponse;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.repository.ArticleRepository;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ArticleService {

    private final TokenProvider tokenProvider;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ApplyRepository applyRepository;
    private final DibsRepository dibsRepository;

    public Article save(HttpServletRequest request, AddArticle addArticle) {
//        // SecurityContext에서 인증 정보 가져오기
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        Authentication authentication = securityContext.getAuthentication();
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // AddArticle에 회원 정보 설정
            addArticle.setMemberId(memberId);
            // Article 엔티티로 변환
            Article article = addArticle.toEntity(member);
            // 게시물 저장
            return articleRepository.save(article);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }

    //게시글 조회
    public Page<ArticleListView> findAll(HttpServletRequest request, Pageable pageable) {
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            Page<Article> articlePage = articleRepository.findAll(pageable);

            List<ArticleListView> articles = articlePage.getContent()
                    .stream()
                    .map(ArticleListView::new)
                    .collect(Collectors.toList());

            return new PageImpl<>(articles, pageable, articlePage.getTotalElements());
        } else {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }

//    public ArticleViewResponse findById(long id) {
//        // SecurityContext에서 인증 정보 가져오기
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        Authentication authentication = securityContext.getAuthentication();
//
//        // 인증 정보에서 회원 ID 가져오기
//        String memberId = authentication.getName();
//        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
//        if (optionalMember.isPresent()) {
//            Article article = articleRepository.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//
//            return new ArticleViewResponse(article);
//        } else {
//            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
//            throw new MemberNotFoundException("Member not found with ID: " + memberId);
//        }
    //    }
    public ArticleViewResponse findById(HttpServletRequest request,long id) {
        // 토큰에서 인증 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        // 회원 ID로 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));

        // 게시물 조회
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        // 게시물 작성자와 요청한 사용자가 동일한지 확인
        boolean isAuthor = article.getMemberId().getMemberNo().equals(member.getMemberNo());

        // 사용자가 해당 게시판에 지원했는지 확인
        boolean isApplier = applyRepository.findByArticleAndMemberId(article, member).isPresent();

        // 사용자가 해당 게시물에 좋아요를 눌렀는지 확인
        boolean isLike = dibsRepository.findByArticleNoAndMemberNo(article, member).isPresent();


        // ArticleViewResponse 생성 및 isAuthor 설정
        ArticleViewResponse response = new ArticleViewResponse(article);
        response.setAuthor(isAuthor);
        response.setApplier(isApplier);
        response.setLike(isLike);

        return response;
    }
    public void delete(long id){
        articleRepository.deleteById(id);
    }
    public Article updateRecruit(HttpServletRequest request, long id) {
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        if (article.getMemberId().getMemberId().equals(memberId)) {
            article.setRecruit(false);
            return articleRepository.save(article);
        } else {
            throw new UnauthorizedException("You are not authorized to update this article");
        }
    }

    public List<MyPageArticleResponse> getArticlesByMemberId(HttpServletRequest request) {
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
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
                myPageArticleResponse.setCardArticle_no(article.getArticle_no());
                myPageArticleResponse.setCardArticleType(article.getArticleType());
                myPageArticleResponse.setCardTitle(article.getTitle());
                myPageArticleResponse.setCardFindMentor(article.isFindMentor());
                myPageArticleResponse.setCardRecruit(article.getRecruit());
                myPageArticleResponse.setCardApply(article.getApply());
                myPageArticleResponse.setCardApplyNow(article.getApplyNow());
                myPageArticleResponse.setCardLikes(article.getLikes());
                myPageArticleResponse.setCardStartDay(article.getStartDay());
                myPageArticleResponse.setCardEndDay(article.getEndDay());
                // 나머지 필드도 필요에 따라 추가
                myPageArticles.add(myPageArticleResponse);
            }

            return myPageArticles;
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }
    @Transactional
    public LikeResponse likeArticle(HttpServletRequest request, Long articleId) {
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }

        Member member = optionalMember.get();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + articleId));

        Optional<Dibs> optionalDibs = dibsRepository.findByMemberNoAndArticleNo(member, article);

        if (optionalDibs.isPresent()) {
            throw new DuplicateLikeException("Member has already liked this article.");
        }

        // 좋아요 수 증가
        article.setLikes(article.getLikes() + 1);
        articleRepository.save(article);

        // Dibs 테이블에 추가
        Dibs dibs = Dibs.builder()
                .memberNo(member)
                .articleNo(article)
                .build();
        dibsRepository.save(dibs);

        LikeResponse response = new LikeResponse();
        response.setArticleNo(articleId);
        response.setLikeCount(article.getLikes());
        return response;
    }
}
