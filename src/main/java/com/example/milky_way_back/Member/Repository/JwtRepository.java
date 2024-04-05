package com.example.milky_way_back.Member.Repository;

import com.example.milky_way_back.Member.Entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByAuthNo(String memberId);
}
