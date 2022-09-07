package com.jhr.algoNote.domain;

import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProblemTest {

    Member member = Member.builder()
        .name(null)
        .email("xxx@gmail.com")
        .role(Role.USER)
        .build();

    //문제 내용 생성
    ProblemContent problemContent = ProblemContent.createProblemContent("CONTNET");

    //태그 생성
    List<ProblemTag> problemTagList = new ArrayList<>();

    @Test
    void 문제_제목은_null_일수없음() {
        Assertions.assertThatNullPointerException()
            .isThrownBy(() -> Problem.builder()
                .title(null)
                .member(member)
                .problemTagList(problemTagList)
                .content(problemContent)
                .site("BAEKJOON")
                .build());
    }

    @Test
    void 문제_내용은_null_일수없음() {
        Assertions.assertThatNullPointerException()
            .isThrownBy(() -> Problem.builder()
                .title("TITLE")
                .member(member)
                .problemTagList(problemTagList)
                .content(null)
                .build());
    }

    @Test
    void 문제_작성자는_null_일수없음() {
        Assertions.assertThatNullPointerException()
            .isThrownBy(() -> Problem.builder()
                .title("TITLE")
                .member(null)
                .problemTagList(problemTagList)
                .content(problemContent)
                .build());
    }




}
