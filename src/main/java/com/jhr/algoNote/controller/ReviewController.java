package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.dto.ReviewCreateRequest;
import com.jhr.algoNote.dto.ReviewDetails;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ReviewService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ReviewController {


    private final ReviewService reviewService;
    private final MemberService memberService;

    final String CREATE = "/reviews/new";
    final String DETAILS = "/reviews/{reviewId}";

    @GetMapping(CREATE)
    public String createForm(Model model, @RequestParam Long pid) {

        //이전 페이지 url가져오기

        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setProblemId(pid);
        model.addAttribute("form", reviewForm);
        //리뷰 생성 폼
        return "/reviews/createReviewForm";
    }


    @PostMapping(CREATE)
    public String create(@Valid ReviewForm reviewForm,
                         @LoginUser SessionUser user) {

        Member member = memberService.findByEmail(user.getEmail());
        //리뷰 생성
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
                .title(reviewForm.getTitle())
                .tagText(reviewForm.getTagText())
                .problemId(reviewForm.getProblemId())
                .contentText(reviewForm.getContentText())
                .build();

        Long reviewId = reviewService.createReview(member.getId(), reviewCreateRequest);

        log.debug("review is created (reviewId={})", reviewId);
        return "redirect:/problems/" + reviewForm.getProblemId();
    }

    @GetMapping(DETAILS)
    public String detailsForm(Model model, @PathVariable Long reviewId) {

        Review review = reviewService.findOne(reviewId);

        ReviewDetails r = ReviewDetails.builder()
                .id(reviewId)
                .problemId(review.getProblem().getId())
                .title(review.getTitle())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .contentText(review.getContent().getText())
                .tagText(reviewService.getTagText(review.getReviewTags()))
                .build();
        model.addAttribute("review", r);
        return "/reviews/reviewDetailsForm";
    }

}
