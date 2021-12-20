package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Member member = Member.builder()
            .name("김")
            .email("xxx@gmail.com")
            .role(Role.GUEST)
            .build();

        Long memberId = memberService.join(member);
        String title = "title";
        String text = "sample text";
        //when
        Long savedProblemId = problemService.register(memberId, title, text, "그리디", "그리디", "greedy",
            "병합정렬");

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(result.getId(), savedProblemId);
        assertEquals(result.getContent().getText(), text);
        assertEquals(result.getId(), savedProblemId);
    }


}