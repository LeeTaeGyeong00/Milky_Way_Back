package com.example.milky_way_back.Member.Jwt.filter;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final Member member;

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // 역할, 권한 가져오기
        Role memberRole = member.getMemberRole();
        String authority = memberRole.getAuthority(); /* todo Authority가 무엇인가? */

        /* todo SimpleGrantedAuthority가 무엇인가? */
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getMemberPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
