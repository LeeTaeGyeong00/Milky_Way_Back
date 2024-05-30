package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Certification findByMember(Member memberNo);
    List<Certification> findAllByMember(Member member);
    void deleteByMember(Member member);
}
