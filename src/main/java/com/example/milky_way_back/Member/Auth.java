package com.example.milky_way_back.Member;

import lombok.*;

import javax.persistence.*;

@Table(name="Auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_no")
    private Long authNo;

    // member join
    @JoinColumn(name="auth_user_no",referencedColumnName="member_no")
    @OneToOne
    private Member member;

    @Column(nullable = false, name="auth_refreshtoken")
    private String authRefreshToken;

    @Column(nullable = false, name="auth_refreshtoken_expiration")
    private long authRefreshTokenExpiration;

    public void updateRefreshToken(String authRefreshToken) {
        this.authRefreshToken = authRefreshToken;
    }

    public void destoryRefreshToken() {
        this.authRefreshToken = null;
    }
}
