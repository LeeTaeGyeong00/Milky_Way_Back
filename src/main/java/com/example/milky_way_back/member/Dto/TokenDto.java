package com.example.milky_way_back.member.Dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class TokenDto {
    private String grantType; // 형식 Bearer
    private String accessToken;
    private String refreshToken;
}
