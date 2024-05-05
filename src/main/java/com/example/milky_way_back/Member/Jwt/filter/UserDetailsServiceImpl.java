package com.example.milky_way_back.Member.Jwt.filter;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 유저에 해당 아이디로 있는 멤버가 있는지 확인
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        return new UserDetailsImpl(member);
    }
}
