package com.example.milky_way_back.article.DTO;

import com.example.milky_way_back.article.entity.Article;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ArticleViewResponse {
    private final Long article_no;
    private final String articleType;
    private final String title;
    //private final Member member;
    private final String userNo;
    private final String content;
    private final String metorTag;
    private final String startDay;
    private final String endDay;
    private final int apply;
    private final int applyNow;
    private final int likes;
    private final boolean findMentor;
    private final LocalDateTime regDate;
    public ArticleViewResponse(Article article){
        this.article_no = article.getArticle_no();
        this.articleType = article.getArticleType();
        this.title = article.getTitle();
        this.userNo = article.getUserNo();
        //this.member = article.getMember();
        this.content = article.getContent();
        this.metorTag = article.getMentorTag();
        this.startDay = article.getStartDay();
        this.endDay = article.getEndDay();
        this.apply = article.getApply();
        this.applyNow = article.getApplyNow();
        this.likes = article.getLikes();
        this.findMentor = article.isFindMentor();
        this.regDate = article.getRegDate();
    }
}
