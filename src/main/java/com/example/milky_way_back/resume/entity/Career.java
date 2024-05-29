package com.example.milky_way_back.resume.entity;


import com.example.milky_way_back.member.Entity.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name="career")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_no")
    private Long carNo;

    @Column(name = "career_name")
    private String carName;

    @Column(name = "career_startdate")
    private LocalDate carStartDay;

    @Column(name = "career_startend")
    private LocalDate carEndDay;

    // member join
    @JoinColumn(name="career_member_no", referencedColumnName = "member_no", nullable = false)
    @OneToOne
    private Member member; // memberNo로 조인

}
