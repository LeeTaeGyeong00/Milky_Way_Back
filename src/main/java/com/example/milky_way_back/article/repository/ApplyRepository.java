package com.example.milky_way_back.article.repository;

import com.example.milky_way_back.article.DTO.response.MyPageApplyResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("SELECT new com.example.milky_way_back.article.DTO.response.ApplyResponse(a.apply_no, a.article.article_no, m.memberName, a.applyDate, a.applyResult) " +
            "FROM Apply a JOIN a.article ar JOIN a.memberId m " +
            "WHERE ar.article_no = ?1")
    List<ApplyResponse> findMemberNamesByArticleNo(Long articleNo);

//    @Query("SELECT new com.example.milky_way_back.article.DTO.response.ApplyResponse(a.apply_no, a.article.article_no, m.memberName, a.applyDate, a.applyResult) " +
//            "FROM Apply a JOIN a.article ar JOIN a.memberId m " +
//            "WHERE ar.article_no = ?1")
//    List<MyPageApplyResponse> findMemberNamesByArticleNo(Long articleNo);

    List<Apply> findByMemberId(Member member);

    //a= apply
    @Query("SELECT a FROM Apply a WHERE a.memberId = :member AND a.article.article_no = :articleNo")
    Optional<Apply> findByMemberAndArticleNo(@Param("member") Member member, @Param("articleNo") Long articleNo);
}
