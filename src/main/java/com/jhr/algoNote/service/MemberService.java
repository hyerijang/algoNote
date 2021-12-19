package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
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
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        return memberRepository.save(member);

    }

    /**
     * 중복 회원 검증 - 이메일 검증
     */
    private void validateDuplicateMember(Member member) {
        log.info("member email:" + member.getEmail());
        Optional<Member> findMembers = memberRepository.findByEmail(member.getEmail());

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("동일한 이메일로 중복 가입 할 수 없습니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 id로 조회 (단건 조회)
     */
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

}
