package com.example.milky_way_back.Member.Repository;

import com.example.milky_way_back.Member.Entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findById(Long authId);
    Optional<Auth> findByMember(Long memberNo);
    Optional<Auth> deleteByAuthRefreshToken(String refreshToken);
    void deleteByMember(Long memberNo);
}
