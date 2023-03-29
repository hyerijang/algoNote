package com.jhr.algoNote.interceptor;

import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class LoginUserInterceptorTest {
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession httpSession;

    @BeforeEach
    public void setUp() {
        httpSession = new MockHttpSession();
        Member member = new Member("홍길동", "abc@naver.com", "pic", null);
        httpSession.setAttribute("user", new SessionUser(member));
    }

    @Test
    void userName_모델_등록() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/").session(httpSession))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userName"))
                .andExpect(MockMvcResultMatchers.model().attribute("userName", "홍길동"));

    }

    @Test
    void userImg_모델_등록() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/").session(httpSession))
                .andExpect(MockMvcResultMatchers.model().attribute("userImg", "pic"));

    }
}
