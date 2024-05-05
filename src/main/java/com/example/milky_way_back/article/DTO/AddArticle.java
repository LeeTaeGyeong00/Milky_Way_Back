package com.example.milky_way_back.article.DTO;

import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.entity.Member;
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
    //private Member member;
    private String userNo;
    private String content;
    private String mentorTag;
    private String startDay;
    private String endDay;
    private int apply;
    private boolean findMentor;
    public Article toEntity(){
        return Article.builder()
                //.member(member)
                .userNo(userNo)
                .articleType(articleType)
                .title(title)
                .content(content)
                .apply(apply)
                .startDay(startDay)
                .endDay(endDay)
                .findMentor(findMentor)
                .mentorTag(mentorTag)
                .build();
    }
}
