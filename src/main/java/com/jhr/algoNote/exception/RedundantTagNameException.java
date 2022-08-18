package com.jhr.algoNote.exception;

import com.jhr.algoNote.exception.basic.UserException;

/**
 * DB에 중복되는 태그 이름을 저장하려 했을 때 발생하는 예외
 */
public class RedundantTagNameException extends UserException {

    public RedundantTagNameException(String message) {
        super(message);
    }
}
