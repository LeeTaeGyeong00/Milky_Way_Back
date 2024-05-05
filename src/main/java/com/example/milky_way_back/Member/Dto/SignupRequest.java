package com.example.milky_way_back.Member.Dto;

import com.example.milky_way_back.Member.Entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String id;
    private String password;
    private String name;
    private String tel;
    private String email;
    private Role role;
}
