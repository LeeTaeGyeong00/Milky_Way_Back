package com.example.milky_way_back.member.Jwt;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;


    @Transactional
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        return memberRepository.findByMemberId(memberId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("not found member"));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getMemberRole().toString());

        return new User(
                String.valueOf(member.getMemberId()),
                member.getMemberPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
