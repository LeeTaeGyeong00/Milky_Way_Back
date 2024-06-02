package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.Dto.StatusResponse;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.resume.dto.*;
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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    public ResponseEntity<StatusResponse> updateBasicInfo(BasicInfoReqeustDto basicInfoReqeustDto, HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        BasicInfo basicInfo = basicInfoRepository.findByMember(member);

        if(basicInfo == null) {

            BasicInfo basicInfoes = BasicInfo.builder()
                    .studentResumeMajor(basicInfoReqeustDto.getStudentMajor())
                    .studentResumeLocate(basicInfoReqeustDto.getStudentLocate())
                    .studentResumeOnelineshow(basicInfoReqeustDto.getStudentOneLineShow())
                    .member(member)
                    .build();

            basicInfoRepository.save(basicInfoes);

        } else {

            try {
                throw new Exception("이미 있는 회원입니다.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "사용자 정보 저장 완료"));
    }



    // 경력, 자격증 저장
    public ResponseEntity<StatusResponse> updateCarCert(HttpServletRequest request,
                                                        MemberInfoResponse memberInfoResponse) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        List<CareerDto> careerDtoList = memberInfoResponse.getCareerDtoList();
        List<CertificationDto> certificationDtoList = memberInfoResponse.getCertificationDtoList();

        if(careerDtoList != null) {
            for (CareerDto dto : careerDtoList) {
                Career career = Career.builder()
                        .carName(dto.getCarName())
                        .carStartDay(dto.getCarStartDay())
                        .carEndDay(dto.getCarEndDay())
                        .member(member)
                        .build();
                careerRepository.save(career);
            }
        }

        if(certificationDtoList != null) {
            for (CertificationDto dto : certificationDtoList) {
                Certification certification = Certification.builder()
                        .certName(dto.getCertName())
                        .certDate(dto.getCertDate())
                        .member(member)
                        .build();

                certificationRepository.save(certification);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "경력/자격증 저장 완료"));

    }

    // 기본 정보 수정 시 기존 내용 삭제
    @Transactional
    public ResponseEntity<StatusResponse> modifyBasicInfo(HttpServletRequest request, BasicInfoReqeustDto basicInfoReqeustDto) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        basicInfoRepository.updateBasicInfoByMember(member,
                basicInfoReqeustDto.getStudentLocate(),
                basicInfoReqeustDto.getStudentMajor(),
                basicInfoReqeustDto.getStudentOneLineShow());

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "기본 정보 수정 완료"));

    }

    // 경력, 자격증 수정
    @Transactional
    public void modifyCareerAndCertification(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

        careerRepository.deleteByMember(member);
        certificationRepository.deleteByMember(member);
    }

    // 기본 정보 값 가져오기
    public ResponseEntity<BasicInfoResponse> findBasicInfo(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));

        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        BasicInfo basicInfos = basicInfoRepository.findByMember(member);

        if(basicInfos == null) {
            BasicInfoResponse basicInfoResponseNull = BasicInfoResponse.builder()
                    .studentLocate(null)
                    .studentMajor(null)
                    .studentOneLineShow(null)
                    .memberId(memberId)
                    .memberName(member.getMemberName())
                    .memberPhoneNum(member.getMemberPhoneNum())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(basicInfoResponseNull);
        }

        BasicInfoResponse basicInfoResponse = BasicInfoResponse.builder()
                .studentLocate(basicInfos.getStudentResumeLocate())
                .studentMajor(basicInfos.getStudentResumeMajor())
                .studentOneLineShow(basicInfos.getStudentResumeOnelineshow())
                .memberId(memberId)
                .memberName(member.getMemberName())
                .memberPhoneNum(member.getMemberPhoneNum())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(basicInfoResponse);

    }

    // 자격증, 경력 값 가져오기
    public ResponseEntity<MemberInfoResponse> findCareerAndCertification(HttpServletRequest request) {

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName(); // 접속한 사람의 아이디 가져오기
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        List<Career> careers = careerRepository.findAllByMember(member);
        List<Certification> certifications = certificationRepository.findAllByMember(member);

        List<CareerDto> careerList = new ArrayList<>();
        for (Career career : careers) {
            CareerDto careerResponse = CareerDto.builder()
                    .carName(career.getCarName())
                    .carStartDay(career.getCarStartDay())
                    .carEndDay(career.getCarEndDay())
                    .build();
            careerList.add(careerResponse);
        }

        List<CertificationDto> certificationDtoList = new ArrayList<>();
        for (Certification certification : certifications) {
            CertificationDto certificationResponse = CertificationDto.builder()
                    .certName(certification.getCertName())
                    .certDate(certification.getCertDate())
                    .build();
            certificationDtoList.add(certificationResponse);
        }

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse();
        memberInfoResponse.setCareerDtoList(careerList);
        memberInfoResponse.setCertificationDtoList(certificationDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(memberInfoResponse);


    }


}
