package com.example.milky_way_back.article.entity;

import com.example.milky_way_back.member.Entity.Member;
import lombok.*;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="article")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
@Getter
public class Article {
    //멤버 일대다 적용
    //멤버 일대다 적용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_no", updatable = false)
    private Long article_no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member memberId;

    @Column(name="article_type")
    private String articleType;

    @Column(name="recruitment_state")
    private Boolean articleRecruitmentState = true;

    @Column(name="article_title", nullable = false)
    private String title;

    @Column(name="article_content", nullable = false)
    private String content;

    @Column(name="article_recruit")
    private Boolean recruit = true;

    @Column(name="article_likes", nullable = false)
    private int likes = 0;

    @Column(name="article_apply", nullable = false)
    private int apply;

    @Column(name="apply_now", nullable = false)
    private int applyNow=0; //이렇게 해도 상관없을려나?

    @Column(name="article_start_day", updatable = false)
    private String startDay;

    @Column(name="article_end_day", updatable = false)
    private String endDay;

    @Column(name="article_find_mentor", updatable = false)
    private boolean findMentor;

    @Column(name="article_mentor_tag", updatable = false) //articleMentorTag
    private String mentorTag;

    @Column(name="article_con_method", updatable = false, nullable = false)
    private String conMethod;

    @Column(name="article_con_info", updatable = false, nullable = false)
    private String conInfo;

    @CreatedDate
    @Column(name="aritcle_regdate", updatable = false)
    private LocalDateTime regDate;

    @Builder
    public Article(String articleType, String title, Member memberId, String content, String mentorTag,
                   String startDay, String endDay,
                   int apply, boolean findMentor, String conMethod, String conInfo){
        this.memberId = memberId;
        this.articleType = articleType;
        this.title = title;
        this.content = content;
        this.apply = apply;
        this.startDay = startDay;
        this.endDay = endDay;
        this.findMentor=findMentor;
        this.mentorTag = mentorTag;
        this.conMethod = conMethod;
        this.conInfo = conInfo;
    }

}
