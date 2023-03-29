package com.jhr.algoNote.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhr.algoNote.api.controller.MemberApiController.UpdateMemberRequest;
import com.jhr.algoNote.api.dto.CreateMemberRequest;
import com.jhr.algoNote.config.auth.SecurityConfig;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.repository.MemberRepository;
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
@WebMvcTest(value = MemberApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureMockMvc(addFilters = false) //403오류시 addFilter = false 추가
class MemberApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MemberService memberService;

    @MockBean
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void initEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MemberApiController(memberService))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        List<Member> memberList = new ArrayList<>();
        Member member1 = Member.builder().name("Hyeri Jang").email("hyeri@naver.com").role(Role.ADMIN).picture("hrj.jpg").build();
        ReflectionTestUtils.setField(member1, "id", 1L);
        Member member2 = Member.builder().name("Kim Minho").email("minho@naver.com").role(Role.USER).picture("minho.jpg").build();
        ReflectionTestUtils.setField(member2, "id", 2L);
        Member member3 = Member.builder().name("Yoo Jaehee").email("yjh123@naver.com").role(Role.USER).picture("yjh.jpg").build();
        ReflectionTestUtils.setField(member3, "id", 3L);

        memberList.add(member1);
        memberList.add(member2);
        memberList.add(member3);

        Mockito.when(memberService.findMembers()).thenReturn(memberList);
    }

    @Test
    void 유저생성() throws Exception {
        //given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        ReflectionTestUtils.setField(createMemberRequest, "name", "이름");
        ReflectionTestUtils.setField(createMemberRequest, "email", "이메일");
        ReflectionTestUtils.setField(createMemberRequest, "picture", "사진");

        Long id = Math.abs(new Random().nextLong());//랜덤한 양의 정수

        //when
        Mockito.when(memberService.join((ArgumentMatchers.any(Member.class)))).thenReturn(id);
        final ResultActions resultActions = mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest)));

        //than
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }


    @Test
    void 유저조회() throws Exception {
        //given

        //when
        final ResultActions resultActions = mockMvc.perform(get("/api/members"));

        //than
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Hyeri Jang"))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].name").value("Kim Minho"));
    }


    @Test
    void 유저정보수정() throws Exception {
        //given
        Member oldMember = Member.builder().name("Old name").picture("Old Picture").email("Old Email").build();
        Member newMember = Member.builder().name("New name").picture("Old Picture").email("Old Email").build();
        //when
        Mockito.when(memberRepository.findById((ArgumentMatchers.anyLong()))).thenReturn(oldMember);//update이전 조회
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest();
        updateMemberRequest.setName("New name");
        Mockito.when(memberService.findOne((ArgumentMatchers.anyLong()))).thenReturn(newMember); // update이후 조회

        final ResultActions resultActions = mockMvc.perform(patch("/api/members/9876")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMemberRequest)));

        //than
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"))
                .andExpect(jsonPath("$.picture").value("Old Picture"));
    }

}