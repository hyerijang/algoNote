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
import com.jhr.algoNote.api.controller.MemberApiController.CreateMemberRequest;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.repository.MemberRepository;
import com.jhr.algoNote.service.MemberService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
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
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;

    @Test
    @WithMockUser(roles = "USER")
    public void 회원_생성() throws Exception {
        //given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setName("이름");
        createMemberRequest.setEmail("aaa@naver.com");
        createMemberRequest.setPicture("url");

        final Long ID = 1L;
        when(memberService.join(Mockito.any(Member.class)))
            .thenAnswer(i -> ID);

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


    @WithMockUser(roles = "USER")
    @RepeatedTest(5)
    public void 회원_조회(RepetitionInfo repetitionInfo) throws Exception {
        //given
        final Integer SIZE = repetitionInfo.getCurrentRepetition();
        when(memberService.findMembers())
            .thenAnswer(i -> {
                List<Member> result = new ArrayList<>();
                for (long id = 0; id < SIZE; id++) {
                    result.add(
                        new Member((Long) id, "name" + id, "email" + id + "@naver.com", "picture",
                            Role.USER));
                }
                return result;
            });

        //when
        mockMvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(SIZE)))
            .andExpect(jsonPath("$.data[0].id", is(0)))
            .andExpect(jsonPath("$.data[0].name", is("name0")))
            .andDo(print());
        //then

    }

}
