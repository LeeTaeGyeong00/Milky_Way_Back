package com.example.milky_way_back.article.entity;

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

@Table(name="Article")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
@Getter
public class Article {
    //멤버 일대다 적용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_no", updatable = false)
    private Long article_no;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_no", nullable = false)
//    private Member member;

    @Column(name="user_no")
    private String userNo;

    @Column(name="article_type")
    private String articleType;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean articleRecruitmentState;

    @Column(name="article_title", nullable = false)
    private String title;

    @Column(name="article_content", nullable = false)
    private String content;

    @ColumnDefault("0")
    private int likes;

    @Column(name="article_apply", nullable = false)
    private int apply;

    @ColumnDefault("0")
    private int applyNow; //이렇게 해도 상관없을려나?

//    @Column(name="article_start_day", updatable = false)
//    private LocalDateTime startDay;
//
//    @Column(name="article_end_day", updatable = false)
//    private LocalDateTime endDay;
    @Column(name="article_start_day", updatable = false)
    private String startDay;

    @Column(name="article_end_day", updatable = false)
    private String endDay;

    @Column(name="article_find_mentor", updatable = false)
    private boolean findMentor;

    @Column(name="article_mentor_tag", updatable = false) //articleMentorTag
    private String mentorTag;

    @CreatedDate
    @Column(name="regDate", updatable = false)
    private LocalDateTime regDate;

//    @Getter
//    @Setter
//    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Apply> applies = new ArrayList<>();

    @Builder
    public Article(String articleType, String title,String userNo, String content,String mentorTag,
                   String startDay, String endDay,
                   int apply, boolean findMentor){
        //this.member = member;
        this.userNo = userNo;
        this.articleType = articleType;
        this.title = title;
        this.content = content;
        this.apply = apply;
        this.startDay = startDay;
        this.endDay = endDay;
        this.findMentor=findMentor;
        this.mentorTag = mentorTag;
    }

}
