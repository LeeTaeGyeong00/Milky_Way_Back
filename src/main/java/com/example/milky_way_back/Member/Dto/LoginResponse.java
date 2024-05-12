package com.example.milky_way_back.Member.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // 로그인을 응답
public class LoginResponse {
    private String accessToken;
    private String memberName;
    private String memberId;
    private Long memberNo;
}
