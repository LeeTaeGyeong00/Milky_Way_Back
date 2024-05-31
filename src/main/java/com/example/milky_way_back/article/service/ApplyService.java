package com.example.milky_way_back.article.service;

import com.example.milky_way_back.article.DTO.ChangeApplyResult;
import com.example.milky_way_back.article.DTO.MemberDTO;
import com.example.milky_way_back.article.DTO.ArticleDTO;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.exception.*;
import com.example.milky_way_back.member.Entity.Member;
import com.example.milky_way_back.member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.ApplyRequest;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.DTO.response.MyPageApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.repository.ArticleRepository;
import com.example.milky_way_back.member.Jwt.TokenProvider;
import com.example.milky_way_back.resume.dto.BasicInfoResponse;
import com.example.milky_way_back.resume.dto.CareerDto;
import com.example.milky_way_back.resume.dto.CertificationDto;
import com.example.milky_way_back.resume.dto.MemberInfoResponse;
import com.example.milky_way_back.resume.entity.BasicInfo;
import com.example.milky_way_back.resume.entity.Career;
import com.example.milky_way_back.resume.entity.Certification;
import com.example.milky_way_back.resume.repository.BasicInfoRepository;
import com.example.milky_way_back.resume.repository.CareerRepository;
import com.example.milky_way_back.resume.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApplyService {
    private final TokenProvider tokenProvider;
    private final ApplyRepository applyRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final CareerRepository careerRepository;
    private final CertificationRepository certificationRepository;
    @Transactional
    //회원 번호와 게시글 번호를 받아 지원 정보를 처리하는 메서드
    public Apply apply(HttpServletRequest request, Long articleNo, ApplyRequest applyRequest) {
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            System.out.println(member);
            // 회원과 게시글 번호를 기반으로 기존 지원 내역 확인
            if (applyRepository.findByMemberAndArticleNo(member, articleNo).isPresent()) {
                throw new DuplicateApplyException("Member has already applied to this article.");
            }
            // 게시글 정보 조회
            Article article = articleRepository.findById(articleNo)
                    .orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + articleNo));
            // AddArticle에 회원 정보 설정
            Apply apply = Apply.builder()
                    .article(articleRepository.findById(articleNo).orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + articleNo)))
                    .member(member) // Set the Member object directly
                    .build();
            // applyNow 필드 증가
            article.setApplyNow(article.getApplyNow() + 1);
            // 게시물 저장
            return applyRepository.save(apply);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }


    public List<ApplyResponse> findMemberNamesByArticleNo( HttpServletRequest request, Long articleNo) {
        // 토큰에서 사용자 정보 추출
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        // 현재 사용자를 조회
        Member currentUser = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));

        // 게시물 조회
        Article article = articleRepository.findById(articleNo)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + articleNo));

        // 게시물 작성자와 현재 사용자 비교
        boolean isAuthor = article.getMemberId().getMemberId().equals(memberId);

        // 게시물에 지원했는지 확인
        boolean hasApplied = applyRepository.findByArticleAndMemberId(article, currentUser).isPresent();

        // 작성자도 아니고 지원자도 아닌 경우 접근 거부
        if (!isAuthor && !hasApplied) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        // 지원자 목록 조회
        List<Apply> applies = applyRepository.findByArticle(article);
        List<ApplyResponse> responses = new ArrayList<>();

        for (Apply apply : applies) {
            ApplyResponse response = new ApplyResponse();
            response.setApplyNo(apply.getApply_no());
            response.setArticleNo(articleNo);
            response.setMemberName(apply.getMemberId().getMemberName());
            response.setApplyDate(apply.getApplyDate());
            response.setApplyResult(apply.getApplyResult());
            responses.add(response);
        }

        return responses;
    }


    public List<MyPageApplyResponse> getAppliesByMemberId(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        // 회원 ID로 회원을 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {
            // 회원이 존재하는 경우, 해당 회원이 신청한 모든 apply를 조회
            Member member = optionalMember.get();
            List<Apply> applies = applyRepository.findByMemberId(member);

            // Apply를 MyPageApplyResponse로 변환하여 리스트에 추가
            List<MyPageApplyResponse> myPageApplies = new ArrayList<>();
            for (Apply apply : applies) {
                MyPageApplyResponse myPageApplyResponse = new MyPageApplyResponse();
                myPageApplyResponse.setApplyNo(apply.getApply_no());
                myPageApplyResponse.setApplyDate(apply.getApplyDate());
                myPageApplyResponse.setApplyResult(apply.getApplyResult());

// Apply의 Article 정보를 가져옴
                Article article = articleRepository.findById(apply.getArticle().getArticle_no())
                        .orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + apply.getArticle().getArticle_no()));

                // Convert Article entity to MyPageArticleDTO
                ArticleDTO articleDTO = new ArticleDTO();
                articleDTO.setArticleNo(article.getArticle_no());
                articleDTO.setTitle(article.getTitle());
                articleDTO.setConMethod(article.getConMethod());
                articleDTO.setConInfo(article.getConInfo());

                // Convert Member entity to ApplyMemberDTO
                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setMemberNo(apply.getArticle().getMemberId().getMemberNo());
                memberDTO.setMemberName(apply.getArticle().getMemberId().getMemberName());

                articleDTO.setMember(memberDTO);

                myPageApplyResponse.setApplyArticle(articleDTO);

                myPageApplies.add(myPageApplyResponse);
            }
            return myPageApplies;
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }

    public String updateApplyResult(HttpServletRequest request, Long applyId, ChangeApplyResult requestBody) {
        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        String memberId = authentication.getName();

        // 지원 정보 조회
        Optional<Apply> optionalApply = applyRepository.findById(applyId);
        if (!optionalApply.isPresent()) {
            throw new ResourceNotFoundException("지원 정보를 찾을 수 없습니다.");
        }

        Apply apply = optionalApply.get();
        Member author = apply.getArticle().getMemberId();

        // 현재 로그인한 사용자와 게시판의 작성자가 일치하는지 확인
        if (!author.getMemberId().equals(memberId)) {
            throw new UnauthorizedException("작성자만 지원자의 합격을 결정할 수 있습니다.");
        }

        // 작성자와 일치하면, 작업 수행
        String applyResult = requestBody.getApplyResult();

        // apply 엔터티의 applyResult 필드를 직접 수정
        apply.setApplyResult(applyResult);
        applyRepository.save(apply);

        // 만약 결과가 "합격"인 경우, Article 엔터티의 applyNow 필드를 1 증가
        if ("합격".equals(applyResult)) {
            Article article = apply.getArticle();
            int currentApplyNow = article.getApplyNow();
            article.setApplyNow(currentApplyNow + 1);
            articleRepository.save(article);
        }

        return "applyResult updated successfully";
    }

    public ResponseEntity<BasicInfoResponse> findBasicInfo(Long memberNo) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberNo));

        BasicInfo basicInfos = basicInfoRepository.findByMember(member);

        BasicInfoResponse basicInfoResponse = basicInfos == null ?
                BasicInfoResponse.builder()
                        .studentLocate(null)
                        .studentMajor(null)
                        .studentOneLineShow(null)
                        .memberId(member.getMemberId())
                        .memberName(member.getMemberName())
                        .memberPhoneNum(member.getMemberPhoneNum())
                        .build() :
                BasicInfoResponse.builder()
                        .studentLocate(basicInfos.getStudentResumeLocate())
                        .studentMajor(basicInfos.getStudentResumeMajor())
                        .studentOneLineShow(basicInfos.getStudentResumeOnelineshow())
                        .memberId(member.getMemberId())
                        .memberName(member.getMemberName())
                        .memberPhoneNum(member.getMemberPhoneNum())
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(basicInfoResponse);
    }

    public ResponseEntity<MemberInfoResponse> findCareerAndCertification(Long memberNo) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberNo));

        List<Career> careers = careerRepository.findAllByMember(member);
        List<Certification> certifications = certificationRepository.findAllByMember(member);

        List<CareerDto> careerList = careers.stream()
                .map(career -> CareerDto.builder()
                        .carName(career.getCarName())
                        .carStartDay(career.getCarStartDay())
                        .carEndDay(career.getCarEndDay())
                        .build())
                .collect(Collectors.toList());

        List<CertificationDto> certificationDtoList = certifications.stream()
                .map(certification -> CertificationDto.builder()
                        .certName(certification.getCertName())
                        .certDate(certification.getCertDate())
                        .build())
                .collect(Collectors.toList());

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse();
        memberInfoResponse.setCareerDtoList(careerList);
        memberInfoResponse.setCertificationDtoList(certificationDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(memberInfoResponse);
    }
}

