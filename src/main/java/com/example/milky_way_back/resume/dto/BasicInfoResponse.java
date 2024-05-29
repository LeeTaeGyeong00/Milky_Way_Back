package com.example.milky_way_back.resume.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasicInfoResponse {
    private String studentLocate;
    private String studentMajor;
    private String studentOneLineShow;
    private Integer studentGrade;
}
