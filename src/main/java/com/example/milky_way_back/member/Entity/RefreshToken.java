package com.example.milky_way_back.member.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Table(name="Auth")
@NoArgsConstructor
@Entity
@Data
@AllArgsConstructor
@Builder
public class RefreshToken {

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

    public RefreshToken updateToken(String token) {
        this.authRefreshToken = token;
        return this;
    }

}
