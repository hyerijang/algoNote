package com.jhr.algoNote.controller.form;

import javax.validation.constraints.NotEmpty;
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
    private String site;
    private Long id;

}
