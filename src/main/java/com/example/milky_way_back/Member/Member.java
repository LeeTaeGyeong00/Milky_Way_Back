package com.example.milky_way_back.Member;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="Member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    private Long memberNo;

    @Column(nullable = false, length = 10, name="member_id")
    private String memberId;

    @Column(nullable = false, length = 20, name="member_password")
    private String memberPassword;

    @Column(nullable = false, length = 20, name="member_name")
    private String memberName;

    @Column(nullable = false, length = 10, name="member_phonenum")
    private String memberPhoneNum;

    @Column(nullable = false, length = 20, name="member_email")
    private String memberEmail; // todo 이메일 인증 할 때 자동으로 입력하도록 하는 것

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="member_role")
    private String memberRole;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(updatable = true)
    private LocalDateTime lastModifiedDate;
}
