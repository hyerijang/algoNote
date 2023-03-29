package com.jhr.algoNote.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.api.controller.MemberApiController.UpdateMemberRequest;
import com.jhr.algoNote.api.dto.CreateMemberRequest;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.repository.MemberRepository;
import com.jhr.algoNote.repository.query.ProblemQueryRepository;
import com.jhr.algoNote.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(value = ProblemApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureMockMvc(addFilters = false) //403오류시 addFilter = false 추가
class ProblemApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProblemQueryRepository problemQueryRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void initEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ProblemApiController(problemQueryRepository))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        Member writer = Member.builder().name("Hyeri Jang").email("hyeri@naver.com").role(Role.ADMIN).picture("hrj.jpg").build();

        List<Problem> problems = new ArrayList<>();
        Problem problem1 = Problem.builder().title("에스컬레이터").member(writer).content(ProblemContent.createProblemContent("123")).problemTagList(new ArrayList<>()).build();
        ReflectionTestUtils.setField(problem1, "id", 1L);
        Problem problem2 = Problem.builder().title("아기 상어").member(writer).content(ProblemContent.createProblemContent("뚜루루뚜루")).problemTagList(new ArrayList<>()).build();
        ReflectionTestUtils.setField(problem2, "id", 2L);
        Problem problem3 = Problem.builder().title("피보나치 수열").member(writer).content(ProblemContent.createProblemContent("피보나치 수열을 구현하세요")).problemTagList(new ArrayList<>()).build();
        ReflectionTestUtils.setField(problem3, "id", 3L);

        problems.add(problem1);
        problems.add(problem2);
        problems.add(problem3);

        Mockito.when(problemQueryRepository.findAll(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(problems);

    }


    @Test
    void 문제조회() throws Exception {
        //given

        //when
        final ResultActions resultActions = mockMvc.perform(get("/api/problems"));

        //than
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].problemId").value(1L))
                .andExpect(jsonPath("$.data[0].problemTitle").value("에스컬레이터"))
                .andExpect(jsonPath("$.data[1].problemId").value(2L))
                .andExpect(jsonPath("$.data[1].problemTitle").value("아기 상어"));
    }


}