package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.resume.dto.CareerDto;
import com.example.milky_way_back.resume.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Integer> {
    List<CareerDto> findByMember(Member member);
    void deleteByMember(Long memberNo);
}
