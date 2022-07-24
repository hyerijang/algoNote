package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.domain.content.ReviewContent;
import com.jhr.algoNote.domain.tag.ReviewTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.dto.ReviewCreateRequest;
import com.jhr.algoNote.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TagService tagService;

    private final ProblemService problemService;
    private final MemberService memberService;

    @Transactional
    public Long createReview(Long memberId, ReviewCreateRequest reviewCreateRequest) {

        Member member = memberService.findOne(memberId);
        Problem problem = problemService.findOne(reviewCreateRequest.getProblemId());

        if (member.getId() != problem.getMember().getId()) {
            throw new IllegalArgumentException("문제 작성자가 아닙니다.");
        }

        //(1)리뷰태그 생성
        List<ReviewTag> reviewTagList = new ArrayList<ReviewTag>();
        String[] tagNames = TagService.sliceTextToTagNames(reviewCreateRequest.getTagText());

        for (int i = 0; i < tagNames.length; i++) {
            Tag tag = tagService.findByName(tagNames[i]);
            if (tag == null) { //미 등록된 태그명이면 새로 등록
                tag = Tag.builder().name(tagNames[i]).build();
                tagService.saveTag(tag);
            }
            //문제태그에 태그 등록
            reviewTagList.add(ReviewTag.createReviewTag(tag));
        }

        //(2)내용생성
        if (reviewCreateRequest.getContentText() == null) {
            throw new NullPointerException("내용의 text 는 null일 수 없습니다.");
        }
        ReviewContent rc = new ReviewContent();
        rc.setText(reviewCreateRequest.getContentText());

        //리뷰 생성
        Review review = Review.builder()
            .member(member)
            .problem(problem)
            .title(reviewCreateRequest.getTitle())
            .reviewTagList(reviewTagList) //(1)
            .content(rc)//(2)
            .build();

        return reviewRepository.save(review);
    }

    /**
     * 회원ID로 해당 회원이 작성한 모든 리뷰를 조회한다.
     *
     * @param memberId
     * @return
     */
    public List<Review> findReviews(Long memberId) {
        return reviewRepository.findByMemberId(memberId);
    }

    /**
     * 리뷰 Id로 단건 조회한다.
     */
    public Review findOne(Long reviewId) {
        return reviewRepository.findOne(reviewId);

    }

    /**
     * ReviewTagList를 String으로 변환
     *
     * @param reviewTagList
     * @return String
     */
    public String getTagText(List<ReviewTag> reviewTagList) {
        if (reviewTagList.size() == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (ReviewTag reviewTag : reviewTagList) {
            sb.append(reviewTag.getTag().getName());
            sb.append(",");
        }

        sb.setLength(sb.length() - 1); //마지막 ','제거
        return sb.toString();

    }

}
