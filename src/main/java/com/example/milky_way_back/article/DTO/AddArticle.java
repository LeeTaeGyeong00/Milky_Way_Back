package com.example.milky_way_back.article.DTO;

import com.example.milky_way_back.article.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticle {
    private String articleType;
    private String title;
    private String content;
    private String metorTag;
    private LocalDateTime startDay;
    private LocalDateTime endDay;
    private int apply;
    private boolean findMentor;
    public Article toEntity(){
        return Article.builder()
                .articleType(articleType)
                .title(title)
                .content(content)
                .metorTag(metorTag)
                .startDay(startDay)
                .endDay(endDay)
                .apply(apply)
                .findMentor(findMentor)
                .build();
    }
}
