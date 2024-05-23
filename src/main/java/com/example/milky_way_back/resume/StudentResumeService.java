package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.resume.dto.BasicInfoReqeustDto;
import com.example.milky_way_back.resume.dto.BasicInfoResponse;
import com.example.milky_way_back.resume.dto.CareerAndCertificationReqeustDto;
import com.example.milky_way_back.resume.dto.CareerAndCertificationResponse;
import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import com.example.milky_way_back.resume.repository.BasicInfoRepository;
import com.example.milky_way_back.resume.repository.CareerRepository;
import com.example.milky_way_back.resume.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentResumeService {

    private final MemberRepository memberRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final CareerRepository careerRepository;
    private final CertificationRepository certificationRepository;
    private final TokenProvider tokenProvider;


    // 기본 정보 저장
    public ResponseEntity<StatusResponse> updateBasicInfo(BasicInfoReqeustDto basicInfoReqeustDto, HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        BasicInfo basicInfo = BasicInfo.builder()
                .studentresumeMajor(basicInfoReqeustDto.getStudentMajor())
                .studentResumeLocate(basicInfoReqeustDto.getStudentLocate())
                .studentResumeOnelineshow(basicInfoReqeustDto.getStudentOneLineShow())
                .member(member)
                .build();

        basicInfoRepository.save(basicInfo);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "사용자 정보 저장 완료"));
    }



    // 경력, 자격증 저장
    public ResponseEntity<StatusResponse> updateCarCert(CareerAndCertificationReqeustDto careerAndCertificationDto, HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기

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

    // 기본 정보 수정 시 기존 내용 삭제
    public void modifyBasicInfo(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

        basicInfoRepository.deleteByMember(member.getMemberNo());
    }

    // 경력, 자격증 수정 시 기존 내용 삭제
    public void modifyCareerAndCertification(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

        careerRepository.deleteByMember(member.getMemberNo());
        certificationRepository.deleteByMember(member.getMemberNo());
    }

    // 기본 정보 값 가져오기
    public ResponseEntity<BasicInfoResponse> findBasicInfo(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        BasicInfo basicInfos = basicInfoRepository.findByMember(member);

        BasicInfoResponse basicInfoReqeustDto = BasicInfoResponse.builder()
                .studentLocate(basicInfos.getStudentResumeLocate())
                .studentMajor(basicInfos.getStudentresumeMajor())
                .studentOneLineShow(basicInfos.getStudentResumeOnelineshow())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(basicInfoReqeustDto);

    }

    // 자격증, 경력 값 가져오기
    public ResponseEntity<CareerAndCertificationResponse> findCareerAndCertification(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        Career careers = careerRepository.findByMember(member);
        Certification certifications = certificationRepository.findByMember(member);

        CareerAndCertificationResponse careerAndCertificationDto = CareerAndCertificationResponse.builder()
                .carName(careers.getCarName())
                .carStartDay(careers.getCarStartDay())
                .carEndDay(careers.getCarEndDay())

                .certName(certifications.getCertName())
                .certDate(certifications.getCertDate())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(careerAndCertificationDto);

    }


}
