package com.example.milky_way_back.Member.Service;

import com.example.milky_way_back.Member.Dto.StatusResponse;
import com.example.milky_way_back.Member.Dto.StudentInformaitonRequest;
import com.example.milky_way_back.Member.Entity.Career;
import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Entity.StudentInfo;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.CareerRepository;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import com.example.milky_way_back.Member.Repository.StudentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentInfoService {

    private final JwtUtils jwtUtils;
    private final StudentInfoRepository studentInfoRepository;
    private final CareerRepository careerRepository;
    private final MemberRepository memberRepository;

    // 등록
    public ResponseEntity<StatusResponse> inputStudentInfo(StudentInformaitonRequest studentInformaitonRequest, String accessToken) {

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(HttpStatus.BAD_REQUEST.value(), "토큰이 없습니다"));
        }

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String memberId = userDetails.getUsername();

        Optional<Member> member = memberRepository.findByMemberId(memberId);

        // 이력 부분
        StudentInfo studentInfo = StudentInfo.builder()
                .studentGrade(studentInformaitonRequest.getStudentGrade())
                .studentMajor(studentInformaitonRequest.getStudentMajor())
                .studentOneLineShow(studentInformaitonRequest.getStudentOneLineShow())
                .studentLocate(studentInformaitonRequest.getStudentLocate())
                .member(member.get())
                .build();

        // 경력 부분
        Career career = Career.builder()
                .careerName(studentInformaitonRequest.getCareerName())
                .careerStartDay(studentInformaitonRequest.getCareerStartDay())
                .careerEndDay(studentInformaitonRequest.getCareerEndDay())
                .member(member.get())
                .build();

        careerRepository.save(career);
        studentInfoRepository.save(studentInfo);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));
    }



    // 조회
    public StudentInformaitonRequest viewInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();
        StudentInfo studentInfo = studentInfoRepository.findByMember(member);
        Career career = careerRepository.findByMember(member);

        return convertToResponse(studentInfo, career);
    }

    private StudentInformaitonRequest convertToResponse(StudentInfo studentInfo, Career career) {

        StudentInformaitonRequest response = new StudentInformaitonRequest();
        Career careerInfo = new Career();

        // 이력 부분
        response.setStudentGrade(studentInfo.getStudentGrade());
        response.setStudentMajor(studentInfo.getStudentMajor());
        response.setStudentOneLineShow(studentInfo.getStudentOneLineShow());
        response.setStudentLocate(studentInfo.getStudentLocate());

        // 경력 부분
        response.setCareerName(careerInfo.getCareerName());
        response.setCareerStartDay(careerInfo.getCareerStartDay());
        response.setCareerEndDay(careerInfo.getCareerEndDay());

        return response;
    }

}


