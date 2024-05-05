package com.example.milky_way_back.Member.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenRequest {
    private String grantType; // 형식 bearer
    private String accessToken;
    private String refreshToken;
}
