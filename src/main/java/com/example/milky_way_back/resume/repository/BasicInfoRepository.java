package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.resume.entity.BasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasicInfoRepository extends JpaRepository<BasicInfo, Long> {
    BasicInfo findByMember(Member member);
    void deleteByMember(Member member);

    @Modifying
    @Query("UPDATE BasicInfo b SET " +
            "b.studentResumeLocate = :studentLocate, " +
            "b.studentResumeMajor = :studentMajor, " +
            "b.studentResumeOnelineshow = :studentOneLineShow " +
            "WHERE b.member = :member")
    void updateBasicInfoByMember(@Param("member") Member member, @Param("studentLocate") String studentLocate, @Param("studentMajor") String studentMajor, @Param("studentOneLineShow") String studentOneLineShow);

}

