package com.example.milky_way_back.resume.dto;

import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class MemberInfoResponse {
    private List<CareerDto> careerDtoList;
    private List<CertificationDto> certificationDtoList;
}
