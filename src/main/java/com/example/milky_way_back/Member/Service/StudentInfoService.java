package com.example.milky_way_back.Member.Service;

import com.auth0.jwt.interfaces.Claim;
import com.example.milky_way_back.Member.Dto.StatusResponse;
import com.example.milky_way_back.Member.Dto.StudentInfoRequest;
import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.StudentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentInfoService {

    private final JwtUtils jwtUtils;
    private final StudentInfoRepository studentInfoRepository;

    // 등록
    public ResponseEntity<StatusResponse> inputInfo(StudentInfoRequest studentInfoRequest, String accessToken) {

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        Object member = authentication.getPrincipal()


        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));

    }


    // 조회
    public ResponseEntity<StatusResponse> viewAll(StudentInfoRequest studentInfoRequest) {



        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));

    }

    // 수정
    public ResponseEntity<StatusResponse> updateInfo(StudentInfoRequest studentInfoRequest) {



        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));

    }

    // 삭제
    public ResponseEntity<StatusResponse> delete(StudentInfoRequest studentInfoRequest) {



        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));

    }

}
