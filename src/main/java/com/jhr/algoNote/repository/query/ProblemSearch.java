package com.jhr.algoNote.repository.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//**
@Getter
@Setter
public class ProblemSearch {
    private final String site;
    private String memberEmail;
    private String keyword;



    @Builder
    public ProblemSearch(String site, String memberEmail, String keyword) {
        this.site = site;
        this.memberEmail = memberEmail;
        this.keyword = keyword;
    }
}
