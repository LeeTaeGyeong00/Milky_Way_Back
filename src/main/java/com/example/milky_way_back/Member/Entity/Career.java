package com.example.milky_way_back.Member.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="career")
@NoArgsConstructor
@Entity
@Data
@AllArgsConstructor
@Builder
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_no")
    private Long careerNo;

    @JoinColumn(name="career_member_no", referencedColumnName = "member_no")
    @OneToOne
    private Member member;

    @Column(name = "career_name")
    private String careerName;

    @Column(name = "career_startDay")
    private LocalDateTime careerStartDay;

    @Column(name = "career_endDay")
    private LocalDateTime careerEndDay;

}
