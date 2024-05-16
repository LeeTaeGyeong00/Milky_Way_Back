package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.resume.dto.BasicInfoDto;
import com.example.milky_way_back.resume.dto.CareerAndCertificationDto;
import com.example.milky_way_back.resume.dto.MemberInfoResponse;
import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import com.example.milky_way_back.resume.repository.BasicInfoRepository;
import com.example.milky_way_back.resume.repository.CareerRepository;
import com.example.milky_way_back.resume.repository.CertificationRepository;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentResumeService {
    private final MemberRepository memberRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final CareerRepository careerRepository;
    private final CertificationRepository certificationRepository;

    // 기본 정보 저장
    public ResponseEntity<StatusResponse> updateBasicInfo(BasicInfoDto basicInfoDto, UserDetails userDetails) {

        String memberId = userDetails.getUsername(); // 접속한 사람의 아이디 가져오기
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        BasicInfo basicInfo = BasicInfo.builder()
                .studentresumeMajor(basicInfoDto.getStudentMajor())
                .studentResumeLocate(basicInfoDto.getStudentLocate())
                .member(member)
                .build();

        basicInfoRepository.save(basicInfo);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "사용자 정보 저장 완료"));
    }

    // 경력, 자격증 저장
    public ResponseEntity<StatusResponse> updateCarCert(CareerAndCertificationDto careerAndCertificationDto, UserDetails userDetails) {
        String memberId = userDetails.getUsername();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        Career career = Career.builder()
                .carName(careerAndCertificationDto.getCarName())
                .carStartDay(careerAndCertificationDto.getCarStartDay())
                .carEndDay(careerAndCertificationDto.getCarEndDay())
                .member(member)
                .build();

        Certification certification = Certification.builder()
                .certName(careerAndCertificationDto.getCertName())
                .certDate(careerAndCertificationDto.getCertDate())
                .member(member)
                .build();

        careerRepository.save(career);
        certificationRepository.save(certification);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "경력/자격증 저장 완료"));

    }

    public ResponseEntity<MemberInfoResponse> findAll(UserDetails userDetails) {

        String memberId = userDetails.getUsername();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();
        List<BasicInfo> basicInfos = basicInfoRepository.findByMember(member);
        List<Career> careers = careerRepository.findByMember(member);
        List<Certification> certifications = certificationRepository.findByMember(member);

        return ResponseEntity.status(HttpStatus.OK).body(new MemberInfoResponse(basicInfos, careers, certifications));

    }


}
