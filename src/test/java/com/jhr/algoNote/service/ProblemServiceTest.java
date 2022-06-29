package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.dto.ProblemRegisterDto;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.ProblemSearch;
import java.util.List;
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


    // == 태그 ==
    @Test
    void 한_문제에_같은_태그를_여러개_등록_가능() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        Long savedProblemId = problemService.register(member.getId(), "title", "sample text",
            "그리디 그리디 병합정렬");

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(3, result.getProblemTags().size());

    }


    @Test
    void 태그를_등록하지_않아도_된다() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        //when
        Long savedProblemId = problemService.register(member.getId(), "title", "sample text");

        //than
        Problem result = problemRepository.findById(savedProblemId);

        assertEquals(0, result.getProblemTags().size());
    }

    // == 검색 ==
    @Test
    void 사이트명으로_검색() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");
        createProblems(member);
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberId(member.getId())
            .siteName("백준")
            .build();

        List<Problem> result = problemService.search(problemSearch);
        //than
        assertEquals(1, result.size());
        assertEquals(result.get(0).getMember().getId(), member.getId(), "자신의 id와 동일해야한다.");
        assertEquals(result.get(0).getTitle(), "오픈 채팅방");

    }

    @Test
    void 문제_제목으로_검색() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");
        Member otherMember = createMember("김영희", "xxx2@gmail.com");
        createProblems(member);
        createProblems(otherMember);
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberId(member.getId())
            .title("채팅")
            .build();

        List<Problem> result = problemService.search(problemSearch);
        //than
        assertEquals(1, result.size());
        assertEquals(result.get(0).getMember().getId(), member.getId(), "자신의 id와 동일해야한다.");
        assertEquals(result.get(0).getSiteName(), "백준");
    }

    // == 테스트 작성에 도움을 주는 메서드 ==
    private void createProblems(Member member) {
        problemService.register(member.getId(), "오픈 채팅방", "content", "", "백준",
            "https://www.acmicpc.net/");
        problemService.register(member.getId(), "문자열 압축", "문자열을 압축하자", "", "프로그래머스",
            "https://programmers.co.kr/learn/challenges");
        problemService.register(member.getId(), "크레인 인형뽑기 게임",
            "게임개발자인 \"죠르디\"는 크레인 인형뽑기 기계를 모바일 게임으로 만들려고 합니다.", "", "프로그래머스",
            "https://programmers.co.kr/learn/courses/30/lessons/64061");
        problemService.register(member.getId(), "BFS",
            "게임개발자인 \"죠르디\"는 크레인 인형뽑기 기계를 모바일 게임으로 만들려고 합니다");
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

    @Test
    void 아이디로_검색() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");
        createProblems(member);
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberId(member.getId())
            .build();

        List<Problem> result = problemService.search(problemSearch);
        //than
        assertEquals(4, result.size());
        assertEquals(result.get(0).getMember().getId(), member.getId(), "자신의 id와 동일해야한다.");

    }


    @Test
    @DisplayName("DTO 사용해서 문제 등록")
    void register_with_dto() {
        //given
        Member member = createMember("홍길동", "xxx@gmail.com");

        String title = "title";
        String content = "sample text";

        //when
        ProblemRegisterDto dto = ProblemRegisterDto.builder().title(title).contentText(content)
            .build();
        Long savedProblemId = problemService.registerWithDto(member.getId(), dto);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(content, result.getContent().getText());
        assertEquals(savedProblemId, result.getId());

    }


}