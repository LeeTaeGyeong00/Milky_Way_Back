package com.example.milky_way_back.article.repository;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("SELECT new com.example.milky_way_back.article.DTO.response.ApplyResponse(a.apply_no, a.article.article_no, m.memberName, a.applyDate, a.applyResult) " +
            "FROM Apply a JOIN a.article ar JOIN a.memberId m " +
            "WHERE ar.article_no = ?1")
    List<ApplyResponse> findMemberNamesByArticleNo(Long articleNo);

    List<Apply> findByMemberId(Member member);

}
