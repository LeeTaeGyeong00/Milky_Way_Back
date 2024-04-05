package com.example.milky_way_back.Member.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Column(nullable = false, name="member_password")
    private String memberPassword;

    @Column(nullable = false, length = 20, name="member_name")
    private String memberName;

    @Column(nullable = false, name="member_phonenum")
    private String memberPhoneNum;

    @Column(nullable = false, name="member_email")
    private String memberEmail; // todo 이메일 인증 할 때 자동으로 입력하도록 하는 것

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="member_role")
    private Role memberRole;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(updatable = true)
    private LocalDateTime lastModifiedDate;

    // 비밀번호 업데이트
    public void updatePassword(PasswordEncoder passwordEncoder, String newPaassword) {
        this.memberPassword = passwordEncoder.encode(newPaassword);
    }

    // 비밀번호 일치여부 확인
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getMemberPassword());
    }

    // 권한 부여
    public void addUserAuthority() {
        this.memberRole = Role.Student;
    }

}
