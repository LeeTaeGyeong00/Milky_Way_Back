package com.example.milky_way_back.article.DTO.response;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.article.entity.Article;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ArticleViewResponse {
    private final Long article_no;
    private final String articleType;
    private final String title;
    private final Member member;

    private final String content;
    private final String metorTag;
    private final String startDay;
    private final String endDay;
    private final int apply;
    private final int applyNow;
    private final int likes;
    private final boolean findMentor;
    private final LocalDateTime regDate;
    private String conMethod;
    private String conInfo;
    private boolean recurit;
    public ArticleViewResponse(Article article){
        this.article_no = article.getArticle_no();
        this.articleType = article.getArticleType();
        this.title = article.getTitle();
        this.member = article.getMemberId();
        this.content = article.getContent();
        this.metorTag = article.getMentorTag();
        this.startDay = article.getStartDay();
        this.endDay = article.getEndDay();
        this.apply = article.getApply();
        this.applyNow = article.getApplyNow();
        this.likes = article.getLikes();
        this.findMentor = article.isFindMentor();
        this.recurit = article.getRecruit();
        this.conMethod = article.getConMethod();
        this.conInfo = article.getConInfo();
        this.regDate = article.getRegDate();
    }
}
