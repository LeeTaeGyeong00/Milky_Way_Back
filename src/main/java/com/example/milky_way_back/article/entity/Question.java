package com.example.milky_way_back.article.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="qreply_no", updatable = false)
    private Long qreply_no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_no")
    private Article article;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private Member member;

    @Column(name="qreply_content", updatable = false)
    private Long content;

    @CreationTimestamp
    @Column(name="qreply_create_date", updatable = false)
    private LocalDateTime regDate;


}
