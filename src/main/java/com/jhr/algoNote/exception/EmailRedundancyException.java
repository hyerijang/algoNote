package com.jhr.algoNote.exception;

import com.jhr.algoNote.exception.basic.UserException;

/**
 * 이메일 중복시 발생하는 예외
 */
public class EmailRedundancyException extends UserException {

    public EmailRedundancyException(String message) {
        super(message);
    }

}
