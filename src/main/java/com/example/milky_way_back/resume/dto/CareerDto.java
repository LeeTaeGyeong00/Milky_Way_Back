package com.example.milky_way_back.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CareerDto {
    // 경력
    private String carName;
    private LocalDate carStartDay;
    private LocalDate carEndDay;
}
