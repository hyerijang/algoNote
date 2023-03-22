package com.jhr.algoNote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.content.ReviewContent;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        Long PROBLEM_ID = 987654321L;

        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);
        Mockito.when(problemService.findOne(ArgumentMatchers.anyLong()))
                .thenReturn(problem);
        //than
        mvc.perform(get("/problems/"+PROBLEM_ID +"/edit"))
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
        Long PROBLEM_ID = 987654321L;
        //when
        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);

        //than
        mvc.perform(post("/problems/"+PROBLEM_ID +"/edit")
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
        Long PROBLEM_ID = 987654321L;
        //when
        Mockito.when(memberService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(member);


        //than
        mvc.perform(post("/problems/"+PROBLEM_ID +"/edit")
                        .flashAttr("problemForm", problemForm) //모델 추가
                        .session(httpSession))
                .andExpect(status().is3xxRedirection());

    }


    //=================================================
    // 문제 검색
    @Test
    @WithMockUser
    @DisplayName("문제 검색 : 리턴하는 View 이름은 problems/problemSearch")
    void searchForm() throws Exception {
        //given
        ProblemSearch problemSearch = ProblemSearch.builder()
                .keyword("")
                .memberEmail(member.getEmail())
                .build();
        //when
        //than
        mvc.perform(get("/problems/search")
                        .session(httpSession)
                        .flashAttr("problemSearch",problemSearch))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/problemSearch"));

    }

    @Test
    @WithMockUser
    @DisplayName("문제 검색 : 성공 - 결과에 해당하는 문제가 없는 경우")
    void problemSearch_no_result() throws Exception {
        //given
        List<Problem> problems = new ArrayList<>();
        ProblemSearch problemSearch = ProblemSearch.builder().keyword("keword").memberEmail(member.getEmail()).build();
        //when
        Mockito.when(problemService.search(ArgumentMatchers.any(ProblemSearch.class)))
                .thenReturn(problems);
        Mockito.when(problemService.getTagText(ArgumentMatchers.any()))
                .thenReturn("");
        Mockito.when(problemService.search(ArgumentMatchers.any(ProblemSearch.class)))
                .thenReturn(problems);
        //than
        mvc.perform(get("/problems/search")
                        .session(httpSession)
                        .flashAttr("problemSearch",problemSearch))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/problemSearch"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("problems"));

        verify(problemService).search(ArgumentMatchers.any(ProblemSearch.class));

    }

    @Test
    @WithMockUser
    @DisplayName("문제 검색 : 성공 - 결과에 해당하는 문제가 있는 경우")
    void problemSearch() throws Exception {
        //given
        List<Problem> problems = new ArrayList<>();
        Problem p = Problem.builder()
                .title("keword")
                .member(member)
                .content(ProblemContent.createProblemContent("hello world"))
                .problemTagList(new ArrayList<>())
                .build();
        problems.add(p);

        ProblemSearch problemSearch = ProblemSearch.builder().keyword("keword").memberEmail(member.getEmail()).build();
        //when
        Mockito.when(problemService.search(ArgumentMatchers.any(ProblemSearch.class)))
                .thenReturn(problems);
        Mockito.when(problemService.getTagText(ArgumentMatchers.any()))
                .thenReturn("");
        Mockito.when(problemService.search(ArgumentMatchers.any(ProblemSearch.class)))
                .thenReturn(problems);
        //than
        mvc.perform(get("/problems/search")
                        .session(httpSession)
                        .flashAttr("problemSearch",problemSearch))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/problemSearch"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("problems"));

        verify(problemService).search(ArgumentMatchers.any(ProblemSearch.class));

    }

    //=================================================
    // 문제 상세
    @Test
    @WithMockUser
    @DisplayName("문제 상세 : 리턴하는 View 이름은 problems/problemDetails")
    void problemDetail() throws Exception {
        //given
        Long PROBLEM_ID = 987654321L;
        Problem p = Problem.builder()
                .title("keword")
                .member(member)
                .content(ProblemContent.createProblemContent("hello world"))
                .problemTagList(new ArrayList<>())
                .build();
        //when
        Mockito.when(problemService.findOne(ArgumentMatchers.anyLong()))
                .thenReturn(p);
        //than
        mvc.perform(get("/problems/"+PROBLEM_ID)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/problemDetails"));

    }

    @Test
    @WithMockUser
    @DisplayName("문제 상세 : 조회된 문제가 없는 경우 리다이렉트")
    void ProblemDetail_NotExist() throws Exception {
        //given
        Long PROBLEM_ID = 987654321L;
        //when
        Mockito.when(problemService.findOne(ArgumentMatchers.anyLong()))
                .thenReturn(null);
        //than
        mvc.perform(get("/problems/"+PROBLEM_ID)
                        .session(httpSession))
                .andExpect(view().name("redirect:"))
                .andExpect(status().is3xxRedirection());
    }


    @Disabled
    @Test
    @WithMockUser
    @DisplayName("문제 상세 : 관련 리뷰도 함께 조회되어야 한다.")
    void problemDetailwithReview() throws Exception {
        //given
        Long PROBLEM_ID = 987654321L;
        //문제
        Problem p = Problem.builder()
                .title("문제제목")
                .member(member)
                .content(ProblemContent.createProblemContent("hello world"))
                .problemTagList(new ArrayList<>())
                .build();
        //리뷰
        List<Review> reviews = new ArrayList<>();
        reviews.add(Review.builder()
                .title("리뷰제목")
                .member(member)
                .problem(p)
                .content(new ReviewContent())
                .reviewTagList(new ArrayList<>())
                .build());
        //when
        Mockito.when(problemService.findOne(ArgumentMatchers.anyLong()))
                .thenReturn(p);

        // TODO : 리뷰 조회를 mock 처리할 방법을 찾을 것
        Mockito.when(p.getReviews())
                .thenReturn(reviews);
        //than
        mvc.perform(get("/problems/"+PROBLEM_ID)
                        .session(httpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("problems/problemDetails"));
        
        verify(any(Problem.class)).getReviews(); //리뷰 조회 함수 실행

    }

}