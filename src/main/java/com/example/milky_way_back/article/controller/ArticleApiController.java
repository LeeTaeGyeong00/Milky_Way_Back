package com.example.milky_way_back.article.controller;

import com.example.milky_way_back.article.exception.MemberNotFoundException;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@PostMapping("/posts/write")
public ResponseEntity<Article> addBoard(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody AddArticle addArticle,
                                        HttpServletRequest request) {
    if (userDetails == null) {
        // 사용자가 인증되지 않은 경우
        throw new UnauthorizedException("사용자가 인증되지 않았습니다.");
    }
    // Jwt 토큰에서 회원 정보를 가져옴
    String accessToken = extractTokenFromRequest(request);
    if (accessToken == null) {
        throw new UnauthorizedException("토큰이 유효하지 않습니다.");
    }
    // 회원 정보를 사용하여 게시글 저장
    Article savedArticle = articleService.save(addArticle);
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

//    @GetMapping("/posts/{id}")
//    public ResponseEntity<ArticleViewResponse> findBoard(@PathVariable long id
//    ){
//        Article article = articleService.findById(id);
//        return ResponseEntity.ok().body(new ArticleViewResponse(article));
//    }
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
}
