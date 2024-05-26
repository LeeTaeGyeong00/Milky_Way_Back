package com.example.milky_way_back.member.Dto;

import com.example.milky_way_back.member.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyPageRequest {
    private Long memberNo;
    private String memberId;
    private String newMemberId;
    private String memberPassword;
    private String memberName;
    private String memberPhoneNum;
    private String memberEmail;
    private Role memberRole;
}
