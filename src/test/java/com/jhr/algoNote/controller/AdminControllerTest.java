package com.jhr.algoNote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.ProblemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(value = AdminController.class,
        excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class) })
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private MockHttpSession httpSession;
    final Member member = new Member("홍길동", "abc@naver.com", "pic", null);

    @BeforeEach
    public void setUp() {
        httpSession = new MockHttpSession();
        httpSession.setAttribute("user", new SessionUser(member));
    }

    @Test
    @WithMockUser(roles = "Role_ADMIN")
    @DisplayName("리턴하는 View 이름은 admin이어야한다.")
    void admin_page() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPage"));
    }

}