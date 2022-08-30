package com.jhr.algoNote.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.repository.TagRepository;
import com.jhr.algoNote.service.TagService;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class TagTest {

    @MockBean
    TagRepository tagRepository;

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
    @DisplayName("태그이름으로 생성될수 없는 문자")
    void IllegalKoreanTextForTagNames() throws Exception {
        // given
        String st = "한글 자음 : ㄱ, ㄴ,ㄷ,ㄹ,ㅁ,ㅂ,ㅅ,ㅎㅎㅎ,"
            + "모음 :ㅏ,ㅐㅓㅣ, ,"
            + "태그,이름이 될 수 없습니다ㄹㄹ."; //한글 자음,모음은 태그 이름이 될 수 없다.
        // when
        String[] names = TagService.sliceTextToTagNames(st);

        // then
        for (String name : names) {
            System.out.println("name ={" + name + "}");
        }
        assertEquals(8, names.length);

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

    @Test
    @DisplayName("태그 이름 생성은'-'와'_'를 포함 할 수 있다")
    void allowedSpecialCharacters() throws Exception {
        // given
        // '-' '_'는 포함 가능
        String st = "-_-";
        TagService tagService = new TagService(tagRepository);
        Method method = tagService.getClass().getDeclaredMethod("stringReplace", String.class);
        method.setAccessible(true);
        // when
        String result = (String) method.invoke("stringReplace", st);
        // then
        result = result.replaceAll("\\s", ""); //비교를 위해 공백 제거
        assertEquals("[-_-]", "[" + result + "]");
    }


}
