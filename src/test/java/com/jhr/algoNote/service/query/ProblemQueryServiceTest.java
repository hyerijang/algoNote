package com.jhr.algoNote.service.query;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProblemQueryServiceTest {

    @Autowired
    ProblemService problemService;

    @Autowired
    MemberService memberService;

    Member member = null;

    @BeforeEach
    private void createProblems() {
        final String EMAIL = "email@gmail.com";
        final String NAME = "NAME";
        final String PICTURE = "PICTURE";

        CreateMemberRequest request = new CreateMemberRequest(NAME, EMAIL, PICTURE);
        member = memberService.findOne(memberService.join(request).getId());

        problemService.register(member.getId(), "오픈 채팅방", "content", "", "백준",
            "https://www.acmicpc.net/");
        problemService.register(member.getId(), "문자열 압축", "문자열을 압축하자", "", "프로그래머스",
            "https://programmers.co.kr/learn/challenges");
        problemService.register(member.getId(), "크레인 인형뽑기 게임",
            "게임개발자인 \"죠르디\"는 크레인 인형뽑기 기계를 모바일 게임으로 만들려고 합니다.", "", "프로그래머스",
            "https://programmers.co.kr/learn/courses/30/lessons/64061");
        problemService.register(member.getId(), "BFS",
            "게임개발자인 \"죠르디\"는 크레인 인형뽑기 기계를 모바일 게임으로 만들려고 합니다", "tag1, tag2,tag3", null, null);
    }

    @Test
    void 사이트명으로_검색() {
        //given
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberEmail(member.getEmail())
            .site("백준")
            .build();

        List<Problem> result = problemService.search(0, 100, problemSearch);
        //than
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals(result.get(0).getMember().getId(), member.getId(),
                "자신의 id와 동일해야한다."),
            () -> assertEquals(result.get(0).getTitle(), "오픈 채팅방")
        );

    }

    @Test
    void 문제_제목으로_검색() {
        //given
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberEmail(member.getEmail())
            .title("채팅")
            .build();

        List<Problem> result = problemService.search(0, 100, problemSearch);
        //than
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals(result.get(0).getMember().getId(), member.getId(),
                "자신의 id와 동일해야한다."),
            () -> assertEquals(result.get(0).getSite(), "백준")
        );
    }

    @Test
    void 이메일로_검색() {
        //given
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberEmail(member.getEmail())
            .build();

        List<Problem> result = problemService.search(0, 100, problemSearch);
        //than
        assertAll(
            () -> assertEquals(4, result.size()),
            () -> assertEquals(result.get(0).getMember().getId(), member.getId(), "자신의 id와 동일해야한다.")
        );

    }

}