package com.example.milky_way_back.Member.Repository;

import com.example.milky_way_back.Member.Entity.Career;
import com.example.milky_way_back.Member.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Long> {
    Career findByMember(Member member);
}
