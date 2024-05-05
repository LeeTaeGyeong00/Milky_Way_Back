package com.example.milky_way_back.article.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="areply_no", updatable = false)
    private Long areply_no;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qreply_no")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_no")
    private Article article;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private Member member;
    @Column(name="areply_content", updatable = false)
    private Long content;

    @CreationTimestamp
    @Column(name="areply_create_date", updatable = false)
    private LocalDateTime regDate;
}
