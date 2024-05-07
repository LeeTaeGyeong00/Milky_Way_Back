package com.example.milky_way_back.Member.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentInfoRequest {
    private int studentGrade;
    private String studentMajor;
    private String studentOneLineShow;
    private String studentLocate;
}

