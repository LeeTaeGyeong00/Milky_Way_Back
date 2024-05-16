package com.example.milky_way_back.member.Dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String memberId;
    private String memberPassword;
}
