package com.example.milky_way_back.article.DTO.response;

import com.example.milky_way_back.article.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyPageArticleResponse {
    private Long article_no;
    private String articleType;
    private String title;
    private boolean findMentor;
    private boolean recruit;
    private int apply;
    private int applyNow;
    private int likes;
}
