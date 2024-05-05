package com.example.milky_way_back.article.controller;

import com.example.milky_way_back.article.DTO.ArticleViewResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@RequiredArgsConstructor
@Controller
public class ArticleController {
//    private final ArticleService articleService;
//
//    //새 게시물 작성
//    @GetMapping("/new-article")
//    public String newBoard(@RequestParam(required = false) Long id, Model model){
//
//        if(id == null) {
//            model.addAttribute("article", new ArticleViewResponse());
//        } else {
//            Article article = articleService.findById(id);
//            model.addAttribute("article", new ArticleViewResponse(article));
//        }
//
//        return "newBoard";
//    }
}
