package com.jhr.algoNote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(value = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureMockMvc(addFilters = false) //403오류시 addFilter = false 추가
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mvc;

    private MockHttpSession httpSession;

    @BeforeEach
    public void setUp() {
        httpSession = new MockHttpSession();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    @DisplayName("리턴하는 View 이름은 members/createMemberForm이어야한다.")
    void create() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/members/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/createMemberForm"));
    }


    @Test
    @WithMockUser
    @DisplayName("유효성 검증 실패 시 가입페이지로 이동")
    void create_binding_fail() throws Exception {
        //given
        MemberForm memberForm = new MemberForm();
        memberForm.setEmail("abc@naver.com");
        memberForm.setPicture("pic");
        memberForm.setRole(Role.USER);
        //when
        memberForm.setName(null); //유효성 검증 실패
        //than
        mvc.perform(MockMvcRequestBuilders.post("/members/new")
                        .flashAttr("memberForm", memberForm) //모델 추가
                        .session(httpSession))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("members/createMemberForm"));

    }

    @Test
    @WithMockUser
    @DisplayName("가입 성공 시 home으로 리다이렉트")
    void create_successful() throws Exception {
        //given
        MemberForm memberForm = new MemberForm();
        memberForm.setName("홍길동");
        memberForm.setEmail("abc@naver.com");
        memberForm.setPicture("pic");
        //when

        //than
        mvc.perform(MockMvcRequestBuilders.post("/members/new")
                        .flashAttr("memberForm", memberForm) //모델 추가
                        .session(httpSession))
                .andExpect(view().name("redirect:/"))
                .andExpect(status().is3xxRedirection());


    }


    @Test
    @WithMockUser
    @DisplayName("리턴하는 View 이름은 members/memberList이어야한다.")
    void list() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/memberList"));
    }

}