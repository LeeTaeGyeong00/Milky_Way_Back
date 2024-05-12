package com.example.milky_way_back.Member.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccessTokenResponse {
    private String accessToken; // 토큰만 반환
}
