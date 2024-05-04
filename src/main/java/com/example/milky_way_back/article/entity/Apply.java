package com.example.milky_way_back.article.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="apply")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="apply_no", updatable = false)
    private Long apply_no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_no")
    @Getter
    @Setter
    private Article article;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_no", nullable = false)
//    private Member member;

    @Column(name="apply_name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name="apply_date", updatable = false)
    private LocalDateTime applyDate;

    @Column(name="apply_result", updatable = true)
    private String applyResult; //status

}
