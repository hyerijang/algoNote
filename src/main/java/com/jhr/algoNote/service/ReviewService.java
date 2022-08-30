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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        validateWriterAndEditorAreSame(member, problem);

        //(1)리뷰태그 생성
        List<ReviewTag> reviewTagList = createReviewTags(reviewCreateRequest.getTagText());
        try {
            //(2)내용생성
            ReviewContent rc = ReviewContent.of(reviewCreateRequest.getContentText());
            //(3)리뷰 생성
            Review review = Review.builder()
                .member(member)
                .problem(problem)
                .title(reviewCreateRequest.getTitle())
                .reviewTagList(reviewTagList) //(1)
                .content(rc)//(2)
                .build();

            return reviewRepository.save(review);

        } catch (DataIntegrityViolationException e) {
            throw new NullPointerException("내용의 text는 null일 수 없습니다.");
        }

    }

    private void validateWriterAndEditorAreSame(Member member, Problem problem) {
        if (member.getId() != problem.getMember().getId()) {
            log.info(
                "user attempt to write a review with other member id (user id={}, stolen id={})",
                member.getId(), problem.getId());
            throw new IllegalArgumentException("문제 작성자가 아닙니다.");
        }
    }

    private void validateReviewContentIsNotNull(ReviewCreateRequest reviewCreateRequest) {
        if (reviewCreateRequest.getContentText() == null) {
            log.info("review text is null");
            throw new NullPointerException("내용의 text 는 null일 수 없습니다.");
        }
    }

    private List<ReviewTag> createReviewTags(String tagText) {

        if (isStringEmpty(tagText)) { //태그가 입력되지 않은경우
            return new ArrayList<ReviewTag>();
        }

        String[] tagNames = TagService.sliceTextToTagNames(tagText);
        //리뷰 태그 리스트 생성
        List<ReviewTag> reviewTagList = new ArrayList<ReviewTag>();
        //태그 정보 조회
        List<Tag> tagList = tagService.getTagList(tagNames);
        //리뷰에 태그 등록
        for (Tag tag : tagList) {
            reviewTagList.add(ReviewTag.createReviewTag(tag));
        }
        return reviewTagList;
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

    /**
     * 입력된 문자열이 null이거나, 빈 문자열이거나, 공백만으로 이루어진 문자열인 경우 true를 리턴
     */
    private boolean isStringEmpty(String str) {
        return str == null || str.isBlank();

    }
}
