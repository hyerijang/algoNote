package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.exception.RedundantTagNameException;
import com.jhr.algoNote.repository.TagRepository;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ProblemService problemService;

    @Test
    void 태그_등록() throws Exception {
        // given
        Tag tag = Tag.builder().name("태그1").build();

        // when
        Long savedId = tagService.saveTag(tag);
        Tag findTag = tagRepository.findById(savedId);

        // then
        assertEquals(tag.getName(), findTag.getName());
    }

    @Test
    void 태그_이름_중복() throws Exception {
        // given
        String name = "태그";
        Tag tag1 = Tag.builder().name(name).build();
        Tag tag2 = Tag.builder().name(name).build();

        // when
        tagService.saveTag(tag1);

        assertThrows(RedundantTagNameException.class,
            () -> {
                tagService.saveTag(tag2); //태그 이름에 특수문자
            },
            "예외가 발생해야 한다.");

    }

    @Test
    void 태그_단건_조회() throws Exception {
        // given
        Tag tag = Tag.builder().name("태그1").build();
        Long savedId = tagService.saveTag(tag);

        // when
        Tag findTag = tagService.findOne(savedId);

        // then
        assertNotEquals(null, findTag.getName());
        assertEquals(tag.getName(), findTag.getName());
    }

    @Test
    void 태그_목록_조회() throws Exception {
        // given
        Tag tag1 = Tag.builder().name("태그1").build();
        Tag tag2 = Tag.builder().name("태그2").build();
        tagService.saveTag(tag1);
        tagService.saveTag(tag2);

        // when
        List<Tag> tagList = tagService.findTags();

        // then
        assertEquals(2, tagList.size());
    }

    @Test
    void 태그이름_특수문자_비허용() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> {
                Tag.builder().name("태그!").build(); //태그 이름에 특수문자
            },
            "예외가 발생해야 한다.");
    }

    @Test
    void 태그이름_공백문자_비허용() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> {
                Tag.builder().name("태 그").build(); //태그 이름에 공백
            },
            "예외가 발생해야 한다.");
    }


    @Test
    @DisplayName("태그이름으로 사용하기 위해 텍스트를 분할")
    void sliceTextToTagNames() throws Exception {
        // given
        String st = "   안녕하세요. 홍-길_동; 입니다,,, 반가워요! \n\n   , 잘 있어요?,      ";
        // when
        String[] names = TagService.sliceTextToTagNames(st);

        // then
        assertEquals(6, names.length);

    }

    @Test
    @DisplayName("태그 이름 생성은 특수문자를 포함 할 수 없다")
    void checkWhiteSpace() throws Exception {
        // given
        // '-' '_'는 포함 가능
        String st = "!\"#$%&(){}@`*:+;.<>,^~|'[]";

        TagService tagService = new TagService(tagRepository);
        Method method = tagService.getClass().getDeclaredMethod("stringReplace", String.class);
        method.setAccessible(true);
        // when

        String result = (String) method.invoke("stringReplace", st);

        // then
        result = result.replaceAll("\\s", ""); //비교를 위해 공백 제거
        assertEquals("[]", "[" + result + "]");
    }



}