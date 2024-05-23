package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.resume.dto.BasicInfoDto;
import com.example.milky_way_back.resume.dto.CareerAndCertificationDto;
import com.example.milky_way_back.resume.dto.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class StudentResumeController {

    private final StudentResumeService studentResumeService;


    // 기본 정보 등록
    @PostMapping("/member/update/info")
    public ResponseEntity<StatusResponse> updateInfo(HttpServletRequest request, @RequestBody BasicInfoDto basicInfoDto) {
        return studentResumeService.updateBasicInfo(basicInfoDto, request);
    }

    // 기본 정보 수정
    @PutMapping("/member/modify/info")
    public ResponseEntity<StatusResponse> modifyInfo(HttpServletRequest request, @RequestBody BasicInfoDto basicInfoDto) {
        studentResumeService.modifyBasicInfo(request);
        return studentResumeService.updateBasicInfo(basicInfoDto, request);
    }

    // 경력, 자격증 저장
    @PostMapping("/member/update/profile")
    public ResponseEntity<StatusResponse> updateCareerAndCertification(HttpServletRequest request, @RequestBody CareerAndCertificationDto careerAndCertificationDto) {
        return studentResumeService.updateCarCert(careerAndCertificationDto, request);
    }

    // 경력, 자격증 수정
    @PutMapping("/member/modify/profile")
    public ResponseEntity<StatusResponse> modifyCareerAndCertification(HttpServletRequest request, @RequestBody CareerAndCertificationDto careerAndCertificationDto) {
        studentResumeService.modifyCarrerAndCertification(request);
        return studentResumeService.updateCarCert(careerAndCertificationDto, request);
    }

    // 기본 정보, 경력, 자격증 조회
    @GetMapping("/myResume")
    public ResponseEntity<MemberInfoResponse> memberInfoResponse(HttpServletRequest request) {
        return studentResumeService.findAll(request);
    }


}
