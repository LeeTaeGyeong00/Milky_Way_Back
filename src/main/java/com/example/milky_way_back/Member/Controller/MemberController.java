package com.example.milky_way_back.Member.Controller;

import com.example.milky_way_back.Member.Dto.*;
import com.example.milky_way_back.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<StatusResponse> signup(@RequestBody SignupRequest request) {
        return memberService.signup(request);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   @RequestHeader(value = "Authorization", required = false) String accessToken) {
        return memberService.login(request, accessToken);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<StatusResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        return memberService.logout(logoutRequest);
    }
}