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
    private List<BasicInfo> basicInfos;
    private List<Career> careers;
    private List<Certification> certifications;

    public MemberInfoResponse(List<BasicInfo> basicInfos, List<Career> careers, List<Certification> certifications) {
        this.basicInfos = basicInfos;
        this.careers = careers;
        this.certifications = certifications;
    }
}
