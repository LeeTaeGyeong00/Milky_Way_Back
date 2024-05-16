package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.resume.entity.BasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicInfoRepository extends JpaRepository<BasicInfo, Long> {
}
