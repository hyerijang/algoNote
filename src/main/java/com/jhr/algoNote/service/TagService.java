package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.tag.ReviewTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.exception.RedundantTagNameException;
import com.jhr.algoNote.repository.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Long saveTag(Tag tag) {
        if (findByName(tag.getName()) != null) {
            throw new RedundantTagNameException("중복되는 태그 이름입니다.");
        }

        Long savedId = tagRepository.save(tag);
        log.debug("Generate new tag : id = {},  name = {}", tag.getId(), tag.getName());
        return savedId;
    }

    public List<Tag> findTags() {
        return tagRepository.findAll();
    }

    /**
     * 태그 id로 조회
     */
    public Tag findOne(Long itemId) {
        return tagRepository.findById(itemId);
    }

    /*
     * 태그 이름으로 조회
     */
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    //== 태그 문자 가공==

    /**
     * 1개의 문자열을 여러개의 태그이름으로 구분
     */
    public static String[] sliceTextToTagNames(String text) {
        text = stringReplace(text).trim(); //마지막에 공백 제거
        return text.split("\\s+");
    }

    /**
     * 한글, 숫자, 영문, 띄어쓰기,언더바, '-' 제외한 모든 특수문자 제거
     */
    private static String stringReplace(String str) {
        String match = "[^\uAC00-\uD7A30-9a-zA-Z\\-_]";
        str = str.replaceAll(match, " ");
        return str;
    }


    @Transactional
    public List<Tag> getTagList(String[] tagNames) {
        ArrayList<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < tagNames.length; i++) {
            Tag tag = getTag(tagNames[i]);
            tagList.add(tag);
        }
        return tagList;
    }

    /**
     * 태그 조회 후, 없으면 신규 등록
     * @param tagName
     * @return tag
     */
    private Tag getTag(String tagName) {
        Tag tag = findByName(tagName);
        //미 등록된 태그명이면 새로 등록
        if (tag == null) {
            tag = Tag.builder().name(tagName).build();
            saveTag(tag);
        }
        return tag;
    }

}
