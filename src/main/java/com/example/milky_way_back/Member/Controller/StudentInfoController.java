package com.example.milky_way_back.Member.Controller;

import com.example.milky_way_back.Member.Dto.StatusResponse;
import com.example.milky_way_back.Member.Dto.StudentInformaiton;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class StudentInfoController {

    private final StudentInfoService studentInfoService;
    private final JwtUtils jwtUtils;

    /* todo end-point 재설정 */
    // 등록 및 수정
    @PostMapping("/{memberId}/input-student-info")
    public ResponseEntity<StatusResponse> inputStudentInfo(@RequestBody StudentInformaiton studentInfoRequest,
                                                           HttpServletRequest request) {

       String accessToken = jwtUtils.getJwtFromHeader(request);
       studentInfoService.inputStudentInfo(studentInfoRequest, accessToken);

       return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));
    }

    // 조회
    @PostMapping("/{memberId}/info")
    public ResponseEntity<StudentInformaiton> studentAllView(HttpServletRequest request) {

        String accessToken = jwtUtils.getJwtFromHeader(request);
        String memberId = jwtUtils.getUserIdFromAccessToken(accessToken);
        StudentInformaiton studentInfo = studentInfoService.viewInfo(memberId);

        return ResponseEntity.ok(studentInfo);
    }
}
