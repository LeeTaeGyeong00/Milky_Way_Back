package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.resume.entity.BasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasicInfoRepository extends JpaRepository<BasicInfo, Long> {
    BasicInfo findByMember(Member member);
    void deleteByMember(Long member);
}

