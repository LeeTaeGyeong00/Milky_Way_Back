package com.example.milky_way_back.Member.Entity;

import lombok.*;

import javax.persistence.*;

@Table(name="studentInfo")
@NoArgsConstructor
@Entity
@Data
@AllArgsConstructor
@Builder
public class StudentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studentInfo_no")
    private Long studentInfoNo;

    @JoinColumn(name="studentInfo_member_no", referencedColumnName = "member_no")
    @OneToOne
    private Member member;

    @Column(name = "student_grade")
    private int studentGrade;

    @Column(name = "student_major")
    private String studentMajor;

    @Column(name = "student_onelineshow")
    private String studentOneLineShow;

    @Column(name = "student_locate")
    private String studentLocate;


}
