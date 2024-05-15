package com.example.milky_way_back.member.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
public class IdRequest {
    private String memberId; // 중복확인
}
