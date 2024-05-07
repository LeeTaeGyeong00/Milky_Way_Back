package com.example.milky_way_back.Member.Controller;

import com.example.milky_way_back.Member.Dto.StatusResponse;
import com.example.milky_way_back.Member.Dto.StudentInfoRequest;
import com.example.milky_way_back.Member.Service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    /* todo end-point 재설정 */
    // 등록
    @PostMapping("/member/input-student-info")
    public ResponseEntity<StatusResponse> inputStudentInfo(@RequestBody StudentInfoRequest studentInfoRequest,
                                                           @RequestHeader(value = "Authorization", required = false) String accessToken) {
        return studentInfoService.inputInfo(studentInfoRequest, accessToken);
    }

    // 수정
    @PostMapping("/member/input-student-update")
    public ResponseEntity<StatusResponse> inputStudentUpdate(@RequestBody StudentInfoRequest studentInfoRequest) {
        return studentInfoService.updateInfo(studentInfoRequest);
    }

    // 삭제
    @PostMapping("/member/delete")
    public ResponseEntity<StatusResponse> studentInfoDelete(@RequestBody StudentInfoRequest studentInfoRequest) {
        return studentInfoService.delete(studentInfoRequest);
    }

    // 조회
    @PostMapping("/member/info")
    public ResponseEntity<StatusResponse> studentAllView(@RequestBody StudentInfoRequest studentInfoRequest) {
        return studentInfoService.viewAll(studentInfoRequest);
    }
}
