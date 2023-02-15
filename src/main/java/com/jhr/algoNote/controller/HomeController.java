package com.jhr.algoNote.controller;

import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;

import javax.servlet.http.HttpSession;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;
    private final ProblemService problemService;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
        }

        //문제 조회
        ProblemSearch problemSearch = ProblemSearch.builder()
                .memberEmail(user.getEmail())
                .build();
        List<Problem> problems = problemService.search(problemSearch);

        model.addAttribute("problems", problems);

        return "home";
    }
}
