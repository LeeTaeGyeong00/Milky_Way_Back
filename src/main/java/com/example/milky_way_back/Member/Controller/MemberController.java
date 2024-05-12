package com.example.milky_way_back.Member.Controller;

import com.example.milky_way_back.Member.Dto.*;
import com.example.milky_way_back.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<StatusResponse> signup(@RequestBody SignupRequest request) {
        return memberService.signup(request);
    }

    // 회원가입 시 아이디 중복 확인
    @PostMapping("/signup/duplicationCheck")
    public ResponseEntity<StatusResponse> dublicationCheck(@RequestBody IdRequest idRequest) {
        return memberService.signupIdDuplication(idRequest);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return memberService.login(loginRequest, request);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<StatusResponse> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }
}