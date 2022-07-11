package com.jhr.algoNote.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Site {
    BAEKJOON("BAEKJOON", "백준"),
    PROGRAMMERS("PROGRAMMERS", "프로그래머스"),
    LEETCODE("LEETCODE", "리트코드");

    private final String code;
    private final String name;
}
