package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.repository.MemberRepository;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
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

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setEmail("xxx@gamil.com");

        Member member2 = new Member();
        member2.setEmail("xxx@gamil.com");

        // when
        memberService.join(member1);
        memberService.join(member2); //예외 발생

        // then
        fail("예외가 발생해야 한다.");
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
        List<Member> result = memberRepository.findAll();

        // then
        assertEquals(result.size(), 2);
    }

}