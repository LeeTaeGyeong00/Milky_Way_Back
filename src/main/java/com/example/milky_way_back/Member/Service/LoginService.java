package com.example.milky_way_back.Member.Service;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("id not exist"));

        return User.builder()
                .username(member.getMemberId())
                .password(member.getMemberPassword())
                .roles(String.valueOf(member.getMemberRole()))
                .build();
    }
}
