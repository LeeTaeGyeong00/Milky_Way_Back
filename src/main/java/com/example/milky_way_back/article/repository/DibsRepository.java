package com.example.milky_way_back.article.repository;

import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.entity.Dibs;
import com.example.milky_way_back.member.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DibsRepository extends JpaRepository<Dibs, Long> {
    Optional<Dibs> findByMemberNoAndArticleNo(Member member, Article article);
    List<Dibs> findByMemberNo(Member member);
    Optional<Dibs> findByArticleNoAndMemberNo(Article article, Member member);
}
