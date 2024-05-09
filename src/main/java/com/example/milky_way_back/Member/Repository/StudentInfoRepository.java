package com.example.milky_way_back.Member.Repository;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Entity.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Long> {
    StudentInfo findByMember(Member member);
}
