package com.example.milky_way_back.member.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequestDto {
    String accessToken;
    String refreshToken;
}
