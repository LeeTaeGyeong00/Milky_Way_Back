package com.example.milky_way_back.member.Controller;


import com.example.milky_way_back.member.Dto.*;
import com.example.milky_way_back.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/logout")
    public ResponseEntity<StatusResponse> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }

    // 리프레시 토큰
    @PostMapping("/reissue")
    public ResponseEntity<TokenRequestAndResponseDto> reissue(@RequestBody TokenRequestAndResponseDto tokenRequestDto) {
        return ResponseEntity.ok(memberService.reissue(tokenRequestDto));
    }

}