package com.example.milky_way_back.Member.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentInformaitonRequest {

    // 기본 정보
    private int studentGrade;
    private String studentMajor;
    private String studentOneLineShow;
    private String studentLocate;

    // 경력
    private String careerName;
    private LocalDateTime careerStartDay;
    private LocalDateTime careerEndDay;
}

