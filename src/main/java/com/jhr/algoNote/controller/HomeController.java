package com.jhr.algoNote.controller;

import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;

import javax.servlet.http.HttpSession;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Site;
import com.jhr.algoNote.dto.ProblemCard;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());

            //문제 조회
            ProblemSearch problemSearch = ProblemSearch.builder()
                    .memberEmail(user.getEmail())
                    .build();
            List<Problem> problems = problemService.search(problemSearch);

            //ProblemCard dto로 변경
            List<ProblemCard> ProblemCards = new ArrayList<>();
            for (Problem problem : problems) {
                ProblemCard problemCard = new ProblemCard();
                String tagText = problemService.getTagText(problem.getProblemTags());
                problemCard.setId(problem.getId());
                problemCard.setSiteName(problem.getSite());
                problemCard.setTitle(problem.getTitle());
                problemCard.setTagText(tagText);
                ProblemCards.add(problemCard);
            }
            model.addAttribute("problems", ProblemCards);
            //사이트 정보
            model.addAttribute("sites", Site.values());
        }


        return "home";
    }
}
