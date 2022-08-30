package com.jhr.algoNote.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;


public class MemberTest {

    @MockBean
    private MemberRepository memberRepository;

    @Test
    void 회원_이름은_null일_수_없음() throws Exception {
        // given
        assertThrows(NullPointerException.class, () -> {
            Member member = Member.builder()
                .name(null)
                .email("xxx@gmail.com")
                .role(Role.USER)
                .build();
            memberRepository.save(member);
        }, "이름이 null일때 에러가 발생해야합니다.");
    }
}
