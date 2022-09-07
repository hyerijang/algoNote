package com.jhr.algoNote.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Site {
    BAEKJOON("BAEKJOON", "BAEKJOON"),
    PROGRAMMERS("PROGRAMMERS", "PROGRAMMERS"),
    LEETCODE("LEETCODE", "리트코드"),
    NO("NO", "없음");
    private final String code;
    private final String name;
}
