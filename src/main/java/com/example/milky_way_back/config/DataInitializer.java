package com.example.milky_way_back.config;

import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Entity.Role;
import com.example.milky_way_back.member.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;

    @Bean
    public CommandLineRunner loadData(MemberRepository memberRepository) {
        return args -> {
            // Create sample users
            createSampleUsers(memberRepository);

            // Execute data.sql script
            executeDataSqlScript();
        };
    }

    private void createSampleUsers(MemberRepository memberRepository) {
        Member user1 = new Member();
        user1.setMemberId("test1");
        user1.setMemberPassword(bCryptPasswordEncoder.encode("test1"));
        user1.setMemberName("김정민");
        user1.setMemberPhoneNum("0102434343");
        user1.setMemberEmail("adsds@dfdf");
        user1.setMemberRole(Role.STUDENT);
        user1.setCreateTime(LocalDateTime.now());

        Member user2 = new Member();
        user2.setMemberId("test2");
        user2.setMemberPassword(bCryptPasswordEncoder.encode("test2"));
        user2.setMemberName("이태석");
        user2.setMemberPhoneNum("0102434343");
        user2.setMemberEmail("adsds@dfdf");
        user2.setMemberRole(Role.STUDENT);
        user2.setCreateTime(LocalDateTime.now());

        // 더미 데이터를 추가합니다.
        Member user3 = new Member();
        user3.setMemberId("test3");
        user3.setMemberPassword(bCryptPasswordEncoder.encode("test3"));
        user3.setMemberName("김태겸");
        user3.setMemberPhoneNum("0102434343");
        user3.setMemberEmail("adsds@dfdf");
        user3.setMemberRole(Role.STUDENT);
        user3.setCreateTime(LocalDateTime.now());

        Member user4 = new Member();
        user4.setMemberId("test4");
        user4.setMemberPassword(bCryptPasswordEncoder.encode("test4"));
        user4.setMemberName("장유빈");
        user4.setMemberPhoneNum("0102434343");
        user4.setMemberEmail("adsds@dfdf");
        user4.setMemberRole(Role.STUDENT);
        user4.setCreateTime(LocalDateTime.now());

        Member user5 = new Member();
        user5.setMemberId("test5");
        user5.setMemberPassword(bCryptPasswordEncoder.encode("test5"));
        user5.setMemberName("김준석");
        user5.setMemberPhoneNum("0102434343");
        user5.setMemberEmail("adsds@dfdf");
        user5.setMemberRole(Role.STUDENT);
        user5.setCreateTime(LocalDateTime.now());

        Member user6 = new Member();
        user6.setMemberId("test6");
        user6.setMemberPassword(bCryptPasswordEncoder.encode("test6"));
        user6.setMemberName("문채현");
        user6.setMemberPhoneNum("0102434343");
        user6.setMemberEmail("adsds@dfdf");
        user6.setMemberRole(Role.STUDENT);
        user6.setCreateTime(LocalDateTime.now());

        Member user7 = new Member();
        user7.setMemberId("test7");
        user7.setMemberPassword(bCryptPasswordEncoder.encode("test7"));
        user7.setMemberName("이종섭");
        user7.setMemberPhoneNum("0102434343");
        user7.setMemberEmail("adsds@dfdf");
        user7.setMemberRole(Role.STUDENT);
        user7.setCreateTime(LocalDateTime.now());

        Member user8 = new Member();
        user8.setMemberId("test8");
        user8.setMemberPassword(bCryptPasswordEncoder.encode("test8"));
        user8.setMemberName("김태현");
        user8.setMemberPhoneNum("0102434343");
        user8.setMemberEmail("adsds@dfdf");
        user8.setMemberRole(Role.STUDENT);
        user8.setCreateTime(LocalDateTime.now());

        Member user9 = new Member();
        user9.setMemberId("test9");
        user9.setMemberPassword(bCryptPasswordEncoder.encode("test9"));
        user9.setMemberName("김민정");
        user9.setMemberPhoneNum("0102434343");
        user9.setMemberEmail("adsds@dfdf");
        user9.setMemberRole(Role.STUDENT);
        user9.setCreateTime(LocalDateTime.now());

        Member user10 = new Member();
        user10.setMemberId("test10");
        user10.setMemberPassword(bCryptPasswordEncoder.encode("test10"));
        user10.setMemberName("김겸희");
        user10.setMemberPhoneNum("0102434343");
        user10.setMemberEmail("adsds@dfdf");
        user10.setMemberRole(Role.STUDENT);
        user10.setCreateTime(LocalDateTime.now());

        memberRepository.save(user1);
        memberRepository.save(user2);
        memberRepository.save(user3);
        memberRepository.save(user4);
        memberRepository.save(user5);
        memberRepository.save(user6);
        memberRepository.save(user7);
        memberRepository.save(user8);
        memberRepository.save(user9);
        memberRepository.save(user10);
    }

    private void executeDataSqlScript() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("data.sql"));
        resourceDatabasePopulator.execute(dataSource);
    }
}
