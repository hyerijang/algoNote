package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.repository.ProblemRepository;
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

    @Transactional
    public Long register(Long memberId, String title, String content, ProblemTag... problemTags) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(content);

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
            Problem.createProblem(member, title, problemContent, problemTags)
        );
    }


    @Transactional
    public Long register(Long memberId, String title, String content, String siteName, String url,
        ProblemTag... problemTags) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(content);

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
            Problem.createProblem(member, title, problemContent, siteName, url, problemTags)
        );
    }

    /**
     * tagNames 을 활용하여 ProblemTagList 생성
     */
    public ProblemTag[] createProblemTagList(String... tagNames) {
        ProblemTag[] ProblemTagList = new ProblemTag[tagNames.length];
        for (int i = 0; i < tagNames.length; i++) {
            Tag tag = tagService.findByName(tagNames[i]);
            if (tag == null) {
                tag = Tag.builder().name(tagNames[i]).build();
                tagService.saveTag(tag);
            }
            ProblemTagList[i] = ProblemTag.createProblemTag(tag);
        }
        return ProblemTagList;
    }


}
