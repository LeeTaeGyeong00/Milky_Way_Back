package com.example.milky_way_back.article.DTO.request;


import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.article.entity.Apply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplyRequest {
    private Long articleNo;
    // 회원 번호를 여기에 저장할 필요 없으므로 삭제 또는 주석 처리
     private String memberId;
}