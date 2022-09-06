package com.jhr.algoNote.api.controller;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.dto.CreateMemberResponse;
import com.jhr.algoNote.dto.MemberResponse;
import com.jhr.algoNote.repository.MemberRepository;
import com.jhr.algoNote.service.MemberService;
import java.util.Arrays;
import java.util.List;
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

@WebMvcTest(controllers = MemberApiController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    })
public class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    final Long ID = 1L;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .id(ID)
            .name("TEST_NAME")
            .picture("TEST_PICTURE")
            .email("TEST_EMAIL")
            .build();

        when(memberService.join(Mockito.any(CreateMemberRequest.class)))
            .thenReturn(new CreateMemberResponse(ID));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 회원_생성() throws Exception {
        //given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("TEST_NAME","TEST_EMAIL", "TEST_PICTURE");

        //when
        mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
                .content(objectMapper.writeValueAsString(createMemberRequest)))
            .andExpect(status().isOk())
            .andExpect((jsonPath("$.id", is(ID), Long.class)))
            .andDo(print());

    }

    @Test
    @WithMockUser(roles = "USER")
    public void 회원_조회() throws Exception {
        //given
        List<MemberResponse> response = Arrays.asList(
            new MemberResponse(1L, "이름1", "사진1"),
            new MemberResponse(2L, "이름2", "사진2"),
            new MemberResponse(3L, "이름3", "사진3")
        );
        when(memberService.findMembers()).thenReturn(response);

        //when
        mockMvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(response.size())))
            .andExpect(jsonPath("$.data[0].id", is(1L), Long.class))
            .andExpect(jsonPath("$.data[0].name", is("이름1")))
            .andDo(print());
        //then

    }

}
