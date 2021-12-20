package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.exception.EmailRedundancyException;
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
        Member member = new Member().builder()
            .name("김")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();
        // when
        Long savedId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findById(savedId));
    }

    @Test
    public void 메일_중복_예외() throws Exception {
        // given
        String duplicateEmail = "xxx@gamil.com";

        Member member1 = new Member().builder()
            .name("김철수")
            .email(duplicateEmail)
            .role(Role.GUEST)
            .build();

        System.out.println("member1.getEmail() = " + member1.getEmail());

        Member member2 = new Member().builder()
            .name("김철수")
            .email(duplicateEmail)
            .role(Role.GUEST)
            .build();

        // when
        memberService.join(member1);

        // then
        assertThrows(EmailRedundancyException.class, () -> {
            memberService.join(member2); //예외 발생
        }, "예외가 발생해야 한다.");
    }

    @Test
    public void 회원_전체_조회() throws Exception {
        // given
        Member member1 = new Member().builder()
            .name("김철수")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();

        Member member2 = new Member().builder()
            .name("김철수")
            .email("xxx2@gmail.com")
            .role(Role.GUEST)
            .build();

        // when
        memberService.join(member1);
        memberService.join(member2);
        List<Member> result = memberService.findMembers();

        // then
        assertEquals(result.size(), 2);
    }

    @Test
    public void 회원_1명_조회() throws Exception {
        // given
        Member member = new Member().builder()
            .name("홍길동")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();

        // when
        memberService.join(member);
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals(findMember.getId(), member.getId());
        assertEquals(findMember.getName(), member.getName());
    }

    @Test
    void 회원_이름_변경() throws Exception {
        // given
        Member member = new Member().builder()
            .name("홍길동")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();
        memberService.join(member);

        // when
        member.updateName("수정된이름");
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals(findMember.getName(), "수정된이름");
    }

    @Test
    void 회원_사진_변경() throws Exception {
        // given
        Member member = new Member().builder()
            .name("홍길동")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();
        memberService.join(member);

        // when
        member.updatePicture("수정된사진");
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals(findMember.getPicture(), "수정된사진");
    }


}