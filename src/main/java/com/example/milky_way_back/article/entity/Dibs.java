package com.example.milky_way_back.article.entity;

import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.member.Entity.Member;
import lombok.*;

import javax.persistence.*;

@Table(name="dibs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
@Setter
public class Dibs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dibs_no")
    private Long dibsNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member memberNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_no", nullable = false)
    private Article articleNo;
}
