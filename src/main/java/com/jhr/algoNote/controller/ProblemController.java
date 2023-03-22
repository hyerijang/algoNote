package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.domain.Site;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.dto.ProblemDetails;
import com.jhr.algoNote.dto.ProblemUpdateRequest;
import com.jhr.algoNote.dto.ReviewDetails;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.ReviewService;
import com.jhr.algoNote.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final MemberService memberService;
    private final ReviewService reviewService;

    //URI
    private final String CREAT = "/new";
    private final String EDIT = "/{id}/edit";
    private final String SEARCH = "/search";
    private final String DETAILS = "/{id}";

    @GetMapping(CREAT)
    public String createForm(Model model) {
        model.addAttribute("problemForm", new ProblemForm());
        //사이트 정보
        model.addAttribute("sites", Site.values());
        return "problems/createProblemForm";
    }

    @PostMapping(CREAT)
    public String creat(@Valid ProblemForm problemForm, BindingResult bindingResult, Model model, @LoginUser SessionUser user) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("sites", Site.values());
            return "problems/createProblemForm";
        }

        Member member = memberService.findByEmail(user.getEmail());
        ProblemCreateRequest problemCreateRequest = ProblemCreateRequest.builder()
                .title(problemForm.getTitle())
                .contentText(problemForm.getContentText())
                .url(problemForm.getUrl())
                .tagText(problemForm.getTagText())
                .site(problemForm.getSite())
                .build();
        Long problemId = problemService.register(member.getId(), problemCreateRequest);
        log.error("문제아이디 {}",problemId);
        return "redirect:/problems/"+problemId;
    }

    /**
     * 로그인한 유저가 등록한 문제 조회
     *
     * @param model
     * @return
     */
    @GetMapping
    public String list(Model model, @LoginUser SessionUser user) {

        ProblemSearch problemSearch = ProblemSearch.builder()
                .memberEmail(user.getEmail())
                .build();

        List<Problem> problems = problemService.search(problemSearch);

        //ProblemCard dto로 변경
        List<ProblemDetails> list = new ArrayList<>();
        for (Problem problem : problems) {
            String tagText = problemService.getTagText(problem.getProblemTags());
            ProblemDetails dto = ProblemDetails.builder()
                    .id(problem.getId())
                    .title(problem.getTitle())
                    .siteName(problem.getSite())
                    .tagText(tagText)
                    .createdDate(problem.getCreatedDate())
                    .modifiedDate(problem.getModifiedDate())
                    .build();
            list.add(dto);
        }
        model.addAttribute("problems", list);
        //사이트 정보
        model.addAttribute("sites", Site.values());

        return "problems/problemList";
    }

    @GetMapping(EDIT)
    public String updateProblemForm(@PathVariable Long id, Model model) {
        Problem problem = problemService.findOne(id);
        ProblemForm form = new ProblemForm();
        //문제태그정보 text로 변환
        String tagText = problemService.getTagText(problem.getProblemTags());
        form.setId(problem.getId());
        form.setTitle(problem.getTitle());
        form.setUrl(problem.getUrl());
        form.setContentText(problem.getContent().getText());
        form.setTagText(tagText);
        form.setSite(problem.getSite());

        model.addAttribute("problemForm", form);
        //사이트 정보
        model.addAttribute("sites", Site.values());
        return "problems/updateProblemForm";
    }

    @PostMapping(EDIT)
    public String edit(@Valid ProblemForm problemForm, BindingResult bindingResult, Model model, @LoginUser SessionUser user) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("sites", Site.values());
            return "problems/updateProblemForm";
        }

        Member member = memberService.findByEmail(user.getEmail());

        ProblemUpdateRequest dto = ProblemUpdateRequest.builder()
                .title(problemForm.getTitle())
                .contentText(problemForm.getContentText())
                .url(problemForm.getUrl())
                .tagText(problemForm.getTagText())
                .site(problemForm.getSite())
                .id(problemForm.getId())
                .build();

        problemService.edit(member.getId(), dto);
        return "redirect:";
    }

    @GetMapping(SEARCH)
    public String searchProblemForm(@ModelAttribute("problemSearch") ProblemSearch problemSearch,
                                    Model model, @LoginUser SessionUser user) {
        model.addAttribute("userEmail", user.getEmail());
        //자신의 문제만 검색 가능
        problemSearch.setMemberEmail(user.getEmail());
        //검색 DTO 생성
        List<ProblemDetails> list = new ArrayList<>();
        for (Problem problem : problemService.search(problemSearch)) {
            //태그 추출
            String tagText = problemService.getTagText(problem.getProblemTags());
            //DTO 변환
            ProblemDetails dto = ProblemDetails.builder()
                    .id(problem.getId())
                    .title(problem.getTitle())
                    .siteName(problem.getSite())
                    .tagText(tagText)
                    .createdDate(problem.getCreatedDate())
                    .modifiedDate(problem.getModifiedDate())
                    .build();
            list.add(dto);
        }

        model.addAttribute("problems", list);

        return "problems/problemSearch";
    }


    @GetMapping(DETAILS)
    public String ProblemDetails(@PathVariable Long id, Model model) {
        // 문제 조회
        Problem problem = problemService.findOne(id);
        if (problem == null) {
            log.warn("problemId:{id}를 조회할 수 없습니다.", id);
            return  "redirect:";
        }
        ProblemDetails p = ProblemDetails.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .url(problem.getUrl())
                .contentText(problem.getContent().getText())
                .tagText(problemService.getTagText(problem.getProblemTags()))
                .siteName(problem.getSite())
                .build();

        //리뷰 정보
        List<ReviewDetails> reviews = new ArrayList<>();
        for (Review r : problem.getReviews()) {
            ReviewDetails reviewDetails = ReviewDetails.builder()
                    .id(r.getId())
                    .title(r.getTitle())
                    .createdDate(r.getCreatedDate())
                    .tagText(reviewService.getTagText(r.getReviewTags()))
                    .build();

            reviews.add(reviewDetails);
        }

        model.addAttribute("form", p);
        model.addAttribute("reviews", reviews);
        model.addAttribute("sites", Site.values());
        return "problems/problemDetails";
    }


}
