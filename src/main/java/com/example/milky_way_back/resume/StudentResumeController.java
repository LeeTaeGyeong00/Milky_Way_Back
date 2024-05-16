package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.resume.dto.BasicInfoDto;
import com.example.milky_way_back.resume.dto.CareerAndCertificationDto;
import com.example.milky_way_back.resume.dto.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StudentResumeController {

    private final StudentResumeService studentResumeService;

    // 기본 정보 등록
    @PostMapping("/member/updateInfo")
    public ResponseEntity<StatusResponse> updateInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BasicInfoDto basicInfoDto) {
        return studentResumeService.updateBasicInfo(basicInfoDto, userDetails);
    }

    // 경력, 자격증 저장
    @PostMapping("/member/updateCareerAndCertification")
    public ResponseEntity<StatusResponse> updateCareerAndCertification(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CareerAndCertificationDto careerAndCertificationDto) {
        return studentResumeService.updateCarCert(careerAndCertificationDto, userDetails);
    }

    // 기본 정보, 경력, 자격증 조회
    @GetMapping("/myResume")
    public ResponseEntity<MemberInfoResponse> memberInfoResponse(@AuthenticationPrincipal UserDetails userDetails) {
        return studentResumeService.findAll(userDetails);
    }


}
