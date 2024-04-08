package com.example.milky_way_back.article.entity;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Getter
public class Article {
    //멤버 일대다 적용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_no", updatable = false)
    private Long article_no;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private Member member;

//    @Column(name="article_member_id")
//    private String writer;
//    member로 가져와야할듯

    @Column(name="article_type", nullable = false)
    private String articleType;

    @Column(name="article_title", nullable = false)
    private String title;

    @Column(name="article_content", nullable = false)
    private String content;

    @Builder.Default
    @Column(name="article_likes", nullable = false)
    private int likes = 0;

    @Column(name="article_apply", nullable = false)
    private int apply;

    @Builder.Default
    @Column(name="article_apply_now", nullable = false)
    private int applyNow = 0; //이렇게 해도 상관없을려나?

    @Column(name="article_start_day", updatable = false)
    private LocalDateTime startDay;

    @Column(name="article_end_day", updatable = false)
    private LocalDateTime endDay;

    @Column(name="article_find_mentor", updatable = false)
    private boolean findMentor;

    @Column(name="article_mentor_tag", updatable = false)
    private String metorTag;

    @CreationTimestamp
    @Column(name="regDate", updatable = false)
    private LocalDateTime regDate;


    @Builder
    public Article(String articleType, String title, String content,String metorTag,
                   LocalDateTime startDay, LocalDateTime endDay,
                   int apply, boolean findMentor){
        this.articleType = articleType;
        this.title = title;
        this.content = content;
        this.metorTag = metorTag;
        this.startDay = startDay;
        this.endDay = endDay;
        this.apply = apply;
        this.findMentor=findMentor;
    }

}
