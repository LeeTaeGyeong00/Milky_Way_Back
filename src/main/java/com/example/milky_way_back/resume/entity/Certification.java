package com.example.milky_way_back.resume.entity;


import com.example.milky_way_back.member.Entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="Member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cert_no")
    private Long certNo;

    @Column(name = "cert_name")
    private String certName;

    @Column(name = "cert_date")
    private LocalDateTime certDate;

    // member join
    @JoinColumn(name="cert_member_no", referencedColumnName = "member_no", nullable = false)
    @ManyToOne
    private Member member; // memberNo로 조인

    /*todo file 처리 */


}
