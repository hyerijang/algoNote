package com.jhr.algoNote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.dto.ProblemUpdateRequest;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.query.ProblemSearch;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

    Member member = null;

    @BeforeEach
    private void createProblems() {
        member = Member.builder()
            .name("name")
            .email("email")
            .role(Role.USER)
            .build();

        memberService.join(member);

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


    @Test
    @DisplayName("문제 등록")
    void register() {
        //given
        String title = "title";
        String content = "sample text";
        //when
        Long savedProblemId = problemService.register(member.getId(), title, content);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertAll(
            () -> assertEquals(content, result.getContent().getText()),
            () -> assertEquals(savedProblemId, result.getId())
        );

    }


    @Test
    @DisplayName("문제 등록 with 사이트명 ,  url")
    void register_with_non_essential_param() {
        //given
        String title = "문제 제목";
        String content = "문제 내용";
        //when

        Long savedProblemId = problemService.register(member.getId(), title, content, "", "백준",
            "https://www.acmicpc.net/");

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertAll(
            () -> assertEquals(savedProblemId, result.getId()),
            () -> assertEquals(content, result.getContent().getText()),
            () -> assertEquals("백준", result.getSite())
        );
    }

    // == 태그 ==
    @Test
    void 한_문제에_같은_태그를_여러개_등록_가능() {
        //given
        Long savedProblemId = problemService.register(member.getId(), "title", "sample text",
            "그리디 그리디 병합정렬");

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(3, result.getProblemTags().size());

    }

    @Test
    void 태그를_등록하지_않아도_된다() {
        //given
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
        //when
        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberEmail(member.getEmail())
            .site("백준")
            .build();

        List<Problem> result = problemService.search(problemSearch);
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

        List<Problem> result = problemService.search(problemSearch);
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

        List<Problem> result = problemService.search(problemSearch);
        //than
        assertAll(
            () -> assertEquals(4, result.size()),
            () -> assertEquals(result.get(0).getMember().getId(), member.getId(), "자신의 id와 동일해야한다.")
        );

    }


    @Test
    @DisplayName("DTO 사용해서 문제 등록")
    void register_with_dto() {
        //given
        String title = "title";
        String content = "sample text";

        //when
        ProblemCreateRequest dto = ProblemCreateRequest.builder().title(title).contentText(content)
            .build();
        Long savedProblemId = problemService.register(member.getId(), dto);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertAll(
            () -> assertEquals(content, result.getContent().getText()),
            () -> assertEquals(savedProblemId, result.getId()));

    }

    @Test
    void 문제_ID로_조회() {
        //given
        String title = "title";
        String content = "sample text";
        ProblemCreateRequest dto = ProblemCreateRequest.builder().title(title).contentText(content)
            .build();
        Long savedProblemId = problemService.register(member.getId(), dto);

        //when
        Problem result = problemService.findOne(savedProblemId);
        //than
        assertAll(
            () -> assertEquals(content, result.getContent().getText()),
            () -> assertEquals(savedProblemId, result.getId()));

    }

    @Test
    void 문제_수정() {
        //given
        ProblemCreateRequest dto = ProblemCreateRequest.builder()
            .title("")
            .contentText("")
            .build();
        Long savedProblemId = problemService.register(member.getId(), dto);
        Problem problem = problemRepository.findById(savedProblemId);

        // 수정 dto 생성
        final String TITLE = "새로운제목";
        final String TAGTEXT = "DP,배열,알고리즘,새로운태그";
        final int TAGSIZE = 4;
        final String CONTENT = "hello world";
        ProblemUpdateRequest dto2 = ProblemUpdateRequest.builder()
            .id(savedProblemId)
            .title(TITLE)
            .tagText(TAGTEXT)
            .contentText(CONTENT)
            .build();

        //when
        Long updatedProblemId = problemService.edit(member.getId(), dto2);

        //than
        assertEquals(savedProblemId, updatedProblemId);

        Problem updatedProblem = problemService.findOne(savedProblemId);
        assertAll(
            () -> assertEquals(TITLE, updatedProblem.getTitle()),
            () -> assertEquals(CONTENT, updatedProblem.getContent().getText()),
            () -> assertEquals(TAGSIZE, updatedProblem.getProblemTags().size()));
    }

    @Test
    public void 문제_생성_및_수정_날짜() {
        //given
        LocalDateTime now = LocalDateTime.now();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ProblemCreateRequest dto = ProblemCreateRequest.builder()
            .title("")
            .contentText("")
            .build();
        Long savedProblemId = problemService.register(member.getId(), dto);

        //when
        Problem problem = problemRepository.findById(savedProblemId);

        //then
        assertAll(
            () -> assertThat(problem.getCreatedDate()).isAfter(now),
            () -> assertThat(problem.getModifiedDate()).isAfter(now)
        );

    }

    @Test
    void 문제의_태그리스트를_텍스트로_변경() {
        // given
        String tagText = "사과,오렌지,딸기,오렌지,사과";
        ProblemCreateRequest dto = ProblemCreateRequest.builder()
            .title("")
            .contentText("")
            .tagText(tagText)
            .build();
        Long savedProblemId = problemService.register(member.getId(), dto);
        // when
        Problem problem = problemRepository.findById(savedProblemId);
        // then
        String result = problemService.getTagText(problem.getProblemTags());
        System.out.println("result = " + result);
        assertEquals(tagText, result);

    }

    @Test
    void 문제_태그_수정() {
        //given
        ProblemCreateRequest dto = ProblemCreateRequest.builder()
            .title("")
            .contentText("")
            .tagText("123,1234,1")
            .build();
        Long savedProblemId = problemService.register(member.getId(), dto);
        Problem problem = problemRepository.findById(savedProblemId);

        // 수정 dto 생성
        final String TAGTEXT = "DP,배열,알고리즘,새로운태그";
        final int TAGSIZE = 4;
        ProblemUpdateRequest updateRequest = ProblemUpdateRequest.builder()
            .id(savedProblemId)
            .tagText(TAGTEXT)
            .title("")
            .contentText("")
            .build();

        //when
        Long updatedProblemId = problemService.edit(member.getId(), updateRequest);
        Problem updatedProblem = problemService.findOne(savedProblemId);

        //than
        assertAll(
            () -> assertEquals(savedProblemId, updatedProblemId),
            () -> assertEquals(TAGSIZE, updatedProblem.getProblemTags().size()));


    }


    /**
     * Problem 객체의 연관 메서드인 addProblemTag, setContent 가 제대로 동작하는지 테스트
     */
    @Test
    @DisplayName("연관관계 메서드 테스트")
    void relationMethod() {
        //given
        String content = "sample text";
        String tagText = "tag1, tag2";

        ProblemCreateRequest dto = ProblemCreateRequest.builder()
            .title("title")
            .contentText(content)
            .tagText(tagText)
            .build();

        Problem entity = problemService.findOne(problemService.register(member.getId(), dto));
        //when
        // 수정 dto 생성

        ProblemUpdateRequest updateRequest = ProblemUpdateRequest.builder()
            .id(entity.getId())
            .contentText("edit")
            .tagText("edit")
            .build();

        Problem saved = problemService.findOne(problemService.edit(member.getId(), updateRequest));

        //than
        assertAll(
            () -> assertEquals(saved.getProblemTags().size(), entity.getProblemTags().size()),
            () -> assertEquals(saved.getContent().getText(), entity.getContent().getText()));


    }


}