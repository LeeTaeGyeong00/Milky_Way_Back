package com.example.milky_way_back.article.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

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
