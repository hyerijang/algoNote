package com.jhr.algoNote.api.controller;

import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.query.ProblemQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ProblemRepository problemRepository;
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


}
