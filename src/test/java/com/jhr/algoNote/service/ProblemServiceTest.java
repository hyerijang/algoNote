package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
        Member member = Member.builder()
            .name("김")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();

        Long memberId = memberService.join(member);
        String title = "title";
        String content = "sample text";
        //when
        ProblemTag[] problemTags = problemService.createProblemTagList("그리디", "그리디", "greedy",
            "병합정렬");
        Long savedProblemId = problemService.register(memberId, title, content, problemTags);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(result.getId(), savedProblemId);
        assertEquals(result.getContent().getText(), content);
        assertEquals(result.getId(), savedProblemId);
    }


    @Test
    @DisplayName("문제 등록 with 사이트명 ,  url")
    void register_with_non_essential_param() {
        //given
        Member member = Member.builder()
            .name("김")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();

        Long memberId = memberService.join(member);
        String title = "문제 제목";
        String content = "문제 내용";
        //when

        ProblemTag[] problemTagList = problemService.createProblemTagList("그리디", "그리디", "greedy",
            "병합정렬");

        Long savedProblemId = problemService.register(memberId, title, content, "백준",
            "https://www.acmicpc.net/",
            problemTagList);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(result.getId(), savedProblemId);
        assertEquals(result.getContent().getText(), content);
        assertEquals(result.getId(), savedProblemId);
    }


    @Test
    void 문제_제목은_null_일수없음() {
        //given
        Member member = Member.builder()
            .name("김")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();

        Long memberId = memberService.join(member);

        ProblemTag[] problemTagList = problemService.createProblemTagList("그리디", "그리디", "greedy",
            "병합정렬");
        //when
        //than

        assertThrows(DataIntegrityViolationException.class, () -> {
            problemService.register(memberId, null, "content", problemTagList);
        }, "제목이 null일때 에러가 발생해야합니다.");

    }


}