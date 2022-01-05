package com.jhr.algoNote.config.auth;

import com.jhr.algoNote.config.auth.dto.OAuthAttributes;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.repository.MemberRepository;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class CustomOAuth2UserServiceTest {

    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("신규 유저 생성")
    void saveWithAttributes() throws Exception {
        //given
        CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(
            memberRepository, new MockHttpSession());

        Method method = customOAuth2UserService.getClass().getDeclaredMethod("saveOrUpdate",
            OAuthAttributes.class);
        method.setAccessible(true);

        OAuthAttributes attributes = new OAuthAttributes(null, null, "수정된이름", "이메일", "수정된사진");

        //when
        Member member = (Member) method.invoke(customOAuth2UserService, attributes);

        //than
        Assertions.assertEquals("수정된이름", member.getName());
    }

    @Test
    @DisplayName("기존 유저 수정")
    void UpdateWithAttributes() throws Exception {

        //given
        CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(
            memberRepository, new MockHttpSession());

        Method method = customOAuth2UserService.getClass().getDeclaredMethod("saveOrUpdate",
            OAuthAttributes.class);
        method.setAccessible(true);

        OAuthAttributes attributes = new OAuthAttributes(null, null, "이름", "이메일", "사진");
        Member member = (Member) method.invoke(customOAuth2UserService, attributes);
        Long savedId = member.getId();

        //when
        OAuthAttributes attributes2 = new OAuthAttributes(null, null, "수정된이름", "이메일", "사진");
        Member result = (Member) method.invoke(customOAuth2UserService, attributes2);

        //than
        Assertions.assertEquals(savedId, result.getId());
        Assertions.assertEquals("수정된이름", result.getName()); //수정된 정보
        Assertions.assertEquals("사진", result.getPicture()); //기존 정보 유지
    }

}