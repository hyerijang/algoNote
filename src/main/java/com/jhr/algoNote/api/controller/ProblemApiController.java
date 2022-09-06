package com.jhr.algoNote.api.controller;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.dto.CreateProblemRequest;
import com.jhr.algoNote.service.ProblemService;
import javax.validation.Valid;
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

    @GetMapping
    public Result problems(@RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new Result(problemService.findAll(offset, limit));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {

        T data;
    }

    @PostMapping("/new")
    public CreateProblemResponse create(@RequestBody @Valid CreateProblemRequest request) {
        Long problemId = problemService.register(request);
        Problem problem = problemService.findOne(problemId);
        return new CreateProblemResponse(problem.getId(), problem.getTitle(),
            problem.getMember().getId());
    }


    @Data
    @AllArgsConstructor
    public static class CreateProblemResponse {

        private Long Id;
        private String title;
        private Long memberId;

    }

}
