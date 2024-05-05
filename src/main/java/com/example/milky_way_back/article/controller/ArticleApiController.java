package com.example.milky_way_back.article.controller;

import com.example.milky_way_back.article.DTO.AddArticle;
import com.example.milky_way_back.article.DTO.ArticleListView;
import com.example.milky_way_back.article.DTO.ArticleViewResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController //http 응답으로 객체 데이터를 json 형태로 변환
public class ArticleApiController {
    private final ArticleService articleService;

    @PostMapping("/posts/edit") // method가 post형식의 /api/boards/ 요청이 들어오면 실행
    public ResponseEntity<Article> addBoard(@RequestBody AddArticle request){
        System.out.println(request.getArticleType());
        System.out.println(request.getTitle());
        System.out.println(request.getMentorTag());
        System.out.println(request.getUserNo());
        System.out.println(request.getApply());
        System.out.println(request.getEndDay());
        System.out.println(request.getUserNo());
        System.out.println(request.getContent());
        Article savedBoard = articleService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    @GetMapping("/posts?page={number}&size={data count}&sort={column}")
    public ResponseEntity<List<ArticleListView>> findAllBoards(){
        List<ArticleListView> articles = articleService.findAll(Pageable.unpaged()).stream().map(ArticleListView::new).collect(Collectors.toList());
        //List<ArticleListView> articles = articleService.findAll(Pageable.unpaged()).stream().map(ArticleListView::new).toList();
        //자바 16이상
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ArticleViewResponse> findBoard(@PathVariable long id){
        Article article = articleService.findById(id);
        return ResponseEntity.ok().body(new ArticleViewResponse(article));
    }
    //DELETE
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        System.out.println(id);
        articleService.delete(id);
        return ResponseEntity.ok().build();
    }
    //UPDATE
//    @PutMapping("/api/boards/{id}")
//    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateRequest request) {
//        Board updatedBoard = boardService.update(id, request);
//        return ResponseEntity.ok().body(updatedBoard);
//    } 수정 넣을건가요?
}
