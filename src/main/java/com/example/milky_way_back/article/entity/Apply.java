package com.example.milky_way_back.article.entity;

import com.example.milky_way_back.member.Entity.Member;
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
@Getter
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="apply_no", updatable = false)
    private Long apply_no;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_no")
    private Article article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_no", nullable = false)
    private Member memberId;

    @CreationTimestamp
    @Column(name="apply_date", updatable = false)
    private LocalDateTime applyDate;

    @Column(name="apply_result", updatable = true, nullable = true)
    private String applyResult = "신청"; //status

    @Builder
    public Apply(Article article, Member member) {
        this.article = article;
        this.memberId = member;
    }
}
