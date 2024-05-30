package com.example.milky_way_back.article.DTO.response;

import com.example.milky_way_back.article.DTO.MemberDTO;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.article.entity.Article;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleViewResponse {
    private final Long article_no;
    private final String articleType;
    private final String title;
    private final MemberDTO member;
    private final String content;
    private final String mentorTag;
    private final String startDay;
    private final String endDay;
    private final int apply;
    private final int applyNow;
    private final int likes;
    private final boolean findMentor;
    private final LocalDateTime regDate;
    private String conMethod;
    private String conInfo;
    private boolean recruit;
    @JsonProperty("isAuthor")
    private boolean isAuthor;
    @JsonProperty("isApplier")
    private boolean isApplier;
    @JsonProperty("isLike")
    private boolean isLike;
    public ArticleViewResponse(Article article){
        this.article_no = article.getArticle_no();
        this.articleType = article.getArticleType();
        this.title = article.getTitle();

        Member memberEntity = article.getMemberId();
        this.member = new MemberDTO(memberEntity.getMemberNo(), memberEntity.getMemberName());

        this.content = article.getContent();
        this.mentorTag = article.getMentorTag();
        this.startDay = article.getStartDay();
        this.endDay = article.getEndDay();
        this.apply = article.getApply();
        this.applyNow = article.getApplyNow();
        this.likes = article.getLikes();
        this.findMentor = article.isFindMentor();
        this.recruit = article.getRecruit();
        this.conMethod = article.getConMethod();
        this.conInfo = article.getConInfo();
        this.regDate = article.getRegDate();
    }
}
