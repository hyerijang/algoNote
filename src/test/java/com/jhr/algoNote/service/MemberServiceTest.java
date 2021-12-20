package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("김");
        // when
        Long savedId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findById(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        String email = "xxx@gamil.com";

        Member member1 = new Member();
        member1.setEmail(email);

        Member member2 = new Member();
        member2.setEmail(email);

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2); //예외 발생
        }, "예외가 발생해야 한다.");
    }

    @Test
    public void 회원_전체_조회() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("홍길동");

        Member member2 = new Member();
        member2.setName("김철수");

        // when
        memberService.join(member1);
        memberService.join(member2);
        List<Member> result = memberService.findMembers();

        // then
        assertEquals(result.size(), 2);
    }

}