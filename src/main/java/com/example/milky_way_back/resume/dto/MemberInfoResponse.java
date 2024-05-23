package com.example.milky_way_back.resume.dto;

import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class MemberInfoResponse {
    private String studentLocate;
    private String studentMajor;
    private String studentOneLineShow;

    // 경력
    private String carName;
    private LocalDateTime carStartDay;
    private LocalDateTime carEndDay;

    // 자격증
    private String certName;
    private LocalDateTime certDate;

}
