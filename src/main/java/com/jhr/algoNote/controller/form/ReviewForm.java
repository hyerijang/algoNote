package com.jhr.algoNote.controller.form;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewForm {

    @NotEmpty(message = "제목은 필수입니다")
    private String title;
    private String contentText;
    private String tagText;
    private Long problemId;
    private Long reviewId;

}
