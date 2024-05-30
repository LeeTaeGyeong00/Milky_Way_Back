package com.example.milky_way_back.resume.entity;

import com.example.milky_way_back.member.Entity.Member;
import lombok.*;

import javax.persistence.*;

@Table(name="studentresume")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class BasicInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studentresume_no")
    private Long studentResumeNo;

    @Column(name = "studentresume_major")
    private String studentResumeMajor;

    @Column(name = "studentresume_onelineshow")
    private String studentResumeOnelineshow;

    @Column(name = "studentresume_locate")
    private String studentResumeLocate;

    // member join
    @JoinColumn(name="studentresume_member_no", referencedColumnName = "member_no", nullable = false)
    @OneToOne
    private Member member; // memberNo로 조인


}
