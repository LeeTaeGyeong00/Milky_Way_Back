package com.example.milky_way_back.Member.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String memberId;
    private String accessToken;
}
