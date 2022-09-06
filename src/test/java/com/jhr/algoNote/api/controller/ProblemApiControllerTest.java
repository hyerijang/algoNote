package com.jhr.algoNote.api.controller;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.api.controller.ProblemApiController.CreateProblemRequest;
import com.jhr.algoNote.api.controller.ProblemApiController.CreateProblemResponse;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.repository.MemberRepository;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.query.ProblemQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProblemApiController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    })
public class ProblemApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProblemService problemService;
    @MockBean
    private ProblemRepository problemRepository;

    @MockBean
    private ProblemQueryService problemQueryService;

    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Problem problem;
    final Long WRITER_ID = 1234L;
    final Long PROBLEM_ID = 987654321L;
    final String PROBLEM_TITLE = "플로이드";
    final String PROBLEM_CONTENT_TEXT = "n(2 ≤ n ≤ 100)개의 도시가 있다. 그리고 한 도시에서 출발하여 다른 도시에 도착하는 m(1 ≤ m ≤ 100,000)개의 버스가 있다. 각 버스는 한 번 사용할 때 필요한 비용이 있다. 모든 도시의 쌍 (A, B)에 대해서 도시 A에서 B로 가는데 필요한 비용의 최솟값을 구하는 프로그램을 작성하시오.";
    final String PROBLEM_TAG_TEXT = "11404,백준,플로이드,플로이드와샬";

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .id(WRITER_ID)
            .name("TEST_NAME")
            .picture("TEST_PICTURE")
            .email("TEST_EMAIL")
            .build();

        when(memberService.findOne(Mockito.any(Long.class)))
            .thenReturn(member);

        ProblemContent pc = ProblemContent.createProblemContent(PROBLEM_CONTENT_TEXT);
        problem = Problem.builder()
            .title(PROBLEM_TITLE)
            .content(pc)
            .member(member)
            .build();

        when(problemService.register(Mockito.any(Long.class), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
        ).thenReturn(PROBLEM_ID);
        when(problemService.findOne(Mockito.any(Long.class))).thenReturn(problem);

    }

    @Test
    @WithMockUser(roles = "USER")
    public void 문제_생성() throws Exception {
        //given
        CreateProblemRequest createProblemRequest = new CreateProblemRequest();
        createProblemRequest.setTitle(PROBLEM_TITLE);
        createProblemRequest.setContent(PROBLEM_CONTENT_TEXT);
        createProblemRequest.setMemberId(WRITER_ID);

        CreateProblemResponse createProblemResponse = new CreateProblemResponse(PROBLEM_ID,
            PROBLEM_TITLE, WRITER_ID);

        //when
        mockMvc.perform(post("/api/problems/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
                .content(objectMapper.writeValueAsString(createProblemRequest)))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.writerId", is(WRITER_ID), Long.class)))
            .andExpect((jsonPath("$.title", is(PROBLEM_TITLE), String.class)))
            .andDo(print());

    }


}
