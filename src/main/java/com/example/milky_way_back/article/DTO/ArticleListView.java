package com.example.milky_way_back.article.DTO;

import com.example.milky_way_back.article.entity.Article;
import lombok.Getter;

@Getter
public class ArticleListView {
    private final Long article_no;
    private final String articleType;
    private final String title;
    private final int apply;
    private final int applyNow;
    private final int likes;

    public ArticleListView(Article article){
        this.article_no = article.getArticle_no();
        this.articleType = article.getArticleType();
        this.title = article.getTitle();
        this.apply = article.getApply();
        this.applyNow = article.getApplyNow();
        this.likes = article.getLikes();
    }
}
