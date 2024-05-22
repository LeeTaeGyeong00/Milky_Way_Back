package com.example.milky_way_back.member.Controller;


import com.example.milky_way_back.member.Dto.*;
import com.example.milky_way_back.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(memberService.login(loginRequest));
    }

    // 중복 확인
    @PostMapping("/signup/checkId")
    public ResponseEntity<StatusResponse> checkId(@RequestBody IdRequest idRequest) {
        return memberService.duplicationIdCheck(idRequest);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<StatusResponse> logout(HttpServletRequest request) {
        return memberService.logout();
    }

    // 리프레시 토큰
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
        return ResponseEntity.ok(memberService.reissue(tokenDto));
    }

}