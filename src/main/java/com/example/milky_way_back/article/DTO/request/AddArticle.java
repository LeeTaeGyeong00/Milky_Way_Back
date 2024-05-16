package com.example.milky_way_back.article.DTO.request;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.article.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddArticle {
    private String articleType;
    private String title;
    private String memberId;
    private String content;
    private String mentorTag;
    private String startDay;
    private String endDay;
    private int apply;
    private boolean findMentor;
    private String conMethod;
    private String conInfo;
    public Article toEntity(Member member){
        return Article.builder()
                .memberId(member)
                .articleType(articleType)
                .title(title)
                .content(content)
                .apply(apply)
                .startDay(startDay)
                .endDay(endDay)
                .findMentor(findMentor)
                .mentorTag(mentorTag)
                .conMethod(conMethod)
                .conInfo(conInfo)
                .build();
    }
}
