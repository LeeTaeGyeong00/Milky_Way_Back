package com.example.milky_way_back.article.DTO.request;


import lombok.Getter;
import lombok.Setter;


public class UpdateArticleRequest {
    private Boolean recruit;

    public Boolean getRecruit() {
        return recruit;
    }

    public void setRecruit(Boolean recruit) {
        this.recruit = recruit;
    }

}
