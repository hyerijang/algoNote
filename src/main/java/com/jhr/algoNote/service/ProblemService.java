package com.jhr.algoNote.service;

import static java.util.stream.Collectors.toList;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.dto.CreateProblemRequest;
import com.jhr.algoNote.dto.ProblemResponse;
import com.jhr.algoNote.dto.UpdateProblemRequest;
import com.jhr.algoNote.dto.UpdateProblemResponse;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.ProblemTagRepository;
import com.jhr.algoNote.repository.query.ProblemQueryRepository;
import com.jhr.algoNote.repository.query.ProblemSearch;
import java.util.ArrayList;
import java.util.List;

import com.jhr.algoNote.repository.query.ProblemTagQueryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {

    private final MemberService memberService;
    private final TagService tagService;
    private final ProblemRepository problemRepository;

    private final ProblemTagRepository problemTagRepository;
    private final ProblemQueryRepository problemQueryRepository;

    private final ProblemTagQueryRepository problemTagQueryRepository;

    @Deprecated
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
        String tagText, String site, String url) {
        return register(new CreateProblemRequest(title, url, content, tagText, site, memberId));
    }

    @Transactional
    public Long register(CreateProblemRequest createProblemRequest) {
        //엔티티 조회
        Member member = memberService.findOne(createProblemRequest.getMemberId());

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(
            createProblemRequest.getContentText());

        //태그 생성 및 등록
        List<ProblemTag> problemTagList = createProblemTagListWithText(
            createProblemRequest.getTagText());

        //문제 등록
        return problemRepository.save(
            Problem.builder()
                .member(member)
                .title(createProblemRequest.getTitle())
                .content(problemContent)
                .problemTagList(problemTagList)
                .url(createProblemRequest.getUrl())
                .site(createProblemRequest.getSite())
                .build()
        );
    }

    /**
     * tagText 을 활용하여 problemTagList 생성
     */
    private List<ProblemTag> createProblemTagListWithText(String tagText) {

        if (isStringEmpty(tagText)) {
            return new ArrayList<ProblemTag>();
        }
        List<ProblemTag> problemTagList = new ArrayList<ProblemTag>();

        for (Tag tag : tagService.getTagList(TagService.sliceTextToTagNames(tagText))) {
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


    public Problem findOne(Long id) {
        return problemRepository.findById(id);
    }


    /**
     * 문제 수정, 수정시 요청자와 문제 작성자가 다르면 예외 발생
     *
     * @param editorId 수정자 id
     * @param request
     * @return 수정된 문제의 id
     * @throws IllegalArgumentException 작성자가 아닙니다.
     */
    @Transactional
    public Long update(@NonNull Long editorId, @NonNull Long problemId, UpdateProblemRequest request) {
        validateWriterAndEditorAreSame(editorId, problemRepository.findById(problemId).getMember().getId());
        update(problemId, request);
        return problemId;
    }
    private void validateWriterAndEditorAreSame(Long editorId, Long writerId) {
        if (!editorId.equals(writerId)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
    }
    


    private static boolean equals(String tagText, String originalTegText) {
        if ((originalTegText.length() == tagText.length()) &&
            originalTegText.equals(tagText)) {
            return true;
        }
        return false;
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


    public List<Problem> search(int offset, int limit, ProblemSearch problemSearch) {
        return problemQueryRepository.search(offset, limit, problemSearch);
    }

    public List<ProblemResponse> findAll(int offset, int limit) {
        List<Problem> problems = problemQueryRepository.findAll(offset, limit);
        return problems.stream().map(p -> new ProblemResponse(p)).collect(toList());
    }


    @Transactional
    public UpdateProblemResponse update(Long problemId, UpdateProblemRequest request) {

        if (!equals(request.getTagText(), getTagText(problemTagQueryRepository.findAllByProblemId(problemId)))) {
            problemTagRepository.deleteAllByProblemId(problemId); //벌크연산
            Problem problem = problemRepository.findById(problemId);
            problem.renewalProblemTag(createProblemTagListWithText(request.getTagText()));
        }

        Problem problem = problemRepository.findById(problemId);
        problem.patch(request.getTitle(), request.getContentText(), request.getSite(),
            request.getUrl());
        return UpdateProblemResponse.of(problem,getTagText(problem.getProblemTags()));
    }

}
