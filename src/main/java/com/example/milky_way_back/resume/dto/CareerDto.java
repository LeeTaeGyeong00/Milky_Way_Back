package com.example.milky_way_back.resume.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CareerDto {
    // 경력
    private String carName;
    private LocalDate carStartDay;
    private LocalDate carEndDay;
}
