package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProblemServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ProblemService problemService;


    @Test
    @DisplayName("문제 등록")
    void register() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        String title = "title";
        String content = "sample text";
        //when
        Long savedProblemId = problemService.register(member.getId(), title, content);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(content, result.getContent().getText());
        assertEquals(savedProblemId, result.getId());

    }


    @Test
    @DisplayName("문제 등록 with 사이트명 ,  url")
    void register_with_non_essential_param() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        String title = "문제 제목";
        String content = "문제 내용";
        //when

        Long savedProblemId = problemService.register(member.getId(), title, content, "", "백준",
            "https://www.acmicpc.net/");

        //than
        Problem result = problemRepository.findById(savedProblemId);

        assertEquals(savedProblemId, result.getId());
        assertEquals(content, result.getContent().getText());
        assertEquals("백준", result.getSiteName());
    }


    @Test
    void 문제_제목은_null_일수없음() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        //when
        //than

        assertThrows(NullPointerException.class, () -> {
            problemService.register(member.getId(), null, "content");
        }, "제목이 null일때 에러가 발생해야합니다.");

    }


    @Test
    void 문제_내용은_null_일수없음() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        //when
        //than
        assertThrows(NullPointerException.class, () -> {
            problemService.register(member.getId(), "title", null);
        }, "내용이 null일때 에러가 발생해야합니다.");

    }

    @Test
    void 문제_작성자는_null_일수없음() {
        //given
        //when
        //than
        assertThrows(NullPointerException.class, () ->
            problemService.register(null, "title", "content"), "member id가 null일때 에러가 발생해야합니다.");

    }

    @Test
    void 태그등록() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        Long savedProblemId = problemService.register(member.getId(), "title", "sample text",
            "그리디 그리디 병합정렬");

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(3, result.getProblemTags().size());

    }


    @Test
    void 태그_미등록() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        //when
        Long savedProblemId = problemService.register(member.getId(), "title", "sample text");

        //than
        Problem result = problemRepository.findById(savedProblemId);

        assertEquals(0, result.getProblemTags().size());
    }

    private Member createMember(String name, String email) {
        Member member = Member.builder()
            .name(name)
            .email(email)
            .role(Role.GUEST)
            .build();

        memberService.join(member);
        return member;
    }
}