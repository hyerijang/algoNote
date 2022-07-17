package com.jhr.algoNote.controller;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

    @NotEmpty(message = "제목은 필수입니다")
    private String title;
    private String contentText;
    private String tagText;
    private Long problemId;

}
