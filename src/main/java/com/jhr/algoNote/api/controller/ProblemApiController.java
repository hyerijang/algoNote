package com.jhr.algoNote.api.controller;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.query.ProblemQueryService;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * xToOne(ManyToOne, OneToOne) Problem -> Member Problem -> Contnet
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemApiController {

    private final ProblemService problemService;
    private final ProblemQueryService problemQueryService;


    @GetMapping
    public Result problems(@RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new Result(problemQueryService.findAll(offset, limit));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {

        T data;
    }

    @PostMapping("/new")
    public CreateProblemResponse create(@RequestBody  @Valid  CreateProblemRequest request) {
        Long problemId = problemService.register(request.memberId, request.getTitle(),
            request.getContent(),
            request.tagText, request.site, request.getUrl());
        Problem problem = problemService.findOne(problemId);
        return new CreateProblemResponse(problem.getId(), problem.getTitle(),
            problem.getMember().getId());
    }


    @Data
    static class CreateProblemRequest {

        private Long memberId;
        @NotEmpty(message = "제목은 필수입니다.")
        private String title;
        private String site;
        private String url;
        private String tagText;
        @NotNull
        private String content;
    }

    @Data
    @AllArgsConstructor
    public static class CreateProblemResponse {

        private Long problemId;
        private String title;
        private Long writerId;

    }

}
