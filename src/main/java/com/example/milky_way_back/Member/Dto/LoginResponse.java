package com.example.milky_way_back.Member.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // 로그인을 응답하는 클래스
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String memberName;
    private String memberId;
    private Long memberNo;
}
