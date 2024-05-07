package com.example.milky_way_back.Member.Repository;

import com.example.milky_way_back.Member.Dto.StudentInfoRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentInfoRepository extends JpaRepository<StudentInfoRequest, Long> {
}
