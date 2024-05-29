package com.example.milky_way_back.resume.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CareerAndCertificationResponse {
    // 경력
    private String carName;
    private LocalDate carStartDay;
    private LocalDate carEndDay;

    // 자격증
    private String certName;
    private LocalDate certDate;
}
