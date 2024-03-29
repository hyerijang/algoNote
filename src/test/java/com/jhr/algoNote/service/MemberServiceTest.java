package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.exception.EmailRedundancyException;
import com.jhr.algoNote.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
        Member member = Member.builder()
                .name("김")
                .email("xxx@gmail.com")
                .role(Role.USER)
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

        Member member1 = Member.builder()
                .name("김철수")
                .email(duplicateEmail)
                .role(Role.USER)
                .build();

        System.out.println("member1.getEmail() = " + member1.getEmail());

        Member member2 = Member.builder()
                .name("김철수")
                .email(duplicateEmail)
                .role(Role.USER)
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
        Member member1 = Member.builder()
                .name("김철수")
                .email("xxx@gmail.com")
                .role(Role.USER)
                .build();

        Member member2 = Member.builder()
                .name("김철수")
                .email("xxx2@gmail.com")
                .role(Role.USER)
                .build();

        // when
        memberService.join(member1);
        memberService.join(member2);
        List<Member> result = memberService.findMembers();

        // then
        assertEquals(2, result.size());
    }

    @Test
    public void 회원_1명_조회() throws Exception {
        // given
        Member member = Member.builder()
                .name("홍길동")
                .email("xxx@gmail.com")
                .role(Role.USER)
                .build();

        // when
        memberService.join(member);
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getName(), findMember.getName());
    }

    @Test
    void 회원_이름_변경() throws Exception {
        // given
        Member member = Member.builder()
                .name("홍길동")
                .email("xxx@gmail.com")
                .role(Role.USER)
                .build();
        memberService.join(member);

        // when
        member.updateName("수정된이름");
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals("수정된이름", findMember.getName());
    }

    @Test
    void 회원_사진_변경() throws Exception {
        // given
        Member member = Member.builder()
                .name("홍길동")
                .email("xxx@gmail.com")
                .role(Role.USER)
                .build();
        memberService.join(member);

        // when
        member.updatePicture("수정된사진");
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals("수정된사진", findMember.getPicture());
    }

    @Test
    void 회원_이름은_null일_수_없음() throws Exception {
        // given
        assertThrows(NullPointerException.class, () -> {
            Member member = Member.builder()
                    .name(null)
                    .email("xxx@gmail.com")
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);
        }, "이름이 null일때 에러가 발생해야합니다.");

    }

    @DisplayName("회원 이메일로 조회")
    @Test
    void findByEmail() throws Exception {
        // given
        final String EMAIL = "test2022@gmail.com";
        Member member = Member.builder()
                .name("홍길동")
                .email(EMAIL)
                .role(Role.USER)
                .build();

        // when
        memberService.join(member);
        Member findMember = memberService.findByEmail(EMAIL);
        // then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getName(), findMember.getName());

    }

    @Test
    void 회원_이메일_조회시_null로_조회할_수_없음() throws Exception {
        // given
        assertThrows(NullPointerException.class, () -> {
            memberService.findByEmail(null);
        }, "null로 조회하려 하면 에러가 발생해야합니다.");

    }


    @DisplayName("회원 이메일로 조회 - DB에 존재하지 않는 이메일")
    @Test
    void findByNotExist() throws Exception {
        // given
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.findByEmail("123@123");
        }, "등록되지 않은 이메일입니다.");

    }


}