package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.resume.dto.BasicInfoDto;
import com.example.milky_way_back.resume.dto.CareerAndCertificationDto;
import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.repository.BasicInfoRepository;
import com.example.milky_way_back.resume.repository.CareerRepository;
import com.example.milky_way_back.resume.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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


}
