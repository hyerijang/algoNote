package com.jhr.algoNote.controller;

import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Site;
import com.jhr.algoNote.dto.ProblemDetails;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;
    private final ProblemService problemService;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        if (user != null) {
            //문제 조회
            ProblemSearch problemSearch = ProblemSearch.builder()
                    .memberEmail(user.getEmail())
                    .build();
            List<Problem> problems = problemService.search(problemSearch);

            //ProblemCard dto로 변경
            List<ProblemDetails> ProblemCards = new ArrayList<>();
            for (Problem problem : problems) {
                String tagText = problemService.getTagText(problem.getProblemTags());
                ProblemDetails dto = ProblemDetails.builder()
                        .id(problem.getId())
                        .title(problem.getTitle())
                        .siteName(problem.getSite())
                        .tagText(tagText)
                        .createdDate(problem.getCreatedDate())
                        .build();
                ProblemCards.add(dto);
            }
            model.addAttribute("problems", ProblemCards);
            //사이트 정보
            model.addAttribute("sites", Site.values());
        }


        return "home";
    }
}
