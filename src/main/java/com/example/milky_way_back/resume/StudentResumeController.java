package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.resume.dto.BasicInfoReqeustDto;
import com.example.milky_way_back.resume.dto.BasicInfoResponse;
import com.example.milky_way_back.resume.dto.CareerAndCertificationReqeustDto;
import com.example.milky_way_back.resume.dto.CareerAndCertificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class StudentResumeController {

    private final StudentResumeService studentResumeService;


    // 기본 정보 등록
    @PostMapping("/member/update/info")
    public ResponseEntity<StatusResponse> updateInfo(HttpServletRequest request, @RequestBody BasicInfoReqeustDto basicInfoReqeustDto) {
        return studentResumeService.updateBasicInfo(basicInfoReqeustDto, request);
    }

    // 기본 정보 수정
    @PutMapping("/member/modify/info")
    public ResponseEntity<StatusResponse> modifyInfo(HttpServletRequest request, @RequestBody BasicInfoReqeustDto basicInfoReqeustDto) {
        studentResumeService.modifyBasicInfo(request);
        return studentResumeService.updateBasicInfo(basicInfoReqeustDto, request);
    }

    // 경력, 자격증 저장
    @PostMapping("/member/update/profile")
    public ResponseEntity<StatusResponse> updateCareerAndCertification(HttpServletRequest request, @RequestBody CareerAndCertificationReqeustDto careerAndCertificationDto) {
        return studentResumeService.updateCarCert(careerAndCertificationDto, request);
    }

    // 경력, 자격증 수정
    @PutMapping("/member/modify/profile")
    public ResponseEntity<StatusResponse> modifyCareerAndCertification(HttpServletRequest request, @RequestBody CareerAndCertificationReqeustDto careerAndCertificationDto) {
        studentResumeService.modifyCareerAndCertification(request);
        return studentResumeService.updateCarCert(careerAndCertificationDto, request);
    }

    // 기본 정보 조회
    @GetMapping("/myResume/basicInfo")
    public ResponseEntity<BasicInfoResponse> memberInfoResponse(HttpServletRequest request) {
        return studentResumeService.findBasicInfo(request);
    }

    // 자격증, 경력 조회
    @GetMapping("/myResume/careerAndCertification")
    public ResponseEntity<CareerAndCertificationResponse> memberCareerAndCertificationResponse(HttpServletRequest request) {
        return studentResumeService.findCareerAndCertification(request);
    }

}
