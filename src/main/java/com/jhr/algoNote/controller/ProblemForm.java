package com.jhr.algoNote.controller;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.content.ProblemContent;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemForm {

    @NotEmpty(message = "제목은 필수입니다")
    private String title;
    private String url;
    private String contentText;
    private String tagText;
    private String siteName;

}
