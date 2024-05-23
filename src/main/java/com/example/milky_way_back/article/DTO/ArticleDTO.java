package com.example.milky_way_back.article.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleDTO {
    private Long articleNo;
    private MemberDTO Member;
    private String Title;
    private String conMethod;
    private String conInfo;
}
