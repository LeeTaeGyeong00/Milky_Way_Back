package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.resume.dto.CertificationDto;
import com.example.milky_way_back.resume.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<CertificationDto> findByMember(Member member);
    void deleteByMember(Long memberNo);
}
