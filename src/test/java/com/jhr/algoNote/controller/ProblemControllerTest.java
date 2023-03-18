package com.jhr.algoNote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(value = ProblemController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureMockMvc(addFilters = false) //403오류시 addFilter = false 추가
class ProblemControllerTest {

    @MockBean
    MemberService memberService;

    @MockBean
    ProblemService problemService;

    @MockBean
    ReviewService reviewService;

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession httpSession;
    final Member member = new Member("홍길동", "abc@naver.com", "pic", Role.USER);

    @BeforeEach
    public void setUp() {
        httpSession = new MockHttpSession();
        httpSession.setAttribute("user", new SessionUser(member));
        MockitoAnnotations.openMocks(this);
    }

    // 문제 생성
    @Test
    @WithMockUser
    @DisplayName("문제 등록 : 리턴하는 View 이름은 problems/createProblemForm이어야한다.")
    void createForm() throws Exception {
        mvc.perform(get("/problems/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/createProblemForm"));
    }


    @Test
    @WithMockUser
    @DisplayName("문제 등록 : 유효성 검증 실패 시 Form으로 이동")
    void create_binding_fail() throws Exception {
        //given
        ProblemForm problemForm = new ProblemForm();
        problemForm.setTitle(""); //유효성 검증 실패
        //when
        //than
        mvc.perform(post("/problems/new")
                        .flashAttr("problemForm", problemForm) //모델 추가
                        .session(httpSession))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("problems/createProblemForm"));

    }

    @Test
    @WithMockUser
    @DisplayName("문제 등록 : 성공 시 문제 상세 페이지로 리다이렉트")
    void create_successful() throws Exception {
        //given
        ProblemForm problemForm = new ProblemForm();
        problemForm.setTitle("유효한제목"); //유효성 검증 성공
        //when
        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);
        Mockito.when(problemService.register(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ProblemCreateRequest.class)))
                .thenReturn(1234L);


        //than
        mvc.perform(post("/problems/new")
                        .flashAttr("problemForm", problemForm) //모델 추가
                        .session(httpSession))
                .andExpect(status().is3xxRedirection());

    }


    @Test
    @WithMockUser
    @DisplayName("problems/problemList")
    void list() throws Exception {
        //given
        List<Problem> problems = new ArrayList<>();
        Problem p = Problem.builder()
                .title("title")
                .member(member)
                .content(ProblemContent.createProblemContent("123"))
                .problemTagList(new ArrayList<>())
                .build();
        problems.add(p);
        //when

        Mockito.when(problemService.search(ArgumentMatchers.any(ProblemSearch.class)))
                .thenReturn(problems);
        Mockito.when(problemService.getTagText(ArgumentMatchers.any()))
                .thenReturn("");

        //than
        mvc.perform(get("/problems")
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/problemList"));
    }


    //=================================================
    // 문제 수정
    @Test
    @WithMockUser
    @DisplayName("문제 수정 : 리턴하는 View 이름은 problems/updateProblemForm")
    void updateForm() throws Exception {

        //given
        Problem problem = Problem.builder()
                .title("title")
                .member(member)
                .content(ProblemContent.createProblemContent("content text"))
                .problemTagList(new ArrayList<>())
                .build();

        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);
        Mockito.when(problemService.findOne(ArgumentMatchers.anyLong()))
                .thenReturn(problem);
        //than
        mvc.perform(get("/problems/999/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/updateProblemForm"));
    }


    @Test
    @WithMockUser
    @DisplayName("문제 수정 : 유효성 검증 실패 시 Form으로 이동")
    void update_binding_fail() throws Exception {
        //given
        ProblemForm problemForm = new ProblemForm();
        problemForm.setTitle(""); //유효성 검증 실패
        //when
        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);

        //than
        mvc.perform(post("/problems/999/edit")
                        .flashAttr("problemForm", problemForm) //모델 추가
                        .session(httpSession))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("problems/updateProblemForm"));
    }

    @Test
    @WithMockUser
    @DisplayName("문제 수정 : 성공 시 문제 상세 페이지로 리다이렉트")
    void update_successful() throws Exception {
        //given
        ProblemForm problemForm = new ProblemForm();
        problemForm.setTitle("유효한제목"); //유효성 검증 성공
        //when
        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);


        //than
        mvc.perform(post("/problems/999/edit")
                        .flashAttr("problemForm", problemForm) //모델 추가
                        .session(httpSession))
                .andExpect(status().is3xxRedirection());

    }


}