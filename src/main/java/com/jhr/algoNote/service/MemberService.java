package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.dto.CreateMemberResponse;
import com.jhr.algoNote.dto.MemberResponse;
import com.jhr.algoNote.dto.UpdateMemberRequest;
import com.jhr.algoNote.dto.updateMemberResponse;
import com.jhr.algoNote.exception.EmailRedundancyException;
import com.jhr.algoNote.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */

    @Transactional
    public CreateMemberResponse join(CreateMemberRequest request) {
        Member member = Member.builder()
            .name(request.getName())
            .email(request.getEmail())
            .picture(request.getPicture())
            .role(Role.USER) // 기본권한 : ADMIN
            .build();
        validateDuplicateEmail(member); //중복 회원 검증
        return new CreateMemberResponse(memberRepository.save(member));
    }


    /**
     * 중복 회원 검증 - 이메일 검증
     */
    private void validateDuplicateEmail(Member member) {
        List<Member> findMembers = memberRepository.findByEmail(member.getEmail());

        if (!findMembers.isEmpty()) {
            log.info("This email is already exist (email= {})", member.getEmail());
            throw new EmailRedundancyException("동일한 이메일로 중복 가입 할 수 없습니다.");
        }

        log.info("This Email is not duplicated(email= {})", member.getEmail());
    }


    /**
     * 회원 id로 조회 (단건 조회)
     *
     * @Throw IllegalArgumentException 등록되지 않은 회원입니다.
     */
    public Member findOne(Long memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            log.info("This memberId is not exist (memberId={})", memberId);
            throw new IllegalArgumentException("등록되지 않은 회원입니다.");
        }
        return member;
    }

    /**
     * 회원 이메일로 조회 (단건 조회)
     */
    public Member findByEmail(String email) {
        if (email == null) {
            throw new NullPointerException("입력된 이메일이 null 입니다.");
        }

        List<Member> results = memberRepository.findByEmail(email);
        if (results.isEmpty()) {
            throw new IllegalArgumentException("등록되지 않은 회원입니다.");
        }
        return results.get(0);
    }


    public List<MemberResponse> findMembers() {
        List<Member> members = memberRepository.findAll();
        List<MemberResponse> collect = members.stream()
            .map(m -> new MemberResponse(m.getId(), m.getName(), m.getPicture()))
            .collect(Collectors.toList());
        return collect;
    }

    @Transactional
    public updateMemberResponse updateMember(Long id,
        UpdateMemberRequest request) {
        Member member = memberRepository.findById(id);
        member.updateName(request.getName());
        member.updatePicture(request.getPicture());

        return new updateMemberResponse(id, request.getName(),
            request.getPicture());
    }

}
