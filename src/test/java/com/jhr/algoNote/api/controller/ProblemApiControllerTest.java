package com.jhr.algoNote.api.controller;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.dto.CreateProblemRequest;
import com.jhr.algoNote.dto.CreateProblemResponse;
import com.jhr.algoNote.dto.ProblemResponse;
import com.jhr.algoNote.dto.UpdateProblemRequest;
import com.jhr.algoNote.dto.UpdateProblemResponse;
import com.jhr.algoNote.repository.MemberRepository;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import java.util.ArrayList;
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
            .id(PROBLEM_ID)
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
        CreateProblemRequest createProblemRequest = CreateProblemRequest.builder()
            .title(PROBLEM_TITLE).contentText(PROBLEM_CONTENT_TEXT).memberId(WRITER_ID).build();

        CreateProblemResponse createProblemResponse = new CreateProblemResponse(PROBLEM_ID,
            PROBLEM_TITLE, WRITER_ID);

        //when
        mockMvc.perform(post("/api/problems")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
                .content(objectMapper.writeValueAsString(createProblemRequest)))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.id", is(PROBLEM_ID), Long.class)))
            .andExpect((jsonPath("$.memberId", is(WRITER_ID), Long.class)))
            .andExpect((jsonPath("$.title", is(PROBLEM_TITLE), String.class)))
            .andDo(print());

    }

    @Test
    @WithMockUser(roles = "USER")
    public void 문제_수정() throws Exception {
        //given
        final String UPDATED_TITLE = "테트로미노";
        final String UPDATED_CONTENT_TEXT = "폴리오미노란 크기가 1×1인 정사각형을 여러 개 이어서 붙인 도형이며, 다음과 같은 조건을 만족해야 한다.";
        final String UPDATED_SITE = "BAEKJOON";
        final String UPDATED_URL = "https://www.acmicpc.net/problem/14500";
        final String UPDATED_TAG_TEXT = "14500,백준,BFS";

        UpdateProblemRequest request = UpdateProblemRequest.builder()
            .title(UPDATED_TITLE).contentText(UPDATED_CONTENT_TEXT).tagText(UPDATED_TAG_TEXT)
            .build();

        problem.patch(UPDATED_TITLE, UPDATED_CONTENT_TEXT, UPDATED_SITE, UPDATED_URL);
        UpdateProblemResponse response = UpdateProblemResponse.of(problem, UPDATED_TAG_TEXT);

        when(problemService.update(Mockito.any(), Mockito.any())).thenReturn(response);

        //when
        mockMvc.perform(patch("/api/problems/" + PROBLEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.id", is(PROBLEM_ID), Long.class)))
            .andExpect((jsonPath("$.title", is(UPDATED_TITLE), String.class)))
            .andExpect((jsonPath("$.contentText", is(UPDATED_CONTENT_TEXT), String.class)))
            .andExpect((jsonPath("$.tagText", is(UPDATED_TAG_TEXT), String.class)))
            .andDo(print());

    }

    @Test
    @WithMockUser(roles = "USER")
    public void 문제_조회() throws Exception {
        //given
        ArrayList<ProblemResponse> response = new ArrayList<>();
        response.add(new ProblemResponse(problem));
        when(problemService.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(response);

        //when
        mockMvc.perform(get("/api/problems/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
                .param("offset","0")
                .param("limit", "1000")
                .content(objectMapper.writeValueAsString("request")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(response.size())))
            .andExpect(jsonPath("$.data[0].id", is(PROBLEM_ID), Long.class))
            .andDo(print());

    }

}
