package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.dto.ProblemUpdateRequest;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.ProblemTagRepository;
import com.jhr.algoNote.repository.query.ProblemQueryRepository;
import com.jhr.algoNote.repository.query.ProblemSearch;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {

    private final MemberService memberService;
    private final TagService tagService;
    private final ProblemRepository problemRepository;
    private final ProblemQueryRepository problemQueryRepository;

    private final ProblemTagRepository problemTagRepository;

    /**
     * OCP를 위배하고 비효율적임
     */
    @Deprecated
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content) {
        return register(memberId, title, content, null, null, null);
    }

    /**
     * OCP를 위배하고 비효율적임
     */
    @Deprecated
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
                         String tagText) {
        return register(memberId, title, content, tagText, null, null);
    }

    /**
     * 문제 등록 with site and url,  ProblemCreateRequest 를 인자로 받는 다른 register 사용을 권장
     */
    @Deprecated
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
                         String tagText, String site, String url) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(content);

        //태그 생성
        List<ProblemTag> problemTagList = createProblemTagListWithText(tagText);

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
                Problem.builder()
                        .member(member)
                        .title(title)
                        .content(problemContent)
                        .problemTagList(problemTagList)
                        .url(url)
                        .site(site)
                        .build()
        );
    }

    /**
     * tagNames 을 활용하여 problemTagList 생성
     */
    private List<ProblemTag> createProblemTagListWithText(String tagText) {

        if (isStringEmpty(tagText)) { //태그가 입력되지 않은경우
            return new ArrayList<ProblemTag>();
        }

        String[] tagNames = TagService.sliceTextToTagNames(tagText);
        //문제태그 리스트 생성
        List<ProblemTag> problemTagList = new ArrayList<ProblemTag>();
        //태그 정보 조회
        List<Tag> tagList = tagService.getTagList(tagNames);
        //문제에 태그 등록
        for (Tag tag : tagList) {
            problemTagList.add(ProblemTag.createProblemTag(tag));
        }
        return problemTagList;
    }


    /**
     * 입력된 문자열이 null이거나, 빈 문자열이거나, 공백만으로 이루어진 문자열인 경우 true를 리턴
     */
    private boolean isStringEmpty(String str) {
        return str == null || str.isBlank();

    }


    /**
     * 검색
     */
    @Transactional
    public List<Problem> search(ProblemSearch problemSearch) {
        return safe(problemQueryRepository.search(problemSearch));
    }

    /**
     * 문제 등록
     *
     * @param memberId
     * @param problemCreateRequest DTO
     * @return problemId
     */
    @Transactional
    public Long register(@NonNull Long memberId, ProblemCreateRequest problemCreateRequest) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(
                problemCreateRequest.getContentText());

        //태그 생성
        List<ProblemTag> problemTagList = createProblemTagListWithText(
                problemCreateRequest.getTagText());

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
                Problem.builder()
                        .member(member)
                        .title(problemCreateRequest.getTitle())
                        .content(problemContent)
                        .problemTagList(problemTagList)
                        .url(problemCreateRequest.getUrl())
                        .site(problemCreateRequest.getSite())
                        .build()
        );
    }

    public Problem findOne(Long id) {
        return problemRepository.findById(id);
    }


    /**
     * 문제 수정, 수정시 요청자와 문제 작성자가 다르면 예외 발생
     *
     * @param memberId             수정자 id
     * @param problemUpdateRequest
     * @return 수정된 문제의 id
     * @throws IllegalArgumentException 작성자가 아닙니다.
     */
    @Transactional
    public Long edit(@NonNull Long memberId, ProblemUpdateRequest problemUpdateRequest) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);
        Problem problem = problemRepository.findById(problemUpdateRequest.getId());
        validateWriterAndEditorAreSame(memberId, problem);

        //문제 내용 수정
        problem.getContent().editText(problemUpdateRequest.getContentText());

        //태그 정보 변경된 경우 태그 정보 갱신
        Boolean state = isTagChanged(problemUpdateRequest.getTagText(), problem);
        if (state == TRUE) {
            List<ProblemTag> newProblemTags = updateTagList(problemUpdateRequest.getTagText(), problem);
            problem.updateTag(newProblemTags );
        }
        //문제 update
        problem.update(problemUpdateRequest.getTitle(), problemUpdateRequest.getSite(),
                problemUpdateRequest.getUrl());

        return problem.getId();
    }

    /*
        수정자가 작성자와 동일한지 검증
     */
    private void validateWriterAndEditorAreSame(Long memberId, Problem problem) {
        if (!memberId.equals(problem.getMember().getId())) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
    }


    private List<ProblemTag> updateTagList(String tagText,
                                           Problem problem) {
        String originalTegText = getTagText(problem.getProblemTags());

        //태그 정보 변경 된 경우
        //1. 기존 태그정보 삭제
        if (problem.getProblemTags().size() > 0) {
            problemTagRepository.deleteAllByProblemId(problem.getId());
            problem.getProblemTags().clear();
        }

        //2. 입력받은 텍스트로 문제태그 생성
        return createProblemTagListWithText(tagText);
    }


    /**
     * ProblemTagList를 String으로 변환
     *
     * @param problemTagList
     * @return String
     */
    public String getTagText(List<ProblemTag> problemTagList) {
        if (problemTagList.size() == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (ProblemTag problemTag : problemTagList) {
            sb.append(problemTag.getTag().getName());
            sb.append(",");
        }

        sb.setLength(sb.length() - 1); //마지막 ','제거
        return sb.toString();

    }


    private Boolean isTagChanged(String tagText,
                                 Problem problem) {
        String originalTegText = getTagText(problem.getProblemTags());

        //태그정보 변경되지 않은 경우
        if ((originalTegText.length() == tagText.length()) &&
                originalTegText.equals(tagText)) {
            return Boolean.FALSE;
        }

        return TRUE;
    }

    /**
     * problem list가 비어있는 경우 null을 반환
     * @param other
     * @return
     */
    public static List<Problem> safe(List<Problem> other ) {
        return other == null ? Collections.EMPTY_LIST : other;
    }
}
