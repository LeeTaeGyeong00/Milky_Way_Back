package com.example.milky_way_back.resume.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificationDto {
    // 자격증
    private String certName;
    private LocalDateTime certDate;
}
