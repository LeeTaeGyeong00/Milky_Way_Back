package com.example.milky_way_back.article.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplyResponse {
    private Long applyNo;
    private Long articleNo;
    private String memberName;
    private LocalDateTime applyDate;
    private String applyResult;
}
