package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Jwt.TokenProvider;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentResumeService {
    private final MemberRepository memberRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final CareerRepository careerRepository;
    private final CertificationRepository certificationRepository;
    private final TokenProvider tokenProvider;

    // 기본 정보 저장
    public ResponseEntity<StatusResponse> updateBasicInfo(BasicInfoDto basicInfoDto, HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));


        BasicInfo basicInfo = BasicInfo.builder()
                .studentresumeMajor(basicInfoDto.getStudentMajor())
                .studentResumeLocate(basicInfoDto.getStudentLocate())
                .studentResumeOnelineshow(basicInfoDto.getOneLineShow())
                .member(member)
                .build();

        basicInfoRepository.save(basicInfo);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "사용자 정보 저장 완료"));
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
    public void modifyCarrerAndCertification(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

        careerRepository.deleteByMember(member.getMemberNo());
        certificationRepository.deleteByMember(member.getMemberNo());
    }

    // 경력, 자격증 저장
    public ResponseEntity<StatusResponse> updateCarCert(CareerAndCertificationDto careerAndCertificationDto, HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

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

    // 조회
    public ResponseEntity<MemberInfoResponse> findAll(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

        List<BasicInfo> basicInfos = basicInfoRepository.findByMember(member);
        List<Career> careers = careerRepository.findByMember(member);
        List<Certification> certifications = certificationRepository.findByMember(member);

        return ResponseEntity.status(HttpStatus.OK).body(new MemberInfoResponse(basicInfos, careers, certifications));

    }


}
