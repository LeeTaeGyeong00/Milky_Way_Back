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
public class MyPageApplyResponse {
    private Long apply_no;
    private Article article;
    private LocalDateTime applyDate;
    private String applyResult;
}
