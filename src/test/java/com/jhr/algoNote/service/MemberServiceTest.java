package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.dto.CreateMemberResponse;
import com.jhr.algoNote.dto.MemberResponse;
import com.jhr.algoNote.exception.EmailRedundancyException;
import com.jhr.algoNote.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Disabled;
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

    final String EMAIL = "email@gmail.com";
    final String NAME = "NAME";
    final String PICTURE = "PICTURE";

    @Test
    public void 회원가입() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);
        // when
        CreateMemberResponse response = memberService.join(request);
        Member member = memberService.findOne(response.getId());
        // then
        assertEquals(EMAIL, member.getEmail());
        assertEquals(NAME, member.getName());
        assertEquals(PICTURE, member.getPicture());
    }

    @Test
    public void 메일_중복_예외() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);
        CreateMemberRequest request2 = new CreateMemberRequest(NAME+"2", EMAIL, PICTURE+"2");

        // when
        memberService.join(request);

        // then
        assertThrows(EmailRedundancyException.class, () -> {
            memberService.join(request2); //예외 발생
        }, "예외가 발생해야 한다.");
    }

    @Test
    public void 회원_전체_조회() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);
        CreateMemberRequest request2 = new CreateMemberRequest(NAME, "EMAIL2", PICTURE);

        // when
        memberService.join(request);
        memberService.join(request2);
        List<MemberResponse> result = memberService.findMembers();
        // then
        assertEquals(2, result.size());
    }

    @Test
    public void 회원_1명_조회() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);

        // when
        CreateMemberResponse response = memberService.join(request);
        Member member = memberService.findOne(response.getId());
        // then
        assertEquals(NAME,member.getName());
    }

    @Test
    void 회원_이름_변경() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);
        CreateMemberResponse response = memberService.join(request);
        Member member = memberService.findOne(response.getId());

        // when
        member.updateName("수정된이름");
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals("수정된이름", findMember.getName());
    }

    @Test
    void 회원_사진_변경() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);
        CreateMemberResponse response = memberService.join(request);
        Member member = memberService.findOne(response.getId());

        // when
        member.updatePicture("수정된사진");
        Member findMember = memberService.findOne(member.getId());

        // then
        assertEquals("수정된사진", findMember.getPicture());
    }



}