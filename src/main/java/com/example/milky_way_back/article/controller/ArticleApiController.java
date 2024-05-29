package com.example.milky_way_back.article.controller;

import com.example.milky_way_back.article.DTO.response.LikeResponse;
import com.example.milky_way_back.article.exception.DuplicateLikeException;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.repository.DibsRepository;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.AddArticle;
import com.example.milky_way_back.article.DTO.response.ArticleListView;
import com.example.milky_way_back.article.DTO.response.ArticleViewResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.exception.UnauthorizedException;
import com.example.milky_way_back.article.service.ArticleService;
import com.example.milky_way_back.member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController //http 응답으로 객체 데이터를 json 형태로 변환
public class ArticleApiController {
    private final ArticleService articleService;
    private final MemberRepository memberRepository;

        // 게시글 추가
    @PostMapping("/posts/write")
    public ResponseEntity<Article> addBoard(HttpServletRequest request, @RequestBody AddArticle addArticle) {
        Article savedArticle = articleService.save(request, addArticle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }
    //http://localhost:8080/posts/list?page=1&size=30
    //http://localhost:8080/posts/list?page={page-id}&size={size-id}
    //기본값
    @GetMapping("/posts/list")
    public ResponseEntity<Page<ArticleListView>> getArticles(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        if (userDetails == null) {
            // 사용자가 인증되지 않은 경우
            throw new UnauthorizedException("사용자가 인증되지 않았습니다.");
        }
        // Jwt 토큰에서 회원 정보를 가져옴
        String accessToken = extractTokenFromRequest(request);
        if (accessToken == null) {
            throw new UnauthorizedException("토큰이 유효하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleService.findAll(pageable);

        List<ArticleListView> articles = articlePage.getContent()
                .stream()
                .map(ArticleListView::new)
                .collect(Collectors.toList());

        Page<ArticleListView> articleListViewPage = new PageImpl<>(articles, pageable, articlePage.getTotalElements());

        return ResponseEntity.ok(articleListViewPage);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<ArticleViewResponse> getArticleById(@PathVariable long id) {
        try {
            ArticleViewResponse articleDTO = articleService.findById(id);
            return ResponseEntity.ok(articleDTO);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    //DELETE
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        System.out.println(id);
        articleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/posts/done/{id}")
    public ResponseEntity<ArticleViewResponse> updateRecruit(@PathVariable long id) {
        Article updatedArticle = articleService.updateRecruit(id);
        return ResponseEntity.ok().body(new ArticleViewResponse(updatedArticle));
    }
    // HttpServletRequest에서 JWT 토큰 추출하는 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출
        }
        return null;
    }


    @PostMapping("/posts/likes/{id}")
    public ResponseEntity<LikeResponse> likeArticle(@PathVariable("id") Long id) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberNo = authentication.getName();

            LikeResponse response = articleService.likeArticle(id, memberNo);
            return ResponseEntity.ok(response);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (DuplicateLikeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

//package com.example.milky_way_back.article.controller;
//
//import com.example.milky_way_back.article.DTO.request.AddArticle;
//import com.example.milky_way_back.article.DTO.response.ArticleListView;
//import com.example.milky_way_back.article.DTO.response.ArticleViewResponse;
//import com.example.milky_way_back.article.DTO.response.LikeResponse;
//import com.example.milky_way_back.article.entity.Article;
//import com.example.milky_way_back.article.exception.DuplicateLikeException;
//import com.example.milky_way_back.article.exception.MemberNotFoundException;
//import com.example.milky_way_back.article.exception.UnauthorizedException;
//import com.example.milky_way_back.article.service.ArticleService;
//import com.example.milky_way_back.member.Repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@RestController
//public class ArticleApiController {
//
//    private final ArticleService articleService;
//
//    // 게시글 추가
//    @PostMapping("/posts/write")
//    public ResponseEntity<Article> addBoard(HttpServletRequest request, @RequestBody AddArticle addArticle) {
//        Article savedArticle = articleService.save(request, addArticle);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
//    }
//
//    // 게시글 목록 조회
//    @GetMapping("/posts/list")
//    public ResponseEntity<Page<ArticleListView>> getArticles(HttpServletRequest request,
//                                                             @RequestParam(value = "page", defaultValue = "0") int page,
//                                                             @RequestParam(value = "size", defaultValue = "10") int size) {
//        validateRequest(request);
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Article> articlePage = articleService.findAll(pageable);
//        List<ArticleListView> articles = articlePage.getContent().stream().map(ArticleListView::new).collect(Collectors.toList());
//        Page<ArticleListView> articleListViewPage = new PageImpl<>(articles, pageable, articlePage.getTotalElements());
//        return ResponseEntity.ok(articleListViewPage);
//    }
//
//    // 게시글 조회
//    @GetMapping("/posts/{id}")
//    public ResponseEntity<ArticleViewResponse> getArticleById(HttpServletRequest request, @PathVariable long id) {
//        validateRequest(request);
//        try {
//            ArticleViewResponse articleDTO = articleService.findById(id);
//            return ResponseEntity.ok(articleDTO);
//        } catch (MemberNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    // 게시글 삭제
//    @DeleteMapping("/posts/{id}")
//    public ResponseEntity<Void> deleteArticle(HttpServletRequest request, @PathVariable long id) {
//        validateRequest(request);
//        articleService.delete(id);
//        return ResponseEntity.ok().build();
//    }
//
//    // 게시글 업데이트 (모집 완료 표시)
//    @PutMapping("/posts/done/{id}")
//    public ResponseEntity<ArticleViewResponse> updateRecruit(HttpServletRequest request, @PathVariable long id) {
//        validateRequest(request);
//        Article updatedArticle = articleService.updateRecruit(id);
//        return ResponseEntity.ok().body(new ArticleViewResponse(updatedArticle));
//    }
//
//    // 게시글 좋아요
//    @PostMapping("/posts/likes/{id}")
//    public ResponseEntity<LikeResponse> likeArticle(HttpServletRequest request, @PathVariable("id") Long id) {
//        validateRequest(request);
//        try {
//            String memberNo = getCurrentMemberNo(request);
//            LikeResponse response = articleService.likeArticle(id, memberNo);
//            return ResponseEntity.ok(response);
//        } catch (MemberNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        } catch (DuplicateLikeException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    // HttpServletRequest에서 JWT 토큰 추출
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        final String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출
//        }
//        return null;
//    }
//
//    // JWT 토큰에서 사용자 정보 검증
//    private void validateRequest(HttpServletRequest request) {
//        String token = extractTokenFromRequest(request);
//        if (token == null || !isValidToken(token)) {
//            throw new UnauthorizedException("사용자가 인증되지 않았습니다.");
//        }
//    }
//
//    // JWT 토큰 유효성 검사
//    private boolean isValidToken(String token) {
//        // JWT 토큰 유효성 검사 로직을 여기에 추가
//        // 예: JWT 유효성 검사, 토큰 만료 확인 등
//        return true;
//    }
//
//    // 현재 인증된 사용자 정보 가져오기
//    private String getCurrentMemberNo(HttpServletRequest request) {
//        String token = extractTokenFromRequest(request);
//        // JWT 토큰에서 사용자 정보 추출
//        return "extractedMemberNo"; // 실제 구현 필요
//    }
//}
