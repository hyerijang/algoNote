package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.dto.CreateProblemRequest;
import com.jhr.algoNote.dto.ProblemResponse;
import com.jhr.algoNote.dto.UpdateProblemRequest;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.query.ProblemQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ProblemServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ProblemService problemService;

    @Autowired
    ProblemQueryRepository problemQueryRepository;

    @Autowired
    EntityManager entityManager;

    Member member = null;
    CreateProblemRequest createProblemRequest = null;
    String TITLE = "TITLE";
    String CONTENT_TEXT = "CONTENT_TEXT";
    String TAGTEXT = "사과,오렌지,딸기,오렌지,사과";

    Long PROBLEMS_SIZE = -1L;

    @BeforeEach
    private void init() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("NAME", "EMAIL",
                "PICTURE");
        member = memberService.findOne(memberService.join(createMemberRequest).getId());

        PROBLEMS_SIZE = createProblems();
        createProblemRequest = CreateProblemRequest.builder()
                .memberId(member.getId())
                .title(TITLE)
                .contentText(CONTENT_TEXT)
                .tagText(TAGTEXT)
                .build();
    }

    private Long createProblems() {
        problemService.register(member.getId(), "오픈 채팅방", "content", "", "BAEKJOON",
                "https://www.acmicpc.net/");
        problemService.register(member.getId(), "문자열 압축", "문자열을 압축하자", "", "PROGRAMMERS",
                "https://programmers.co.kr/learn/challenges");
        problemService.register(member.getId(), "크레인 인형뽑기 게임",
                "게임개발자인 \"죠르디\"는 크레인 인형뽑기 기계를 모바일 게임으로 만들려고 합니다.", "", "PROGRAMMERS",
                "https://programmers.co.kr/learn/courses/30/lessons/64061");
        problemService.register(member.getId(), "BFS",
                "게임개발자인 \"죠르디\"는 크레인 인형뽑기 기계를 모바일 게임으로 만들려고 합니다", "", null, null);

        return 4L;
    }


    @Test
    @DisplayName("문제 등록")
    void register() {
        //given
        //when
        Long savedProblemId = problemService.register(createProblemRequest);
        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertAll(
                () -> assertEquals(CONTENT_TEXT, result.getContent().getText()),
                () -> assertEquals(TITLE, result.getTitle()),
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
        Long savedProblemId = problemService.register(member.getId(), title, content, "",
                "LEETCODE",
                "https://www.acmicpc.net/");

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertAll(
                () -> assertEquals(savedProblemId, result.getId()),
                () -> assertEquals(content, result.getContent().getText()),
                () -> assertEquals("LEETCODE", result.getSite().getCode())
        );
    }

    // == 태그 ==
    @Test
    void 한_문제에_같은_태그를_여러개_등록_가능() {
        //given
        Long savedProblemId = problemService.register(member.getId(), "title", "sample text",
                "그리디 그리디 병합정렬", null, null);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertEquals(3, result.getProblemTags().size());

    }

    // == 검색 ==


    @Test
    @DisplayName("DTO 사용해서 문제 등록")
    void register_with_dto() {
        //given
        String title = "title";
        String content = "sample text";
        CreateProblemRequest request = CreateProblemRequest.builder().title(title)
                .contentText(content).memberId(member.getId()).build();

        //when
        Long savedProblemId = problemService.register(request);

        //than
        Problem result = problemRepository.findById(savedProblemId);
        assertAll(
                () -> assertEquals(content, result.getContent().getText()),
                () -> assertEquals(savedProblemId, result.getId()));

    }

    @Test
    void 문제_ID로_조회() {
        //given
        Long savedProblemId = problemService.register(createProblemRequest);

        //when
        Problem result = problemService.findOne(savedProblemId);
        //than
        assertAll(
                () -> assertEquals(CONTENT_TEXT, result.getContent().getText()),
                () -> assertEquals(TITLE, result.getTitle()),
                () -> assertEquals(savedProblemId, result.getId()));

    }

    @Test
    void 문제_수정() {
        //given
        Long savedProblemId = problemService.register(createProblemRequest);

        final String TITLE = "새로운제목";
        final String TAGTEXT = "DP,배열,알고리즘,새로운태그";
        final int TAGSIZE = 4;
        final String CONTENT = "hello world";
        UpdateProblemRequest dto2 = UpdateProblemRequest.builder()
                .title(TITLE)
                .tagText(TAGTEXT)
                .contentText(CONTENT)
                .build();

        entityManager.flush();
        entityManager.clear();

        //when
        Long updatedProblemId = problemService.update(member.getId(), savedProblemId, dto2);

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

        Long savedProblemId = problemService.register(createProblemRequest);

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
        Long savedProblemId = problemService.register(createProblemRequest);
        // when
        Problem problem = problemRepository.findById(savedProblemId);
        // then
        String result = problemService.getTagText(problem.getProblemTags());
        assertEquals(TAGTEXT, result);

    }

    @Test
    void 문제_태그_수정() {
        //given
        Long savedProblemId = problemService.register(createProblemRequest);

        final String TAGTEXT = "DP,배열,알고리즘,새로운태그";
        final int TAGSIZE = 4;
        final String NEW_TITLE = "";
        final String NEW_CONTENT = "abc";

        UpdateProblemRequest updateRequest = UpdateProblemRequest.builder()
                .tagText(TAGTEXT)
                .title(NEW_TITLE)
                .contentText(NEW_CONTENT)
                .build();

        entityManager.flush();
        entityManager.clear();

        //when
        Long updatedProblemId = problemService.update(member.getId(), savedProblemId,
                updateRequest);
        Problem updatedProblem = problemService.findOne(updatedProblemId);

        //than
        assertAll(
                () -> assertEquals(NEW_TITLE, updatedProblem.getTitle()),
                () -> assertEquals(NEW_CONTENT, updatedProblem.getContent().getText()),
                () -> assertEquals(TAGSIZE, updatedProblem.getProblemTags().size()));


    }

    @Test
    @DisplayName("List<ProblemResponse> 를 반환하는 findAll() 테스트")
    void 조회테스트() {
        //given

        //when
        List<ProblemResponse> result = problemService.findAll(0, 100);

        //then
        assertAll(
                () -> assertEquals(PROBLEMS_SIZE, result.size()));

    }
}