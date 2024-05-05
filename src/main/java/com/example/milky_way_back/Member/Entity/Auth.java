package com.example.milky_way_back.Member.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Table(name="Auth")
@NoArgsConstructor
@Entity
@Data
@AllArgsConstructor
@Builder
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_no")
    private Long authNo; // 고유 번호

    // member join
    @JoinColumn(name="auth_member_no", referencedColumnName = "member_no")
    @OneToOne
    private Member member; // memberNo로 조인

    @Column(name="auth_refreshtoken")
    private String authRefreshToken; // 리프레시 토큰

    @Column(name="auth_expiration")
    private Instant authExpiration; // 만료시간


    public void refreshUpdate(String refreshToken) {
        this.authRefreshToken = refreshToken;
    }

}
