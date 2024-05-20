package com.example.milky_way_back.member.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenRequestAndResponseDto {
    String accessToken;
}
