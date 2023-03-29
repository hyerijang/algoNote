package com.jhr.algoNote.interceptor;

import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class SearchInterceptorTest {
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession httpSession;

    @BeforeEach
    public void setUp() {
        httpSession = new MockHttpSession();
    }

    @Test
    void problemSearch_모델_추가() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/").session(httpSession))
                .andExpect(MockMvcResultMatchers.model().attributeExists("problemSearch"));

    }
}
