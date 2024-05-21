package com.example.milky_way_back.resume.dto;

import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemberInfoResponse {
    private List<BasicInfoDto> basicInfos;
    private List<CareerDto> careers;
    private List<CertificationDto> certifications;

    public MemberInfoResponse(List<BasicInfoDto> basicInfos, List<CareerDto> careers, List<CertificationDto> certifications) {
        this.basicInfos = basicInfos;
        this.careers = careers;
        this.certifications = certifications;
    }
}
